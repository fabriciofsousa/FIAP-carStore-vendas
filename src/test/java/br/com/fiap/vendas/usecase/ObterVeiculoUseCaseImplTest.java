package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.VeiculoGateway;
import feign.FeignException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObterVeiculoUseCaseImplTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    private ObterVeiculoUseCaseImpl useCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useCase = new ObterVeiculoUseCaseImpl(veiculoGateway);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private VeiculoDTO criarVeiculoDTO() {
        VeiculoDTO veiculo = new VeiculoDTO();
        veiculo.setId(UUID.randomUUID());
        veiculo.setMarca("Fiat");
        veiculo.setModelo("Uno");
        veiculo.setAno(2020);
        return veiculo;
    }

    // ====== obterVeiculoPorId ======
    @Test
    void deveRetornarVeiculoQuandoExistir() {
        UUID id = UUID.randomUUID();
        VeiculoDTO veiculo = criarVeiculoDTO();

        when(veiculoGateway.obterVeiculoPorId(id)).thenReturn(veiculo);

        VeiculoDTO resultado = useCase.obterVeiculoPorId(id);

        assertThat(resultado).isNotNull().isEqualTo(veiculo);
        verify(veiculoGateway).obterVeiculoPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoIdNulo() {
        assertThatThrownBy(() -> useCase.obterVeiculoPorId(null))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("ID do veículo não pode ser nulo");
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(veiculoGateway.obterVeiculoPorId(id)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> useCase.obterVeiculoPorId(id))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("Veículo não encontrado");
    }

    @Test
    void deveLancarExcecaoQuandoErroNoGateway() {
        UUID id = UUID.randomUUID();
        when(veiculoGateway.obterVeiculoPorId(id)).thenThrow(FeignException.class);

        assertThatThrownBy(() -> useCase.obterVeiculoPorId(id))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("Erro ao acessar o serviço de veículo");
    }

    // ====== atualizarVeiculo ======
    @Test
    void deveAtualizarVeiculoComSucesso() {
        UUID id = UUID.randomUUID();
        VeiculoDTO veiculo = criarVeiculoDTO();

        when(veiculoGateway.atualizarVeiculo(id, veiculo)).thenReturn(veiculo);

        VeiculoDTO resultado = useCase.atualizarVeiculo(id, veiculo);

        assertThat(resultado).isEqualTo(veiculo);
        verify(veiculoGateway).atualizarVeiculo(id, veiculo);
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarVeiculoComIdNulo() {
        VeiculoDTO veiculo = criarVeiculoDTO();

        assertThatThrownBy(() -> useCase.atualizarVeiculo(null, veiculo))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("ID do veículo não pode ser nulo");
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarVeiculoComDTOInvalido() {
        UUID id = UUID.randomUUID();
        VeiculoDTO veiculo = new VeiculoDTO(); // vazio

        assertThatThrownBy(() -> useCase.atualizarVeiculo(id, veiculo))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("modelo do veículo é obrigatório");
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarVeiculoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        VeiculoDTO veiculo = criarVeiculoDTO();

        when(veiculoGateway.atualizarVeiculo(id, veiculo)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> useCase.atualizarVeiculo(id, veiculo))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("Veículo não encontrado");
    }

    @Test
    void deveLancarExcecaoQuandoErroNoGatewayAoAtualizar() {
        UUID id = UUID.randomUUID();
        VeiculoDTO veiculo = criarVeiculoDTO();

        when(veiculoGateway.atualizarVeiculo(id, veiculo)).thenThrow(FeignException.class);

        assertThatThrownBy(() -> useCase.atualizarVeiculo(id, veiculo))
                .isInstanceOf(VeiculoException.class)
                .hasMessageContaining("Erro ao acessar o serviço de veículo");
    }
}
