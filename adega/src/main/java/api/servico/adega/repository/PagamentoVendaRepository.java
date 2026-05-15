package api.servico.adega.repository;

import api.servico.adega.model.PagamentoVenda;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoVendaRepository extends JpaRepository<PagamentoVenda, Long> {
    // Busca todos os pagamentos associados ao ID de uma venda específica
    List<PagamentoVenda> findByVenda_IdVenda(Long vendaId);
}