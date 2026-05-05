/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;

public class Menu extends JFrame {

    public Menu() {
        setTitle("Sistema Adega");
        setSize(300,250);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("MENU PRINCIPAL");
        titulo.setBounds(80,20,200,30);
        add(titulo);

        JButton btnCadastro = new JButton("Cadastrar Produto");
        btnCadastro.setBounds(60,70,180,30);
        add(btnCadastro);

        JButton btnLista = new JButton("Listar Produtos");
        btnLista.setBounds(60,110,180,30);
        add(btnLista);

        JButton btnSair = new JButton("Sair");
        btnSair.setBounds(60,150,180,30);
        add(btnSair);

        // Ações
        btnCadastro.addActionListener(e -> new CadastroProduto());
        btnLista.addActionListener(e -> new ListaProduto());
        btnSair.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}

