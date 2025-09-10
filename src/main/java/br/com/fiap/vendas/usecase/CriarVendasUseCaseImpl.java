package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.StatusVeiculo;
import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.domain.Vendas.Pagamento;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.ClienteGateway;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.usecase.vendas.ObterAtualizarVeiculoUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterClienteUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CriarVendasUseCaseImpl implements CriarVendasUseCase {

    private final VendasGateway vendasGateway;
    private final ObterAtualizarVeiculoUseCase obterAtualizarVeiculoUseCase;
    private final ObterClienteUseCase obterClienteUseCase;

    public CriarVendasUseCaseImpl(VendasGateway vendasGateway, ObterAtualizarVeiculoUseCase obterAtualizarVeiculoUseCase, ObterClienteUseCase obterClienteUseCase) {
        this.vendasGateway = vendasGateway;
        this.obterAtualizarVeiculoUseCase = obterAtualizarVeiculoUseCase;
        this.obterClienteUseCase = obterClienteUseCase;
    }

    @Override
    public Vendas execute(Vendas venda, Pagamento pagamento) {
        // Verificar se o cliente existe
        obterClienteUseCase.obterClientePorId(venda.getClienteId());

        VeiculoDTO veiculo = getVeiculo(venda);
        if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {
            throw new VeiculoException("Veículo já vendido.");
        }

        // Verificar se já existe uma venda iniciada para o mesmo veículo
        Vendas vendaExistente = vendasGateway.buscarVendaIniciadaPorVeiculo(venda.getVeiculoId());

        // Valor do pagamento enviado na requisição
        BigDecimal valorPagamentoAtual = pagamento.getValor();
        FormaPagamento formaPagamento = pagamento.getFormaPagamento();

        if (vendaExistente != null) {
            // Verificar se não excede o restante
            BigDecimal valorRestante = veiculo.getPreco().subtract(vendaExistente.getTotalPago());
            if (valorPagamentoAtual.compareTo(valorRestante) > 0) {
                throw new IllegalArgumentException("O valor do pagamento não pode ser maior que o valor restante do veículo.");
            }

            // Adicionar pagamento na venda existente
            adicionarPagamento(vendaExistente, valorPagamentoAtual, formaPagamento);
            atualizarStatus(vendaExistente, veiculo);
            return vendasGateway.salvar(vendaExistente);
        }

        // Se não existe venda iniciada, cria uma nova
        if (valorPagamentoAtual.compareTo(veiculo.getPreco().multiply(new BigDecimal("0.1"))) < 0) {
            throw new IllegalArgumentException("O valor da entrada deve ser pelo menos 10% do valor do veículo.");
        }

        if (veiculo.getStatus() == StatusVeiculo.RESERVADO) {
            throw new VeiculoException("Veículo já reservado.");
        }

        veiculo.setStatus(StatusVeiculo.RESERVADO);
        obterAtualizarVeiculoUseCase.atualizarVeiculo(veiculo.getId(), veiculo);

        atualizarStatus(venda, veiculo);
        return vendasGateway.salvar(venda);
    }


    private VeiculoDTO getVeiculo(Vendas venda) {
        return obterAtualizarVeiculoUseCase.obterVeiculoPorId(venda.getVeiculoId());
    }



    private void adicionarPagamento(Vendas venda, BigDecimal valor, FormaPagamento formaPagamento) {
        venda.getPagamentos().add(new Pagamento(valor, formaPagamento, LocalDateTime.now()));
    }

    private void atualizarStatus(Vendas venda, VeiculoDTO veiculo) {
        BigDecimal totalPago = venda.getTotalPago();
        BigDecimal valorTotal = veiculo.getPreco() != null ? veiculo.getPreco() : BigDecimal.ZERO;

        if (totalPago.compareTo(veiculo.getPreco()) > 0) {
            throw new IllegalArgumentException("O valor pago é maior que o valor do veiculo.");
        }

        if (totalPago.compareTo(valorTotal) == 0) {
            // Venda totalmente paga
            venda.setStatus(Status.CONCLUIDA);
            veiculo.setStatus(StatusVeiculo.VENDIDO);
            obterAtualizarVeiculoUseCase.atualizarVeiculo(veiculo.getId(), veiculo);
        } else {
            // Venda parcialmente paga
            venda.setStatus(Status.INICIADA);
            veiculo.setStatus(StatusVeiculo.RESERVADO);
            obterAtualizarVeiculoUseCase.atualizarVeiculo(veiculo.getId(), veiculo);
        }
    }
}
