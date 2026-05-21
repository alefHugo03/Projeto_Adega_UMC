package api.servico.adega.dto.responses;

import lombok.Data;

/*
    Criando a Resposta do Login(O JWT)
*/
@Data
public class LoginResponseDTO {
    private String token;

    private String tipo;

    public LoginResponseDTO(String token, String tipo) {
        this.token = token;
        this.tipo = tipo;
    }
}
