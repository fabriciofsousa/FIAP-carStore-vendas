package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.cliente.ClienteDTO;
import br.com.fiap.vendas.exception.ClienteException;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.ClienteGateway;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObterClienteUseCaseImplTest {

    private ClienteGateway clienteGateway;
    private ObterClienteUseCaseImpl useCase;

    @BeforeEach
    void setup() {
        clienteGateway = mock(ClienteGateway.class);
        useCase = new ObterClienteUseCaseImpl(clienteGateway);
    }

    @Test
    void testObterClientePorId_Sucesso() {
        UUID id = UUID.randomUUID();
        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(id);
        cliente.setNome("Fabricio");

        when(clienteGateway.obterClientePorId(id)).thenReturn(cliente);

        ClienteDTO result = useCase.obterClientePorId(id);

        assertNotNull(result);
        assertEquals("Fabricio", result.getNome());
        verify(clienteGateway, times(1)).obterClientePorId(id);
    }

    @Test
    void testObterClientePorId_ClienteNaoEncontrado() {
        UUID id = UUID.randomUUID();

        when(clienteGateway.obterClientePorId(id)).thenReturn(null);

        ClienteException exception = assertThrows(
                ClienteException.class,
                () -> useCase.obterClientePorId(id)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getMessage().contains("Cliente não encontrado: " + id) ? 404 : 0);
        verify(clienteGateway, times(1)).obterClientePorId(id);
    }

    @Test
    void testObterClientePorId_FeignNotFound() {
        UUID id = UUID.randomUUID();

        when(clienteGateway.obterClientePorId(id)).thenThrow(FeignException.NotFound.class);

        VeiculoException exception = assertThrows(
                VeiculoException.class,
                () -> useCase.obterClientePorId(id)
        );

        assertTrue(exception.getMessage().contains("Cliente não encontrado"));
        verify(clienteGateway, times(1)).obterClientePorId(id);
    }

    @Test
    void testObterClientePorId_FeignExceptionGenerico() {
        UUID id = UUID.randomUUID();

        when(clienteGateway.obterClientePorId(id)).thenThrow(FeignException.FeignClientException.class);

        VeiculoException exception = assertThrows(
                VeiculoException.class,
                () -> useCase.obterClientePorId(id)
        );

        assertTrue(exception.getMessage().contains("Erro ao acessar o serviço de cliente"));
        verify(clienteGateway, times(1)).obterClientePorId(id);
    }

    @Test
    void testObterClientePorId_IdNulo() {
        VeiculoException exception = assertThrows(
                VeiculoException.class,
                () -> useCase.obterClientePorId(null)
        );

        assertEquals("ID do cliente não pode ser nulo", exception.getMessage());
        verify(clienteGateway, times(0)).obterClientePorId(any());
    }
}
