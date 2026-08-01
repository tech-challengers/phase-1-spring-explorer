package br.com.foodapi.controller;

import br.com.foodapi.generated.api.UsersApi;
import br.com.foodapi.generated.model.*;
import br.com.foodapi.service.CreateUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController implements UsersApi {

    private final CreateUserService createUserService;

    @Override
    public ResponseEntity<Void> alterarSenhaUsuario(Long userId, AlteracaoSenhaRequest alteracaoSenhaRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<UsuarioResponse> atualizarUsuario(Long userId, UsuarioAtualizacaoRequest usuarioAtualizacaoRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(UsuarioCadastroRequest usuarioCadastroRequest) {
        var user = this.createUserService.createUser(usuarioCadastroRequest);

        var uri = URI.create("/users/" + user.getId());

        return ResponseEntity.created(uri).body(
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
    public ResponseEntity<Void> excluirUsuario(Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<List<UsuarioResponse>> procurarUsuario(String nome) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<UsuarioResponse> procurarUsuarioPorId(Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
