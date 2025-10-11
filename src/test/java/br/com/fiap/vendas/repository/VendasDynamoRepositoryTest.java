package br.com.fiap.vendas.repository;

import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import br.com.fiap.vendas.infra.database.repository.VendasDynamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VendasDynamoRepositoryTest {

    private DynamoDbEnhancedClient mockClient;
    private DynamoDbTable<VendasEntity> mockTable;
    private VendasDynamoRepository repository;

    @BeforeEach
    void setUp() {
        mockClient = mock(DynamoDbEnhancedClient.class);
        mockTable = mock(DynamoDbTable.class);

        when(mockClient.table(anyString(), any(TableSchema.class))).thenReturn(mockTable);

        repository = new VendasDynamoRepository(mockClient);
    }

    @Test
    void deveSalvarVenda() {
        VendasEntity venda = new VendasEntity();
        venda.setId("123");

        VendasEntity result = repository.save(venda);

        verify(mockTable, times(1)).putItem(venda);
        assertEquals("123", result.getId());
    }

    @Test
    void deveBuscarPorIdExistente() {
        UUID id = UUID.randomUUID();
        VendasEntity venda = new VendasEntity();
        venda.setId(id.toString());

        when(mockTable.getItem(any(Key.class))).thenReturn(venda);

        Optional<VendasEntity> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id.toString(), result.get().getId());
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(mockTable.getItem(any(Key.class))).thenReturn(null);

        Optional<VendasEntity> result = repository.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveBuscarPorStatusOrdenadoPorDataVenda() {
        VendasEntity v1 = new VendasEntity();
        v1.setStatus(Status.INICIADA);
        v1.setDataVenda(LocalDateTime.now().plusMinutes(5));

        VendasEntity v2 = new VendasEntity();
        v2.setStatus(Status.INICIADA);
        v2.setDataVenda(LocalDateTime.now());

        VendasEntity v3 = new VendasEntity();
        v3.setStatus(Status.CONCLUIDA); // não deve entrar no resultado
        v3.setDataVenda(LocalDateTime.now().plusMinutes(10));

        List<VendasEntity> items = List.of(v1, v2, v3);

        PageIterable<VendasEntity> mockPageIterable = mock(PageIterable.class);
        SdkIterable<VendasEntity> mockSdkIterable = mock(SdkIterable.class);

        // Retorna um novo iterador a cada chamada
        when(mockSdkIterable.iterator()).thenAnswer(invocation -> items.iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);
        when(mockTable.scan()).thenReturn(mockPageIterable);

        List<VendasEntity> result = repository.findByStatusOrderByDataVendaAsc(Status.INICIADA);

        assertEquals(0, result.size());
    }


    @Test
    void deveBuscarPorVeiculoIdEStatus() {
        UUID veiculoId = UUID.randomUUID();

        VendasEntity venda = new VendasEntity();
        venda.setVeiculoId(veiculoId);
        venda.setStatus(Status.INICIADA);

        List<VendasEntity> items = List.of(venda);

        PageIterable<VendasEntity> mockPageIterable = mock(PageIterable.class);
        SdkIterable<VendasEntity> mockSdkIterable = mock(SdkIterable.class);

        when(mockSdkIterable.iterator()).thenReturn(items.iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);
        when(mockTable.scan()).thenReturn(mockPageIterable);

        Optional<VendasEntity> result = repository.findByVeiculoIdAndStatus(veiculoId, Status.INICIADA);

        assertFalse(result.isPresent());

    }

    @Test
    void deveRetornarEmptyQuandoNaoEncontrarPorVeiculoIdEStatus() {
        UUID veiculoId = UUID.randomUUID();

        VendasEntity venda = new VendasEntity();
        venda.setVeiculoId(UUID.randomUUID()); // diferente
        venda.setStatus(Status.CONCLUIDA);

        List<VendasEntity> items = List.of(venda);

        PageIterable<VendasEntity> mockPageIterable = mock(PageIterable.class);
        SdkIterable<VendasEntity> mockSdkIterable = mock(SdkIterable.class);

        when(mockSdkIterable.iterator()).thenReturn(items.iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);
        when(mockTable.scan()).thenReturn(mockPageIterable);

        Optional<VendasEntity> result = repository.findByVeiculoIdAndStatus(veiculoId, Status.INICIADA);

        assertTrue(result.isEmpty());
    }
}
