package api.servico.adega.config.dataLoader;

import api.servico.adega.model.Usuario;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.VendaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Responsável por criar vendas de exemplo para popular relatórios e testes.
 */
@Component
@Order(4)
public class VendaDataLoader implements CommandLineRunner {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;

    public VendaDataLoader(VendaRepository vendaRepository, UsuarioRepository usuarioRepository) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (vendaRepository.count() == 0) {
            Usuario vendedor = usuarioRepository.findByEmail("vendedor@vendedor.com").orElse(null);

            if (vendedor != null) {
                Venda v1 = new Venda();
                v1.setFormaPagamento("CARTAO_CREDITO");
                v1.setValorTotal(new BigDecimal("107.90"));
                v1.setDataVenda(LocalDateTime.now());
                v1.setUser(vendedor);

                Venda v2 = new Venda();
                v2.setFormaPagamento("PIX");
                v2.setValorTotal(new BigDecimal("280.00"));
                v2.setDataVenda(LocalDateTime.now().minusDays(1));
                v2.setUser(vendedor);

                Venda v3 = new Venda();
                v3.setFormaPagamento("CARTÃO_DEBITO");
                v3.setValorTotal(new BigDecimal("180.00"));
                v3.setDataVenda(LocalDateTime.now().minusDays(2));
                v3.setUser(vendedor);

                vendaRepository.save(v1);
                vendaRepository.save(v2);
                vendaRepository.save(v3);
                System.out.println(">>> VendaDataLoader: Vendas de teste criadas.");
            }
        }
    }
}
