package br.com.fiap.vendas.infra.database.repository;

import br.com.fiap.vendas.infra.database.entity.StatusVendas;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendasRepository extends JpaRepository<VendasEntity, UUID> {

    List<VendasEntity> findByStatusOrderByDataVendaAsc(StatusVendas status);

}
