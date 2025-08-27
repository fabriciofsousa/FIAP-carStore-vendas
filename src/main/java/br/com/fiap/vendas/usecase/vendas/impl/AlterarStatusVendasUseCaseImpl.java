package br.com.fiap.vendas.usecase.vendas.impl;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.StatusVendas;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AlterarStatusVendasUseCaseImpl implements AlterarStatusVendasUseCase {

    private final VendasGateway vendasGateway;

    public AlterarStatusVendasUseCaseImpl(VendasGateway vendasGateway) {
        this.vendasGateway = vendasGateway;
    }

    @Override
    public Vendas execute(UUID id, String novoStatus) {
        Optional<Vendas> vendaOpt = vendasGateway.buscarPorId(id);

        if (vendaOpt.isEmpty()) {
            throw new VendasNaoEncontradoException("Venda não encontrada: " + id);
        }

        Vendas venda = vendaOpt.get();
        venda.setStatus(converterParaStatus(novoStatus.toUpperCase()));
        return vendasGateway.salvar(venda);
    }

    private Vendas.Status converterParaStatus(String status) {
        try {
            return Vendas.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Status inválido: '" + status + "'. Valores válidos: INICIADA, CONCLUIDA, CANCELADA."
            );
        }
    }
}
