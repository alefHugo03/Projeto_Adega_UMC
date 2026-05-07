package api.servico.adega.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstoqueResponseDTO {

    private Long idEstoque;
    private ProdutoResponseDTO produto;
    private int quantidade;

    public EstoqueResponseDTO(Long idEstoque, ProdutoResponseDTO produto, int quantidade) {
        this.idEstoque = idEstoque;
        this.produto = produto;
        this.quantidade = quantidade;
    }
}
