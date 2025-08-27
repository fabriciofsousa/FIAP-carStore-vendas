package br.com.fiap.vendas.config;

import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleVendasNaoEncontradoException_deveRetornar404() {
        VendasNaoEncontradoException ex = new VendasNaoEncontradoException("Venda não encontrada");

        ResponseEntity<String> response = handler.handleVendasNaoEncontradoException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Venda não encontrada");
    }

    @Test
    void handleRuntimeException_deveRetornar400() {
        RuntimeException ex = new RuntimeException("Erro inesperado");

        ResponseEntity<String> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Erro inesperado");
    }
}
