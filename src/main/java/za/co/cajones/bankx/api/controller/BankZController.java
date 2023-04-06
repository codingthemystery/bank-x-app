package za.co.cajones.bankx.api.controller;

import java.util.List;
import java.util.ArrayList;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.cajones.bankx.api.service.ApiTransactionService;
import za.co.cajones.bankx.dto.TransactionDto;
import za.co.cajones.bankx.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@RequestMapping("api/transactions")
@RestController
public class BankZController {

	@Autowired
	private ApiTransactionService transactionService;

	@Value(value = "${bankz.bankid}")
	private Long bankId;

	final KafkaTemplate kafkaTemplate;
	

	public BankZController(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	// Get all transactions. Non Kafka retrieve.
	@GetMapping
	public ResponseEntity<List<TransactionDto>> getAllTransactions() {

		log.info("****** Bank Id: " + bankId.toString());

		List<TransactionDto> transactions = transactionService.getAllTransactionsByBankId(bankId);
		return new ResponseEntity<>(transactions, HttpStatus.OK);
	}

	// Process several transactions. Add to Kafka queue.
	@PostMapping("/batch")
	public ResponseEntity<String> addTransactions(@RequestBody List<TransactionDto> transactions) {

		for (TransactionDto transaction : transactions) {

			log.info("***** Submitting transactions to Kafka: " + transactions.toString());

			this.kafkaTemplate.send("transactions", transaction);

		}

		return new ResponseEntity("Submitted successfully.", HttpStatus.ACCEPTED);
	}

	// Process reconciliations. Add to Kafka queue.
	@PostMapping("/reconciliations")
	public ResponseEntity<String> addReconciliations(@RequestBody List<TransactionDto> reconciliations) {

		for (TransactionDto reconciliation : reconciliations) {
			
			log.info("***** Submitting reconciliations to Kafka: " + reconciliations.toString());

			this.kafkaTemplate.send("reconcilations", reconciliation);
		}

		return new ResponseEntity("Submitted successfully.", HttpStatus.ACCEPTED);
	}
	

	// Get transaction responses.
	@GetMapping("/batch")
	public ResponseEntity<String> getTransactionResponses() {

    	return new  ResponseEntity<>("Not implemented yet", HttpStatus.OK);
	}

	// Get reconciliation responses.
	@GetMapping("/reconciliations")
	public ResponseEntity<String> getReconciliationResponses() {

    	return new  ResponseEntity<>("Not implemented yet", HttpStatus.OK);
	}

	@ExceptionHandler(RuntimeException.class)
	public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
		return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}