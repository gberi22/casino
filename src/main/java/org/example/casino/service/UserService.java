package org.example.casino.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.casino.model.*;
import org.example.casino.repository.TransactionRepository;
import org.example.casino.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    private static final int MAX_LOCKS = 1000;

    public void createUser(UserRequest request) {
        String username = request.username();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("username already exists");
        }

        User user = User.builder()
                .username(username.trim())
                .build();

        userRepository.save(user);
        log.debug("Created user: {}", username);
    }

    @Transactional
    public void depositMoney(MoneyDepositRequest request) {
        validateMoneyRequest(request.username(), request.amount());

        ReentrantLock lock = getUserLock(request.username());
        lock.lock();
        try {
            User user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            BigDecimal oldBalance = user.getBalance();
            BigDecimal newBalance = oldBalance.add(request.amount());

            user.setBalance(newBalance);
            userRepository.save(user);

            Transaction transaction = Transaction.builder()
                    .user(user)
                    .amount(request.amount())
                    .type(Transaction.TransactionType.DEPOSIT)
                    .build();
            transactionRepository.save(transaction);

            log.debug("Deposited {} for user: {}. Balance: {} -> {}",
                    request.amount(), request.username(), oldBalance, newBalance);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void withdrawMoney(MoneyWithdrawRequest request) {
        validateMoneyRequest(request.username(), request.amount());

        ReentrantLock lock = getUserLock(request.username());
        lock.lock();
        try {
            User user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getBalance().compareTo(request.amount()) < 0) {
                throw new RuntimeException("Not enough money. Available: " +
                        user.getBalance() + ", Requested: " + request.amount());
            }

            BigDecimal oldBalance = user.getBalance();
            BigDecimal newBalance = oldBalance.subtract(request.amount());

            user.setBalance(newBalance);
            userRepository.save(user);

            Transaction transaction = Transaction.builder()
                    .user(user)
                    .amount(request.amount())
                    .type(Transaction.TransactionType.WITHDRAWAL)
                    .build();
            transactionRepository.save(transaction);

            log.debug("Withdrew {} for user: {}. Balance: {} -> {}",
                    request.amount(), request.username(), oldBalance, newBalance);
        } finally {
            lock.unlock();
        }
    }

    public UserResponse walletBalance(UserRequest request) {
        String username = request.username();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponse(user.getBalance());
    }

    private void validateMoneyRequest(String username, BigDecimal amount) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("Amount too large");
        }
    }

    private ReentrantLock getUserLock(String username) {
        if (userLocks.size() > MAX_LOCKS) {
            log.warn("User locks cache is getting large: {} entries", userLocks.size());
        }
        return userLocks.computeIfAbsent(username, u -> new ReentrantLock());
    }
}