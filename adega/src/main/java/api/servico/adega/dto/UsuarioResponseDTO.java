package api.servico.adega.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Essencial para o 'new UsuarioResponseDTO(...)' no seu Service
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
}
