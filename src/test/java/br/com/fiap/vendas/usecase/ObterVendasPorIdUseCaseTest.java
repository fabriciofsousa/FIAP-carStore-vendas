package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.gateway.VendasGateway;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObterVendasPorIdUseCaseImplTest {

    @Mock
    private VendasGateway vendasGateway;

    private ObterVendasPorIdUseCaseImpl useCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useCase = new ObterVendasPorIdUseCaseImpl(vendasGateway);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void deveRetornarVendaQuandoExistir() {
        UUID id = UUID.randomUUID();
        Vendas venda = new Vendas();
        venda.setVeiculoId(id);

        when(vendasGateway.buscarPorId(id)).thenReturn(Optional.of(venda));

        Optional<Vendas> resultado = useCase.execute(id);

        assertThat(resultado).isPresent().contains(venda);
        verify(vendasGateway).buscarPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoVendaNaoEncontrada() {
        UUID id = UUID.randomUUID();

        when(vendasGateway.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(VendasNaoEncontradoException.class)
                .hasMessageContaining("Veículo não encontrado para o ID");
    }

    @Test
    void deveLancarExcecaoQuandoIdNulo() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID do veículo não pode ser nulo");
    }
}
