package za.co.cajones.bankx.controller;


import java.math.BigDecimal;
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
import za.co.cajones.bankx.entity.Bank;
import za.co.cajones.bankx.service.BankService;

@Slf4j
@RestController
@RequestMapping("banks")
public class BankController {

    @Autowired
    private BankService bankService;

    // Get all banks
    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks(){
        List<Bank> banks = bankService.getAllBanks();
        return new ResponseEntity<>(banks, HttpStatus.OK);
    }

    // Get bank by ID
    @GetMapping("{id}")
    public ResponseEntity<Bank> getBankById(@PathVariable("id") Long bankId){
        Bank bank = bankService.getBankById(bankId);
        return new ResponseEntity<>(bank, HttpStatus.OK);
    }

    // Onboard a bank
    @PostMapping
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank){
        Bank savedBank = bankService.createBank(bank);
        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }

    //Update Bank
    @PutMapping("{id}")
    public ResponseEntity<Bank> updateBank(@PathVariable("id") Long bankId,
                                           @RequestBody Bank bank){
        bank.setId(bankId);
        Bank updatedBank = bankService.updateBank(bank);
        return new ResponseEntity<>(updatedBank, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBank(@PathVariable("id") Long bankId){
        bankService.deleteBank(bankId);
        return new ResponseEntity<>("Bank successfully deleted!", HttpStatus.OK);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}