package api.servico.adega.config.dataLoader;

import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Responsável por criar os usuários iniciais do sistema.
 */
@Component
@Order(1)
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@admin.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            usuarioRepository.save(admin);

            Usuario vendedor = new Usuario();
            vendedor.setNome("Vendedor Teste");
            vendedor.setEmail("vendedor@vendedor.com");
            vendedor.setSenha(passwordEncoder.encode("venda123"));
            vendedor.setActive(true);
            usuarioRepository.save(vendedor);

            System.out.println(">>> UsuarioDataLoader: Usuários admin@admin.com e vendedor@vendedor.com criados.");
        }
    }
}