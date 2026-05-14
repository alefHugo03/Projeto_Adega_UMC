package api.servico.adega.dto.responses;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoResponseDTO {
    private Long id;
    private String formaPagamento; // Nome do Enum (ex: PIX, CARTAO_CREDITO)
    private String descricao;      // Descrição amigável (ex: Cartão de Crédito)
    private BigDecimal valorPago;
    private Integer parcelas;
}