package api.servico.adega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Estoque;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Estoque.
 *
 * Usa Spring Data para fornecer operações CRUD básicas e consultas
 * personalizadas a partir do nome dos métodos.
 */
@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findByProduto_IdProduto(Long idProduto);
    List<Estoque> findByProduto_TipoProduto(String tipoProduto);
}
