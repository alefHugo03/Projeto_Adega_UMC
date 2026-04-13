package api.servico.adega.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO usado para retornar dados do usuário na resposta da API.
 *
 * Contém apenas os campos que devem ser expostos ao cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
}
