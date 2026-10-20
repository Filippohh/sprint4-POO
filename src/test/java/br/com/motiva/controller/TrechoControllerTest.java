package br.com.motiva.controller;

import br.com.motiva.model.Trecho;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TrechoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TrechoRepository trechoRepository;

    @Autowired
    private IntervencaoRepository intervencaoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        intervencaoRepository.deleteAll();
        trechoRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/trechos - Deve retornar 200 e lista de trechos")
    void testListarTodos() throws Exception {
        trechoRepository.save(new Trecho("BR-116", 10, 15, 35.5, "UMIDO"));

        mockMvc.perform(get("/api/trechos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rodovia", is("BR-116")))
                .andExpect(jsonPath("$[0].tipo", is("UMIDO")))
                .andExpect(jsonPath("$[0].prioridade", is("URGENTE")));
    }

    @Test
    @DisplayName("GET /api/trechos/{id} - Deve retornar 200 quando existir")
    void testBuscarPorIdSucesso() throws Exception {
        Trecho trecho = trechoRepository.save(new Trecho("SP-070", 50, 55, 12.0, "SECO"));

        mockMvc.perform(get("/api/trechos/" + trecho.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rodovia", is("SP-070")))
                .andExpect(jsonPath("$.prioridade", is("ATENCAO")));
    }

    @Test
    @DisplayName("GET /api/trechos/{id} - Deve retornar 404 quando nao existir")
    void testBuscarPorIdNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/trechos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Recurso Nao Encontrado")));
    }

    @Test
    @DisplayName("POST /api/trechos - Deve retornar 201 Created quando dados forem validos")
    void testCriarTrechoSucesso() throws Exception {
        String json = """
            {
                "rodovia": "SP-330",
                "kmInicial": 100,
                "kmFinal": 120,
                "nivelVegetacao": 25.0,
                "tipo": "UMIDO"
            }
            """;

        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.rodovia", is("SP-330")))
                .andExpect(jsonPath("$.prioridade", is("CRITICO")));
    }

    @Test
    @DisplayName("POST /api/trechos - Deve retornar 400 Bad Request quando KM final for menor ou igual ao inicial")
    void testCriarTrechoKmInvalido() throws Exception {
        String json = """
            {
                "rodovia": "SP-330",
                "kmInicial": 100,
                "kmFinal": 50,
                "nivelVegetacao": 15.0,
                "tipo": "SECO"
            }
            """;

        mockMvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("KM final deve ser estritamente maior")));
    }

    @Test
    @DisplayName("PUT /api/trechos/{id} - Deve retornar 200 OK quando atualizar com sucesso")
    void testAtualizarTrechoSucesso() throws Exception {
        Trecho salvo = trechoRepository.save(new Trecho("BR-116", 10, 20, 15.0, "SECO"));

        String json = """
            {
                "rodovia": "BR-116",
                "kmInicial": 10,
                "kmFinal": 20,
                "nivelVegetacao": 8.0,
                "tipo": "SECO"
            }
            """;

        mockMvc.perform(put("/api/trechos/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelVegetacao", is(8.0)))
                .andExpect(jsonPath("$.prioridade", is("NORMAL")));
    }

    @Test
    @DisplayName("DELETE /api/trechos/{id} - Deve retornar 204 No Content quando remover")
    void testRemoverTrechoSucesso() throws Exception {
        Trecho salvo = trechoRepository.save(new Trecho("BR-101", 1, 10, 5.0, "SECO"));

        mockMvc.perform(delete("/api/trechos/" + salvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/trechos/" + salvo.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/trechos/tipo/{tipo} - Deve filtrar por tipo UMIDO")
    void testBuscarPorTipo() throws Exception {
        trechoRepository.save(new Trecho("BR-116", 10, 15, 35.0, "UMIDO"));
        trechoRepository.save(new Trecho("SP-070", 20, 30, 12.0, "SECO"));

        mockMvc.perform(get("/api/trechos/tipo/UMIDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo", is("UMIDO")));
    }
}
