package api.servico.adega.config.dataLoader;

import api.servico.adega.model.ItemVenda;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.repository.VendaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Responsável por detalhar os itens das vendas criadas no VendaDataLoader.
 */
@Component
@Order(5)
public class ItemVendaDataLoader implements CommandLineRunner {

    private final ItemVendaRepository itemVendaRepository;
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public ItemVendaDataLoader(ItemVendaRepository itemVendaRepository, 
                               VendaRepository vendaRepository, 
                               ProdutoRepository produtoRepository) {
        this.itemVendaRepository = itemVendaRepository;
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (itemVendaRepository.count() == 0) {
            List<Venda> vendas = vendaRepository.findAll();
            List<Produto> produtos = produtoRepository.findAll();

            if (!vendas.isEmpty() && produtos.size() >= 3) {
                // Itens para a primeira venda (Vinho + Cerveja = 107.90)
                Venda v1 = vendas.get(0);
                Venda v2 = vendas.get(1);
                Venda v3 = vendas.get(2);

                ItemVenda item1 = new ItemVenda();
                item1.setVenda(v1);
                item1.setProduto(produtos.get(0));
                item1.setQuantidadeVendida(1);
                itemVendaRepository.save(item1);

                ItemVenda item2 = new ItemVenda();
                item2.setVenda(v2);
                item2.setProduto(produtos.get(1));
                item2.setQuantidadeVendida(1);
                itemVendaRepository.save(item2);

                // Item para a segunda venda (Whisky = 280.00)
                ItemVenda item3 = new ItemVenda();
                item3.setVenda(v3);
                item3.setProduto(produtos.get(2)); // Whisky
                item3.setQuantidadeVendida(1);
                itemVendaRepository.save(item3);

                System.out.println(">>> ItemVendaDataLoader: Detalhes das vendas vinculados aos produtos.");
            }
        }
    }
}