package api.servico.adega.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "is_active")
    private boolean isActive = true;
}
