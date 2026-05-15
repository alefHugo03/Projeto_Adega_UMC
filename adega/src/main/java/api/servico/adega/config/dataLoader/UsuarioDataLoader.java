package api.servico.adega.config.dataLoader;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;

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
            admin.setRole("ROLE_ADMIN"); // Define explicitamente como Administrador
            admin.setActive(true);
            usuarioRepository.save(admin);

            // Criando os vendedores para a competição
            Usuario u1 = new Usuario();
            u1.setNome("Alef");
            u1.setEmail("alef@adego.com");
            u1.setSenha(passwordEncoder.encode("alef123"));
            u1.setActive(true);

            Usuario u2 = new Usuario();
            u2.setNome("Richard");
            u2.setEmail("richard@adego.com");
            u2.setSenha(passwordEncoder.encode("richard123"));
            u2.setActive(true);

            Usuario u3 = new Usuario();
            u3.setNome("Vitor");
            u3.setEmail("vitor@adego.com");
            u3.setSenha(passwordEncoder.encode("vitor123"));
            u3.setActive(true);

            Usuario u4 = new Usuario();
            u4.setNome("Arthur");
            u4.setEmail("arthur@adego.com");
            u4.setSenha(passwordEncoder.encode("arthur123"));
            u4.setActive(true);

            usuarioRepository.saveAll(List.of(u1, u2, u3, u4));

            System.out.println(">>> UsuarioDataLoader: Usuários Admin, Alef, Richard e Vitor criados.");
        }
    }
}