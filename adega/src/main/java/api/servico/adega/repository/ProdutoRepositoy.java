package api.servico.adega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Usuario;

import java.util.List;
import java.util.Opctional;




public interface ProdutoRepositoy extends JpaRepository<Produto, Long> {
    
    Opctional<Produto> findByNomeProduto(String nomeProduto);

}
