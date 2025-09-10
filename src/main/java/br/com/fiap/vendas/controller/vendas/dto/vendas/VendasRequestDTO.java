package br.com.fiap.vendas.controller.vendas.dto.vendas;

import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

public record VendasRequestDTO(
        UUID clienteId,
        UUID veiculoId,
        BigDecimal valorPago,
        FormaPagamento formaPagamento
) {}
