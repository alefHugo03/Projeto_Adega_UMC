package api.servico.adega.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemVendaRequestDTO {

    @NotNull(message = "O id da venda é obrigatório")
    private Long idVenda;

    @NotNull(message = "O id do produto é obrigatório")
    private Long idProduto;

    @Positive(message = "A quantidade vendida deve ser maior que zero")
    private int quantidadeVendida;
}
