package br.com.fiap.vendas.controller.vendas.dto.vendas;

import java.time.LocalDateTime;
import java.util.UUID;

public record VendasResponseDTO(
        String id,
        UUID clienteId,
        UUID veiculoId,
        String status,
        LocalDateTime dataVenda
) {}
