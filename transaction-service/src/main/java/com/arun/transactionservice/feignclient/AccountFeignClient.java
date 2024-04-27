package com.arun.transactionservice.feignclient;

import com.arun.transactionservice.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
@Component
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{id}")
    Account getAccountById(@PathVariable Long id);

    @PutMapping("/api/accounts/update")
    void updateAccount(@RequestBody Account account);
}

