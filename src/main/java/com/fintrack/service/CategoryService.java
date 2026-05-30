package com.fintrack.service;

import com.fintrack.domain.Category;
import com.fintrack.domain.User;
import com.fintrack.dto.request.CategoryRequest;
import com.fintrack.dto.response.CategoryResponse;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> findAll() {
        User user = getAuthenticatedUser();
        return categoryRepository.findByUserIdOrIsDefaultTrue(user.getId())
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        User user = getAuthenticatedUser();

        Category category = Category.builder()
                .user(user)
                .name(request.getName())
                .color(request.getColor())
                .icon(request.getIcon())
                .isDefault(false)
                .build();

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    public CategoryResponse update(UUID id, CategoryRequest request) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para editar esta categoria");
        }

        category.setName(request.getName());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    public void delete(UUID id) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissão para excluir esta categoria");
        }

        categoryRepository.delete(category);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}