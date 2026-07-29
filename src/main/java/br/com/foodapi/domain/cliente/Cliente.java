package br.com.foodapi.domain.cliente;


import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Usuario {

}