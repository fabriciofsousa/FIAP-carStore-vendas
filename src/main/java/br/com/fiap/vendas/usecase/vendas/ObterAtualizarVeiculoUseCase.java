package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;

import java.util.UUID;

public interface ObterAtualizarVeiculoUseCase {
    VeiculoDTO obterVeiculoPorId(UUID id);

    VeiculoDTO atualizarVeiculo(UUID id, VeiculoDTO veiculoDTO);

}
