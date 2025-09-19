package br.com.fiap.vendas.controller.vendas;

import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasRequestDTO;
import br.com.fiap.vendas.controller.vendas.dto.vendas.VendasResponseDTO;
import br.com.fiap.vendas.controller.vendas.mapper.VendasMapper;
import br.com.fiap.vendas.domain.Vendas;
import br.com.fiap.vendas.infra.database.entity.Status;
import br.com.fiap.vendas.usecase.vendas.AlterarStatusVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.CriarVendasUseCase;
import br.com.fiap.vendas.usecase.vendas.ListarVendasVendidasUseCase;
import br.com.fiap.vendas.usecase.vendas.ObterVendasPorIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vendas")
public class VendasController {

    private final CriarVendasUseCase criarVendasUseCase;
    private final ObterVendasPorIdUseCase obterVendasPorIdUseCase;
    private final ListarVendasVendidasUseCase listarVendasVendidasUseCase;
    private final AlterarStatusVendasUseCase alterarStatusVendasUseCase;

    public VendasController(CriarVendasUseCase criarVendasUseCase,
                            ObterVendasPorIdUseCase obterVendasPorIdUseCase,
                            ListarVendasVendidasUseCase listarVendasVendidasUseCase, AlterarStatusVendasUseCase alterarStatusVendasUseCase) {
        this.criarVendasUseCase = criarVendasUseCase;
        this.obterVendasPorIdUseCase = obterVendasPorIdUseCase;
        this.listarVendasVendidasUseCase = listarVendasVendidasUseCase;
        this.alterarStatusVendasUseCase = alterarStatusVendasUseCase;
    }

    @PostMapping
    public ResponseEntity<?> criarVenda(@RequestBody VendasRequestDTO vendasRequestDTO) {
        try {
            Vendas venda = VendasMapper.toDomain(vendasRequestDTO);
            Vendas novaVenda = criarVendasUseCase.execute(venda, Vendas.Pagamento.builder()
                    .valor(vendasRequestDTO.valorPago())
                    .formaPagamento(vendasRequestDTO.formaPagamento())
                    .build());
            return ResponseEntity.ok(VendasMapper.toResponse(novaVenda));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendasResponseDTO> obterVendaPorId(@PathVariable UUID id) {
        return obterVendasPorIdUseCase.execute(id)
                .map(v -> ResponseEntity.ok(VendasMapper.toResponse(v)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VendasResponseDTO>> listarVendasVendidas(@PathVariable String status) {
        List<Vendas> vendidas = listarVendasVendidasUseCase.buscarVendidosOrdenadosPorPreco(Status.validateStatus(status));
        List<VendasResponseDTO> response = vendidas.stream()
                .map(VendasMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VendasResponseDTO> alterarStatus(@PathVariable UUID id,
                                                           @RequestParam String status) {
        Vendas vendaAtualizada = alterarStatusVendasUseCase.execute(id, status);
        return ResponseEntity.ok(VendasMapper.toResponse(vendaAtualizada));
    }

}
