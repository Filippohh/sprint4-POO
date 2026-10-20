package br.com.motiva.dto;

import br.com.motiva.model.Trecho;

/**
 * DTO que representa o detalhamento de cada trecho analisado pelo motor de prioridade.
 */
public record RelatorioItemDTO(
        Long idTrecho,
        String identificacao,
        Double nivelVegetacao,
        String tipo,
        boolean sensorIoT,
        String prioridade,
        String acaoRecomendada
) {
    public static RelatorioItemDTO criar(Trecho trecho, String prioridade, String acaoRecomendada) {
        return new RelatorioItemDTO(
                trecho.getId(),
                trecho.identificacao(),
                trecho.getNivelVegetacao(),
                trecho.getTipo(),
                trecho.temSensorIoT(),
                prioridade,
                acaoRecomendada
        );
    }
}
