package api.servico.adega.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nomeProduto;

    @NotBlank(message = "O tipo de produto é obrigatório")
    private String tipoProduto;

    @NotBlank(message = "O valor unitário é obrigatório")
    private String valorUnitario;
}
