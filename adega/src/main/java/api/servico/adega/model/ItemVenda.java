package api.servico.adega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "item_venda")
public class ItemVenda {
//    Identificação do item de venda
    @Column(name = "id_itemvenda")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idItemVenda;


    @Column(name = "id_venda")
    private Long idVenda;

    // Identificação do produto
    @Column(name = "id_produto")
    private Long idProduto;

    private int quantidadeVendida;

}