package br.com.fiap.vendas.gateway;

import br.com.fiap.vendas.domain.Vendas;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendasGateway {
    Vendas salvar(Vendas vendas);

    Optional<Vendas> buscarPorId(UUID id);

    List<Vendas> buscarVendidosOrdenadosPorPreco();
}
