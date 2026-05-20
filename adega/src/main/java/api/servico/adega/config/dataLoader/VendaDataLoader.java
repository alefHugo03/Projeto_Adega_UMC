package api.servico.adega.config.dataLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

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
                // Define o início da janela de tempo (3 meses atrás) e o fim (agora)
                LocalDateTime dataCorrente = LocalDateTime.now().minusMonths(3);
                LocalDateTime dataFim = LocalDateTime.now();

                // Loop principal: percorre os 3 meses semana por semana
                while (dataCorrente.isBefore(dataFim)) {

                    // Loop secundário: força exatamente 11 vendas dentro desta semana
                    for (int j = 0; j < 11; j++) {
                        Venda venda = new Venda();

                        // Distribui as vendas aleatoriamente pelos dias da semana, horas e minutos
                        int diasAleatorios = random.nextInt(7);
                        int horaAleatoria = random.nextInt(24);
                        int minutoAleatorio = random.nextInt(60);

                        LocalDateTime dataDaVenda = dataCorrente
                                .plusDays(diasAleatorios)
                                .withHour(horaAleatoria)
                                .withMinute(minutoAleatorio);

                        venda.setDataHoraVenda(dataDaVenda);
                        venda.setUser(vendedores.get(random.nextInt(vendedores.size())));
                        venda.setActive(true);
                        venda.setValorTotal(BigDecimal.ZERO); // Valor calculado depois no ItemVenda DataLoader
                        vendaRepository.save(venda);

                        // Adiciona um registro de pagamento temporário com valor ZERO
                        // Escolhe uma forma de pagamento aleatória, excluindo RETIRADA_ADMIN
                        FormaPagamento[] formasValidas = Arrays.stream(FormaPagamento.values())
                                .filter(f -> !FormaPagamento.RETIRADA_ADMIN.equals(f))
                                .toArray(FormaPagamento[]::new);
                        FormaPagamento forma = formasValidas[random.nextInt(formasValidas.length)];
                        pagamentoVendaRepository.save(new PagamentoVenda(null, venda, forma, BigDecimal.ZERO, 1));
                    }

                    // Salta para o próximo dia
                    dataCorrente = dataCorrente.plusDays(1);
                }
            }
        }
    }
}