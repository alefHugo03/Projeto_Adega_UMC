/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.ProdutoDAO;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ListaProduto extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;

    public ListaProduto() {
        setTitle("Lista de Produtos");
        setSize(520,320);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Tipo");
        modelo.addColumn("Valor");
        modelo.addColumn("Quantidade");

        tabela = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20,20,460,180);
        add(scroll);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(280,220,120,30);
        add(btnExcluir);

        btnExcluir.addActionListener(e -> excluir());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(100,220,120,30);
        add(btnEditar);

        btnEditar.addActionListener(e -> editar());

        carregarTabela();

        setVisible(true);
    }

    private void carregarTabela() {
        modelo.setRowCount(0);

        List<Produto> lista = new ProdutoDAO().listar();

        for (Produto p : lista) {
            modelo.addRow(new Object[]{
                p.getId_produto(),
                p.getNome(),
                p.getTipo(),
                p.getValor_unit(),
                p.getQuantidade()
            });
        }
    } // ✅ FECHOU AQUI (FALTAVA ISSO)

    private void excluir() {
        int linha = tabela.getSelectedRow();

        if (linha != -1) {
            int id = (int) tabela.getValueAt(linha, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                new ProdutoDAO().excluir(id);
                JOptionPane.showMessageDialog(this, "Produto excluído!");
                carregarTabela();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }

    private void editar() {
        int linha = tabela.getSelectedRow();

        if (linha != -1) {
            Produto p = new Produto();

            p.setId_produto((int) tabela.getValueAt(linha, 0));
            p.setNome(tabela.getValueAt(linha, 1).toString());
            p.setTipo(tabela.getValueAt(linha, 2).toString());
            p.setValor_unit(Double.parseDouble(tabela.getValueAt(linha, 3).toString()));
            p.setQuantidade(Integer.parseInt(tabela.getValueAt(linha, 4).toString()));

            new CadastroProduto(p);
            dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }
}