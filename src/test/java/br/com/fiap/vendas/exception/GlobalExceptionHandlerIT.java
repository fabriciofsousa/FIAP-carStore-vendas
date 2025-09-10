package br.com.fiap.vendas.exception;

import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "veiculo.api.url=http://localhost:8081")
class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObterVendasPorIdUseCase obterVendasPorIdUseCase;

    private UUID vendaId;

    @BeforeEach
    void setup() {
        vendaId = UUID.randomUUID();
        Mockito.reset(obterVendasPorIdUseCase);
    }

    @Test
    void deveRetornarNotFoundQuandoVendaNaoEncontrada() throws Exception {
        when(obterVendasPorIdUseCase.execute(vendaId))
                .thenThrow(new VendasNaoEncontradoException("Venda não encontrada: " + vendaId));

        mockMvc.perform(get("/vendas/{id}", vendaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Venda não encontrada: " + vendaId));
    }

    @Test
    void deveRetornarBadRequestQuandoRuntimeException() throws Exception {
        when(obterVendasPorIdUseCase.execute(vendaId))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/vendas/{id}", vendaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro inesperado"));
    }
}
