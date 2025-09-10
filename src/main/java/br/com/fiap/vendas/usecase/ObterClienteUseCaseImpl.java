package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.cliente.ClienteDTO;
import br.com.fiap.vendas.exception.ClienteException;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.ClienteGateway;
import br.com.fiap.vendas.usecase.vendas.ObterClienteUseCase;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ObterClienteUseCaseImpl implements ObterClienteUseCase {

    private final ClienteGateway clienteGateway;

    public ObterClienteUseCaseImpl(ClienteGateway clienteGateway) {
        this.clienteGateway = clienteGateway;
    }


    @Override
    public ClienteDTO obterClientePorId(UUID id) {
        try {
            validateId(id);
            ClienteDTO cliente =  clienteGateway.obterClientePorId(id);
            if(cliente == null){
                throw new ClienteException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
            }
            return cliente;
        } catch (FeignException.NotFound ex) {
            throw new VeiculoException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
        } catch (FeignException ex) {
            throw new VeiculoException(HttpStatus.BAD_GATEWAY,
                    "Erro ao acessar o serviço de cliente: " + ex.getMessage());
        }
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new VeiculoException("ID do cliente não pode ser nulo");
        }
    }
}
