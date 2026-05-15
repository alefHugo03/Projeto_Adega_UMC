package api.servico.adega.config.dataLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;

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
            // Busca todos os vendedores ativos (não admins) para a competição
            List<Usuario> vendedores = usuarioRepository.findAll().stream()
                    .filter(u -> u.isActive() && !"ROLE_ADMIN".equals(u.getRole()))
                    .toList();

            Random random = new Random();

            if (!vendedores.isEmpty()) {
                for (int i = 0; i < 7; i++) {
                    LocalDateTime dataDia = LocalDateTime.now().minusDays(i);
                    for (int j = 0; j < 11; j++) { // 11 vendas por dia
                        Venda venda = new Venda();
                        venda.setDataVenda(dataDia.minusMinutes(j * 30));
                        // Sorteia Alef, Richard ou Vitor para a venda
                        venda.setUser(vendedores.get(random.nextInt(vendedores.size())));
                        venda.setActive(true);
                        venda.setValorTotal(BigDecimal.ZERO); // Valor será atualizado pelo ItemVendaDataLoader ou lógica de soma
                        vendaRepository.save(venda);

                        // Adiciona um pagamento aleatório para a venda
                        FormaPagamento forma = FormaPagamento.values()[random.nextInt(FormaPagamento.values().length)];
                        pagamentoVendaRepository.save(new PagamentoVenda(null, venda, forma, BigDecimal.ZERO, 1));
                    }
                }
                System.out.println(">>> VendaDataLoader: 77 vendas criadas (11 por dia nos últimos 7 dias).");
            }
        }
    }
}
