package com.fintrack.controller;

import com.fintrack.domain.enums.TransactionType;
import com.fintrack.dto.request.TransactionRequest;
import com.fintrack.dto.response.DashboardResponse;
import com.fintrack.dto.response.TransactionResponse;
import com.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> findWithFilters(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                transactionService.findWithFilters(month, year, startDate, endDate, categoryId, type, search));
    }

    @GetMapping("/period")
    public ResponseEntity<List<TransactionResponse>> findByPeriod(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(transactionService.findByPeriod(month, year));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(transactionService.getDashboard(month, year));
    }

    @PostMapping
    public ResponseEntity<List<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable UUID id,
                                                      @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}