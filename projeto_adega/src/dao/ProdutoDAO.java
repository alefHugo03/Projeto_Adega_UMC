/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connection.ConnectionFactory;
import model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void cadastrar(Produto p) {
        String sql = "INSERT INTO produto (nome, tipo, valor_unit, quantidade) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getTipo());
            ps.setDouble(3, p.getValor_unit());
            ps.setInt(4, p.getQuantidade());

            ps.execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();

        String sql = "SELECT * FROM produto";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId_produto(rs.getInt("id_produto"));
                p.setNome(rs.getString("nome"));
                p.setTipo(rs.getString("tipo"));
                p.setValor_unit(rs.getDouble("valor_unit"));
                p.setQuantidade(rs.getInt("quantidade"));

                lista.add(p);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return lista;
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void atualizar(Produto p) {
    String sql = "UPDATE produto SET nome=?, tipo=?, valor_unit=?, quantidade=? WHERE id_produto=?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, p.getNome());
        ps.setString(2, p.getTipo());
        ps.setDouble(3, p.getValor_unit());
        ps.setInt(4, p.getId_produto());

        ps.execute();

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}
