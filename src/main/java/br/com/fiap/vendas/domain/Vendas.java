package br.com.fiap.vendas.domain;

import br.com.fiap.vendas.infra.database.entity.StatusVendas;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vendas {

    private UUID id;
    private UUID veiculoId;
    private UUID clienteId;

    @Builder.Default
    private Status status = Status.INICIADA;

    @Builder.Default
    private LocalDateTime dataVenda = LocalDateTime.now();

    public enum Status {
        INICIADA,
        CONCLUIDA,
        CANCELADA;

        public static Status getStatusByName(Status status) {
            return Status.valueOf(status.name());
        }
    }
}
