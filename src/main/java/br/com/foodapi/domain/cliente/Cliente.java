package br.com.foodapi.domain.cliente;


import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CLIENTE")
//@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class Cliente extends Usuario {

//    private TipoUsuario tipoUsuario = CLIENTE;
//
//    //@OneToOne(optional = false)
//    @JoinColumn(name = "usuario_id")
//    private Usuario usuario;

}