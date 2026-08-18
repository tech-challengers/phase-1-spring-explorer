package br.com.foodapi.service;

import br.com.foodapi.domain.model.Cliente;
import br.com.foodapi.domain.model.DonoRestaurante;
import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.model.AlteracaoSenhaRequest;
import br.com.foodapi.generated.model.TipoUsuario;
import br.com.foodapi.generated.model.UsuarioCadastroRequest;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import br.com.foodapi.infra.errors.UserNotFoundException;
import br.com.foodapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
        this.user.setId(1L);
    }

    @Test
    void shouldCreateUserWithValidEmailAndLogin() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(Optional.empty());
        when(repository.findByLogin("johndoe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SomePasswordValid!@#")).thenReturn("encoded-password");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario user = userService.createUser(this.userDTO);

        assertEquals("John Doe", user.getNome());
        assertEquals("johndoe@email.com", user.getEmail());
        assertEquals("johndoe", user.getLogin());
        assertEquals("encoded-password", user.getSenha());
        assertInstanceOf(Cliente.class, user);

        verify(repository).save(user);
        verify(passwordEncoder).encode("SomePasswordValid!@#");
    }

    @Test
    void shouldCreateRestaurantOwnerWithValidEmailAndLogin() {
        UsuarioCadastroRequest restaurantOwnerDTO = new UsuarioCadastroRequest(
                "Jane Doe",
                "janedoe@email.com",
                "janedoe",
                "SomePasswordValid!@#",
                TipoUsuario.DONO_RESTAURANTE
        );

        when(repository.findByEmail("janedoe@email.com")).thenReturn(Optional.empty());
        when(repository.findByLogin("janedoe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SomePasswordValid!@#")).thenReturn("encoded-password");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario user = userService.createUser(restaurantOwnerDTO);

        assertEquals("Jane Doe", user.getNome());
        assertEquals("janedoe@email.com", user.getEmail());
        assertEquals("janedoe", user.getLogin());
        assertEquals("encoded-password", user.getSenha());
        assertInstanceOf(DonoRestaurante.class, user);

        verify(repository).save(user);
        verify(passwordEncoder).encode("SomePasswordValid!@#");
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(Optional.ofNullable(this.user));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(this.userDTO)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldNotCreateUserWithInvalidLogin() {
        when(repository.findByEmail("johndoe@email.com")).thenReturn(Optional.empty());
        when(repository.findByLogin("johndoe")).thenReturn(Optional.of(this.user));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(this.userDTO)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingUser() {
        Long userId = 1L;
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(repository).findById(userId);
        verify(repository).delete(user);
    }

    @Test
    void shouldNotDeleteNonExistingUser() {
        Long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(userId)
        );

        assertEquals("User not found", exception.getMessage());

        verify(repository).findById(userId);
        verify(repository, never()).delete(any(Usuario.class));
    }

    @Test
    void shouldNotUpdatePasswordFromUserNotFound() {
        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        AlteracaoSenhaRequest data = new AlteracaoSenhaRequest(this.userDTO.getSenha(), "NewPassword!@#2026");

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserPassword(user.getId(), data)
        );

        assertEquals("User not found", exception.getMessage());

        verify(repository).findById(user.getId());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldNotUpdateUserIncorrectPassword() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        AlteracaoSenhaRequest data = new AlteracaoSenhaRequest("INVALID_PASSWORD", "NewPassword!@#2026");

        when(passwordEncoder.matches(data.getSenhaAtual(), user.getSenha())).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.updateUserPassword(user.getId(), data)
        );

        assertEquals("Current password is invalid", exception.getMessage());

        verify(repository).findById(user.getId());
        verify(passwordEncoder).matches(data.getSenhaAtual(), user.getSenha());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldNotUpdatePasswordEqualsToPreviousOne() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        AlteracaoSenhaRequest data = new AlteracaoSenhaRequest(this.userDTO.getSenha(), this.userDTO.getSenha());

        when(passwordEncoder.matches(data.getSenhaAtual(), user.getSenha())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserPassword(user.getId(), data)
        );

        assertEquals("New password must be different from current password", exception.getMessage());

        verify(repository).findById(user.getId());
        verify(passwordEncoder, times(2)).matches(data.getSenhaAtual(), user.getSenha());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldBeAbleToUpdateUsersPassword() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        AlteracaoSenhaRequest data = new AlteracaoSenhaRequest(this.userDTO.getSenha(), "NewValidPassword!@#2026");

        when(passwordEncoder.matches(data.getSenhaAtual(), user.getSenha())).thenReturn(true);
        when(passwordEncoder.matches(data.getNovaSenha(), user.getSenha())).thenReturn(false);
        when(passwordEncoder.encode(data.getNovaSenha())).thenReturn("new-encoded-password");

        userService.updateUserPassword(user.getId(), data);

        assertEquals("new-encoded-password", user.getSenha());
        verify(repository).findById(user.getId());
        verify(passwordEncoder).matches(data.getSenhaAtual(), "encoded-password");
        verify(passwordEncoder).matches(data.getNovaSenha(), "encoded-password");
        verify(passwordEncoder).encode(data.getNovaSenha());
        verify(repository).save(user);
    }
}
