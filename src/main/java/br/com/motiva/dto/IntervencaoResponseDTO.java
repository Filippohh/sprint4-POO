package br.com.motiva.dto;

import br.com.motiva.model.Intervencao;
import java.time.LocalDate;

/**
 * DTO que encapsula a resposta de intervencoes operacionais da API REST.
 */
public record IntervencaoResponseDTO(
        Long id,
        Long idTrecho,
        String identificacaoTrecho,
        Long idEquipe,
        String nomeEquipe,
        String tipo,
        LocalDate dataExecucao,
        Double nivelAntes,
        Double nivelDepois,
        Double reducaoObtida,
        Double custo
) {
    public static IntervencaoResponseDTO aPartirDe(Intervencao intervencao) {
        Long idEquipe = intervencao.getEquipe() != null ? intervencao.getEquipe().getId() : null;
        String nomeEquipe = intervencao.getEquipe() != null ? intervencao.getEquipe().getNome() : "Sem Equipe Designada";
        String idTrechoStr = intervencao.getTrecho() != null ? intervencao.getTrecho().identificacao() : "Trecho Desconhecido";
        Long idTrechoVal = intervencao.getTrecho() != null ? intervencao.getTrecho().getId() : null;

        return new IntervencaoResponseDTO(
                intervencao.getId(),
                idTrechoVal,
                idTrechoStr,
                idEquipe,
                nomeEquipe,
                intervencao.getTipo(),
                intervencao.getDataExecucao(),
                intervencao.getNivelAntes(),
                intervencao.getNivelDepois(),
                intervencao.reducaoObtida(),
                intervencao.getCusto()
        );
    }
}
