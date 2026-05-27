package api.servico.adega.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável por devolver as páginas HTML (Thymeleaf) para o navegador.
 * Usamos @Controller (e não @RestController) porque queremos retornar o NOME do arquivo HTML, 
 * e não um JSON com dados.
 */
@Controller
@RequestMapping("/")
public class ViewController implements ErrorController {

    /**
     * Mapeia a tela de Login.
     * Possui uma trava de UX (Experiência do Usuário): se o usuário já estiver logado 
     * e tentar acessar "localhost:8443/login" pela barra de endereços, o sistema 
     * empurra ele para a /home, evitando que ele faça login duas vezes.
     */
    @GetMapping("/login")
    public String login() {
        if (isUsuarioLogado()) {
            return "redirect:/home";
        }
        return "login"; // Procura o arquivo login.html
    }

    /**
     * Páginas normais do sistema.
     * Quando o usuário acessa essas rotas, o Spring Security primeiro verifica o JWT.
     * Se estiver tudo certo, este método é chamado e entrega o HTML correspondente.
     */
    @GetMapping("/home")
    public String home() { return "home"; }

    @GetMapping("/produtos")
    public String produtos() { return "produtos"; }

    @GetMapping("/estoque")
    public String estoque() { return "estoque"; }

    @GetMapping("/vendas")
    public String vendas() { return "vendas"; }

    @GetMapping("/usuarios")
    public String usuarios() { return "usuarios"; }

    /**
     * O Coração do Tratamento de Erros de Navegação.
     * Toda vez que o usuário digita uma URL que não existe (ex: /savfsa) ou acontece
     * uma falha grave, o servidor Tomcat redireciona a requisição internamente para "/error".
     * Este método intercepta essa requisição para decidirmos o que mostrar.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Pega o código do erro gerado pelo servidor (ex: 404, 500, 403)
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // Se o erro for 404 (Página não encontrada)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                
                // Se o usuário tem um token válido, ele está logado.
                // Retornamos a string "error/404" pura. Isso faz o Thymeleaf desenhar a tela
                // de erro SEM mudar a URL que o usuário digitou, quebrando loops infinitos.
                if (isUsuarioLogado()) {
                    return "error/404"; 
                }
                
                // Se o usuário não tem token, ele é um intruso ou a sessão expirou.
                // Mandamos ele para a tela de login.
                return "redirect:/login"; 
            }
        }
        
        // Se for qualquer outro erro (como o banco de dados cair = erro 500), 
        // mostra uma página genérica de erro.
        return "error"; 
    }

    /**
     * Função auxiliar de Segurança.
     * Ela olha para o "Cofre" temporário do Spring (SecurityContextHolder) e verifica
     * se a requisição atual possui um usuário autenticado e se esse usuário não é 
     * apenas um visitante anônimo padrão do framework.
     */
    private boolean isUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}