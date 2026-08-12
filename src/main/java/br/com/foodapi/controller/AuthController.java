package br.com.foodapi.controller;

import br.com.foodapi.generated.api.AuthenticationApi;
import br.com.foodapi.generated.model.LoginRequest;
import br.com.foodapi.generated.model.LoginResponse;
import br.com.foodapi.repository.UserRepository;
import br.com.foodapi.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getLogin(),
                                loginRequest.getSenha()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token =
                jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(true, token)
        );
    }
}
