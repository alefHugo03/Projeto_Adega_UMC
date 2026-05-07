package api.servico.adega.dto.responses;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class VendaResponseDTO {

    private Long idVenda;
    private String formaPagamento;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private UsuarioResponseDTO user;

    public VendaResponseDTO(Long idVenda, String formaPagamento, LocalDateTime dataVenda, BigDecimal valorTotal, UsuarioResponseDTO user) {
        this.idVenda = idVenda;
        this.formaPagamento = formaPagamento;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.user = user;
    }
}
