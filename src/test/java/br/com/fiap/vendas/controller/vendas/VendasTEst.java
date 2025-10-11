package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendasTest {

    @Test
    void deveCalcularTotalPagoCorretamente() {
        Vendas.Pagamento pagamento1 = Vendas.Pagamento.builder()
                .valor(BigDecimal.valueOf(100))
                .formaPagamento(FormaPagamento.PIX)
                .dataPagamento(LocalDateTime.now())
                .build();

        Vendas.Pagamento pagamento2 = Vendas.Pagamento.builder()
                .valor(BigDecimal.valueOf(50))
                .formaPagamento(FormaPagamento.CARTAO)
                .dataPagamento(LocalDateTime.now())
                .build();

        Vendas venda = Vendas.builder()
                .id("venda123")
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .valorTotal(BigDecimal.valueOf(150))
                .pagamentos(List.of(pagamento1, pagamento2))
                .status(Status.CONCLUIDA)
                .dataVenda(LocalDateTime.now())
                .build();

        BigDecimal totalPago = venda.getTotalPago();

        assertEquals(BigDecimal.valueOf(150), totalPago);
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverPagamentos() {
        Vendas venda = Vendas.builder()
                .id("vendaSemPagamentos")
                .valorTotal(BigDecimal.valueOf(0))
                .pagamentos(List.of())
                .build();

        assertEquals(BigDecimal.ZERO, venda.getTotalPago());
    }

    @Test
    void deveConstruirPagamentoComGettersESetters() {
        Vendas.Pagamento pagamento = new Vendas.Pagamento();
        BigDecimal valor = BigDecimal.valueOf(200);
        LocalDateTime data = LocalDateTime.now();

        pagamento.setValor(valor);
        pagamento.setFormaPagamento(FormaPagamento.CARTAO);
        pagamento.setDataPagamento(data);

        assertEquals(valor, pagamento.getValor());
        assertEquals(FormaPagamento.CARTAO, pagamento.getFormaPagamento());
        assertEquals(data, pagamento.getDataPagamento());
    }

    @Test
    void deveUsarBuilderCorretamente() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataVenda = LocalDateTime.now();

        Vendas venda = Vendas.builder()
                .id("v1")
                .clienteId(clienteId)
                .veiculoId(veiculoId)
                .valorTotal(BigDecimal.TEN)
                .status(Status.INICIADA)
                .dataVenda(dataVenda)
                .build();

        assertEquals("v1", venda.getId());
        assertEquals(clienteId, venda.getClienteId());
        assertEquals(veiculoId, venda.getVeiculoId());
        assertEquals(BigDecimal.TEN, venda.getValorTotal());
        assertEquals(Status.INICIADA, venda.getStatus());
        assertEquals(dataVenda, venda.getDataVenda());
    }

    @Test
    void deveGerarEqualsHashCodeToStringCorretamente() {
        Vendas venda1 = Vendas.builder()
                .id("abc")
                .valorTotal(BigDecimal.TEN)
                .build();

        Vendas venda2 = Vendas.builder()
                .id("abc")
                .valorTotal(BigDecimal.TEN)
                .build();

        assertEquals(venda1, venda2);
        assertEquals(venda1.hashCode(), venda2.hashCode());
        assertTrue(venda1.toString().contains("Vendas"));
    }

    @Test
    void deveGerarEqualsHashCodeToStringParaPagamento() {
        Vendas.Pagamento p1 = Vendas.Pagamento.builder()
                .valor(BigDecimal.TEN)
                .formaPagamento(FormaPagamento.PIX)
                .dataPagamento(LocalDateTime.now())
                .build();

        Vendas.Pagamento p2 = Vendas.Pagamento.builder()
                .valor(BigDecimal.TEN)
                .formaPagamento(FormaPagamento.PIX)
                .dataPagamento(p1.getDataPagamento())
                .build();

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertTrue(p1.toString().contains("Pagamento"));
    }
}
