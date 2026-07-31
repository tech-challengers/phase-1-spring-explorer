package br.com.foodapi.service;

import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.model.TipoUsuario;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import br.com.foodapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Usuario user;

    private UsuarioCadastroRequest userDTO;

    @BeforeEach
    void setup() {
        this.userDTO = new UsuarioCadastroRequest(
                "John Doe",
                "johndoe@email.com",
                "johndoe",
                "SomePasswordValid!@#",
                TipoUsuario.CLIENTE
        );


        this.user = new Usuario(this.userDTO, "encoded-password");
    }

    @Test
    void shouldCreateUserWithValidEmailAndLogin() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(null);
        when(repository.findByLogin("johndoe")).thenReturn(null);
        when(passwordEncoder.encode("SomePasswordValid!@#")).thenReturn("encoded-password");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario user = userService.createUser(this.userDTO);

        assertEquals("John Doe", user.getNome());
        assertEquals("johndoe@email.com", user.getEmail());
        assertEquals("johndoe", user.getLogin());
        assertEquals("encoded-password", user.getSenha());

        verify(repository).save(user);
        verify(passwordEncoder).encode("SomePasswordValid!@#");
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(this.user);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(this.userDTO)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldNotCreateUserWithInvalidLogin() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(null);
        when(repository.findByLogin("johndoe")).thenReturn(this.user);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(this.userDTO)
        );

        verify(repository, never()).save(any());
    }
}
