package br.com.fiap.vendas.infra.database.entity;

import br.com.fiap.vendas.domain.Vendas;
import lombok.*;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendasEntity {

    public static final String TABLE_NAME = "carstore-vendas";

    @Id
    private String id;
    private UUID clienteId;
    private UUID veiculoId;
    private BigDecimal valorTotal;
    @Builder.Default
    private Status status = Status.INICIADA;
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Builder.Default
    private List<Vendas.Pagamento> pagamentos = new ArrayList<>();

    @DynamoDbPartitionKey
    @DynamoDbAttribute("venda_id")
    public String getId() {
        return id.toString(); // converte UUID para String
    }

    public void setId(String id) {
        this.id = id; // converte String de volta para UUID
    }
}




