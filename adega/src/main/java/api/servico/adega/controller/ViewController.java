package api.servico.adega.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller para gerenciar a renderização das visualizações (HTML/Thymeleaf).
 * Diferente dos @RestController, este retorna o nome do template.
 */
@Controller
@RequestMapping("/")
public class ViewController {
    
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
}
