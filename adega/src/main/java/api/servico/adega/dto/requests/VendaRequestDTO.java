package api.servico.adega.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class VendaRequestDTO {

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;

    @NotNull(message = "O id do usuário é obrigatório")
    private Long idUser;

    private String dataVenda;
}
