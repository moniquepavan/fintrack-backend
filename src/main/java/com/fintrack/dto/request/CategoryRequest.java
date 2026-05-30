package com.fintrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Cor e obrigatoria")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color;

    @NotBlank(message = "Icone e obrigatorio")
    @Size(max = 30)
    private String icon;

    private BigDecimal budget;
}