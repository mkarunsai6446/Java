package com.arun.accountservice.controller;


import com.arun.accountservice.model.Account;
import com.arun.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {

        return accountService.getAccountById(id);
    }

    @PutMapping("/update")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {

        return accountService.updateAccount(account);
    }
}

