package com.fintrack.service;

import com.fintrack.domain.Category;
import com.fintrack.domain.Transaction;
import com.fintrack.domain.User;
import com.fintrack.domain.enums.TransactionType;
import com.fintrack.dto.request.TransactionRequest;
import com.fintrack.dto.response.DashboardResponse;
import com.fintrack.dto.response.TransactionResponse;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<TransactionResponse> findAll() {
        User user = getAuthenticatedUser();
        return transactionRepository
                .findByUserIdOrderByTransactionDateDesc(user.getId())
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    public List<TransactionResponse> findByPeriod(int month, int year) {
        User user = getAuthenticatedUser();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        user.getId(), start, end)
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    public List<TransactionResponse> findWithFilters(
            Integer month, Integer year,
            LocalDate startDate, LocalDate endDate,
            UUID categoryId, TransactionType type,
            String search) {

        User user = getAuthenticatedUser();

        LocalDate start = startDate;
        LocalDate end = endDate;

        if (start == null && month != null && year != null) {
            start = LocalDate.of(year, month, 1);
            end = start.withDayOfMonth(start.lengthOfMonth());
        }

        List<Transaction> transactions = (start != null && end != null)
                ? transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(user.getId(), start, end)
                : transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId());

        return transactions.stream()
                .filter(t -> categoryId == null || (t.getCategory() != null && t.getCategory().getId().equals(categoryId)))
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> search == null || search.isBlank() ||
                        t.getDescription().toLowerCase().contains(search.toLowerCase()))
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    public DashboardResponse getDashboard(int month, int year) {
        User user = getAuthenticatedUser();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal totalIncome = transactionRepository.sumByUserIdAndTypeAndPeriod(
                user.getId(), TransactionType.INCOME, start, end);
        BigDecimal totalExpense = transactionRepository.sumByUserIdAndTypeAndPeriod(
                user.getId(), TransactionType.EXPENSE, start, end);

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .month(month)
                .year(year)
                .build();
    }

    public List<TransactionResponse> create(TransactionRequest request) {
        User user = getAuthenticatedUser();

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoria nao encontrada"));
        }

        List<Transaction> saved = new ArrayList<>();

        if (request.getInstallmentTotal() != null && request.getInstallmentTotal() > 1) {
            UUID groupId = UUID.randomUUID();
            BigDecimal installmentAmount = request.getAmount()
                    .divide(BigDecimal.valueOf(request.getInstallmentTotal()), 2, RoundingMode.HALF_UP);

            for (int i = 1; i <= request.getInstallmentTotal(); i++) {
                Transaction t = Transaction.builder()
                        .user(user)
                        .category(category)
                        .description(request.getDescription())
                        .amount(installmentAmount)
                        .type(request.getType())
                        .transactionDate(request.getTransactionDate().plusMonths(i - 1))
                        .isRecurring(false)
                        .paymentMethod(request.getPaymentMethod())
                        .installmentNumber(i)
                        .installmentTotal(request.getInstallmentTotal())
                        .installmentGroupId(groupId)
                        .build();
                saved.add(transactionRepository.save(t));
            }
        } else {
            Transaction t = Transaction.builder()
                    .user(user)
                    .category(category)
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .type(request.getType())
                    .transactionDate(request.getTransactionDate())
                    .isRecurring(request.isRecurring())
                    .recurrenceRule(request.getRecurrenceRule())
                    .paymentMethod(request.getPaymentMethod())
                    .build();
            saved.add(transactionRepository.save(t));
        }

        return saved.stream().map(TransactionResponse::fromEntity).toList();
    }

    public TransactionResponse update(UUID id, TransactionRequest request) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacao nao encontrada"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissao para editar esta transacao");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoria nao encontrada"));
        }

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategory(category);
        transaction.setRecurring(request.isRecurring());
        transaction.setRecurrenceRule(request.getRecurrenceRule());
        transaction.setPaymentMethod(request.getPaymentMethod());

        return TransactionResponse.fromEntity(transactionRepository.save(transaction));
    }

    public void delete(UUID id) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacao nao encontrada"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Sem permissao para excluir esta transacao");
        }

        transactionRepository.delete(transaction);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
    }
}