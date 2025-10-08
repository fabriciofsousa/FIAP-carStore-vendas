package br.com.fiap.vendas.exception;

import br.com.fiap.vendas.config.GlobalExceptionHandler;
import br.com.fiap.vendas.controller.vendas.VendasController;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VendasController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "veiculo.api.url=http://localhost:8081")
class VendasControllerExceptionIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CriarVendasUseCase criarVendasUseCase;

    @Autowired
    private ObterVendasPorIdUseCase obterVendasPorIdUseCase;

    @Autowired
    private ListarVendasUseCase listarVendasVendidasUseCase;

    @Autowired
    private AlterarStatusVendasUseCase alterarStatusVendasUseCase;

    @TestConfiguration
    static class TestConfig {
        @Bean
        CriarVendasUseCase criarVendasUseCase() {
            return Mockito.mock(CriarVendasUseCase.class);
        }

        @Bean
        ObterVendasPorIdUseCase obterVendasPorIdUseCase() {
            return Mockito.mock(ObterVendasPorIdUseCase.class);
        }

        @Bean
        ListarVendasUseCase listarVendasVendidasUseCase() {
            return Mockito.mock(ListarVendasUseCase.class);
        }

        @Bean
        AlterarStatusVendasUseCase alterarStatusVendasUseCase() {
            return Mockito.mock(AlterarStatusVendasUseCase.class);
        }
    }

    @Test
    void getById_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new VendasNaoEncontradoException("Venda não encontrada"));

        mockMvc.perform(get("/vendas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Venda não encontrada"));
    }

    @Test
    void getById_quandoRuntimeException_entao400_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/vendas/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro inesperado"));
    }

    @Test
    void post_quandoRuntimeException_entao400_comMensagemDoHandler() throws Exception {
        when(criarVendasUseCase.execute(any(), any()))
                .thenThrow(new RuntimeException("Erro ao criar"));

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":\"" + UUID.randomUUID() + "\", \"veiculoId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Valor do pagamento deve ser positivo"));
    }
}


