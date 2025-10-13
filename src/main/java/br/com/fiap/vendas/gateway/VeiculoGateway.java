package br.com.fiap.vendas.gateway;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "veiculo-service", url = "${veiculo.api.url}")
public interface VeiculoGateway {

    @GetMapping("/veiculo/{id}")
    VeiculoDTO obterVeiculoPorId(@PathVariable("id") UUID id);

    @PutMapping("/veiculo/{id}")
    VeiculoDTO atualizarVeiculo(@PathVariable("id") UUID id, @RequestBody VeiculoDTO veiculoDTO);
}
