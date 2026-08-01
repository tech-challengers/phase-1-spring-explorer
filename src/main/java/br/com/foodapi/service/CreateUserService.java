package br.com.foodapi.service;

import br.com.foodapi.domain.usuario.Usuario;
import br.com.foodapi.domain.usuario.UserRepository;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CreateUserService {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario createUser(UsuarioCadastroRequest data) throws RuntimeException {
        Optional.ofNullable(repository.findByEmail(data.getEmail()))
                .map((user) -> repository.findByLogin(user.getLogin()))
                .orElseThrow(() -> new RuntimeException("User or email already in use"));

        Usuario user = new Usuario(data, this.passwordEncoder.encode(data.getSenha()));

        return this.repository.save(user);
    }
}
