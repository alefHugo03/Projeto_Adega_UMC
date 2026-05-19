package api.servico.adega.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController; // Este import funcionará após a correção do pom.xml
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para gerenciar a renderização das visualizações (HTML/Thymeleaf).
 * Diferente dos @RestController, este retorna o nome do template.
 */
@Controller
@RequestMapping("/")
public class ViewController implements ErrorController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Procura por src/main/resources/templates/login.html
    }

    @GetMapping("/home")
    public String home() {
        return "home"; // Procura por src/main/resources/templates/home.html
    }

    @GetMapping("/produtos")
    public String produtos() {
        return "produtos"; // Procura por src/main/resources/templates/produtos.html
    }

    @GetMapping("/estoque")
    public String estoque() {
        return "estoque"; // Procura por src/main/resources/templates/estoque.html
    }

    @GetMapping("/vendas")
    public String vendas() {
        return "vendas"; // Procura por src/main/resources/templates/vendas.html
    }
    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios"; // Procura por src/main/resources/templates/usuarios.html
    }

    @GetMapping("/error/404")
    public String error404() {
        return "error/404";
    }

    /**
     * Captura todos os erros da aplicação.
     * Se o status for 404, redireciona o navegador para a URL /error/404.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404"; // Retorna o template diretamente
            }
        }
        return "error"; 
    }
}
