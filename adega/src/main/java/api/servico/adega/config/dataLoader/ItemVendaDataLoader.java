package api.servico.adega.config.dataLoader;

import api.servico.adega.model.ItemVenda;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Venda;
import api.servico.adega.model.PagamentoVenda;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.repository.PagamentoVendaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import api.servico.adega.enums.FormaPagamento;

/**
 * Responsável por detalhar os itens das vendas criadas no VendaDataLoader.
 */
@Component
@Order(5)
public class ItemVendaDataLoader implements CommandLineRunner {

    private final ItemVendaRepository itemVendaRepository;
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoVendaRepository pagamentoVendaRepository;

    public ItemVendaDataLoader(ItemVendaRepository itemVendaRepository,
                               VendaRepository vendaRepository,
                               ProdutoRepository produtoRepository,
                               PagamentoVendaRepository pagamentoVendaRepository) {
        this.itemVendaRepository = itemVendaRepository;
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.pagamentoVendaRepository = pagamentoVendaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (itemVendaRepository.count() == 0) {
            List<Venda> vendas = vendaRepository.findAll();
            List<Produto> produtos = produtoRepository.findAll();
            Random random = new Random();

            if (!vendas.isEmpty() && !produtos.isEmpty()) {
                for (Venda venda : vendas) {
                    // Seleciona um produto aleatório da lista disponível
                    Produto p = produtos.get(random.nextInt(produtos.size()));

                    // Sorteia uma quantidade pequena: de 1 a 4 itens por pessoa
                    int qtdVendida = random.nextInt(4) + 1;

                    ItemVenda item = new ItemVenda();
                    item.setVenda(venda);
                    item.setProduto(p);
                    item.setQuantidadeVendida(qtdVendida);
                    item.setActive(true);
                    itemVendaRepository.save(item);

                    // Calcula o valor total da venda baseado na quantidade alterada
                    BigDecimal total = p.getValorUnitario().multiply(new BigDecimal(qtdVendida));
                    venda.setValorTotal(total);
                    vendaRepository.save(venda);

                    // ALTERADO: Passando o ID numérico da venda (.getIdVenda())
                    List<PagamentoVenda> pgs = pagamentoVendaRepository.findByVenda_IdVenda(venda.getIdVenda());
                    if (!pgs.isEmpty()) {
                        PagamentoVenda pg = pgs.get(0);
                        if (FormaPagamento.RETIRADA_ADMIN.equals(pg.getFormaPagamento())) {
                            // Manter retirada administrativa com valor zerado e marcar motivo
                            pg.setValorPago(BigDecimal.ZERO);
                            venda.setMotivo("Retirada administrativa (seed)");
                            venda.setValorTotal(BigDecimal.ZERO);
                            vendaRepository.save(venda);
                            pagamentoVendaRepository.save(pg);
                        } else {
                            pg.setValorPago(total);
                            pagamentoVendaRepository.save(pg);
                        }
                    }
                }
            }
        }
    }
}