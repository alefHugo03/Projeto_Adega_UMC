package api.servico.adega.model;

import jakarta.persistence.*;
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
    private String valorUnitario;
}
