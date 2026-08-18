package br.com.foodapi.controller;

import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.api.UsersApi;
import br.com.foodapi.generated.model.*;
import br.com.foodapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZoneOffset;
import java.util.List;

import static br.com.foodapi.controller.AbstractController.CONSTANT_PATH;

@AllArgsConstructor
@RequestMapping(CONSTANT_PATH)
@RestController
public class UserController extends AbstractController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> alterarSenhaUsuario(Long userId, AlteracaoSenhaRequest alteracaoSenhaRequest) {
        userService.updateUserPassword(userId, alteracaoSenhaRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UsuarioResponse> atualizarUsuario(Long userId, UsuarioAtualizacaoRequest usuarioAtualizacaoRequest) {
        Usuario user = userService.updateUser(userId, usuarioAtualizacaoRequest);

        return ResponseEntity.ok(
                new UsuarioResponse(
                        user.getId(),
                        user.getNome(),
                        user.getEmail(),
                        user.getLogin(),
                        TipoUsuario.valueOf(user.getTipoUsuario().name()),
                        user.getDataCadastro().atOffset(ZoneOffset.UTC),
                        user.getDataAlteracao().atOffset(ZoneOffset.UTC)
                )
        );
    }

    @Override
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(UsuarioCadastroRequest usuarioCadastroRequest) {
        Usuario user = this.userService.createUser(usuarioCadastroRequest);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(uri).body(
                new UsuarioResponse(
                        user.getId(),
                        user.getNome(),
                        user.getEmail(),
                        user.getLogin(),
                        TipoUsuario.valueOf(user.getTipoUsuario().name()),
                        user.getDataCadastro().atOffset(ZoneOffset.UTC),
                        null
                )
        );
    }

    @Override
    public ResponseEntity<Void> excluirUsuario(Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UsuarioResponse>> procurarUsuario(String nome) {

        List<Usuario> users = userService.findByName(nome);

        List<UsuarioResponse> response = users.stream()
                .map(user -> new UsuarioResponse(
                        user.getId(),
                        user.getNome(),
                        user.getEmail(),
                        user.getLogin(),
                        TipoUsuario.valueOf(user.getTipoUsuario().name()),
                        user.getDataCadastro().atOffset(ZoneOffset.UTC),
                        user.getDataAlteracao().atOffset(ZoneOffset.UTC)
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UsuarioResponse> procurarUsuarioPorId(Long userId) {
        Usuario user = userService.findById(userId);

        return ResponseEntity.ok(UsuarioResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nome(user.getNome())
                .login(user.getLogin())
                .tipoUsuario(TipoUsuario.valueOf(user.getTipoUsuario().name()))
                .dataCadastro(user.getDataCadastro().atOffset(ZoneOffset.UTC))
                .build());
    }
}
