package api.servico.adega.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import api.servico.adega.model.Usuario;
import api.servico.adega.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret:minhasenhamuitosecreta123}")
    private String secret;

    @PostConstruct
    public void validarConfiguracao() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set api.security.token.secret or API_SECURITY_TOKEN_SECRET with a non-empty value.");
        }
    }

    @Override
    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
            return JWT.create()
                    .withIssuer("Minha_API")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId()) // Adiciona o ID para identificação automática nas vendas
                    .withClaim("role", usuario.getRole()) // Adiciona a role no payload do JWT
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    @Override
    public String validarToken(String token) {
        try {
            var algoritmo = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
            return JWT.require(algoritmo)
                    .withIssuer("Minha_API")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    @Override
    public String getSubject(String token) {
        return validarToken(token);
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
