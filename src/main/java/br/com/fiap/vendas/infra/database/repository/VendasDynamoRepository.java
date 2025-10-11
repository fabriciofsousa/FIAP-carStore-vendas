package br.com.fiap.vendas.infra.database.repository;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import org.springframework.stereotype.Repository;
import scala.None;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.*;

@Repository
public class VendasDynamoRepository {
    private final DynamoDbTable<VendasEntity> vendaTable;

    public VendasDynamoRepository(DynamoDbEnhancedClient client) {
        this.vendaTable = client.table(VendasEntity.TABLE_NAME, TableSchema.fromBean(VendasEntity.class));
    }

    public VendasEntity save(VendasEntity venda) {
        vendaTable.putItem(venda);
        return venda;
    }

    public Optional<VendasEntity> findById(UUID id) {
        Key key = Key.builder().partitionValue(id.toString()).build();
        VendasEntity result = vendaTable.getItem(key);
        return Optional.ofNullable(result);
    }

    public List<VendasEntity> findByStatusOrderByDataVendaAsc(Status status) {
        List<VendasEntity> list = new ArrayList<>();
        vendaTable.scan().items().forEach(item -> {
            if (item.getStatus() == status) {
                list.add(item);
            }
        });
        list.sort(Comparator.comparing(VendasEntity::getDataVenda));
        return list;
    }

    public Optional<VendasEntity> findByVeiculoIdAndStatus(UUID veiculoId, Status status) {
        return vendaTable.scan().items()
                .stream()
                .filter(v -> v.getVeiculoId().equals(veiculoId) && v.getStatus() == status)
                .findFirst();
    }
}
