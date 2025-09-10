package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.exception.VendasNaoEncontradoException;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.Status;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlterarStatusVendasUseCaseImplTest {

    @Mock
    private VendasGateway vendasGateway;

    private AlterarStatusVendasUseCaseImpl useCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        useCase = new AlterarStatusVendasUseCaseImpl(vendasGateway);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void deveAlterarStatusQuandoVendaExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        Vendas venda = new Vendas();
        venda.setId(id);
        venda.setStatus(Status.INICIADA);

        when(vendasGateway.buscarPorId(id)).thenReturn(Optional.of(venda));
        when(vendasGateway.salvar(any(Vendas.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Vendas result = useCase.execute(id, "CONCLUIDA");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.CONCLUIDA);
        verify(vendasGateway).buscarPorId(id);
        verify(vendasGateway).salvar(venda);
    }

    @Test
    void deveLancarExcecaoQuandoVendaNaoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(vendasGateway.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, "CANCELADA"))
                .isInstanceOf(VendasNaoEncontradoException.class)
                .hasMessageContaining("Venda não encontrada: " + id);

        verify(vendasGateway).buscarPorId(id);
        verify(vendasGateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        // Arrange
        UUID id = UUID.randomUUID();
        Vendas venda = new Vendas();
        venda.setId(id);
        venda.setStatus(Status.INICIADA);

        when(vendasGateway.buscarPorId(id)).thenReturn(Optional.of(venda));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, "FINALIZADA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status inválido: 'FINALIZADA'");

        verify(vendasGateway).buscarPorId(id);
        verify(vendasGateway, never()).salvar(any());
    }
}
