package br.com.fiap.vendas.infra.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "vendas")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendasEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_venda")
    private UUID id;

    @Column(name = "id_cliente", nullable = false)
    private UUID clienteId;

    @Column(name = "id_veiculo", nullable = false)
    private UUID veiculoId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "data_venda", updatable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();
}




