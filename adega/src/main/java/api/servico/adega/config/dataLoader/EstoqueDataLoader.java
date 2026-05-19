package api.servico.adega.config.dataLoader;

import api.servico.adega.model.Estoque;
import api.servico.adega.model.Produto;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Responsável por inicializar o estoque para os produtos existentes.
 */
@Component
@Order(3)
public class EstoqueDataLoader implements CommandLineRunner {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueDataLoader(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (estoqueRepository.count() == 0) {
            List<Produto> produtos = produtoRepository.findAll();
            Random random = new Random();

            if (!produtos.isEmpty()) {
                for (Produto p : produtos) {
                    Estoque e = new Estoque();
                    e.setProduto(p);
                    // Quantidade aleatória entre 50 e 150
                    e.setQuantidade(random.nextInt(101) + 50);
                    
                    estoqueRepository.save(e);
                }
            }
        }
    }
}
