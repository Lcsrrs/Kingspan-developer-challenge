package com.kingspan.challenge.requests.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateRequestDTO(
        @NotBlank(message = "Título obrigatório")
        String title,

        String description,

        @NotNull(message = "Valor obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal amount,

        @NotBlank(message = "Categoria é obrigatória")
        String category
) {
}
