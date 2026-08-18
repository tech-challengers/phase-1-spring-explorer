package br.com.foodapi.domain.factory;

import br.com.foodapi.domain.model.Cliente;
import br.com.foodapi.domain.model.DonoRestaurante;
import br.com.foodapi.domain.model.TipoUsuario;
import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;

public final class UserFactory {

    private UserFactory() {
    }

    public static Usuario create(UsuarioCadastroRequest data, String hashedPassword) {
        TipoUsuario tipoUsuario = TipoUsuario.valueOf(data.getTipoUsuario().name());

        Usuario usuario = switch (tipoUsuario) {
            case CLIENTE -> new Cliente();
            case DONO_RESTAURANTE -> new DonoRestaurante();
        };

        usuario.setEmail(data.getEmail());
        usuario.setLogin(data.getLogin());
        usuario.setNome(data.getNome());
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setSenha(hashedPassword);

        return usuario;
    }
}
