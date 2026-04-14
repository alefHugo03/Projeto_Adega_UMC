package api.servico.adega.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario") // Nome da tabela conforme aparece nos seus logs do Hibernate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;
}
