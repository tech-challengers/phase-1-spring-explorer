package br.com.foodapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import(ControllerAdvice.class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarProblemQuandoUsuarioJaExiste() throws Exception {

        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Usuário já cadastrado"))
                .andExpect(jsonPath("$.instance").value("/test/exception"));
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

}