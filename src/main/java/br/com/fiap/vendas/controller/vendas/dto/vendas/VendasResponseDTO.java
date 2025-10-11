package br.com.fiap.vendas.controller.vendas.dto.vendas;

import br.com.fiap.vendas.domain.Vendas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VendasResponseDTO(
        String id,
        UUID clienteId,
        UUID veiculoId,
        String status,
        LocalDateTime dataVenda,
        List<Vendas.Pagamento> pagamento
) {}
