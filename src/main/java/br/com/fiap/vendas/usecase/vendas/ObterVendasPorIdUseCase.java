package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.domain.Vendas;

import java.util.Optional;
import java.util.UUID;

public interface ObterVendasPorIdUseCase {
    Optional<Vendas> execute(UUID id);
}
