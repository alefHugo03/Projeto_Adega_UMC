/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.ProdutoDAO;
import model.Produto;

import javax.swing.*;

public class CadastroProduto extends JFrame {

    private JTextField txtNome, txtTipo, txtValor;

    // 👉 usado quando for edição
    private Produto produtoEdicao;

    private JTextField txtQuantidade;
    
    // 🔹 CONSTRUTOR NORMAL (cadastro)
    public CadastroProduto() {
        setTitle("Cadastro de Produto");
        setSize(320,260);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("Nome:");
        l1.setBounds(20,20,80,25);
        add(l1);

        txtNome = new JTextField();
        txtNome.setBounds(100,20,170,25);
        add(txtNome);

        JLabel l2 = new JLabel("Tipo:");
        l2.setBounds(20,60,80,25);
        add(l2);

        txtTipo = new JTextField();
        txtTipo.setBounds(100,60,170,25);
        add(txtTipo);

        JLabel l3 = new JLabel("Valor:");
        l3.setBounds(20,100,80,25);
        add(l3);

        txtValor = new JTextField();
        txtValor.setBounds(100,100,170,25);
        add(txtValor);
        
        JLabel l4 = new JLabel("Quantidade:");
        l4.setBounds(20,140,80,25);
        add(l4);

        txtQuantidade = new JTextField();
        txtQuantidade.setBounds(100,140,170,25);
        add(txtQuantidade);

        JButton btn = new JButton("Salvar");
        btn.setBounds(100,150,100,30);
        add(btn);

        btn.addActionListener(e -> salvar());

        setVisible(true);
    }

    // 🔹 CONSTRUTOR PARA EDIÇÃO
    public CadastroProduto(Produto p) {
        this(); // chama a tela normal
        this.produtoEdicao = p;

        txtNome.setText(p.getNome());
        txtTipo.setText(p.getTipo());
        txtValor.setText(String.valueOf(p.getValor_unit()));
        txtQuantidade.setText(String.valueOf(p.getQuantidade()));
    }

    // 🔥 MÉTODO PRINCIPAL (cadastrar + editar)
    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String tipo = txtTipo.getText().trim();
            String valorTexto = txtValor.getText().trim().replace(",", ".");
            int quantidade = Integer.parseInt(txtQuantidade.getText());

            // validação
            if (nome.isEmpty() || tipo.isEmpty() || valorTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            double valor = Double.parseDouble(valorTexto);

            Produto p = new Produto();
            p.setNome(nome);
            p.setTipo(tipo);
            p.setValor_unit(valor);
            p.setQuantidade(quantidade);

            ProdutoDAO dao = new ProdutoDAO();

            // 🔁 DIFERENCIA CADASTRO DE EDIÇÃO
            if (produtoEdicao == null) {
                dao.cadastrar(p);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            } else {
                p.setId_produto(produtoEdicao.getId_produto());
                dao.atualizar(p);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            }

            limparCampos();
            dispose(); // fecha tela após salvar

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um valor válido! Ex: 10.50 ou 10,50");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTipo.setText("");
        txtValor.setText("");
        txtNome.requestFocus();
    }
}