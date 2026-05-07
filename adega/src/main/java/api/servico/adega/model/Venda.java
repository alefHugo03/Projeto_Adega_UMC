package api.servico.adega.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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