package br.com.fiap.vendas.infra.database.entity;

import br.com.fiap.vendas.domain.Vendas;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vendas") // nome da coleção no MongoDB
public class VendasEntity {

    @Id
    private String id; // Mongo usa String como ID

    private UUID clienteId;
    private UUID veiculoId;
    private BigDecimal valorTotal;

    @Builder.Default
    private Status status = Status.INICIADA;

    @Builder.Default
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Builder.Default
    private List<Vendas.Pagamento> pagamentos = new ArrayList<>();
}
