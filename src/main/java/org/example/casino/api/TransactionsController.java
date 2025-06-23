package org.example.casino.api;

import lombok.RequiredArgsConstructor;
import org.example.casino.model.TransactionResponse;
import org.example.casino.model.UserRequest;
import org.example.casino.service.TransactionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionsService transactionsService;

    @GetMapping
    public ResponseEntity<TransactionResponse> getUserTransactions(@RequestBody UserRequest request) {
        TransactionResponse response = transactionsService.getTransactionHistory(request);
        return ResponseEntity.ok(response);
    }
}
