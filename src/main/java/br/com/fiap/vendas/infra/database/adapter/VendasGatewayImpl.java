package br.com.fiap.vendas.infra.database.adapter;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import br.com.fiap.vendas.infra.database.repository.VendasMongoRepository;
import br.com.fiap.vendas.infra.database.repository.VendasMongoRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class VendasGatewayImpl implements VendasGateway {

    private final VendasMongoRepository VendasMongoRepository;

    public VendasGatewayImpl(VendasMongoRepository VendasMongoRepository) {
        this.VendasMongoRepository = VendasMongoRepository;
    }

    @Override
    public Vendas salvar(Vendas venda) {
        VendasEntity entity = toEntity(venda);
        VendasEntity savedEntity = VendasMongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Vendas> buscarPorId(UUID id) {
        return VendasMongoRepository.findById(String.valueOf(id)).map(this::toDomain);
    }

    @Override
    public List<Vendas> buscarVendidosOrdenadosPorPreco(Status status) {
        return VendasMongoRepository.findByStatusOrderByDataVendaAsc(status)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Vendas buscarVendaIniciadaPorVeiculo(UUID veiculoId) {
        VendasEntity vendasEntity =  VendasMongoRepository.findByVeiculoIdAndStatus(veiculoId, Status.INICIADA).orElse(null);
        return vendasEntity != null ? toDomain(vendasEntity) : null;
    }

    private static VendasEntity toEntity(Vendas venda) {
        VendasEntity entity = new VendasEntity();
        entity.setId(venda.getId() == null ? UUID.randomUUID().toString() : venda.getId());
        entity.setClienteId(venda.getClienteId());
        entity.setVeiculoId(venda.getVeiculoId());
        entity.setStatus(Status.valueOf(venda.getStatus().name()));
        entity.setDataVenda(venda.getDataVenda());
        entity.setPagamentos(venda.getPagamentos());
        return entity;
    }

    private Vendas toDomain(VendasEntity entity) {
        return Vendas.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .status(entity.getStatus())
                .pagamentos(entity.getPagamentos() != null ? new ArrayList<>(entity.getPagamentos()) : new ArrayList<>())
                .dataVenda(entity.getDataVenda())
                .build();
    }
}
