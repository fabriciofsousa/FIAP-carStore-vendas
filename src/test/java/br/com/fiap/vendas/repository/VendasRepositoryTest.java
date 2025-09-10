package br.com.fiap.vendas.repository;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import br.com.fiap.vendas.infra.database.repository.VendasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class VendasRepositoryTest {

    @Autowired
    private VendasRepository vendasRepository;

    private VendasEntity venda1;
    private VendasEntity venda2;

    @BeforeEach
    void setUp() {
        vendasRepository.deleteAll();

        venda1 = new VendasEntity();
        venda1.setId(UUID.randomUUID());
        venda1.setClienteId(UUID.randomUUID());
        venda1.setVeiculoId(UUID.randomUUID());
        venda1.setStatus(Status.CONCLUIDA);
        venda1.setDataVenda(LocalDateTime.now().minusDays(1));
        vendasRepository.save(venda1);

        venda2 = new VendasEntity();
        venda2.setId(UUID.randomUUID());
        venda2.setClienteId(UUID.randomUUID());
        venda2.setVeiculoId(UUID.randomUUID());
        venda2.setStatus(Status.CONCLUIDA);
        venda2.setDataVenda(LocalDateTime.now());
        vendasRepository.save(venda2);
    }

    @Test
    void findByStatusOrderByDataVendaAsc_deveRetornarVendasOrdenadas() {
        List<VendasEntity> result = vendasRepository.findByStatusOrderByDataVendaAsc(Status.CONCLUIDA);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDataVenda()).isBefore(result.get(1).getDataVenda());
    }

    @Test
    void findByVeiculoIdAndStatus_deveRetornarVendaQuandoExistir() {
        Optional<Vendas> result = vendasRepository.findByVeiculoIdAndStatus(venda1.getVeiculoId(), Status.CONCLUIDA);

        assertThat(result).isPresent();
        assertThat(result.get().getVeiculoId()).isEqualTo(venda1.getVeiculoId());
    }

    @Test
    void findByVeiculoIdAndStatus_deveRetornarVazioQuandoNaoExistir() {
        Optional<Vendas> result = vendasRepository.findByVeiculoIdAndStatus(UUID.randomUUID(), Status.CONCLUIDA);

        assertThat(result).isEmpty();
    }
}

