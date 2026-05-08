package api.servico.adega.service;

import api.servico.adega.model.Usuario;
import org.springframework.stereotype.Service;


@Service
public interface TokenService {

   String gerarToken(Usuario usuario);

   String validarToken(String token);

}
