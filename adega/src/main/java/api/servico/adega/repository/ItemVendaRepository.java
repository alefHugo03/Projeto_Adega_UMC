package api.servico.adega.repository;

import api.servico.adega.model.ItemVenda;

import java.util.Optional;

public interface ItemVendaRepository {
    
    Optional<ItemVenda> findById(Long id);
}
