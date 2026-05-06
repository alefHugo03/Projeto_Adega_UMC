package api.servico.adega.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {

    private Long idEstoque;
    private Long idProduto;
    private int quantidade;
}
