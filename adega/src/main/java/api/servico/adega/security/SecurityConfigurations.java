package api.servico.adega.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigurations {

    // 💡 Correção do Erro 1: A variável final exige este construtor exato abaixo
    private final SecurityFilter securityFilter;

    public SecurityConfigurations(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                // Força o reconhecimento do canal seguro (HTTPS)
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    // Liberação das rotas estáticas e páginas de login/erro
                    req.requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/error", "/error/404").permitAll();
                    
                    req.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        // Trata rotas web inexistentes redirecionando limpo para o login
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                org.springframework.security.web.util.matcher.AnyRequestMatcher.INSTANCE
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .deleteCookies("jwt_token")
                        .logoutSuccessUrl("/login")
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 💡 Correção dos Erros 2, 3 e 4: O tipo "AuthenticationManager" precisa estar explícito aqui
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}