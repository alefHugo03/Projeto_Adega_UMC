package api.servico.adega.config;

import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente que carrega dois usuários modelos no banco de dados
 * quando a aplicação é inicializada pela primeira vez.
 */
@Component
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario usuario1 = new Usuario();
            usuario1.setNome("Maria Silva");
            usuario1.setEmail("maria.silva@adega.com.br");

            Usuario usuario2 = new Usuario();
            usuario2.setNome("João Pereira");
            usuario2.setEmail("joao.pereira@adega.com.br");

            usuarioRepository.save(usuario1);
            usuarioRepository.save(usuario2);
        }
    }
}
