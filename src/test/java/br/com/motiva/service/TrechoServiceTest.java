package br.com.motiva.service;

import br.com.motiva.dto.TrechoRequestDTO;
import br.com.motiva.exception.BusinessException;
import br.com.motiva.model.Trecho;
import br.com.motiva.repository.TrechoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrechoServiceTest {

    @Mock
    private TrechoRepository trechoRepository;

    @InjectMocks
    private TrechoService trechoService;

    @Test
    @DisplayName("Regra Sprint 1: Nao deve permitir KM final menor ou igual ao KM inicial")
    void testValidacaoKmFinalInvalido() {
        TrechoRequestDTO dto = new TrechoRequestDTO("BR-116", 50, 40, 10.0, "UMIDO");

        BusinessException ex = assertThrows(BusinessException.class, () -> trechoService.criar(dto));
        assertTrue(ex.getMessage().contains("KM final deve ser estritamente maior"));
    }

    @Test
    @DisplayName("Regra Sprint 1: Nao deve permitir nivel de vegetacao negativo")
    void testValidacaoNivelVegetacaoNegativo() {
        TrechoRequestDTO dto = new TrechoRequestDTO("BR-116", 10, 20, -5.0, "UMIDO");

        BusinessException ex = assertThrows(BusinessException.class, () -> trechoService.criar(dto));
        assertTrue(ex.getMessage().contains("nivel da vegetacao nao pode ser negativo"));
    }

    @Test
    @DisplayName("Regra Sprint 2: Trecho UMIDO cresce o dobro e implementa IoT")
    void testCrescimentoTrechoUmido() {
        Trecho trecho = new Trecho("BR-116", 10, 20, 10.0, "UMIDO");
        assertTrue(trecho.temSensorIoT());

        trecho.registrarCrescimento(5.0); // 10.0 + (5.0 * 2) = 20.0
        assertEquals(20.0, trecho.getNivelVegetacao());
        assertEquals(20.0, trecho.transmitirDadosSensor());
    }

    @Test
    @DisplayName("Regra Sprint 2: Trecho SECO cresce metade da taxa")
    void testCrescimentoTrechoSeco() {
        Trecho trecho = new Trecho("SP-070", 10, 20, 10.0, "SECO");
        assertFalse(trecho.temSensorIoT());

        trecho.registrarCrescimento(4.0); // 10.0 + (4.0 * 0.5) = 12.0
        assertEquals(12.0, trecho.getNivelVegetacao());
    }
}
