package api.servico.adega.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    Criando a Resposta do Usuário
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Essencial para o 'new UsuarioResponseDTO(...)' no seu Service
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private boolean isActive;
}
