package api.servico.adega.repository;

import api.servico.adega.model.PagamentoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoVendaRepository extends JpaRepository<PagamentoVenda, Long> {
}