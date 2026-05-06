package api.servico.adega.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaResponseDTO {

    private Long idItemVenda;
    private Long idVenda;
    private Long idProduto;
    private int quantidadeVendida;
}
