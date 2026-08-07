package br.com.foodapi.service;

import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.repository.UserRepository;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario createUser(UsuarioCadastroRequest data) throws UserAlreadyExistsException {
        if (repository.findByEmail(data.getEmail()) != null) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        if (repository.findByLogin(data.getLogin()) != null) {
            throw new UserAlreadyExistsException("Username already in use");
        }

        Usuario user = new Usuario(data, this.passwordEncoder.encode(data.getSenha()));

        return this.repository.save(user);
    }
}
