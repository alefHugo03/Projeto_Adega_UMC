package api.servico.adega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.ItemVenda;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Item Venda.
 *
 * Usa Spring Data para fornecer operações CRUD básicas e consultas
 * personalizadas a partir do nome dos métodos.
 */
@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda_IdVenda(Long idVenda);
    List<ItemVenda> findByProduto_IdProduto(Long idProduto);
    List<ItemVenda> findByQuantidadeVendida(int quantidadeVendida);
}
