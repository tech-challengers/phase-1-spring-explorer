package br.com.foodapi.domain.usuario;

public record CreatedUserDTO(String email, String login, String nome) {
    public CreatedUserDTO(Usuario usuario) {
        this(usuario.getEmail(), usuario.getLogin(), usuario.getNome());
    }
}
