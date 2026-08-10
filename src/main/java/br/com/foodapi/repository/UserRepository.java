package br.com.foodapi.repository;

import br.com.foodapi.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario findByLogin(String login);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}