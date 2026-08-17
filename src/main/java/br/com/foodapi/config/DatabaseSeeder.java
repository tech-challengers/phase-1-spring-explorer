package br.com.foodapi.config;

import br.com.foodapi.domain.model.Cliente;
import br.com.foodapi.domain.model.DonoRestaurante;
import br.com.foodapi.domain.model.TipoUsuario;
import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByLogin("fulano")) {
            Usuario fulano = new DonoRestaurante();

            fulano.setEmail("fulano@email.com");
            fulano.setLogin("fulano");
            fulano.setNome("Fulano");
            fulano.setSenha(passwordEncoder.encode("12345678"));
            fulano.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);

            userRepository.save(fulano);
        }

        if (!userRepository.existsByLogin("siclano")) {
            Usuario siclano = new Cliente();

            siclano.setEmail("siclano@email.com");
            siclano.setLogin("siclano");
            siclano.setNome("Siclano");
            siclano.setSenha(passwordEncoder.encode("12345678"));
            siclano.setTipoUsuario(TipoUsuario.CLIENTE);

            userRepository.save(siclano);
        }
    }
}
