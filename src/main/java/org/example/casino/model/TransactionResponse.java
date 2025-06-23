package org.example.casino.model;

import java.util.List;

public record TransactionResponse (
    List<Transaction> transactions
){}
