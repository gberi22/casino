package org.example.casino.service;

import lombok.RequiredArgsConstructor;
import org.example.casino.model.TransactionResponse;
import org.example.casino.model.User;
import org.example.casino.model.UserRequest;
import org.example.casino.repository.TransactionRepository;
import org.example.casino.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse getTransactionHistory(UserRequest request) {
        String username = request.username();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new TransactionResponse(transactionRepository.findAllByUser(user));
    }
}
