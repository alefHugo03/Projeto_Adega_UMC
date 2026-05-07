package api.servico.adega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Produto;

import java.util.Optional;




public interface ProdutoRepositoy extends JpaRepository<Produto, Long> {
    
    Optional<Produto> findByNomeProduto(String nomeProduto);

    List<Produto> findByTipoProduto(String tipoProduto);
        
    List<Produto> findByValorUnitario(String valorUnitario);

}
