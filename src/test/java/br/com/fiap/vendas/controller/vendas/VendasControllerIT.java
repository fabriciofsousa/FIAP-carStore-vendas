package br.com.fiap.vendas.controller.vendas;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import br.com.fiap.vendas.controller.vendas.dto.VendasRequestDTO;
import br.com.fiap.vendas.domain.Vendas;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VendasControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        io.restassured.RestAssured.registerParser("text/plain", io.restassured.parsing.Parser.TEXT); // Register parser globally
    }

    @Nested
    class CadastroVendas {

        @Test
        void deveCriarVendaValida() {
            VendasRequestDTO venda = new VendasRequestDTO(UUID.randomUUID(), UUID.randomUUID());

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(venda)
                    .when()
                    .post("/vendas")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("clienteId", notNullValue())
                    .body("veiculoId", notNullValue())
                    .body("status", equalTo(Vendas.Status.INICIADA.name()));
        }

        @Test
        void naoDeveCriarVendaComClientIdInvalido() {
            VendasRequestDTO venda = new VendasRequestDTO(null, null);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(venda)
                    .when()
                    .post("/vendas")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(containsString("O campo 'clienteId' não pode ser nulo"));
        }

        @Test
        void naoDeveCriarVendaComVeiculoIdInvalido() {
            VendasRequestDTO venda = new VendasRequestDTO(UUID.randomUUID(), null);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(venda)
                    .when()
                    .post("/vendas")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(containsString("O campo 'veiculoId' não pode ser nulo"));
        }
    }

    @Nested
    class AtualizacaoStatus {

        @Test
        void deveAtualizarStatusParaVendido() {
            VendasRequestDTO venda = new VendasRequestDTO(UUID.randomUUID(), UUID.randomUUID());
            UUID id = criarVendaEObterId(venda);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("status", "CONCLUIDA")
                    .when()
                    .patch("/vendas/{id}/status", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("status", equalTo(Vendas.Status.CONCLUIDA.name()))
                    .body("dataVenda", notNullValue());
        }

        @Test
        void naoDeveAtualizarStatusInvalido() {
            VendasRequestDTO venda = new VendasRequestDTO(UUID.randomUUID(), UUID.randomUUID());
            UUID id = criarVendaEObterId(venda);

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("status", "INVALIDO")
                    .when()
                    .patch("/vendas/{id}/status", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(containsString("Status inválido"));
        }
    }

    @Nested
    class ObterVendas {

        @Test
        void deveObterVendaPorId() {
            VendasRequestDTO venda = new VendasRequestDTO(UUID.randomUUID(), UUID.randomUUID());
            UUID id = criarVendaEObterId(venda);

            given()
                    .when()
                    .get("/vendas/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("clienteId", notNullValue())
                    .body("veiculoId", notNullValue())
                    .body("status", equalTo(Vendas.Status.INICIADA.name()));
        }

        @Test
        void naoDeveObterVendaInexistente() {
            given()
                    .when()
                    .get("/vendas/{id}", UUID.randomUUID())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .body(containsString("Veículo não encontrado para o ID"));
        }
    }

    @Nested
    class ListagemVendasVendidas {

        @Test
        void deveListarTodasVendasVendidas() {
            given()
                    .when()
                    .get("/vendas/vendidas")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }
    }

    private UUID criarVendaEObterId(VendasRequestDTO venda) {
        String idAsString = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(venda)
                .when()
                .post("/vendas")
                .then()
                .extract()
                .path("id");
        return UUID.fromString(idAsString);
    }
}
