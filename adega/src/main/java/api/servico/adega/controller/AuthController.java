package api.servico.adega.controller;

import api.servico.adega.dto.requests.LoginRequestDTO;
import api.servico.adega.dto.responses.LoginResponseDTO;
import api.servico.adega.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dadosLogin) {

        // 1. Chama o service passando os dados extraídos do Request DTO
        String jwtToken = authService.login(dadosLogin.getEmail(), dadosLogin.getSenha());

        // 2. Monta o Response DTO
        LoginResponseDTO resposta = new LoginResponseDTO(jwtToken, "Bearer");

        // 3. Devolve para o usuário com o status 200 OK
        return ResponseEntity.ok(resposta);
    }
}