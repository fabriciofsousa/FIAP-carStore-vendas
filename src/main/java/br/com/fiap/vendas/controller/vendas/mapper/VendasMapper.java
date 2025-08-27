package br.com.fiap.vendas.controller.vendas.mapper;

import br.com.fiap.vendas.controller.vendas.dto.VendasRequestDTO;
import br.com.fiap.vendas.controller.vendas.dto.VendasResponseDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;

import java.time.LocalDateTime;

public class VendasMapper {

    public static Vendas toDomain(VendasRequestDTO dto) {
        return Vendas.builder()
                .id(null)
                .clienteId(dto.clienteId())
                .veiculoId(dto.veiculoId())
                .status(Vendas.Status.INICIADA)
                .dataVenda(LocalDateTime.now())
                .build();
    }

    private Vendas toDomain(VendasEntity entity) {
        return Vendas.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .status(Vendas.Status.valueOf(entity.getStatus().name()))
                .dataVenda(entity.getDataVenda())
                .build();
    }


    public static VendasResponseDTO toResponse(Vendas venda) {
        return new VendasResponseDTO(
                venda.getId(),
                venda.getClienteId(),
                venda.getVeiculoId(),
                venda.getStatus().name(),
                venda.getDataVenda()
        );
    }
}
