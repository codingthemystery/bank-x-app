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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.service.TransactionService;

@Slf4j
@RestController
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@RequestMapping("transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	// Get all transactions.
	@GetMapping
	public ResponseEntity<List<Transaction>> getAllTransactions() {
		List<Transaction> transactions = transactionService.getAllTransactions();
		return new ResponseEntity<>(transactions, HttpStatus.OK);
	}

	// Get transaction by ID
	@GetMapping("{id}")
	public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") Long transactionId) {
		Transaction transaction = transactionService.getTransactionById(transactionId);
		return new ResponseEntity<>(transaction, HttpStatus.OK);
	}


    @GetMapping("/originations/{id}")
    public ResponseEntity<List<Transaction>> getTransactionsOriginated(@PathVariable("id") Long id){
        List<Transaction> transactions = transactionService.getOriginatingTransactionsByAccountId(id);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

	// Process several transactions.
	@PostMapping("/multi")
	public ResponseEntity<List<Transaction>> addTransactions(@RequestBody List<Transaction> transactions) {
		List<Transaction> savedTransactions = transactionService.addTransactions(transactions);
		return new ResponseEntity<>(savedTransactions, HttpStatus.CREATED);
	}

	// Create a transaction (transfer, payment, etc.)
	@PostMapping
	public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
		log.debug("Creating transaction");
		Transaction savedTransaction = transactionService.createTransaction(transaction);
		return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
	}

	// Update Transaction
	@PutMapping("{id}")
	public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") Long transactionId,
			@RequestBody Transaction transaction) {
		transaction.setId(transactionId);
		Transaction updatedTransaction = transactionService.updateTransaction(transaction);
		return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
	}

	// Delete
	@DeleteMapping("{id}")
	public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long transactionId) {
		transactionService.deleteTransaction(transactionId);
		return new ResponseEntity<>("Transaction successfully deleted!", HttpStatus.OK);
	}

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}