package api.servico.adega.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;

@Component
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o administrador padrão já existe para evitar duplicidade
        if (!usuarioRepository.existsByEmail("admin@admin.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@admin.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setActive(true); // Ativa o usuário para permitir o login
            
            usuarioRepository.save(admin);
        }
    }
}