package api.servico.adega.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO usado para receber os dados de criação ou atualização de usuário.
 *
 * Esse objeto representa apenas os campos que o cliente envia na requisição.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    private String nome;
    private String email;
}
