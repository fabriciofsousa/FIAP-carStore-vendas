package br.com.fiap.vendas.infra.database.repository;

import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public interface VendasMongoRepository extends MongoRepository<VendasEntity, String> {

    List<VendasEntity> findByStatusOrderByDataVendaAsc(Status status);

    Optional<VendasEntity> findByVeiculoIdAndStatus(UUID veiculoId, Status status);
}

