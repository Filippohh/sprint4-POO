package br.com.motiva.controller;

import br.com.motiva.model.Trecho;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RelatorioControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TrechoRepository trechoRepository;

    @Autowired
    private IntervencaoRepository intervencaoRepository;

    @Autowired
    private RelatorioPrioridadeRepository relatorioPrioridadeRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        intervencaoRepository.deleteAll();
        relatorioPrioridadeRepository.deleteAll();
        trechoRepository.deleteAll();

        // Cria trechos nas quatro faixas de prioridade
        trechoRepository.save(new Trecho("BR-116", 10, 15, 35.5, "UMIDO")); // URGENTE (>= 30)
        trechoRepository.save(new Trecho("BR-116", 20, 25, 25.0, "UMIDO")); // CRITICO (>= 20)
        trechoRepository.save(new Trecho("SP-070", 50, 55, 12.0, "SECO"));  // ATENCAO (>= 10)
        trechoRepository.save(new Trecho("SP-070", 60, 68, 5.0, "SECO"));   // NORMAL (< 10)
    }

    @Test
    @DisplayName("POST /api/relatorios - Deve executar o motor de prioridade e salvar relatorio no historico")
    void testGerarRelatorioSucesso() throws Exception {
        mockMvc.perform(post("/api/relatorios"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.qtUrgente", is(1)))
                .andExpect(jsonPath("$.qtCritico", is(1)))
                .andExpect(jsonPath("$.qtAtencao", is(1)))
                .andExpect(jsonPath("$.qtNormal", is(1)))
                .andExpect(jsonPath("$.totalTrechos", is(4)))
                .andExpect(jsonPath("$.totalAcaoImediata", is(2)))
                .andExpect(jsonPath("$.itens", hasSize(4)));
    }

    @Test
    @DisplayName("GET /api/relatorios - Deve listar historico de relatorios gerados")
    void testListarHistorico() throws Exception {
        mockMvc.perform(post("/api/relatorios")).andExpect(status().isCreated());

        mockMvc.perform(get("/api/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].totalTrechos", is(4)));
    }

    @Test
    @DisplayName("GET /api/relatorios/periodo - Deve consultar historico por periodo de datas")
    void testBuscarPorPeriodo() throws Exception {
        mockMvc.perform(post("/api/relatorios")).andExpect(status().isCreated());

        String hoje = LocalDate.now().toString();

        mockMvc.perform(get("/api/relatorios/periodo")
                        .param("inicio", hoje)
                        .param("fim", hoje))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
