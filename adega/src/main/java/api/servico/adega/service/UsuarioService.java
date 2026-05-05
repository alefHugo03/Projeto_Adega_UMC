package api.servico.adega.service;

import api.servico.adega.dto.requests.UsuarioRequestDTO;
import api.servico.adega.dto.responses.UsuarioResponseDTO;

import java.util.List;

/**
 * Interface do serviço de usuário que define as operações disponíveis.
 *
 * O service é responsável por orquestrar a lógica de criação, leitura,
 * atualização e exclusão de usuários e por converter entre entidade e DTO.
 */
public interface UsuarioService {

    /**
     * Retorna todos os usuários cadastrados.
     */
    List<UsuarioResponseDTO> listarTodos();

    /**
     * Busca um usuário pelo ID.
     */
    UsuarioResponseDTO buscarPorId(Long id);

    /**
     * Cria um novo usuário a partir dos dados recebidos.
     */
    UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO);

    /**
     * Atualiza um usuário existente.
     */
    UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    /**
     * Exclui um usuário pelo ID.
     */
    void excluirUsuario(Long id);

    /**
     * Busca usuários cujo nome contenha o valor informado.
     */
    List<UsuarioResponseDTO> buscarPorNome(String nome);

    /**
     * Busca um usuário pelo email.
     */
    UsuarioResponseDTO buscarPorEmail(String email);
}
