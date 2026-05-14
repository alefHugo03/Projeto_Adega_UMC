package api.servico.adega.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vendas")
public class Venda {

    @Column(name = "id_venda")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idVenda;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagamentoVenda> pagamentos = new ArrayList<>();

    @Column(name = "data_venda")
    private LocalDateTime dataVenda;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;


    @ManyToOne
    @JoinColumn(name = "id_user")
    private Usuario user;

    @Column(name = "is_active")
    private boolean isActive = true;
}