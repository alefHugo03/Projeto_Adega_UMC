package api.servico.adega.service;

/**
 * Interface para criação do Service de Autenticação
 */
public interface AuthService {

    String login(String email, String senha);
}
