package api.servico.adega.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.UsuarioRequestDTO;
import api.servico.adega.dto.responses.UsuarioResponseDTO;
import api.servico.adega.exception.ConflictException;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.service.UsuarioService;

/**
 * Implementação do service de usuário.
 *
 * Aqui ficam as regras de negócio e a conversão entre a entidade Usuario
 * e os objetos de transferência de dados (DTOs).
 */
@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Substituímos a exceção genérica do Spring pela sua 'ResourceNotFoundException'.
        // Isso garante que o erro seja capturado pelo seu GlobalExceptionHandler, retornando um JSON padronizado.
        // O retorno do tipo 'Usuario' agora é aceito como 'UserDetails' porque implementamos a interface na Model.
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", username));
        return usuario;
    }

    /**
     * Retorna todos os usuários cadastrados transformando cada entidade em DTO.
     */
    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isActive)
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um usuário por ID e lança exceção caso não exista.
     */
    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        if (!usuario.isActive()) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        return toResponseDTO(usuario);
    }

    /**
     * Cria um novo usuário usando os dados do request DTO.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new ConflictException("E-mail já cadastrado no sistema.");
        }

        Usuario usuario = toEntity(usuarioRequestDTO);
        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        
        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    /**
     * Atualiza um usuário existente com os dados recebidos.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Impede a edição de um usuário desativado
        if (!usuarioExistente.isActive()) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }

        // Verifica se o e-mail mudou e se o novo já existe para outro usuário
        if (!usuarioExistente.getEmail().equals(usuarioRequestDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioRequestDTO.getEmail())) {
            throw new ConflictException("O novo e-mail já está em uso por outro usuário.");
        }

        usuarioExistente.setNome(usuarioRequestDTO.getNome());
        usuarioExistente.setEmail(usuarioRequestDTO.getEmail());
        Usuario atualizado = usuarioRepository.save(usuarioExistente);
        return toResponseDTO(atualizado);
    }

    /**
     * Desativa um usuário existente (Soft Delete).
     */
    @Override
    @Transactional
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActive(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Busca usuários cujo nome contenha a string informada, sem diferenciar maiúsculas/minúsculas.
     */
    @Override
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .filter(Usuario::isActive)
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um usuário pelo email e retorna um DTO.
     */
    @Override
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return toResponseDTO(usuario);
    }

    /**
     * Converte uma entidade Usuario para o DTO de resposta.
     */
    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    /**
     * Converte o DTO de criação para a entidade Usuario.
     */
    private Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setActive(true); // Garante que o usuário comece ativo para permitir o login
        return usuario;
    }
}
