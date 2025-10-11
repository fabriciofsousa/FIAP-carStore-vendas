package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VendasControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;

    @Mock private CriarVendasUseCase criarVendasUseCase;
    @Mock private ObterVendasPorIdUseCase obterVendasPorIdUseCase;
    @Mock private ListarVendasUseCase listarVendasVendidasUseCase;
    @Mock private AlterarStatusVendasUseCase alterarVendasUseCase;

    @InjectMocks private VendasController vendasController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vendasController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void deveCriarVendaComSucesso() throws Exception {
        VendasRequestDTO request = new VendasRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(2000),
                FormaPagamento.PIX
        );

        Vendas vendaMock = new Vendas();
        vendaMock.setId(UUID.randomUUID().toString());
        vendaMock.setClienteId(request.clienteId());
        vendaMock.setVeiculoId(request.veiculoId());
        vendaMock.setStatus(Status.INICIADA);

        when(criarVendasUseCase.execute(any(Vendas.class), any())).thenReturn(vendaMock);

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.INICIADA.name())))
                .andExpect(jsonPath("$.clienteId", notNullValue()))
                .andExpect(jsonPath("$.veiculoId", notNullValue()));

        verify(criarVendasUseCase, times(1)).execute(any(Vendas.class), any());
    }

    @Test
    void deveRetornarBadRequestQuandoCriarVendaInvalida() throws Exception {
        VendasRequestDTO request = new VendasRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.ZERO,
                FormaPagamento.CARTAO
        );

        when(criarVendasUseCase.execute(any(Vendas.class), any()))
                .thenThrow(new IllegalArgumentException("Valor do pagamento deve ser positivo"));

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Valor do pagamento deve ser positivo"));
    }

    @Test
    void deveObterVendaPorId() throws Exception {
        UUID vendaId = UUID.randomUUID();
        Vendas vendaMock = new Vendas();
        vendaMock.setId(vendaId.toString());
        vendaMock.setClienteId(UUID.randomUUID());
        vendaMock.setVeiculoId(UUID.randomUUID());
        vendaMock.setStatus(Status.CONCLUIDA);

        when(obterVendasPorIdUseCase.execute(vendaId)).thenReturn(Optional.of(vendaMock));

        mockMvc.perform(get("/vendas/{id}", vendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is(Status.CONCLUIDA.name())));
    }

    @Test
    void deveRetornarNotFoundQuandoVendaNaoExiste() throws Exception {
        UUID vendaId = UUID.randomUUID();
        when(obterVendasPorIdUseCase.execute(vendaId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/vendas/{id}", vendaId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarStatusVenda() throws Exception {
        UUID vendaId = UUID.randomUUID();
        Vendas vendaMock = new Vendas();
        vendaMock.setId(vendaId.toString());
        vendaMock.setStatus(Status.CONCLUIDA);

        when(alterarVendasUseCase.execute(vendaId, "CONCLUIDA")).thenReturn(vendaMock);

        mockMvc.perform(patch("/vendas/{id}/status", vendaId)
                        .param("status", "CONCLUIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONCLUIDA")));
    }
}
