package br.com.foodapi.domain.restaurante;

import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DONO_RESTAURANTE")
@Getter
@Setter
@NoArgsConstructor

public class DonoRestaurante extends Usuario {



}