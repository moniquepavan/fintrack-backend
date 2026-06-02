package com.fintrack.service;

import com.fintrack.domain.PaymentMethod;
import com.fintrack.domain.User;
import com.fintrack.dto.request.PaymentMethodRequest;
import com.fintrack.dto.response.PaymentMethodResponse;
import com.fintrack.repository.PaymentMethodRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public List<PaymentMethodResponse> findAll() {
        User user = getAuthenticatedUser();
        return paymentMethodRepository.findByUserId(user.getId())
                .stream()
                .map(PaymentMethodResponse::fromEntity)
                .toList();
    }

    public PaymentMethodResponse create(PaymentMethodRequest request) {
        User user = getAuthenticatedUser();

        PaymentMethod pm = PaymentMethod.builder()
                .user(user)
                .name(request.getName())
                .build();

        return PaymentMethodResponse.fromEntity(paymentMethodRepository.save(pm));
    }

    public PaymentMethodResponse update(UUID id, PaymentMethodRequest request) {
        User user = getAuthenticatedUser();
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));

        if (!pm.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para editar esta forma de pagamento");
        }

        pm.setName(request.getName());
        return PaymentMethodResponse.fromEntity(paymentMethodRepository.save(pm));
    }

    public void delete(UUID id) {
        User user = getAuthenticatedUser();
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));

        if (!pm.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para excluir esta forma de pagamento");
        }

        paymentMethodRepository.delete(pm);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
