package br.com.foodapi.domain.restaurante;

import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DONO_RESTAURANTE")
//@Table(name = "donos_restaurante")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class DonoRestaurante extends Usuario {

    //private TipoUsuario tipoUsuario = DONO_RESTAURANTE;

}