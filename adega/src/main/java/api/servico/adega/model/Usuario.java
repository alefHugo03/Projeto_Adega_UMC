package api.servico.adega.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario") // Nome da tabela conforme aparece nos seus logs do Hibernate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Implementamos UserDetails para que o Spring Security reconheça esta classe como um objeto de usuário válido
// Isso resolve o erro de "Type mismatch" no Service e permite que o filtro acesse permissões do usuário.
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(name = "is_active")
    private boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna as permissões do usuário. No momento, todos os usuários recebem o perfil "ROLE_USER".
        // Isso resolve o erro "getAuthorities() is undefined" que ocorria no filtro de segurança.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Indica ao Spring Security que o campo 'senha' deve ser usado para validação de senha.
        return senha;
    }

    @Override
    public String getUsername() {
        // Indica ao Spring Security que o campo 'email' será o identificador único (username) no login.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Define se a conta expirou. Retornamos 'true' para indicar que a conta é sempre válida.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Define se a conta está bloqueada. Retornamos 'true' para indicar que está desbloqueada.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Define se as credenciais (senha) expiraram. Retornamos 'true' para evitar bloqueio por senha antiga.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Vincula a ativação do usuário no Spring Security ao campo 'isActive' do seu banco de dados.
        // Se isActive for false, o Spring Security impedirá o login automaticamente.
        return isActive;
    }
}