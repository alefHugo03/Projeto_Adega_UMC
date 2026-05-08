package api.servico.adega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findByProduto_IdProduto(Long idProduto);
    List<Estoque> findByProduto_TipoProduto(String tipoProduto);
}
