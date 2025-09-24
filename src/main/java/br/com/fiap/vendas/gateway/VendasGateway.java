package br.com.fiap.vendas.gateway;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendasGateway {
    Vendas salvar(Vendas vendas);

    Optional<Vendas> buscarPorId(UUID id);

    List<Vendas> buscarVendidosOrdenadosPorPreco(Status status);

    Vendas buscarVendaIniciadaPorVeiculo(UUID veiculoId);
}
