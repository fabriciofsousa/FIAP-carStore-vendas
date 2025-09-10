package br.com.fiap.vendas.usecase;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.StatusVeiculo;
import br.com.fiap.vendas.controller.vendas.dto.veiculo.VeiculoDTO;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.domain.Vendas.Pagamento;
import br.com.fiap.vendas.exception.VeiculoException;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.usecase.vendas.ObterAtualizarVeiculoUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterClienteUseCase;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriarVendasUseCaseImplTest {

    @Mock
    private VendasGateway vendasGateway;

    @Mock
    private ObterAtualizarVeiculoUseCase obterAtualizarVeiculoUseCase;

    @Mock
    private ObterClienteUseCase obterClienteUseCase;

    private CriarVendasUseCaseImpl useCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        Vendas vendaMock = mock(Vendas.class);
        when(vendaMock.getTotalPago()).thenReturn(BigDecimal.ZERO);
        useCase = new CriarVendasUseCaseImpl(vendasGateway, obterAtualizarVeiculoUseCase, obterClienteUseCase);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private VeiculoDTO criarVeiculo(BigDecimal preco, StatusVeiculo status) {
        VeiculoDTO veiculo = new VeiculoDTO();
        veiculo.setId(UUID.randomUUID());
        veiculo.setPreco(preco);
        veiculo.setStatus(status);
        return veiculo;
    }

    private Vendas criarVenda(UUID veiculoId, BigDecimal totalPago) {
        Vendas venda = new Vendas();
        venda.setVeiculoId(veiculoId);
        venda.setStatus(Status.INICIADA);
        venda.setPagamentos(new ArrayList<>());
        if (totalPago.compareTo(BigDecimal.ZERO) > 0) {
            // Adiciona pagamento para que getTotalPago() some corretamente
            venda.getPagamentos().add(new Pagamento(totalPago, FormaPagamento.CARTAO, LocalDateTime.now()));
        }
        return venda;
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoJaVendido() {
        Vendas venda = criarVenda(UUID.randomUUID(), BigDecimal.ZERO);
        Pagamento pagamento = new Pagamento(BigDecimal.TEN, FormaPagamento.CARTAO, null);

        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.VENDIDO);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(venda.getVeiculoId())).thenReturn(veiculo);

        assertThatThrownBy(() -> useCase.execute(venda, pagamento))
                .isInstanceOf(VeiculoException.class)
                .hasMessage("Veículo já vendido.");
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoJaReservado() {
        UUID veiculoId = UUID.randomUUID();

        Vendas venda = criarVenda(veiculoId, BigDecimal.ZERO);

        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(20), FormaPagamento.CARTAO, null);

        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.RESERVADO);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(veiculoId)).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(veiculoId)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(venda, pagamento))
                .isInstanceOf(VeiculoException.class)
                .hasMessage("Veículo já reservado.");
    }


    @Test
    void deveLancarExcecaoQuandoPagamentoMaiorQueValorRestante() {
        Vendas vendaExistente = criarVenda(UUID.randomUUID(), BigDecimal.valueOf(90)); // totalPago = 90
        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(20), FormaPagamento.CARTAO, null); // excede restante = 10

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(vendaExistente.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(vendaExistente.getVeiculoId())).thenReturn(vendaExistente);

        assertThatThrownBy(() -> useCase.execute(vendaExistente, pagamento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O valor do pagamento não pode ser maior");
    }


    @Test
    void deveAdicionarPagamentoQuandoVendaExistenteParcial() {
        Vendas vendaExistente = criarVenda(UUID.randomUUID(), BigDecimal.valueOf(50));
        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(30), FormaPagamento.CARTAO, null);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(vendaExistente.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(vendaExistente.getVeiculoId())).thenReturn(vendaExistente);
        when(vendasGateway.salvar(any(Vendas.class))).thenAnswer(inv -> inv.getArgument(0));

        Vendas result = useCase.execute(vendaExistente, pagamento);

        assertThat(result.getStatus()).isEqualTo(Status.INICIADA);
        assertThat(result.getPagamentos()).hasSize(2);
        verify(vendasGateway).salvar(vendaExistente);
    }

    // 3c. Venda existente - pagamento total
    @Test
    void deveConcluirVendaQuandoPagamentoCompletaValorTotal() {
        Vendas vendaExistente = criarVenda(UUID.randomUUID(), BigDecimal.valueOf(80));
        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(20), FormaPagamento.CARTAO, null);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(vendaExistente.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(vendaExistente.getVeiculoId())).thenReturn(vendaExistente);
        when(vendasGateway.salvar(any(Vendas.class))).thenAnswer(inv -> inv.getArgument(0));

        Vendas result = useCase.execute(vendaExistente, pagamento);

        assertThat(result.getStatus()).isEqualTo(Status.CONCLUIDA);
        verify(obterAtualizarVeiculoUseCase).atualizarVeiculo(veiculo.getId(), veiculo);
    }

    // 4a. Nova venda - entrada menor que 10%
    @Test
    void deveLancarExcecaoQuandoEntradaMenorQue10PorCento() {
        Vendas venda = criarVenda(UUID.randomUUID(), BigDecimal.ZERO);
        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(5), FormaPagamento.CARTAO, null);

        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(venda.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(venda.getVeiculoId())).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(venda, pagamento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos 10%");
    }

    @Test
    void deveCriarVendaQuandoEntradaValida() {
        Vendas novaVenda = criarVenda(UUID.randomUUID(), BigDecimal.ZERO);
        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(20), FormaPagamento.CARTAO, null);

        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(novaVenda.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(novaVenda.getVeiculoId())).thenReturn(null);
        when(vendasGateway.salvar(any(Vendas.class))).thenAnswer(inv -> inv.getArgument(0));

        Vendas result = useCase.execute(novaVenda, pagamento);

        assertThat(result.getStatus()).isEqualTo(Status.INICIADA);
        assertThat(veiculo.getStatus()).isEqualTo(StatusVeiculo.RESERVADO);
        verify(vendasGateway).salvar(novaVenda);
        verify(obterAtualizarVeiculoUseCase, times(2)).atualizarVeiculo(veiculo.getId(), veiculo);
    }

    @Test
    void deveLancarExcecaoQuandoValorPagoMaiorQueVeiculo() {
        Vendas vendaExistente = criarVenda(UUID.randomUUID(), BigDecimal.valueOf(120));
        VeiculoDTO veiculo = criarVeiculo(BigDecimal.valueOf(100), StatusVeiculo.DISPONIVEL);

        Pagamento pagamento = new Pagamento(BigDecimal.valueOf(10), FormaPagamento.CARTAO, null);

        when(obterAtualizarVeiculoUseCase.obterVeiculoPorId(vendaExistente.getVeiculoId())).thenReturn(veiculo);
        when(vendasGateway.buscarVendaIniciadaPorVeiculo(vendaExistente.getVeiculoId())).thenReturn(vendaExistente);

        assertThatThrownBy(() -> useCase.execute(vendaExistente, pagamento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ser maior que o valor restante do veículo");
    }
}
