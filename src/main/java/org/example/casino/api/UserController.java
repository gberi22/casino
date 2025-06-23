package org.example.casino.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.casino.model.MoneyDepositRequest;
import org.example.casino.model.MoneyWithdrawRequest;
import org.example.casino.model.UserRequest;
import org.example.casino.model.UserResponse;
import org.example.casino.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestBody UserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> depositMoney(@RequestBody MoneyDepositRequest request) {
        userService.depositMoney(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawMoney(@RequestBody MoneyWithdrawRequest request) {
        userService.withdrawMoney(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<UserResponse> userBalance(@RequestBody UserRequest request) {
        UserResponse response = userService.walletBalance(request);
        return ResponseEntity.ok(response);
    }
}
