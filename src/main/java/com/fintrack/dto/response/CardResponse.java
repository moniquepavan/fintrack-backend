package com.fintrack.dto.response;

import com.fintrack.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private UUID id;
    private String name;

    public static CardResponse fromEntity(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .build();
    }
}
