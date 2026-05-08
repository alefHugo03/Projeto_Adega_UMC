package api.servico.adega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "item_venda", uniqueConstraints = @UniqueConstraint(columnNames = "id_venda"))
public class ItemVenda {
//    Identificação do item de venda
    @Column(name = "id_itemvenda")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idItemVenda;


    @ManyToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    // Identificação do produto
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    private int quantidadeVendida;

}