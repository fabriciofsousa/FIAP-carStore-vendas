package br.com.fiap.vendas.domain;

import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vendas {

    private UUID id;
    private UUID veiculoId;
    private UUID clienteId;

    @NotNull
    @Positive
    private BigDecimal valorTotal;

    @Builder.Default
    private List<Pagamento> pagamentos = new ArrayList<>();

    @Builder.Default
    private Status status = Status.INICIADA;

    @Builder.Default
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Pagamento {
        private BigDecimal valor;
        private FormaPagamento formaPagamento;
        private LocalDateTime dataPagamento;
    }

    public BigDecimal getTotalPago() {
        return pagamentos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
