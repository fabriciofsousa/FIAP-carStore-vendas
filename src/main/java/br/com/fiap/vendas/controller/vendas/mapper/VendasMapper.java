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
import java.util.List;
import java.util.UUID;

public class VendasMapper {

    // --- Request DTO → Domain ---
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

    // --- Domain → Response DTO ---
    public static VendasResponseDTO toResponse(Vendas venda) {
        return new VendasResponseDTO(
                venda.getId(),
                venda.getClienteId(),
                venda.getVeiculoId(),
                venda.getStatus().name(),
                venda.getDataVenda(),
                venda.getPagamentos().isEmpty() ? null : venda.getPagamentos()
        );
    }

    // --- Domain → Entity (para salvar no DynamoDB) ---
    public static VendasEntity toEntity(Vendas domain) {
        if (domain == null) return null;

        return VendasEntity.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .veiculoId(domain.getVeiculoId())
                .valorTotal(domain.getValorTotal())
                .status(domain.getStatus())
                .dataVenda(domain.getDataVenda())
                .pagamentos(new ArrayList<>(domain.getPagamentos()))
                .build();
    }

    // --- Entity → Domain (para ler do DynamoDB) ---
    public static Vendas toDomain(VendasEntity entity) {
        if (entity == null) return null;

        return Vendas.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .valorTotal(entity.getValorTotal())
                .status(entity.getStatus())
                .dataVenda(entity.getDataVenda())
                .pagamentos(new ArrayList<>(entity.getPagamentos()))
                .build();
    }

    private static void validarCamposObrigatorios(VendasRequestDTO venda, BigDecimal valorPago, FormaPagamento formaPagamento) {
        if (venda == null) throw new IllegalArgumentException("Venda não pode ser nula");
        if (venda.clienteId() == null) throw new IllegalArgumentException("ClienteId não pode ser nulo");
        if (venda.veiculoId() == null) throw new IllegalArgumentException("VeiculoId não pode ser nulo");
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        if (formaPagamento == null) throw new IllegalArgumentException("Forma de pagamento obrigatória");
    }
}
