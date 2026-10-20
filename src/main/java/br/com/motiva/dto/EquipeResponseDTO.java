package br.com.motiva.dto;

import br.com.motiva.model.Equipe;

/**
 * DTO que encapsula a resposta de equipes da API REST.
 */
public record EquipeResponseDTO(
        Long id,
        String nome,
        String turno,
        Integer qtIntegrantes,
        String ativa,
        String statusDescricao
) {
    public static EquipeResponseDTO aPartirDe(Equipe equipe) {
        String statusDesc = "S".equalsIgnoreCase(equipe.getAtiva()) ? "ATIVA" : "INATIVA";
        return new EquipeResponseDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getTurno(),
                equipe.getQtIntegrantes(),
                equipe.getAtiva(),
                statusDesc
        );
    }
}
