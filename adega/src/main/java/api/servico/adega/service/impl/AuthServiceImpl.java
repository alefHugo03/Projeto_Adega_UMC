package api.servico.adega.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import api.servico.adega.model.Usuario;
import api.servico.adega.service.AuthService;
import api.servico.adega.service.TokenService;

@Service
public class AuthServiceImpl implements AuthService {


        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private TokenService tokenService;

        @Override
        public String login(String email, String senha) {
            // Cria um token de autenticação do Spring
            var tokenAcesso = new UsernamePasswordAuthenticationToken(email, senha);

            // O Spring Security vai verificar se o email e a senha (hash) batem no banco
            var authentication = authenticationManager.authenticate(tokenAcesso);

            // Se passar, gera o nosso JWT
            var usuarioAutenticado = (Usuario) authentication.getPrincipal();
            return tokenService.gerarToken(usuarioAutenticado);
        }

}
