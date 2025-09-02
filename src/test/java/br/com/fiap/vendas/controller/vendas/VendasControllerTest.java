package br.com.fiap.vendas.controller.vendas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;

class VendasControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;

    @Mock private CriarVendasUseCase criarVendasUseCase;
    @Mock private ObterVendasPorIdUseCase obterVendasPorIdUseCase;
    @Mock private AlterarStatusVendasUseCase alterarVendasUseCase;

    @InjectMocks private VendasController vendasController;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vendasController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Nested
    class CadastroVendas {

        @Test //@Disabled
        void deveCriarVendaValida() throws Exception {
            UUID id = UUID.randomUUID();
            Vendas venda = Vendas.builder()
                    .id(id)
                    .clienteId(UUID.randomUUID())
                    .veiculoId(UUID.randomUUID())
                    .status(Status.INICIADA)
                    .build();

            when(criarVendasUseCase.execute(any(), any(), any())).thenReturn(venda);

            mockMvc.perform(post("/vendas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venda)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.status").value(Status.INICIADA.name()));
        }

        @Test //@Disabled
        void naoDeveCriarVendaComCamposInvalidos() throws Exception {
            when(criarVendasUseCase.execute(any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Campos inválidos"));

            mockMvc.perform(post("/vendas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(Vendas.builder().build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Campos inválidos"));
        }
    }

    @Nested
    class AtualizacaoVendas {

        @Test
        void deveAtualizarStatusParaVendido() throws Exception {
            UUID id = UUID.randomUUID();
            Vendas atualizado = Vendas.builder()
                    .id(id)
                    .clienteId(UUID.randomUUID())
                    .veiculoId(UUID.randomUUID())
                    .status(Status.CONCLUIDA)
                    .build();

            when(alterarVendasUseCase.execute(eq(id), any())).thenReturn(atualizado);

            mockMvc.perform(patch("/vendas/{id}/status", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("status", "CONCLUIDA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(Status.CONCLUIDA.name()));
        }

        @Test
        void naoDeveAtualizarStatusInvalido() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(patch("/vendas/{id}/status", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"INVALIDO\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ConsultaVendas {

        @Test
        void deveObterVendaPorId() throws Exception {
            UUID id = UUID.randomUUID();
            Vendas venda = Vendas.builder()
                    .id(id)
                    .clienteId(UUID.randomUUID())
                    .veiculoId(UUID.randomUUID())
                    .status(Status.INICIADA)
                    .build();

            when(obterVendasPorIdUseCase.execute(id)).thenReturn(Optional.of(venda));

            mockMvc.perform(get("/vendas/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.status").value(Status.INICIADA.name()));
        }
    }

    private String asJsonString(Object obj) {
        try {
            if(obj ==null){
                return "";
            }
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}