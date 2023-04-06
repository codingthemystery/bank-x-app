package za.co.cajones.bankx.api.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.api.service.ApiTransactionService;
import za.co.cajones.bankx.dto.TransactionDto;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.repository.TransactionRepository;
import za.co.cajones.bankx.components.DtoUtils;

@Slf4j
@Service
public class ApiTransactionServiceImpl implements ApiTransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public List<TransactionDto> getAllTransactionsByBankId(Long bankId) {
		List<Transaction> transactions = transactionRepository.findByProcessingBankOrdersByExternalReferenceAsc(bankId);

		if (transactions != null) {
			log.debug("***** Get all BankZ No of transactions " + transactions.size());
			List<TransactionDto> transactionDtos =  DtoUtils.convertEntitiesToDtos(transactions);
			log.debug("***** Get all BankZ No of converted transactions " + transactionDtos.size());
			return transactionDtos;
		} else {
			return new ArrayList();
		}
	}

	@Override
	public TransactionDto getTransactionsById(Long transactionId) {

		Optional<Transaction> transaction = transactionRepository.findById(transactionId);

		if (transaction == null) {
			log.debug("***** Bank Z, Get by Id " + transactionId.toString() + " returned null");
			return new TransactionDto();
		} else {
			return DtoUtils.convertEntityToDto(transaction.get());
		}
	}

	@Override
	public List<TransactionDto> getTransactionByExternalReference(String externalReference) {

		List<Transaction> transactions = transactionRepository.findByExternalReference(externalReference);
		return DtoUtils.convertEntitiesToDtos(transactions);
	}

	// @Override
	// public Boolean addTransactions(List<TransactionDto> transactions) {

	// Add to Kafka transactions topic.
	// try {

	// } catch (Exception ex) {
	// return false;
	// }
	// return true;
	// }

	// @Override
	// public Boolean addTransaction(TransactionDto transaction) {

	// Add to Kafka transactions topic.
	// try {

	// } catch (Exception ex) {
	// return false;
	// }
	// return true;
	// }

	// @Override
	// public Boolean addReconciliations(List<TransactionDto> reconciliations) {

	// Add to Kafka transactions topic.
	// try {

	// } catch (Exception ex) {
	// return false;
	// }
	// return true;
	// }

}