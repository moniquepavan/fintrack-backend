package com.fintrack.dto.request;

import com.fintrack.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionRequest {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(min = 2, max = 200)
    private String description;

    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull(message = "Tipo e obrigatorio")
    private TransactionType type;

    @NotNull(message = "Data e obrigatoria")
    private LocalDate transactionDate;

    private UUID categoryId;
    private UUID cardId;
    private boolean isRecurring;
    private boolean updateFollowing;
    private String recurrenceRule;
    private String paymentMethod;
    private Integer installmentTotal;
}