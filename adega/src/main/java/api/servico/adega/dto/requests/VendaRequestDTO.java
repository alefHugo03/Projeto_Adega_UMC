package api.servico.adega.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VendaRequestDTO {

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @NotNull(message = "O id do usuário é obrigatório")
    private Long idUser;

    private String dataVenda;

    @NotEmpty(message = "A venda deve conter ao menos um item")
    private List<ItemVendaRequestDTO> itens;

    private List<PagamentoRequestDTO> pagamentos;

    @Getter
    @Setter
    public static class ItemVendaRequestDTO {
        @NotNull(message = "O id do produto é obrigatório")
        private Long idProduto;

        @NotNull(message = "A quantidade é obrigatória")
        private Integer quantidade;
    }
}
