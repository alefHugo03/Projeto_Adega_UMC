package api.servico.adega.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import api.servico.adega.model.Estoque;
import api.servico.adega.model.ItemVenda;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Usuario;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.VendaRepository;

/**
 * Componente que carrega dados iniciais no banco de dados
 * quando a aplicação é inicializada pela primeira vez.
 */
@Component
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;

    public UsuarioDataLoader(UsuarioRepository usuarioRepository,
                            ProdutoRepository produtoRepository,
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
        if (vendaRepository.count() == 0) {
            Usuario usuario1 = null;
            Usuario usuario2 = null;

            // Criar usuários se não existirem
            usuario1 = usuarioRepository.findByEmail("maria.silva@adega.com.br").orElse(null);
            if (usuario1 == null) {
                usuario1 = new Usuario();
                usuario1.setNome("Maria Silva");
                usuario1.setEmail("maria.silva@adega.com.br");
                usuario1.setActive(true);
                usuarioRepository.save(usuario1);
            }

            usuario2 = usuarioRepository.findByEmail("joao.pereira@adega.com.br").orElse(null);
            if (usuario2 == null) {
                usuario2 = new Usuario();
                usuario2.setNome("João Pereira");
                usuario2.setEmail("joao.pereira@adega.com.br");
                usuario2.setActive(true);
                usuarioRepository.save(usuario2);
            }

            // Carregar Produtos
            Produto vinho1 = new Produto();
            vinho1.setNomeProduto("Vinho Tinto Reserva");
            vinho1.setTipoProduto("Tinto");
            vinho1.setValorUnitario(String.valueOf(new BigDecimal("85.00")));

            Produto vinho2 = new Produto();
            vinho2.setNomeProduto("Vinho Branco Chardonnay");
            vinho2.setTipoProduto("Branco");
            vinho2.setValorUnitario(String.valueOf(new BigDecimal("65.00")));

            Produto vinho3 = new Produto();
            vinho3.setNomeProduto("Vinho Rosé Premium");
            vinho3.setTipoProduto("Rosé");
            vinho3.setValorUnitario(String.valueOf(new BigDecimal("55.00")));

            Produto vinho4 = new Produto();
            vinho4.setNomeProduto("Espumante Brut");
            vinho4.setTipoProduto("Espumante");
            vinho4.setValorUnitario(String.valueOf(new BigDecimal("45.00")));

            produtoRepository.save(vinho1);
            produtoRepository.save(vinho2);
            produtoRepository.save(vinho3);
            produtoRepository.save(vinho4);

            // Carregar Estoque
            Estoque estoque1 = new Estoque();
            estoque1.setProduto(vinho1);
            estoque1.setQuantidade(50);

            Estoque estoque2 = new Estoque();
            estoque2.setProduto(vinho2);
            estoque2.setQuantidade(75);

            Estoque estoque3 = new Estoque();
            estoque3.setProduto(vinho3);
            estoque3.setQuantidade(40);

            Estoque estoque4 = new Estoque();
            estoque4.setProduto(vinho4);
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
            itemVenda1.setVenda(venda1);
            itemVenda1.setProduto(vinho1);
            itemVenda1.setQuantidadeVendida(2);

            ItemVenda itemVenda2 = new ItemVenda();
            itemVenda2.setVenda(venda1);
            itemVenda2.setProduto(vinho2);
            itemVenda2.setQuantidadeVendida(1);

            ItemVenda itemVenda3 = new ItemVenda();
            itemVenda3.setVenda(venda2);
            itemVenda3.setProduto(vinho3);
            itemVenda3.setQuantidadeVendida(3);

            ItemVenda itemVenda4 = new ItemVenda();
            itemVenda4.setVenda(venda2);
            itemVenda4.setProduto(vinho4);
            itemVenda4.setQuantidadeVendida(2);

            itemVendaRepository.save(itemVenda1);
            itemVendaRepository.save(itemVenda2);
            itemVendaRepository.save(itemVenda3);
            itemVendaRepository.save(itemVenda4);
        }
    }
}
