package br.com.motiva.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * DTO para criacao e atualizacao de intervencoes operacionais.
 */
public record IntervencaoRequestDTO(
        @NotNull(message = "O ID do trecho e obrigatorio")
        Long idTrecho,

        Long idEquipe,

        @NotBlank(message = "O tipo da intervencao e obrigatorio (ROCADA_MECANIZADA ou PULVERIZACAO)")
        @Pattern(regexp = "^(?i)(ROCADA_MECANIZADA|PULVERIZACAO)$",
                message = "Tipo de intervencao deve ser ROCADA_MECANIZADA ou PULVERIZACAO")
        String tipo,

        LocalDate dataExecucao,

        @NotNull(message = "O nivel de vegetacao antes da intervencao e obrigatorio")
        @Min(value = 0, message = "O nivel antes nao pode ser negativo")
        Double nivelAntes,

        @NotNull(message = "O nivel de vegetacao depois da intervencao e obrigatorio")
        @Min(value = 0, message = "O nivel depois nao pode ser negativo")
        Double nivelDepois,

        @NotNull(message = "O custo da intervencao e obrigatorio")
        @Min(value = 0, message = "O custo nao pode ser negativo")
        Double custo
) {
}
