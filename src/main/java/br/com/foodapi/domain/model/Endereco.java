package br.com.foodapi.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logradouro;

    private String numero;

    private String bairro;

    private String cidade;

    private String cep;

    private String complemento;

    private String estado;

    private String pais;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}