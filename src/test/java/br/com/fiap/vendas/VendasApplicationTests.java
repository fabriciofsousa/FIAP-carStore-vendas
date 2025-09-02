package br.com.fiap.vendas;

import br.com.fiap.vendas.gateway.VeiculoGateway;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasVendidasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@EntityScan(basePackages = "br.com.fiap.vendas.infra.database.entity")
@TestPropertySource(properties = "veiculo.api.url=http://localhost:8081")
class VendasApplicationTests {

	@MockBean
	private CriarVendasUseCase criarVendasUseCase;

	@MockBean
	private VeiculoGateway veiculoGateway;

	@MockBean
	private ObterVendasPorIdUseCase obterVendasPorIdUseCase;

	@MockBean
	private ListarVendasVendidasUseCase listarVendasVendidasUseCase;

	@MockBean
	private AlterarStatusVendasUseCase alterarStatusVendasUseCase;


	@Test
	void contextLoads() {
	}

}
