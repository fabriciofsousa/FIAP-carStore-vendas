package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.VeiculoGateway;
import br.com.fiap.vendas.usecase.vendas.ObterAtualizarVeiculoUseCase;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ObterVeiculoUseCaseImpl implements ObterAtualizarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;

    public ObterVeiculoUseCaseImpl(VeiculoGateway veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public VeiculoDTO obterVeiculoPorId(UUID id) {
        try {
            validateId(id);
            return veiculoGateway.obterVeiculoPorId(id);
        } catch (FeignException.NotFound ex) {
            throw new VeiculoException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id);
        } catch (FeignException ex) {
            throw new VeiculoException(HttpStatus.BAD_GATEWAY,
                    "Erro ao acessar o serviço de veículo: " + ex.getMessage());
        }
    }

    @Override
    public VeiculoDTO atualizarVeiculo(UUID id, VeiculoDTO veiculoDTO) {
        try {
            validateId(id);
            validateVeiculoDTO(veiculoDTO);
            return veiculoGateway.atualizarVeiculo(id, veiculoDTO);
        } catch (FeignException.NotFound ex) {
            throw new VeiculoException(HttpStatus.NOT_FOUND, "Veículo não encontrado: " + id);
        } catch (FeignException ex) {
            throw new VeiculoException(HttpStatus.BAD_GATEWAY,
                    "Erro ao acessar o serviço de veículo: " + ex.getMessage());
        }
    }


    private void validateId(UUID id) {
        if (id == null) {
            throw new VeiculoException("ID do veículo não pode ser nulo");
        }
    }

    private void validateVeiculoDTO(VeiculoDTO veiculoDTO) {
        if (veiculoDTO == null) {
            throw new VeiculoException("VeiculoDTO não pode ser nulo");
        }

        if (!StringUtils.hasText(veiculoDTO.getModelo())) {
            throw new VeiculoException("O modelo do veículo é obrigatório");
        }

        if (!StringUtils.hasText(veiculoDTO.getMarca())) {
            throw new VeiculoException("A marca do veículo é obrigatória");
        }

        if (veiculoDTO.getAno() == null || veiculoDTO.getAno() <= 0) {
            throw new VeiculoException("O ano do veículo é inválido");
        }
    }
}
