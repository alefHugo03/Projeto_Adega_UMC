package api.servico.adega.dto.requests;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoRequestDTO {

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    @NotNull(message = "O valor pago é obrigatório")
    @Positive(message = "O valor pago deve ser positivo")
    private BigDecimal valorPago;

    @NotNull(message = "A quantidade de parcelas é obrigatória")
    private Integer parcelas;
}