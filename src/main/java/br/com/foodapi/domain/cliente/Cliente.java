package br.com.foodapi.domain.cliente;

import br.com.foodapi.domain.usuario.TipoUsuario;
import br.com.foodapi.domain.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import static br.com.foodapi.domain.usuario.TipoUsuario.CLIENTE;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class Cliente extends Usuario {

    private TipoUsuario tipoUsuario = CLIENTE;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}