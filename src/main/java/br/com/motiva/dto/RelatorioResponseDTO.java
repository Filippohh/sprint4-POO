package br.com.motiva.dto;

import br.com.motiva.model.RelatorioPrioridade;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que encapsula a resposta de relatorios de prioridade da API REST.
 */
public record RelatorioResponseDTO(
        Long id,
        LocalDateTime dataGeracao,
        Integer qtUrgente,
        Integer qtCritico,
        Integer qtAtencao,
        Integer qtNormal,
        Integer totalTrechos,
        Integer totalAcaoImediata,
        String resumo,
        List<RelatorioItemDTO> itens
) {
    public static RelatorioResponseDTO aPartirDe(RelatorioPrioridade relatorio, List<RelatorioItemDTO> itens) {
        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getDataGeracao(),
                relatorio.getQtUrgente(),
                relatorio.getQtCritico(),
                relatorio.getQtAtencao(),
                relatorio.getQtNormal(),
                relatorio.totalTrechos(),
                relatorio.totalAcaoImediata(),
                relatorio.getResumo(),
                itens
        );
    }
}
