package br.com.fiap.vendas.controller.vendas.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VendasResponseDTO(
        UUID id,
        UUID clienteId,
        UUID veiculoId,
        String status,
        LocalDateTime dataVenda
) {}
