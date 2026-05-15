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
            Produto p1 = new Produto();
            p1.setNomeProduto("Vinho Tinto Malbec");
            p1.setTipoProduto("Vinho");
            p1.setValorUnitario(new BigDecimal("85.90"));

            Produto p2 = new Produto();
            p2.setNomeProduto("Cerveja Heineken 600ml");
            p2.setTipoProduto("Cerveja");
            p2.setValorUnitario(new BigDecimal("14.50"));

            Produto p3 = new Produto();
            p3.setNomeProduto("Whisky Single Malt");
            p3.setTipoProduto("Destilado");
            p3.setValorUnitario(new BigDecimal("280.00"));

            Produto p4 = new Produto();
            p4.setNomeProduto("Gin Tanqueray");
            p4.setTipoProduto("Destilado");
            p4.setValorUnitario(new BigDecimal("120.00"));

            Produto p5 = new Produto();
            p5.setNomeProduto("Batata Pringles Original");
            p5.setTipoProduto("Snack");
            p5.setValorUnitario(new BigDecimal("18.90"));

            Produto p6 = new Produto();
            p6.setNomeProduto("Amendoim Japonês");
            p6.setTipoProduto("Snack");
            p6.setValorUnitario(new BigDecimal("8.50"));

            Produto p7 = new Produto();
            p7.setNomeProduto("Mix de Castanhas");
            p7.setTipoProduto("Snack");
            p7.setValorUnitario(new BigDecimal("15.00"));

            produtoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7));
            System.out.println(">>> ProdutoDataLoader: Produtos de teste carregados.");
        }
    }
}
