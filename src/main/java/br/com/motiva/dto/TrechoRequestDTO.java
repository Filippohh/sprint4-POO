package br.com.motiva.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para criacao e atualizacao de trechos com validacoes declarativas (Bean Validation).
 */
public record TrechoRequestDTO(
        @NotBlank(message = "O nome da rodovia e obrigatorio")
        @Size(max = 20, message = "O nome da rodovia deve ter no maximo 20 caracteres")
        String rodovia,

        @NotNull(message = "O KM inicial e obrigatorio")
        @Min(value = 0, message = "O KM inicial nao pode ser negativo")
        Integer kmInicial,

        @NotNull(message = "O KM final e obrigatorio")
        @Min(value = 1, message = "O KM final deve ser maior que zero")
        Integer kmFinal,

        @NotNull(message = "O nivel de vegetacao e obrigatorio")
        @Min(value = 0, message = "O nivel de vegetacao nao pode ser negativo")
        Double nivelVegetacao,

        @NotBlank(message = "O tipo do trecho e obrigatorio (UMIDO ou SECO)")
        @Pattern(regexp = "^(?i)(UMIDO|SECO)$", message = "O tipo do trecho deve ser UMIDO ou SECO")
        String tipo
) {
}
