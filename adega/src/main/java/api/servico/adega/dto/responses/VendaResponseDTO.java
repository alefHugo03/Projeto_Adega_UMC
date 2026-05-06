package api.servico.adega.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseDTO {

    private Long idVenda;
    private String formaPagamento;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private UsuarioResponseDTO user;
}
