package br.com.fiap.vendas.controller.vendas.dto.veiculo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoDTO {
    private UUID id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private BigDecimal preco;
    private Integer quilometragem;
    private StatusVeiculo status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;


}