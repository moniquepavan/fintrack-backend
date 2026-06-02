package com.fintrack.service;

import com.fintrack.domain.Card;
import com.fintrack.domain.User;
import com.fintrack.dto.request.CardRequest;
import com.fintrack.dto.response.CardResponse;
import com.fintrack.repository.CardRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<CardResponse> findAll() {
        User user = getAuthenticatedUser();
        return cardRepository.findByUserId(user.getId())
                .stream()
                .map(CardResponse::fromEntity)
                .toList();
    }

    public CardResponse create(CardRequest request) {
        User user = getAuthenticatedUser();

        Card card = Card.builder()
                .user(user)
                .name(request.getName())
                .build();

        return CardResponse.fromEntity(cardRepository.save(card));
    }

    public CardResponse update(UUID id, CardRequest request) {
        User user = getAuthenticatedUser();
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para editar este cartão");
        }

        card.setName(request.getName());
        return CardResponse.fromEntity(cardRepository.save(card));
    }

    public void delete(UUID id) {
        User user = getAuthenticatedUser();
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para excluir este cartão");
        }

        cardRepository.delete(card);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
