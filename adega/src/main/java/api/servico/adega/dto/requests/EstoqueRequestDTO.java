package api.servico.adega.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstoqueRequestDTO {

    @NotNull(message = "O id do produto é obrigatório")
    private Long idProduto;

    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    private int quantidade;
}
