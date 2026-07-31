package br.com.foodapi.domain.usuario;

import br.com.foodapi.domain.endereco.Endereco;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime dataAlteracao;

    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Endereco> enderecos = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        this.dataCadastro = agora;
        this.dataAlteracao = agora;
    }

    @PreUpdate
    private void preUpdate() {
        this.dataAlteracao = LocalDateTime.now();
    }

    public Usuario(CreateUserDTO data) {
        this.email = data.email();
        this.login = data.login();
        this.nome = data.nome();

        // TODO: Não salvar a senha no banco. Implementacao temporaria
        this.senha = data.senha();
    }
}