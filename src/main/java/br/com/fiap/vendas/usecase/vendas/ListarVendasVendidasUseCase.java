package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.domain.Vendas;

import java.util.List;

public interface ListarVendasVendidasUseCase {

    List<Vendas> buscarVendidosOrdenadosPorPreco();
}
