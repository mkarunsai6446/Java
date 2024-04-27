package com.arun.transactionservice.service;

import com.arun.transactionservice.model.Account;
import com.arun.transactionservice.model.Transaction;
import com.arun.transactionservice.repository.TransactionRepository;
import com.arun.transactionservice.feignclient.AccountFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountFeignClient accountFeignClient;

    public ResponseEntity<?> deposit(Transaction transaction) {
        try{
            if (transaction.getAmount() <= 0) {
                return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
            }
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setTransactionType("deposit");
            Account account = accountFeignClient.getAccountById(transaction.getAccountId());
            account.setBalance(account.getBalance() + transaction.getAmount());
            accountFeignClient.updateAccount(account);
            Transaction savedTransaction = transactionRepository.save(transaction);
            double currentBalance = account.getBalance();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("currentBalance", currentBalance);
            responseMap.put("transaction", savedTransaction);


            // Include balance in the response
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> withdraw(Transaction transaction) {
        try {
            if (transaction.getAmount() <= 0) {
                return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
            }
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setTransactionType("withdrawal");
            Account account = accountFeignClient.getAccountById(transaction.getAccountId());

            if (account.getBalance() >= transaction.getAmount()) {
                account.setBalance(account.getBalance() - transaction.getAmount());
                accountFeignClient.updateAccount(account);
                Transaction savedTransaction = transactionRepository.save(transaction);
                double currentBalance = account.getBalance();

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("currentBalance", currentBalance);
                responseMap.put("transaction", savedTransaction);


                // Include balance in the response
                return new ResponseEntity<>(responseMap, HttpStatus.OK);


            } else {
                System.out.println("Insufficient balance. Current balance: " + account.getBalance());
                return new ResponseEntity<>("Insufficient balance. Current balance: " + account.getBalance(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<?> transfer(Transaction transaction) {
        try {
            if (transaction.getAmount() <= 0) {
                return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
            }
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setTransactionType("transfer");
            Account sourceAccount = accountFeignClient.getAccountById(transaction.getAccountId());
            Account destinationAccount = accountFeignClient.getAccountById(transaction.getDestinationAccountId());

            if (sourceAccount.getBalance() >= transaction.getAmount()) {
                sourceAccount.setBalance(sourceAccount.getBalance() - transaction.getAmount());
                destinationAccount.setBalance(destinationAccount.getBalance() + transaction.getAmount());

                accountFeignClient.updateAccount(sourceAccount);
                accountFeignClient.updateAccount(destinationAccount);

                Transaction savedTransaction = transactionRepository.save(transaction);
                double currentBalance = sourceAccount.getBalance();

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("currentBalance", currentBalance);
                responseMap.put("transaction", savedTransaction);


                // Include balance in the response
                return new ResponseEntity<>(responseMap, HttpStatus.OK);

            }
            else {
                System.out.println("Insufficient balance. Current balance: " + sourceAccount.getBalance());
                return new ResponseEntity<>("Insufficient balance. Current balance: " + sourceAccount.getBalance(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Transaction>> getTransactionHistory(Long accountId) {
        try {
            List<Transaction> transactions = transactionRepository.findAllByAccountIdOrderByTimestampDesc(accountId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
