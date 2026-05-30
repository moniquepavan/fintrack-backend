package com.fintrack.dto.response;

import com.fintrack.domain.Category;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;
    private String name;
    private String color;
    private String icon;
    private boolean isDefault;
    private BigDecimal budget;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .icon(category.getIcon())
                .isDefault(category.isDefault())
                .budget(category.getBudget())
                .build();
    }
}