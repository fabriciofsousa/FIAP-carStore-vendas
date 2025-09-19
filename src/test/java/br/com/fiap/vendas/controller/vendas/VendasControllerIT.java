package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.domain.Vendas;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class VendasControllerIT extends BaseIntegrationTest {

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
        vendaMock.setId(UUID.randomUUID());
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
        vendaMock.setId(vendaId);
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
        venda1.setId(UUID.randomUUID());
        venda1.setStatus(Status.CONCLUIDA);

        Vendas venda2 = new Vendas();
        venda2.setId(UUID.randomUUID());
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
        vendaMock.setId(vendaId);
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
