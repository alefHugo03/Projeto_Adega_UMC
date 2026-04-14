package api.servico.adega.repository;

import api.servico.adega.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 *
 * Usa Spring Data para fornecer operações CRUD básicas e consultas
 * personalizadas a partir do nome dos métodos.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Procura um usuário pelo email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca usuários cujo nome contenha a string informada (case insensitive).
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * Verifica se já existe usuário com o email informado.
     */
    boolean existsByEmail(String email);
}
