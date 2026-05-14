package api.servico.adega.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    // Identificação do produto
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    private int quantidadeVendida;

    @Column(name = "is_active")
    private boolean isActive = true;

}