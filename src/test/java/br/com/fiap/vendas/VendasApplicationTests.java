package br.com.fiap.vendas;

import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasVendidasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@EntityScan(basePackages = "br.com.fiap.vendas.infra.database.entity")
class VendasApplicationTests {

	@MockBean
	private CriarVendasUseCase criarVendasUseCase;

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
