package api.servico.adega.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProdutoResponseDTO {

    private Long idProduto;
    private String nomeProduto;
    private String tipoProduto;
    private BigDecimal valorUnitario;

    public ProdutoResponseDTO(Long idProduto, String nomeProduto, String tipoProduto, BigDecimal valorUnitario) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.tipoProduto = tipoProduto;
        this.valorUnitario = valorUnitario;
    }
}
