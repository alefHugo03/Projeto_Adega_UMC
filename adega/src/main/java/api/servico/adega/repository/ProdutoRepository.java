package api.servico.adega.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.model.Produto;




public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    Optional<Produto> findByNomeProduto(String nomeProduto);

    List<Produto> findByTipoProduto(String tipoProduto);
        
    List<Produto> findByValorUnitario(BigDecimal valorUnitario);

}
