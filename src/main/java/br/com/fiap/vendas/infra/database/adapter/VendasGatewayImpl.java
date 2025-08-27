package br.com.fiap.vendas.infra.database.adapter;

import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.gateway.VendasGateway;
import br.com.fiap.vendas.infra.database.entity.StatusVendas;
import br.com.fiap.vendas.infra.database.entity.VendasEntity;
import br.com.fiap.vendas.infra.database.repository.VendasRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class VendasGatewayImpl implements VendasGateway {

    private final VendasRepository vendasRepository;

    public VendasGatewayImpl(VendasRepository vendasRepository) {
        this.vendasRepository = vendasRepository;
    }

    @Override
    public Vendas salvar(Vendas venda) {
        VendasEntity entity = toEntity(venda);
        VendasEntity savedEntity = vendasRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Vendas> buscarPorId(UUID id) {
        return vendasRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Vendas> buscarVendidosOrdenadosPorPreco() {
        return vendasRepository.findByStatusOrderByDataVendaAsc(StatusVendas.CONCLUIDA)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }


    private VendasEntity toEntity(Vendas venda) {
        VendasEntity entity = new VendasEntity();
        entity.setId(venda.getId());
        entity.setClienteId(venda.getClienteId());
        entity.setVeiculoId(venda.getVeiculoId());
        entity.setStatus(StatusVendas.valueOf(venda.getStatus().name()));
        entity.setDataVenda(venda.getDataVenda());
        return entity;
    }

    private Vendas toDomain(VendasEntity entity) {
        return Vendas.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .status(Vendas.Status.valueOf(entity.getStatus().name()))
                .dataVenda(entity.getDataVenda())
                .build();
    }
}
