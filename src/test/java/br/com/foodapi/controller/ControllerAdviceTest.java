package br.com.foodapi.controller;

import br.com.foodapi.domain.model.TipoUsuario;
import br.com.foodapi.domain.model.Usuario;
import br.com.foodapi.generated.model.AlteracaoSenhaRequest;
import br.com.foodapi.infra.errors.InvalidPasswordException;
import br.com.foodapi.infra.errors.UserNotFoundException;
import br.com.foodapi.service.CustomUserDetailsService;
import br.com.foodapi.service.JwtService;
import br.com.foodapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TestController.class, UserController.class})
@Import(ControllerAdvice.class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserService userService;

    @Test
    void deveRetornarProblemQuandoUsuarioJaExiste() throws Exception {

        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Usuário já cadastrado"))
                .andExpect(jsonPath("$.instance").value("/exception"));
    }

    @Test
    void deveRetornarProblemQuandoRequestForInvalido() throws Exception {

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome":""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("nome: Nome é obrigatório"))
                .andExpect(jsonPath("$.instance")
                        .value("/test/validation"));
    }

    @Test
    void deveRetornarUmUsuarioQuandoProcurarPorId() throws Exception {

        when(userService.findById(1L)).thenReturn(Usuario.builder()
                .id(1L)
                .email("fulano@email.com")
                .nome("Fulano")
                .dataCadastro(LocalDateTime.now())
                .dataAlteracao(null)
                .login("fulano")
                .tipoUsuario(TipoUsuario.DONO_RESTAURANTE)
                .build());

        mockMvc.perform(get("/api/v1/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fulano"))
                .andExpect(jsonPath("$.email").value("fulano@email.com"))
                .andExpect(jsonPath("$.login").value("fulano"))
                .andExpect(jsonPath("$.tipoUsuario").value("DONO_RESTAURANTE"));
    }

    @Test
    void deveRetornarErroQuandProcurarPorIdENaoEncontrar() throws Exception {

        when(userService.findById(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("User not found"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User not found"))
                .andExpect(jsonPath("$.instance").value("/exception"));
    }

    @Test
    void deveRetornarBadRequestQuandoSenhaAtualForInvalida() throws Exception {
        doThrow(new InvalidPasswordException("Current password is invalid"))
                .when(userService)
                .updateUserPassword(anyLong(), any(AlteracaoSenhaRequest.class));

        mockMvc.perform(patch("/api/v1/users/{userId}/senha", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "senhaAtual":"senha-incorreta",
                                    "novaSenha":"NovaSenha@123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Current password is invalid"));
    }

    @Test
    void deveRetornarBadRequestQuandoNovaSenhaForIgualAAtual() throws Exception {
        doThrow(new InvalidPasswordException("New password must be different from current password"))
                .when(userService)
                .updateUserPassword(anyLong(), any(AlteracaoSenhaRequest.class));

        mockMvc.perform(patch("/api/v1/users/{userId}/senha", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "senhaAtual":"SenhaAtual@123",
                                    "novaSenha":"SenhaAtual@123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("New password must be different from current password"));    }
    @Test
    void deveRetornarBadRequestQuandoEmailForInvalido() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nome": "Fulano",
                                "email": "email-invalido",
                                "login": "fulano",
                                "senha": "Senha@123",
                                "tipoUsuario": "CLIENTE"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }
    @Test
    void deveRetornarBadRequestQuandoSenhaNaoTiverMaiuscula() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nome": "Fulano",
                                "email": "fulano@email.com",
                                "login": "fulano",
                                "senha": "senha@123",
                                "tipoUsuario": "CLIENTE"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }
    @Test
    void deveRetornarBadRequestQuandoNovaSenhaForFraca() throws Exception {

        mockMvc.perform(patch("/api/v1/users/{userId}/senha", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "senhaAtual": "12345678",
                                "novaSenha": "senha123"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));

        verify(userService, never())
                .updateUserPassword(
                        anyLong(),
                        any(AlteracaoSenhaRequest.class)
                );
    }
    @Test
    void deveRetornarBadRequestQuandoSenhaNaoTiverNumero() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "nome": "Fulano",
                            "email": "fulano@email.com",
                            "login": "fulano",
                            "senha": "Senha@Teste",
                            "tipoUsuario": "CLIENTE"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }
    @Test
    void deveRetornarBadRequestQuandoSenhaNaoTiverCaractereEspecial() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "nome": "Fulano",
                            "email": "fulano@email.com",
                            "login": "fulano",
                            "senha": "Senha123",
                            "tipoUsuario": "CLIENTE"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }


}
