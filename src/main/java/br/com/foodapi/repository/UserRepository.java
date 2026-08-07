package br.com.foodapi.repository;

import br.com.foodapi.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    Usuario findByLogin(String login);
}
