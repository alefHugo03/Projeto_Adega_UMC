package api.servico.adega.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity @Table(name = "produto")
public class Produto {

    @Column(name = "id_produto")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idProduto;

    @Column(name = "nome_produto")
    private String nomeProduto;

    @Column(name = "tipo_produto")
    private String tipoProduto;

    @Column(name = "valor_unitario")
    private BigDecimal valorUnitario;

    public void setValorUnitario(@NotBlank(message = "O valor unitário é obrigatório") String valorUnitario) {
    }
}
