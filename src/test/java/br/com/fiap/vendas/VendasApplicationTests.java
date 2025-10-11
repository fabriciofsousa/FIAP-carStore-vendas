package br.com.fiap.vendas;

import br.com.fiap.vendas.gateway.VeiculoGateway;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
		properties = {
				"spring.main.lazy-initialization=true",
				"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
						"org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
				"CLIENTE_API_URL=localhost:8082",
				"veiculo.api.url=http://localhost:8081"
		}
)
@EntityScan(basePackages = "br.com.fiap.vendas.infra.database.entity")
@TestPropertySource(properties = "veiculo.api.url=http://localhost:8081")
@ActiveProfiles("test")
class VendasApplicationTests {

	@MockBean
	private CriarVendasUseCase criarVendasUseCase;

	@MockBean
	private VeiculoGateway veiculoGateway;

	@MockBean
	private ObterVendasPorIdUseCase obterVendasPorIdUseCase;

	@MockBean
	private ListarVendasUseCase listarVendasVendidasUseCase;

	@MockBean
	private AlterarStatusVendasUseCase alterarStatusVendasUseCase;


	@Test
	void contextLoads() {
	}

}
