package br.com.foodapi.controller;

import br.com.foodapi.dto.TestRequest;
import br.com.foodapi.infra.errors.UserAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/exception")
    public void exception() {
        throw new UserAlreadyExistsException("Usuário já cadastrado");
    }

    @PostMapping("/validation")
    public void validation(@Valid @RequestBody TestRequest request) {
    }

}
