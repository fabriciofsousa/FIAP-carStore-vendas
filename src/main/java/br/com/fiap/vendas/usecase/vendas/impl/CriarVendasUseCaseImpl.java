package br.com.fiap.vendas.usecase.vendas.impl;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.StatusVeiculo;
import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.domain.Vendas.Pagamento;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.gateway.VeiculoGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CriarVendasUseCaseImpl implements CriarVendasUseCase {

    private final VendasGateway vendasGateway;
    private final VeiculoGateway veiculoGateway;

    public CriarVendasUseCaseImpl(VendasGateway vendasGateway, VeiculoGateway veiculoGateway) {
        this.vendasGateway = vendasGateway;
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public Vendas execute(Vendas venda, BigDecimal valorPago, FormaPagamento formaPagamento) {
        validarCamposObrigatorios(venda, valorPago, formaPagamento);
        VeiculoDTO veiculo = veiculoGateway.obterVeiculoPorId(venda.getVeiculoId());

        BigDecimal valorTotalPago = venda.getTotalPago();
        BigDecimal valorRestante = veiculo.getPreco().subtract(valorTotalPago);

        if(valorPago.compareTo(valorRestante) > 0) {
            throw new IllegalArgumentException("O valor do pagamento não pode ser maior que o valor restante do veículo.");
        }

        if(valorTotalPago.equals(BigDecimal.ZERO)) {
            BigDecimal valorMinimoEntrada = veiculo.getPreco().multiply(new BigDecimal("0.1"));
            if(valorPago.compareTo(valorMinimoEntrada) < 0) {
                throw new IllegalArgumentException("O valor da entrada deve ser pelo menos 10% do valor do veículo.");
            }
            veiculo.setStatus(StatusVeiculo.RESERVADO);
            veiculoGateway.atualizarVeiculo(veiculo.getId(), veiculo);
        }

        adicionarPagamento(venda, valorPago, formaPagamento);
        atualizarStatus(venda, veiculo);

        return vendasGateway.salvar(venda);
    }

    private void validarCamposObrigatorios(Vendas venda, BigDecimal valorPago, FormaPagamento formaPagamento) {
        if(venda == null) throw new IllegalArgumentException("Venda não pode ser nula");
        if(venda.getClienteId() == null) throw new IllegalArgumentException("ClienteId não pode ser nulo");
        if(venda.getVeiculoId() == null) throw new IllegalArgumentException("VeiculoId não pode ser nulo");
        if(valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        if(formaPagamento == null) throw new IllegalArgumentException("Forma de pagamento obrigatória");
    }

    private void adicionarPagamento(Vendas venda, BigDecimal valor, FormaPagamento formaPagamento) {
        venda.getPagamentos().add(new Pagamento(valor, formaPagamento, LocalDateTime.now()));
    }

    private void atualizarStatus(Vendas venda, VeiculoDTO veiculo) {
        BigDecimal totalPago = venda.getTotalPago();
        if(totalPago.compareTo(venda.getValorTotal()) >= 0) {
            venda.setStatus(Status.CONCLUIDA);
            veiculo.setStatus(StatusVeiculo.VENDIDO);
            veiculoGateway.atualizarVeiculo(veiculo.getId(), veiculo);
        }
    }
}
