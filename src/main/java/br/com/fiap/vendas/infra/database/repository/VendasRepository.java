package br.com.fiap.vendas.infra.database.repository;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import scala.None;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendasRepository extends MongoRepository<VendasEntity, UUID> {

    List<VendasEntity> findByStatusOrderByDataVendaAsc(Status status);

    Optional<Vendas> findByVeiculoIdAndStatus(UUID veiculoId, Status status);
}
