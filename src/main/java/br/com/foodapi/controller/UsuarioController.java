package br.com.foodapi.controller;

import br.com.foodapi.domain.usuario.*;
import br.com.foodapi.service.CriarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("user")
public class UsuarioController {

    @Autowired
    private CriarUsuarioService criarUsuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<DetailedUserDTO> detailUser(@PathVariable Long id) throws RuntimeException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @PostMapping
    public ResponseEntity<CreatedUserDTO> create(@RequestBody @Valid CreateUserDTO data, UriComponentsBuilder uriBuilder) {
        Usuario user = this.criarUsuarioService.criarUsuario(data);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new CreatedUserDTO(user));
    }
}
