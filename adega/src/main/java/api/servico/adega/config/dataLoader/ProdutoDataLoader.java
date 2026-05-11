package api.servico.adega.config.dataLoader;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import api.servico.adega.model.Produto;
import api.servico.adega.repository.ProdutoRepository;

/**
 * Responsável por criar produtos básicos para teste.
 */
@Component
@Order(2)
public class ProdutoDataLoader implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;

    public ProdutoDataLoader(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (produtoRepository.count() == 0) {
            // Criando produtos com os campos exatos do seu Model
            Produto p1 = new Produto();
            p1.setNomeProduto("Vinho Tinto Malbec");
            p1.setTipoProduto("Vinho");
            p1.setValorUnitario(new BigDecimal("85.90"));

            Produto p2 = new Produto();
            p2.setNomeProduto("Cerveja Artesanal Stout");
            p2.setTipoProduto("Cerveja");
            p2.setValorUnitario(new BigDecimal("22.00"));

            Produto p3 = new Produto();
            p3.setNomeProduto("Whisky Single Malt");
            p3.setTipoProduto("Destilado");
            p3.setValorUnitario(new BigDecimal("280.00"));

            produtoRepository.saveAll(List.of(p1, p2, p3));
            System.out.println(">>> ProdutoDataLoader: Produtos de teste carregados.");
        }
    }
}
