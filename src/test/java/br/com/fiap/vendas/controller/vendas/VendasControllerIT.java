package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.config.SecurityConfig;
import br.com.fiap.vendas.config.TestSecurityConfig;
import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.gateway.ClienteGateway;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.usecase.ObterClienteUseCaseImpl;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "CLIENTE_API_URL=localhost:8082",
                "veiculo.api.url=localhost:8081",
                "COGNITO_USER_EMAIL=test",
                "COGNITO_USER_PASSWORD=test",
                "cognito.auth.url=http://localhost:8080/auth",
                "cognito.client.id=meu-client-id",
                "cognito.client.secret=meu-client-secret"
        }
)
@ActiveProfiles("test")
public class VendasControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssured.registerParser("text/plain", io.restassured.parsing.Parser.TEXT);
    }
    @Test
    void deveCriarVendaComSucesso() throws Exception {
        // Arrange
        VendasRequestDTO request = new VendasRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(5000),
                FormaPagamento.CARTAO
        );

        Vendas vendaMock = new Vendas();
        vendaMock.setId(UUID.randomUUID().toString());
        vendaMock.setClienteId(request.clienteId());
        vendaMock.setVeiculoId(request.veiculoId());
        vendaMock.setStatus(Status.INICIADA);

        when(criarVendasUseCase.execute(any(), any())).thenReturn(vendaMock);

        // Act + Assert
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/vendas")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo(Status.INICIADA.name()))
                .body("veiculoId", notNullValue())
                .body("clienteId", notNullValue());

        verify(criarVendasUseCase, times(1)).execute(any(), any());
    }

    @Test
    void deveRetornarBadRequestQuandoValorEntradaInvalido() {
        VendasRequestDTO request = new VendasRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.ZERO,
                FormaPagamento.PIX
        );

        when(criarVendasUseCase.execute(any(), any()))
                .thenThrow(new IllegalArgumentException("Valor do pagamento deve ser positivo"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/vendas")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Valor do pagamento deve ser positivo"));
    }

    @Test
    void deveBuscarVendaPorId() {
        UUID vendaId = UUID.randomUUID();
        Vendas vendaMock = new Vendas();
        vendaMock.setId(vendaId.toString());
        vendaMock.setClienteId(UUID.randomUUID());
        vendaMock.setVeiculoId(UUID.randomUUID());
        vendaMock.setStatus(Status.CONCLUIDA);

        when(obterVendasPorIdUseCase.execute(vendaId)).thenReturn(Optional.of(vendaMock));

        given()
                .when()
                .get("/vendas/{id}", vendaId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("status", equalTo(Status.CONCLUIDA.name()));

        verify(obterVendasPorIdUseCase, times(1)).execute(vendaId);
    }

    @Test
    void deveRetornarNotFoundQuandoVendaNaoExiste() {
        UUID vendaId = UUID.randomUUID();
        when(obterVendasPorIdUseCase.execute(vendaId)).thenReturn(Optional.empty());

        given()
                .when()
                .get("/vendas/{id}", vendaId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deveListarVendasVendidas() {
        Vendas venda1 = new Vendas();
        venda1.setId(UUID.randomUUID().toString());
        venda1.setStatus(Status.CONCLUIDA);

        Vendas venda2 = new Vendas();
        venda2.setId(UUID.randomUUID().toString());
        venda2.setStatus(Status.CONCLUIDA);

        when(listarVendasVendidasUseCase.buscarVendidosOrdenadosPorPreco(Status.CONCLUIDA)).thenReturn(List.of(venda1, venda2));

        given()
                .when()
                .get("/vendas/status/CONCLUIDA")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2))
                .body("[0].status", equalTo(Status.CONCLUIDA.name()));
    }

    @Test
    void deveAlterarStatusVenda() {
        UUID vendaId = UUID.randomUUID();
        Vendas vendaMock = new Vendas();
        vendaMock.setId(vendaId.toString());
        vendaMock.setStatus(Status.CONCLUIDA);

        when(alterarStatusVendasUseCase.execute(vendaId, "CONCLUIDA")).thenReturn(vendaMock);

        given()
                .param("status", "CONCLUIDA")
                .when()
                .patch("/vendas/{id}/status", vendaId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("CONCLUIDA"));
    }
}
