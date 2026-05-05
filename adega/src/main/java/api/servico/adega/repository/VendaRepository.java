package api.servico.adega.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long>{
    /**
     * Procura um usuário pelo email.
     */
    Optional<Venda> findById(Long id);

    /**
     * Verifica se já existe usuário com o email informado.
     */
    boolean existsById(Long id);
}
