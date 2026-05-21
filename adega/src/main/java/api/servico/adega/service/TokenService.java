package api.servico.adega.service;

import api.servico.adega.model.Usuario;
import org.springframework.stereotype.Service;

/**
 * Interface para criação do Service dos Tokens
 */
@Service
public interface TokenService {

   String gerarToken(Usuario usuario);

   String validarToken(String token);

   String getSubject(String tokenJWT);

}
