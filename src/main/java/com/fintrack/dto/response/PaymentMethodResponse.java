package com.fintrack.dto.response;

import com.fintrack.domain.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {

    private UUID id;
    private String name;

    public static PaymentMethodResponse fromEntity(PaymentMethod pm) {
        return PaymentMethodResponse.builder()
                .id(pm.getId())
                .name(pm.getName())
                .build();
    }
}
