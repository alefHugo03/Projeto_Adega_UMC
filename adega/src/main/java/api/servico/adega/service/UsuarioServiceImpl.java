package api.servico.adega.service;

import api.servico.adega.dto.UsuarioRequestDTO;
import api.servico.adega.dto.UsuarioResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Usuario;
import api.servico.adega.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do service de usuário.
 *
 * Aqui ficam as regras de negócio e a conversão entre a entidade Usuario
 * e os objetos de transferência de dados (DTOs).
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Retorna todos os usuários cadastrados transformando cada entidade em DTO.
     */
    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário por ID e lança exceção caso não exista.
     */
    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return toResponseDTO(usuario);
    }

    /**
     * Cria um novo usuário usando os dados do request DTO.
     */
    @Override
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = toEntity(usuarioRequestDTO);
        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    /**
     * Atualiza um usuário existente com os dados recebidos.
     */
    @Override
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuarioExistente.setNome(usuarioRequestDTO.getNome());
        usuarioExistente.setEmail(usuarioRequestDTO.getEmail());
        Usuario atualizado = usuarioRepository.save(usuarioExistente);
        return toResponseDTO(atualizado);
    }

    /**
     * Exclui um usuário existente. Se o usuário não for encontrado, lança exceção.
     */
    @Override
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuarioRepository.delete(usuario);
    }

    /**
     * Busca usuários cujo nome contenha a string informada, sem diferenciar maiúsculas/minúsculas.
     */
    @Override
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário pelo email e retorna um DTO.
     */
    @Override
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
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
        return usuario;
    }
}
