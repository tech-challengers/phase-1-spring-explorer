package br.com.foodapi.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    Usuario findByLogin(String login);
}
