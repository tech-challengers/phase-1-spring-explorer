package br.com.foodapi.service;

import br.com.foodapi.domain.factory.UserFactory;
import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.model.AlteracaoSenhaRequest;
import br.com.foodapi.generated.model.UsuarioAtualizacaoRequest;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import br.com.foodapi.infra.errors.InvalidPasswordException;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import br.com.foodapi.infra.errors.UserNotFoundException;
import br.com.foodapi.repository.UserRepository;
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
        this.findByEmail(data.getEmail());
        this.verifyLoginInUse(data.getLogin());

        Usuario user = UserFactory.create(data, this.passwordEncoder.encode(data.getSenha()));
        return this.repository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        repository.delete(usuario);
    }

    @Transactional
    public Usuario updateUser(Long id, UsuarioAtualizacaoRequest data) {

        Usuario user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        verifyLoginInUse(data.getLogin());
        findByEmail(data.getEmail());
        Usuario emailUser = repository.findByEmail(data.getEmail())
                .orElse(null);

        if (emailUser != null && !emailUser.getId().equals(id)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        Usuario loginUser = repository.findByLogin(data.getLogin())
                .orElse(null);

        if (loginUser != null && !loginUser.getId().equals(id)) {
            throw new UserAlreadyExistsException("Username already in use");
        }

        user.setNome(data.getNome());
        user.setEmail(data.getEmail());
        user.setLogin(data.getLogin());

        return repository.save(user);
    }

    @Transactional
    public void updateUserPassword(Long userId, AlteracaoSenhaRequest alteracaoSenhaRequest) {
        Usuario user = this.findById(userId);

        boolean isCurrentPasswordNotValid = !passwordEncoder
                .matches(alteracaoSenhaRequest.getSenhaAtual(), user.getSenha());

        if (isCurrentPasswordNotValid) {
            throw new InvalidPasswordException("Current password is invalid");
        }

        boolean isNewPasswordEqualsPrevious = passwordEncoder
                .matches(alteracaoSenhaRequest.getNovaSenha(), user.getSenha());

        if (isNewPasswordEqualsPrevious) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        user.setSenha(passwordEncoder.encode(alteracaoSenhaRequest.getNovaSenha()));

        this.repository.save(user);
    }

    private void verifyLoginInUse(String login) {
        if (repository.findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException("Username is not unavailable");
        }
    }

    private void findByEmail(String email) {
        if (repository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }
    }

    public List<Usuario> findByName(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public Usuario findById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
