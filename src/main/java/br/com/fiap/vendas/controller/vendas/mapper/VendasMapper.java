package br.com.fiap.vendas.controller.vendas.mapper;

import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasResponseDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class VendasMapper {

    public static Vendas toDomain(VendasRequestDTO dto) {
        validarCamposObrigatorios(dto, dto.valorPago(), dto.formaPagamento());

        Vendas.Pagamento pagamento = Vendas.Pagamento.builder()
                .valor(dto.valorPago())
                .formaPagamento(dto.formaPagamento())
                .dataPagamento(LocalDateTime.now())
                .build();

        return Vendas.builder()
                .id(UUID.randomUUID().toString())
                .clienteId(dto.clienteId())
                .veiculoId(dto.veiculoId())
                .valorTotal(dto.valorPago())
                .pagamentos(new ArrayList<>(List.of(pagamento)))
                .status(Status.INICIADA)
                .dataVenda(LocalDateTime.now())
                .build();
    }


    public static VendasResponseDTO toResponse(Vendas venda) {
        return new VendasResponseDTO(
                venda.getId(),
                venda.getClienteId(),
                venda.getVeiculoId(),
                venda.getStatus().name(),
                venda.getDataVenda());
    }

    private static void validarCamposObrigatorios(VendasRequestDTO venda, BigDecimal valorPago, FormaPagamento formaPagamento) {
        if(venda == null) throw new IllegalArgumentException("Venda não pode ser nula");
        if(venda.clienteId() == null) throw new IllegalArgumentException("ClienteId não pode ser nulo");
        if(venda.veiculoId() == null) throw new IllegalArgumentException("VeiculoId não pode ser nulo");
        if(valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        if(formaPagamento == null) throw new IllegalArgumentException("Forma de pagamento obrigatória");
    }
}
