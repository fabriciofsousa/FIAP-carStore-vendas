package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.domain.Vendas;

import java.util.UUID;

public interface AlterarStatusVendasUseCase {
    Vendas execute(UUID id, String novoStatus);
}