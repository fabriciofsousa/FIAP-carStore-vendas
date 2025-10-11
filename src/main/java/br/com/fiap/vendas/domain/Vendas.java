package br.com.fiap.vendas.domain;

import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

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
    @DynamoDbBean
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Pagamento {
        private BigDecimal valor;
        private FormaPagamento formaPagamento;
        private LocalDateTime dataPagamento;

        @DynamoDbAttribute("valor")
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }

        @DynamoDbAttribute("formaPagamento")
        public FormaPagamento getFormaPagamento() { return formaPagamento; }
        public void setFormaPagamento(FormaPagamento formaPagamento) {
            this.formaPagamento = formaPagamento;
        }

        @DynamoDbAttribute("dataPagamento")
        public LocalDateTime getDataPagamento() { return dataPagamento; }
        public void setDataPagamento(LocalDateTime dataPagamento) {
            this.dataPagamento = dataPagamento;
        }
    }

    public BigDecimal getTotalPago() {
        return pagamentos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
