package api.servico.adega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.ItemVenda;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda_IdVenda(Long idVenda);
    List<ItemVenda> findByProduto_IdProduto(Long idProduto);
    List<ItemVenda> findByQuantidadeVendida(int quantidadeVendida);
}
