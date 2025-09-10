package br.com.fiap.vendas.performance;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.UUID;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class PerformanceSimulation extends Simulation {

    // ===== Configuração HTTP =====
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .header("Content-Type", "application/json");

    // ===== Requisições =====
    private final ActionBuilder criarVendaRequest = http("Criar Venda")
            .post("/vendas")
            .body(StringBody("{ \"clienteId\": \"" + UUID.randomUUID() + "\", \"veiculoId\": \"" + UUID.randomUUID() + "\" }"))
            .check(status().is(200));

    private final ActionBuilder obterVendaRequest = http("Obter Venda por ID")
            .get(session -> "/vendas/" + UUID.randomUUID()) // pode ser ajustado para IDs reais
            .check(status().in(200, 404));

    private final ActionBuilder listarVendasRequest = http("Listar Vendas")
            .get("/vendas")
            .check(status().is(200));

    private final ActionBuilder alterarStatusRequest = http("Alterar Status da Venda")
            .put(session -> "/vendas/" + UUID.randomUUID() + "/status")
            .body(StringBody("{ \"status\": \"PAGO\" }"))
            .check(status().in(200, 400, 404));

    // ===== Cenários =====
    private final ScenarioBuilder cenarioCriarVenda = scenario("Cenário Criar Venda")
            .exec(criarVendaRequest);

    private final ScenarioBuilder cenarioObterVenda = scenario("Cenário Obter Venda")
            .exec(obterVendaRequest);

    private final ScenarioBuilder cenarioListarVendas = scenario("Cenário Listar Vendas")
            .exec(listarVendasRequest);

    private final ScenarioBuilder cenarioAlterarStatus = scenario("Cenário Alterar Status")
            .exec(alterarStatusRequest);

    // ===== Setup =====
    {
        setUp(
                cenarioCriarVenda.injectOpen(
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(20))
                ),
                cenarioObterVenda.injectOpen(
                        rampUsersPerSec(1).to(15).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(15).during(Duration.ofSeconds(20))
                ),
                cenarioListarVendas.injectOpen(
                        rampUsersPerSec(1).to(5).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(20))
                ),
                cenarioAlterarStatus.injectOpen(
                        rampUsersPerSec(1).to(8).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(8).during(Duration.ofSeconds(20))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        // Nenhum request deve falhar
                        global().failedRequests().count().is(0L),

                        // 95% das respostas abaixo de 2 segundos
                        global().responseTime().percentile(95).lt(2000),

                        // Tempo máximo aceitável de resposta
                        global().responseTime().max().lt(5000)
                );
    }
}
