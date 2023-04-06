package za.co.cajones.bankx.api.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.components.DtoUtils;
import za.co.cajones.bankx.dto.TransactionDto;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.service.TransactionService;

@Slf4j
@Component
class BankZListener {

	// BankZ groupId;
	@Value(value = "${bankz.groupid}")
	private String groupId;

	@Autowired
	TransactionService transactionService;

	@Autowired
	KafkaTemplate<String, TransactionDto> kafkaTemplate;

	@KafkaListener(topics = "transactions", containerFactory = "transactionKafkaListenerContainerFactory")
	void listenerTransactions(TransactionDto transactionDto) {
		log.info("***** Transaction Listener [{}]" + transactionDto.getExternalReference(), transactionDto);

		Transaction convertedTransaction = DtoUtils.convertDtoToEntity(transactionDto);

		// Add transactions to the database.
		Transaction processedTransaction = transactionService.createTransaction(convertedTransaction);

		log.debug("***** Tramsaction Processed: Id: " + processedTransaction.getId() + ", External Reference: " + processedTransaction.getExternalReference());

		TransactionDto processedTransactionDto = DtoUtils.convertEntityToDto(processedTransaction);
		this.kafkaTemplate.send("transactions-responses", processedTransactionDto);
	}

	@KafkaListener(topics = "reconciliations", containerFactory = "transactionKafkaListenerContainerFactory")
	void listenerReconciliations(TransactionDto transactionDto) {
		log.info("***** Reconciliation Listener [{}]" + transactionDto.getExternalReference(), transactionDto);

		Transaction convertedTransaction = DtoUtils.convertDtoToEntity(transactionDto);

		Transaction processedTransaction = transactionService.reconcileTransaction(convertedTransaction);

		log.debug("***** Reconcilations Processed: Id: " + processedTransaction.getId() + ", External Reference: " + processedTransaction.getExternalReference());

		TransactionDto processedTransactionDto = DtoUtils.convertEntityToDto(processedTransaction);
		this.kafkaTemplate.send("reconciliations-responses", processedTransactionDto);
	}


}