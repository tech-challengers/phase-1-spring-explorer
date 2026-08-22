package br.com.foodapi.service;

import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario user = getRepository().findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getSenha(),
                List.of(new SimpleGrantedAuthority(user.getTipoUsuario().name())) //TODO needs fix rule implementation
        );
    }
}
