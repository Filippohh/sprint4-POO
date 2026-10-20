package br.com.motiva.dto;

import br.com.motiva.model.Trecho;
import java.time.LocalDate;

/**
 * DTO que encapsula a resposta de trechos da API REST.
 */
public record TrechoResponseDTO(
        Long id,
        String rodovia,
        Integer kmInicial,
        Integer kmFinal,
        Double nivelVegetacao,
        String tipo,
        LocalDate ultimaLeitura,
        boolean temSensorIoT,
        String identificacao,
        String prioridade,
        String acaoRecomendada
) {
    public static TrechoResponseDTO aPartirDe(Trecho trecho, String prioridade, String acaoRecomendada) {
        return new TrechoResponseDTO(
                trecho.getId(),
                trecho.getRodovia(),
                trecho.getKmInicial(),
                trecho.getKmFinal(),
                trecho.getNivelVegetacao(),
                trecho.getTipo(),
                trecho.getUltimaLeitura(),
                trecho.temSensorIoT(),
                trecho.identificacao(),
                prioridade,
                acaoRecomendada
        );
    }
}
