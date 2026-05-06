package api.servico.adega.config;

import api.servico.adega.model.Usuario;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Estoque;
import api.servico.adega.model.Venda;
import api.servico.adega.model.ItemVenda;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.ProdutoRepositoy;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.repository.ItemVendaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Componente que carrega dados iniciais no banco de dados
 * quando a aplicação é inicializada pela primeira vez.
 */
@Component
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepositoy produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;

    public UsuarioDataLoader(UsuarioRepository usuarioRepository,
                            ProdutoRepositoy produtoRepository,
                            EstoqueRepository estoqueRepository,
                            VendaRepository vendaRepository,
                            ItemVendaRepository itemVendaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            // Carregar Usuários
            Usuario usuario1 = new Usuario();
            usuario1.setNome("Maria Silva");
            usuario1.setEmail("maria.silva@adega.com.br");
            // usuario1.setIsActive(true);

            Usuario usuario2 = new Usuario();
            usuario2.setNome("João Pereira");
            usuario2.setEmail("joao.pereira@adega.com.br");
            // usuario2.setIsActive(true);

            usuarioRepository.save(usuario1);
            usuarioRepository.save(usuario2);

            // Carregar Produtos
            Produto vinho1 = new Produto();
            vinho1.setNomeProduto("Vinho Tinto Reserva");
            vinho1.setTipoProduto("Tinto");
            vinho1.setValorUnitario("85.00");

            Produto vinho2 = new Produto();
            vinho2.setNomeProduto("Vinho Branco Chardonnay");
            vinho2.setTipoProduto("Branco");
            vinho2.setValorUnitario("65.00");

            Produto vinho3 = new Produto();
            vinho3.setNomeProduto("Vinho Rosé Premium");
            vinho3.setTipoProduto("Rosé");
            vinho3.setValorUnitario("55.00");

            Produto vinho4 = new Produto();
            vinho4.setNomeProduto("Espumante Brut");
            vinho4.setTipoProduto("Espumante");
            vinho4.setValorUnitario("45.00");

            produtoRepository.save(vinho1);
            produtoRepository.save(vinho2);
            produtoRepository.save(vinho3);
            produtoRepository.save(vinho4);

            // Carregar Estoque
            Estoque estoque1 = new Estoque();
            estoque1.setIdProduto(vinho1.getIdProduto());
            estoque1.setQuantidade(50);

            Estoque estoque2 = new Estoque();
            estoque2.setIdProduto(vinho2.getIdProduto());
            estoque2.setQuantidade(75);

            Estoque estoque3 = new Estoque();
            estoque3.setIdProduto(vinho3.getIdProduto());
            estoque3.setQuantidade(40);

            Estoque estoque4 = new Estoque();
            estoque4.setIdProduto(vinho4.getIdProduto());
            estoque4.setQuantidade(30);

            estoqueRepository.save(estoque1);
            estoqueRepository.save(estoque2);
            estoqueRepository.save(estoque3);
            estoqueRepository.save(estoque4);

            // Carregar Vendas
            Venda venda1 = new Venda();
            venda1.setFormaPagamento("Cartão de Crédito");
            venda1.setDataVenda(LocalDateTime.now().minusDays(5));
            venda1.setValorTotal(new BigDecimal("255.00"));
            venda1.setUser(usuario1);

            Venda venda2 = new Venda();
            venda2.setFormaPagamento("PIX");
            venda2.setDataVenda(LocalDateTime.now().minusDays(2));
            venda2.setValorTotal(new BigDecimal("320.00"));
            venda2.setUser(usuario2);

            vendaRepository.save(venda1);
            vendaRepository.save(venda2);

            // Carregar Itens de Venda
            ItemVenda itemVenda1 = new ItemVenda();
            itemVenda1.setIdVenda(venda1.getIdVenda());
            itemVenda1.setIdProduto(vinho1.getIdProduto());
            itemVenda1.setQuantidadeVendida(2);

            ItemVenda itemVenda2 = new ItemVenda();
            itemVenda2.setIdVenda(venda1.getIdVenda());
            itemVenda2.setIdProduto(vinho2.getIdProduto());
            itemVenda2.setQuantidadeVendida(1);

            ItemVenda itemVenda3 = new ItemVenda();
            itemVenda3.setIdVenda(venda2.getIdVenda());
            itemVenda3.setIdProduto(vinho3.getIdProduto());
            itemVenda3.setQuantidadeVendida(3);

            ItemVenda itemVenda4 = new ItemVenda();
            itemVenda4.setIdVenda(venda2.getIdVenda());
            itemVenda4.setIdProduto(vinho4.getIdProduto());
            itemVenda4.setQuantidadeVendida(2);

            itemVendaRepository.save(itemVenda1);
            itemVendaRepository.save(itemVenda2);
            itemVendaRepository.save(itemVenda3);
            itemVendaRepository.save(itemVenda4);
        }
    }
}
