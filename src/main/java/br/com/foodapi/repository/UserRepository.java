package br.com.foodapi.repository;

import br.com.foodapi.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByLogin(String login);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    boolean existsByLogin(String login);
}