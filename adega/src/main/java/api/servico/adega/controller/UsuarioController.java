package api.servico.adega.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UsuarioController {
    
    @GetMapping
    public String Bemvindo(){
        return "Bem vindo ao inicio";
    }
}
