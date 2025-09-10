package br.com.fiap.vendas.exception;

import br.com.fiap.vendas.config.GlobalExceptionHandler;
import br.com.fiap.vendas.controller.vendas.VendasController;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.usecase.vendas.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;

    @Mock private CriarVendasUseCase criarVendasUseCase;
    @Mock private ObterVendasPorIdUseCase obterVendasPorIdUseCase;
    @Mock private ListarVendasVendidasUseCase listarVendasVendidasUseCase;
    @Mock private AlterarStatusVendasUseCase alterarStatusVendasUseCase;

    @InjectMocks private VendasController vendasController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(vendasController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void deveRetornarNotFoundQuandoVendaNaoEncontrada() throws Exception {
        UUID id = UUID.randomUUID();

        when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new VendasNaoEncontradoException("Venda não encontrada: " + id));

        mockMvc.perform(get("/vendas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Venda não encontrada: " + id));
    }

    @Test
    void deveRetornarBadRequestQuandoRuntimeException() throws Exception {
        UUID id = UUID.randomUUID();

        when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/vendas/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro inesperado"));
    }
}
