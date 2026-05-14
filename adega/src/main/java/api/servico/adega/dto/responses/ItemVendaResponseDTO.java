package api.servico.adega.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemVendaResponseDTO {

    private Long idItemVenda;
    private VendaResponseDTO venda;
    private ProdutoResponseDTO produto;
    private int quantidadeVendida;
    private boolean active;


    public ItemVendaResponseDTO(Long idItemVenda, VendaResponseDTO venda, ProdutoResponseDTO produto, int quantidadeVendida, boolean active) {
        this.idItemVenda = idItemVenda;
        this.venda = venda;
        this.produto = produto;
        this.quantidadeVendida = quantidadeVendida;
        this.active = active;
    }

}
