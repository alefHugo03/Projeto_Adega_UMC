package api.servico.adega.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
}
