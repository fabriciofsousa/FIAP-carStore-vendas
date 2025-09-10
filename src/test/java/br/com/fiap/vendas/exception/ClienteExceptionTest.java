package br.com.fiap.vendas.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ClienteExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Cliente não encontrado";
        ClienteException exception = new ClienteException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithHttpStatusAndMessage() {
        String message = "Erro ao processar cliente";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Apenas verificar se a exceção é criada com a mensagem correta
        ClienteException exception = new ClienteException(status, message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testThrowsClienteException() {
        String message = "Cliente inválido";

        RuntimeException thrown = assertThrows(
                ClienteException.class,
                () -> { throw new ClienteException(message); }
        );

        assertEquals(message, thrown.getMessage());
    }
}
