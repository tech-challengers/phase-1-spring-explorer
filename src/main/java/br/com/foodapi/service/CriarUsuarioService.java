package br.com.foodapi.service;

import br.com.foodapi.domain.usuario.CreateUserDTO;
import br.com.foodapi.domain.usuario.Usuario;
import br.com.foodapi.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarUsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public Usuario criarUsuario(CreateUserDTO data) throws RuntimeException {
        Usuario alreadyHasUser = repository.findByEmail(data.email());
        if (alreadyHasUser != null) {
            throw new RuntimeException("O e-mail já está cadastrado a um usuário");
        }

        alreadyHasUser = repository.findByLogin(data.login());

        if (alreadyHasUser != null) {
            throw  new RuntimeException("O login já está cadastrado a um usuário");
        }

        Usuario user = new Usuario(data);

        this.repository.save(user);

        return user;
    }
}
