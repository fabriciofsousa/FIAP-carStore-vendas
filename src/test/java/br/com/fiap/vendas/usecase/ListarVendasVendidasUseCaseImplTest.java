package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.gateway.VendasGateway;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListarVendasVendidasUseCaseImplTest {

    @Mock
    private VendasGateway vendasGateway;

    private ListarVendasVendidasUseCaseImpl useCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useCase = new ListarVendasVendidasUseCaseImpl(vendasGateway);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void deveRetornarListaDeVendasVendidasOrdenadasPorPreco() {
        // Dados simulados
        Vendas venda1 = new Vendas();
        venda1.setVeiculoId(UUID.randomUUID());
        venda1.setValorTotal(BigDecimal.valueOf(5000));

        Vendas venda2 = new Vendas();
        venda2.setVeiculoId(UUID.randomUUID());
        venda2.setValorTotal(BigDecimal.valueOf(10000));

        List<Vendas> vendasMock = Arrays.asList(venda2, venda1); // já ordenadas por preço desc
        when(vendasGateway.buscarVendidosOrdenadosPorPreco()).thenReturn(vendasMock);

        // Execução
        List<Vendas> resultado = useCase.buscarVendidosOrdenadosPorPreco();

        // Verificações
        assertThat(resultado).isNotNull()
                .hasSize(2)
                .containsExactly(venda2, venda1);

        verify(vendasGateway, times(1)).buscarVendidosOrdenadosPorPreco();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremVendas() {
        when(vendasGateway.buscarVendidosOrdenadosPorPreco()).thenReturn(List.of());

        List<Vendas> resultado = useCase.buscarVendidosOrdenadosPorPreco();

        assertThat(resultado).isEmpty();
        verify(vendasGateway, times(1)).buscarVendidosOrdenadosPorPreco();
    }
}
