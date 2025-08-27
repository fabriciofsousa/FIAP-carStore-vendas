package br.com.fiap.vendas.config;

import br.com.fiap.vendas.controller.vendas.VendasController;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasVendidasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VendasController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CriarVendasUseCase criarVendasUseCase;

    @MockBean
    private ObterVendasPorIdUseCase obterVendasPorIdUseCase;

    @MockBean
    private ListarVendasVendidasUseCase listarVendasVendidasUseCase;

    @MockBean
    private AlterarStatusVendasUseCase alterarStatusVendasUseCase;

    @Test
    void quandoVendasNaoEncontrado_entao404() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new VendasNaoEncontradoException("Venda não encontrada"));

        mockMvc.perform(get("/vendas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Venda não encontrada"));
    }

    @Test
    void quandoRuntimeException_entao400() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(obterVendasPorIdUseCase.execute(id))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/vendas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro inesperado"));
    }
}
