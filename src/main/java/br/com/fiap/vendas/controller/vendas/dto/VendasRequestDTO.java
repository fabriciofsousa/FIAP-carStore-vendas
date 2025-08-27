package br.com.fiap.vendas.controller.vendas.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VendasRequestDTO(
        UUID clienteId,
        UUID veiculoId
) {}