package br.com.foodapi.dto;

import jakarta.validation.constraints.NotBlank;

public record TestRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome

) {
}
