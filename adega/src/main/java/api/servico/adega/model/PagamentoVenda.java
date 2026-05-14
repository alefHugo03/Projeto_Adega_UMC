package api.servico.adega.model;

import api.servico.adega.enums.FormaPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "pagamentos_venda")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PagamentoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento")
    private FormaPagamento formaPagamento;

    @Column(name = "valor_pago")
    private BigDecimal valorPago;

    @Column(name = "parcelas")
    private Integer parcelas = 1;
}