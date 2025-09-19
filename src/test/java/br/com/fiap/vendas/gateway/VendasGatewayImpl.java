package br.com.fiap.vendas.gateway;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.adapter.VendasGatewayImpl;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import br.com.fiap.vendas.infra.database.repository.VendasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendasGatewayImplTest {

    private VendasRepository vendasRepository;
    private VendasGatewayImpl vendasGateway;

    @BeforeEach
    void setUp() {
        vendasRepository = mock(VendasRepository.class);
        vendasGateway = new VendasGatewayImpl(vendasRepository);
    }

    @Test
    void salvar_deveChamarRepositoryESalvar() {
        Vendas venda = Vendas.builder()
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .status(Status.INICIADA)
                .dataVenda(LocalDateTime.now())
                .build();

        VendasEntity savedEntity = new VendasEntity();
        savedEntity.setId(venda.getId() != null ? venda.getId() : UUID.randomUUID());
        savedEntity.setClienteId(venda.getClienteId());
        savedEntity.setVeiculoId(venda.getVeiculoId());
        savedEntity.setStatus(Status.INICIADA);
        savedEntity.setDataVenda(venda.getDataVenda());

        when(vendasRepository.save(any(VendasEntity.class))).thenReturn(savedEntity);

        Vendas result = vendasGateway.salvar(venda);

        assertNotNull(result.getId());
        assertEquals(venda.getClienteId(), result.getClienteId());
        verify(vendasRepository, times(1)).save(any(VendasEntity.class));
    }

    @Test
    void buscarPorId_deveRetornarVendaQuandoEncontrada() {
        UUID id = UUID.randomUUID();
        VendasEntity entity = new VendasEntity();
        entity.setId(id);
        entity.setClienteId(UUID.randomUUID());
        entity.setVeiculoId(UUID.randomUUID());
        entity.setStatus(Status.CONCLUIDA);
        entity.setDataVenda(LocalDateTime.now());

        when(vendasRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Vendas> result = vendasGateway.buscarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        verify(vendasRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(vendasRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Vendas> result = vendasGateway.buscarPorId(id);

        assertTrue(result.isEmpty());
        verify(vendasRepository, times(1)).findById(id);
    }

    @Test
    void buscarVendidosOrdenadosPorPreco_deveRetornarListaDeVendas() {
        VendasEntity entity1 = new VendasEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setStatus(Status.CONCLUIDA);

        VendasEntity entity2 = new VendasEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setStatus(Status.CONCLUIDA);

        when(vendasRepository.findByStatusOrderByDataVendaAsc(Status.CONCLUIDA))
                .thenReturn(List.of(entity1, entity2));

        List<Vendas> result = vendasGateway.buscarVendidosOrdenadosPorPreco(Status.CONCLUIDA);

        assertEquals(2, result.size());
        verify(vendasRepository, times(1))
                .findByStatusOrderByDataVendaAsc(Status.CONCLUIDA);
    }

    @Test
    void buscarVendaIniciadaPorVeiculo_deveRetornarVendaQuandoExistir() {
        UUID veiculoId = UUID.randomUUID();
        Vendas entity = new Vendas();
        entity.setVeiculoId(veiculoId);
        entity.setStatus(Status.INICIADA);

        when(vendasRepository.findByVeiculoIdAndStatus(veiculoId, Status.INICIADA))
                .thenReturn(Optional.of(entity));

        Vendas result = vendasGateway.buscarVendaIniciadaPorVeiculo(veiculoId);

        assertNotNull(result);
        assertEquals(veiculoId, result.getVeiculoId());
        verify(vendasRepository, times(1))
                .findByVeiculoIdAndStatus(veiculoId, Status.INICIADA);
    }

    @Test
    void buscarVendaIniciadaPorVeiculo_deveRetornarNullQuandoNaoExistir() {
        UUID veiculoId = UUID.randomUUID();
        when(vendasRepository.findByVeiculoIdAndStatus(veiculoId, Status.INICIADA))
                .thenReturn(Optional.empty());

        Vendas result = vendasGateway.buscarVendaIniciadaPorVeiculo(veiculoId);

        assertNull(result);
        verify(vendasRepository, times(1))
                .findByVeiculoIdAndStatus(veiculoId, Status.INICIADA);
    }
}
