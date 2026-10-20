package br.com.motiva.controller;

import br.com.motiva.model.Equipe;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
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
class EquipeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private IntervencaoRepository intervencaoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        intervencaoRepository.deleteAll();
        equipeRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/equipes - Deve retornar 200 e lista de equipes")
    void testListarTodas() throws Exception {
        equipeRepository.save(new Equipe("Alfa-01", "MANHA", 6, "S"));

        mockMvc.perform(get("/api/equipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Alfa-01")))
                .andExpect(jsonPath("$[0].turno", is("MANHA")))
                .andExpect(jsonPath("$[0].statusDescricao", is("ATIVA")));
    }

    @Test
    @DisplayName("GET /api/equipes/{id} - Deve retornar 200 quando equipe existir")
    void testBuscarPorIdSucesso() throws Exception {
        Equipe salva = equipeRepository.save(new Equipe("Bravo-02", "TARDE", 4, "S"));

        mockMvc.perform(get("/api/equipes/" + salva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Bravo-02")));
    }

    @Test
    @DisplayName("POST /api/equipes - Deve retornar 201 Created quando dados forem validos")
    void testCriarEquipeSucesso() throws Exception {
        String json = """
            {
                "nome": "Charlie-03",
                "turno": "NOITE",
                "qtIntegrantes": 5,
                "ativa": "S"
            }
            """;

        mockMvc.perform(post("/api/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome", is("Charlie-03")));
    }

    @Test
    @DisplayName("POST /api/equipes - Deve retornar 400 Bad Request quando turno for invalido")
    void testCriarEquipeTurnoInvalido() throws Exception {
        String json = """
            {
                "nome": "Delta-04",
                "turno": "MADRUGADA",
                "qtIntegrantes": 4,
                "ativa": "S"
            }
            """;

        mockMvc.perform(post("/api/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.turno").exists());
    }

    @Test
    @DisplayName("DELETE /api/equipes/{id} - Deve retornar 204 No Content")
    void testRemoverEquipeSucesso() throws Exception {
        Equipe salva = equipeRepository.save(new Equipe("Eco-05", "MANHA", 3, "N"));

        mockMvc.perform(delete("/api/equipes/" + salva.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/equipes/" + salva.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/equipes/ativas - Deve filtrar apenas equipes ativas")
    void testBuscarAtivas() throws Exception {
        equipeRepository.save(new Equipe("Ativa-01", "MANHA", 5, "S"));
        equipeRepository.save(new Equipe("Inativa-02", "TARDE", 3, "N"));

        mockMvc.perform(get("/api/equipes/ativas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Ativa-01")));
    }
}
