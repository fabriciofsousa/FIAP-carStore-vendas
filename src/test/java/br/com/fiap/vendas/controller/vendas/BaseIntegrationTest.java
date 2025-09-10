package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.usecase.vendas.*;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "veiculo.api.url=http://localhost:8081")
public abstract class BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean protected VendasGateway vendasGateway;
    @MockitoBean protected ObterAtualizarVeiculoUseCase obterAtualizarVeiculoUseCase;
    @MockitoBean protected CriarVendasUseCase criarVendasUseCase;
    @MockitoBean protected ObterVendasPorIdUseCase obterVendasPorIdUseCase;
    @MockitoBean protected ListarVendasVendidasUseCase listarVendasVendidasUseCase;
    @MockitoBean protected AlterarStatusVendasUseCase alterarStatusVendasUseCase;

    @BeforeEach
    void setupMockMvc() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }
}
