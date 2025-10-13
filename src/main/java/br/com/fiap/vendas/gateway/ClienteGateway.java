package br.com.fiap.vendas.gateway;

import br.com.fiap.vendas.controller.vendas.dto.cliente.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "cliente-service",
        url = "${cliente.api.url}"
)
public interface ClienteGateway {

    @GetMapping("/clientes/{id}")
    ClienteDTO obterClientePorId(@PathVariable("id") UUID id);
}
