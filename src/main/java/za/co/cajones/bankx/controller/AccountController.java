package za.co.cajones.bankx.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.service.AccountService;

@Slf4j
@RestController
@RequestMapping("accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;


    // Get all Accounts.
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts(){
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get an Account by id.
    @GetMapping("{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Long accountId){
        Account account = accountService.getAccountById(accountId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    // Create an Account.
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account){
        Account savedAccount = accountService.createAccount(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    // Update an Account.
    @PutMapping("{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable("id") Long accountId,
                                           @RequestBody Account account){
        account.setId(accountId);
        Account updatedAccount = accountService.updateAccount(account);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    // Build Delete Account REST API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") Long accountId){
        accountService.deleteAccount(accountId);
        return new ResponseEntity<>("Account successfully deleted!", HttpStatus.OK);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}