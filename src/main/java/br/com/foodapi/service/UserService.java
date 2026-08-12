package br.com.foodapi.service;

import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.repository.UserRepository;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Getter
public class UserService {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario createUser(UsuarioCadastroRequest data) throws UserAlreadyExistsException {
        if (repository.findByEmail(data.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        if (repository.findByLogin(data.getLogin()).isPresent()) {
            throw new UserAlreadyExistsException("Username is not unavailable");
        }

        Usuario user = new Usuario(data, this.passwordEncoder.encode(data.getSenha()));
        return this.repository.save(user);
    }
    public List<Usuario> findByName(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

}
