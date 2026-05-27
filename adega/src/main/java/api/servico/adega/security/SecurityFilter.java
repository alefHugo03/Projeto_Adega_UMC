package api.servico.adega.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
   Criação dos tokens de acesso e regras para que eles funcionem
   e salvem nos cookies
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository repository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        
        // 1. Recursos estáticos continuam sendo ignorados completamente (ganho de performance)
        boolean isRecursoEstatico = uri.startsWith("/css/") || 
                                    uri.startsWith("/js/")  || 
                                    uri.startsWith("/img/") || 
                                    uri.equals("/favicon.ico");

        if (isRecursoEstatico) {
            filterChain.doFilter(request, response);
            return;
        }

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                
                if (subject != null) {
                    var usuario = repository.findByEmail(subject).orElse(null);

                    if (usuario != null) {
                        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 💡 A CHAVE AQUI: Autenticamos o usuário para o "/error" e "/login", 
                        // mas NÃO geramos um novo cookie para essas rotas de controle.
                        boolean isRotaDeControle = uri.equals("/error") || uri.equals("/login");

                        if (!isRotaDeControle) {
                            String novoToken = tokenService.getSubject(tokenJWT); // ou tokenService.gerarToken(usuario);
                            Cookie cookie = new Cookie("jwt_token", tokenService.gerarToken(usuario));
                            cookie.setPath("/");
                            cookie.setHttpOnly(true); 
                            cookie.setSecure(true);   
                            cookie.setMaxAge(600);  
                            response.addCookie(cookie);
                        }
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        // 1. Tenta recuperar do cabeçalho (para o query.js)
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        // 2. Tenta recuperar do Cookie (para navegação entre páginas)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
    /**
     * IMPORTANTE: Permite que o filtro seja executado durante os despachos internos 
     * de erro do Spring (como o forward para /error). 
     * Isso impede que o contexto de segurança seja apagado antes de o ViewController 
     * decidir qual página HTML de erro mostrar.
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}