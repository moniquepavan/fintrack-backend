package com.fintrack.dto.response;

import com.fintrack.domain.Transaction;
import com.fintrack.domain.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private UUID categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private boolean isRecurring;
    private String recurrenceRule;
    private String paymentMethod;
    private Integer installmentNumber;
    private Integer installmentTotal;
    private UUID installmentGroupId;

    public static TransactionResponse fromEntity(Transaction transaction) {
        TransactionResponseBuilder builder = TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .transactionDate(transaction.getTransactionDate())
                .isRecurring(transaction.isRecurring())
                .recurrenceRule(transaction.getRecurrenceRule())
                .paymentMethod(transaction.getPaymentMethod())
                .installmentNumber(transaction.getInstallmentNumber())
                .installmentTotal(transaction.getInstallmentTotal())
                .installmentGroupId(transaction.getInstallmentGroupId());

        if (transaction.getCategory() != null) {
            builder.categoryId(transaction.getCategory().getId())
                    .categoryName(transaction.getCategory().getName())
                    .categoryColor(transaction.getCategory().getColor())
                    .categoryIcon(transaction.getCategory().getIcon());
        }

        return builder.build();
    }
}