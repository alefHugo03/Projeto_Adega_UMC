package api.servico.adega.repository;

import api.servico.adega.model.Estoque;

import java.util.Optional;


public interface EstoqueRepository {
    
    Optional<Estoque> findById(Long id);

    /**
     * Verifica se já existe usuário com o email informado.
     */
    boolean existsById(Long id);
}
