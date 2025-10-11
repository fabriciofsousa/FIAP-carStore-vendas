package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasResponseDTO;
import br.com.fiap.vendas.controller.vendas.mapper.VendasMapper;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendasMapperTest {

    @Test
    void deveConverterRequestDtoParaDomainComPagamento() {
        // Arrange
        VendasRequestDTO dto = new VendasRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(1000),
                FormaPagamento.PIX
        );

        // Act
        Vendas domain = VendasMapper.toDomain(dto);

        // Assert
        assertNotNull(domain);
        assertEquals(dto.clienteId(), domain.getClienteId());
        assertEquals(dto.veiculoId(), domain.getVeiculoId());
        assertEquals(BigDecimal.valueOf(1000), domain.getValorTotal());
        assertEquals(Status.INICIADA, domain.getStatus());
        assertNotNull(domain.getDataVenda());
        assertEquals(1, domain.getPagamentos().size());
        assertEquals(FormaPagamento.PIX, domain.getPagamentos().get(0).getFormaPagamento());
    }

    @Test
    void deveConverterDomainParaResponseIncluindoPagamentos() {
        Vendas.Pagamento pagamento = Vendas.Pagamento.builder()
                .valor(BigDecimal.valueOf(500))
                .formaPagamento(FormaPagamento.CARTAO)
                .dataPagamento(LocalDateTime.now())
                .build();

        Vendas domain = Vendas.builder()
                .id("abc123")
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .valorTotal(BigDecimal.valueOf(500))
                .status(Status.INICIADA)
                .pagamentos(List.of(pagamento))
                .dataVenda(LocalDateTime.now())
                .build();

        VendasResponseDTO response = VendasMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getId(), response.id());
        assertEquals(domain.getClienteId(), response.clienteId());
        assertEquals(domain.getVeiculoId(), response.veiculoId());
        assertEquals(domain.getStatus().name(), response.status());
    }

    @Test
    void deveConverterDomainParaEntity() {
        Vendas.Pagamento pagamento = Vendas.Pagamento.builder()
                .valor(BigDecimal.valueOf(250))
                .formaPagamento(FormaPagamento.CARTAO)
                .dataPagamento(LocalDateTime.now())
                .build();

        Vendas domain = Vendas.builder()
                .id("venda-001")
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .valorTotal(BigDecimal.valueOf(250))
                .status(Status.INICIADA)
                .pagamentos(List.of(pagamento))
                .dataVenda(LocalDateTime.now())
                .build();

        VendasEntity entity = VendasMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getClienteId(), entity.getClienteId());
        assertEquals(domain.getVeiculoId(), entity.getVeiculoId());
        assertEquals(domain.getValorTotal(), entity.getValorTotal());
        assertEquals(domain.getPagamentos().size(), entity.getPagamentos().size());
    }

    @Test
    void deveConverterEntityParaDomain() {
        Vendas.Pagamento pagamento = Vendas.Pagamento.builder()
                .valor(BigDecimal.valueOf(700))
                .formaPagamento(FormaPagamento.PIX)
                .dataPagamento(LocalDateTime.now())
                .build();

        VendasEntity entity = VendasEntity.builder()
                .id("venda-xyz")
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .valorTotal(BigDecimal.valueOf(700))
                .status(Status.INICIADA)
                .pagamentos(List.of(pagamento))
                .dataVenda(LocalDateTime.now())
                .build();

        Vendas domain = VendasMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getClienteId(), domain.getClienteId());
        assertEquals(entity.getVeiculoId(), domain.getVeiculoId());
        assertEquals(entity.getPagamentos().size(), domain.getPagamentos().size());
    }

    @Test
    void deveLancarExcecaoQuandoCamposObrigatoriosForemInvalidos() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> VendasMapper.toDomain(new VendasRequestDTO(null, null, null, null)));

        VendasRequestDTO dtoSemCliente = new VendasRequestDTO(null, veiculoId, BigDecimal.TEN, FormaPagamento.PIX);
        assertThrows(IllegalArgumentException.class,
                () -> VendasMapper.toDomain(dtoSemCliente));

        VendasRequestDTO dtoSemVeiculo = new VendasRequestDTO(clienteId, null, BigDecimal.TEN, FormaPagamento.PIX);
        assertThrows(IllegalArgumentException.class,
                () -> VendasMapper.toDomain(dtoSemVeiculo));

        VendasRequestDTO dtoValorZero = new VendasRequestDTO(clienteId, veiculoId, BigDecimal.ZERO, FormaPagamento.PIX);
        assertThrows(IllegalArgumentException.class,
                () -> VendasMapper.toDomain(dtoValorZero));

        VendasRequestDTO dtoSemForma = new VendasRequestDTO(clienteId, veiculoId, BigDecimal.TEN, null);
        assertThrows(IllegalArgumentException.class,
                () -> VendasMapper.toDomain(dtoSemForma));
    }
}
