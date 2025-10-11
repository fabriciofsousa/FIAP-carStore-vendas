package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;

import java.util.List;

public interface ListarVendasUseCase {

    List<Vendas> buscarVendidosOrdenadosPorPreco(Status status);
}
