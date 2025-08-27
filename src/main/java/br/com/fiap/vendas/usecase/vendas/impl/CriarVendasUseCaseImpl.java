package br.com.fiap.vendas.usecase.vendas.impl;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.StatusVendas;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import org.springframework.stereotype.Service;

@Service
public class CriarVendasUseCaseImpl implements CriarVendasUseCase {

    private final VendasGateway vendasGateway;

    public CriarVendasUseCaseImpl(VendasGateway vendasGateway) {
        this.vendasGateway = vendasGateway;
    }

    @Override
    public Vendas execute(Vendas vendas) {
        validarVendas(vendas);
        return vendasGateway.salvar(vendas);
    }

    private void validarVendas(Vendas vendas) {

        if(vendas == null){
            throw new IllegalArgumentException("O objeto 'vendas' não pode ser nulo");
        }

        validarCampoNaoNulo("clienteId", vendas.getClienteId());
        validarCampoNaoNulo("veiculoId", vendas.getVeiculoId());
        validarStatus(vendas.getStatus());

        if (vendas.getStatus() == null) {
            vendas.setStatus(Vendas.Status.INICIADA);
        }

    }

    private void validarCampoNaoNulo(String campo, Object valor) {
        if (valor == null || (valor instanceof String && ((String) valor).trim().isEmpty())) {
            throw new IllegalArgumentException("O campo '" + campo + "' não pode ser nulo");
        }
    }

    private void validarStatus(Vendas.Status status) {
        if (status != null && !(status == Vendas.Status.INICIADA
                || status == Vendas.Status.CONCLUIDA
                || status == Vendas.Status.CANCELADA)) {
            throw new IllegalArgumentException("Status da venda inválido");
        }
    }


}
