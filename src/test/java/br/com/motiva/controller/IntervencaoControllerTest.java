package br.com.motiva.controller;

import br.com.motiva.model.Equipe;
import br.com.motiva.model.Intervencao;
import br.com.motiva.model.Trecho;
import br.com.motiva.repository.EquipeRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class IntervencaoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IntervencaoRepository intervencaoRepository;

    @Autowired
    private TrechoRepository trechoRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    private MockMvc mockMvc;
    private Trecho trecho;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        intervencaoRepository.deleteAll();
        trechoRepository.deleteAll();
        equipeRepository.deleteAll();

        trecho = trechoRepository.save(new Trecho("BR-116", 10, 15, 35.5, "UMIDO"));
        equipe = equipeRepository.save(new Equipe("Alfa-01", "MANHA", 6, "S"));
    }

    @Test
    @DisplayName("GET /api/intervencoes - Deve listar intervencoes cadastradas")
    void testListarTodas() throws Exception {
        intervencaoRepository.save(new Intervencao(trecho, equipe, "ROCADA_MECANIZADA", LocalDate.now(), 35.5, 2.0, 4800.0));

        mockMvc.perform(get("/api/intervencoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo", is("ROCADA_MECANIZADA")))
                .andExpect(jsonPath("$[0].reducaoObtida", is(33.5)));
    }

    @Test
    @DisplayName("POST /api/intervencoes - Deve criar intervencao e atualizar nivel de vegetacao do trecho")
    void testCriarIntervencaoSucesso() throws Exception {
        String json = String.format("""
            {
                "idTrecho": %d,
                "idEquipe": %d,
                "tipo": "PULVERIZACAO",
                "dataExecucao": "%s",
                "nivelAntes": 35.5,
                "nivelDepois": 15.0,
                "custo": 1800.0
            }
            """, trecho.getId(), equipe.getId(), LocalDate.now());

        mockMvc.perform(post("/api/intervencoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.tipo", is("PULVERIZACAO")))
                .andExpect(jsonPath("$.nivelDepois", is(15.0)));

        // Verifica se o trecho foi atualizado conforme regra de negocio da Sprint 2
        mockMvc.perform(get("/api/trechos/" + trecho.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelVegetacao", is(15.0)))
                .andExpect(jsonPath("$.prioridade", is("ATENCAO")));
    }
}
