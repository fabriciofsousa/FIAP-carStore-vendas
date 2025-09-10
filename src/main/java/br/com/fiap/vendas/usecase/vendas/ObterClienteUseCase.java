package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.controller.vendas.dto.cliente.ClienteDTO;
import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;

import java.util.UUID;

public interface ObterClienteUseCase {
    ClienteDTO obterClientePorId(UUID id);

}
