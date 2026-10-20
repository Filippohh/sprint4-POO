package br.com.motiva.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para criacao e atualizacao de equipes com validacoes declarativas.
 */
public record EquipeRequestDTO(
        @NotBlank(message = "O nome da equipe e obrigatorio")
        @Size(max = 60, message = "O nome da equipe deve ter no maximo 60 caracteres")
        String nome,

        @NotBlank(message = "O turno da equipe e obrigatorio (MANHA, TARDE ou NOITE)")
        @Pattern(regexp = "^(?i)(MANHA|TARDE|NOITE)$", message = "O turno deve ser MANHA, TARDE ou NOITE")
        String turno,

        @NotNull(message = "A quantidade de integrantes e obrigatoria")
        @Min(value = 1, message = "A equipe deve ter pelo menos 1 integrante")
        Integer qtIntegrantes,

        @Pattern(regexp = "^(?i)(S|N)$", message = "O status deve ser 'S' para ativa ou 'N' para inativa")
        String ativa
) {
}
