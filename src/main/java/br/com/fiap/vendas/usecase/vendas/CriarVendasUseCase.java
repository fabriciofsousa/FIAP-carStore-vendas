package br.com.fiap.vendas.usecase.vendas;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.FormaPagamento;

import java.math.BigDecimal;

public interface CriarVendasUseCase {
    Vendas execute (Vendas venda, Vendas.Pagamento pagamento);
}
