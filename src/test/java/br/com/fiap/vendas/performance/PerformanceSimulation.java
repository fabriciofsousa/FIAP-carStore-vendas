package br.com.fiap.vendas.performance;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

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
            .body(StringBody("{ \"clienteId\": \"" + java.util.UUID.randomUUID() + "\", \"veiculoId\": \"" + java.util.UUID.randomUUID() + "\" }"))
            .check(status().is(200));

    // ===== Cenários =====
    private final ScenarioBuilder cenarioCriarVenda = scenario("Cenário Criar Venda")
            .exec(criarVendaRequest);

    // ===== Setup =====
    {
        setUp(
                cenarioCriarVenda.injectOpen(
                        // Ramp-up inicial de 1 para 10 usuários por segundo em 8s
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(8)),

                        // Fluxo constante de 10 usuários por segundo durante 8s
                        constantUsersPerSec(10).during(Duration.ofSeconds(8)),

                        // Ramp-down de 10 para 1 usuário por segundo em 8s
                        rampUsersPerSec(10).to(1).during(Duration.ofSeconds(8))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        // Tempo máximo de resposta menor que 5000ms
                        global().responseTime().max().lt(5000)
                );
    }
}