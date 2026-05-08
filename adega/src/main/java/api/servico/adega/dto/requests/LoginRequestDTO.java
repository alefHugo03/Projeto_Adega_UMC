package api.servico.adega.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "O e-mail e a senha são obrigatórios")
    @Email(message = "E-mail inválido")
    private String email;
    
    @NotBlank(message = "O e-mail e a senha são obrigatórios")
    private String senha;

    public LoginRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
