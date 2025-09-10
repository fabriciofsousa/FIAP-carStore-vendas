package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ObterVendasPorIdUseCaseImpl implements ObterVendasPorIdUseCase {

    private final VendasGateway vendasGateway;

    public ObterVendasPorIdUseCaseImpl(VendasGateway vendasGateway) {
        this.vendasGateway = vendasGateway;
    }

    @Override
    public Optional<Vendas> execute(UUID id) {
        validarId(id);
        Optional<Vendas> vendaOpt = vendasGateway.buscarPorId(id);

        if (vendaOpt.isEmpty()) {
            throw new VendasNaoEncontradoException("Veículo não encontrado para o ID: " + id);
        }

        return vendaOpt;
    }

    private void validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do veículo não pode ser nulo");
        }
    }
}


