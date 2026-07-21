package br.com.foodapi.domain.restaurante;

import br.com.foodapi.domain.usuario.TipoUsuario;
import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import static br.com.foodapi.domain.usuario.TipoUsuario.DONO_RESTAURANTE;

@Entity
@Table(name = "donos_restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class DonoRestaurante extends Usuario {

    private TipoUsuario tipoUsuario = DONO_RESTAURANTE;

}