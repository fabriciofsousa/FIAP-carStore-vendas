package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.usecase.vendas.ListarVendasUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVendasUseCaseImpl implements ListarVendasUseCase {

    private final VendasGateway vendasGateway;

    public ListarVendasUseCaseImpl(VendasGateway vendasGateway) {
        this.vendasGateway = vendasGateway;
    }

    @Override
    public List<Vendas> buscarVendidosOrdenadosPorPreco(Status status) {
        return vendasGateway.buscarVendidosOrdenadosPorPreco(status);
    }
}

