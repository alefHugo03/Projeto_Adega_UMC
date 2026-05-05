package api.servico.adega.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "forma_pagamento")
    private String formaPagamento;

    @Column(name = "data_venda")
    private LocalDateTime dataVenda;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;


    @ManyToOne
    @JoinColumn(name = "id_user")
    private Usuario user;

}