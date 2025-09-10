package br.com.fiap.vendas.controller.vendas.dto.cliente;

import br.com.fiap.vendas.controller.vendas.dto.veiculo.StatusVeiculo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private LocalDateTime dataCadastro;

}
