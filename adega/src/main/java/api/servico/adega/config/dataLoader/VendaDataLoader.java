package api.servico.adega.config.dataLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import api.servico.adega.enums.FormaPagamento;
import api.servico.adega.model.PagamentoVenda;
import api.servico.adega.model.Usuario;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.PagamentoVendaRepository;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.VendaRepository;

/**
 * Responsável por criar vendas de exemplo para popular relatórios e testes.
 */
@Component
@Order(4)
public class VendaDataLoader implements CommandLineRunner {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoVendaRepository pagamentoVendaRepository;

    public VendaDataLoader(VendaRepository vendaRepository, UsuarioRepository usuarioRepository, PagamentoVendaRepository pagamentoVendaRepository) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagamentoVendaRepository = pagamentoVendaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (vendaRepository.count() == 0) {
            Usuario vendedor = usuarioRepository.findByEmail("vendedor@vendedor.com").orElse(null);

            if (vendedor != null) {
                Venda v1 = new Venda();
                v1.setValorTotal(new BigDecimal("107.90"));
                v1.setDataVenda(LocalDateTime.now());
                v1.setUser(vendedor);
                v1.setActive(true);
                vendaRepository.save(v1);
                pagamentoVendaRepository.save(new PagamentoVenda(null, v1, FormaPagamento.CARTAO_CREDITO, v1.getValorTotal(), 1));

                Venda v2 = new Venda();
                v2.setValorTotal(new BigDecimal("280.00"));
                v2.setDataVenda(LocalDateTime.now().minusDays(1));
                v2.setUser(vendedor); // Corrigido de sLcUser para setUser
                v2.setActive(true);
                vendaRepository.save(v2);
                pagamentoVendaRepository.save(new PagamentoVenda(null, v2, FormaPagamento.PIX, v2.getValorTotal(), 1));

                Venda v3 = new Venda();
                v3.setValorTotal(new BigDecimal("180.00"));
                v3.setDataVenda(LocalDateTime.now().minusDays(2)); // Garantir que LocalDateTime está correto
                v3.setUser(vendedor);
                v3.setActive(true);
                vendaRepository.save(v3);
                pagamentoVendaRepository.save(new PagamentoVenda(null, v3, FormaPagamento.CARTAO_DEBITO, v3.getValorTotal(), 1));

                System.out.println(">>> VendaDataLoader: Vendas de teste criadas.");
            }
        }
    }
}
