package api.servico.adega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import api.servico.adega.model.Produto;

import java.util.Optional;




public interface ProdutoRepositoy extends JpaRepository<Produto, Long> {
    
    Optional<Produto> findByNomeProduto(String nomeProduto);

}
