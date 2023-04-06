package za.co.cajones.bankx.components;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;
import za.co.cajones.bankx.model.ProcessingType;
import za.co.cajones.bankx.model.TransactionStatus;
import za.co.cajones.bankx.model.TransactionType;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.dto.TransactionDto;
import za.co.cajones.bankx.entity.Bank;
import za.co.cajones.bankx.entity.Account;

@Slf4j
@Component
public final class DtoUtils {

	public static TransactionDto convertEntityToDto(Transaction transaction) {
		// @TODO Map fields
		TransactionDto newTransaction = new TransactionDto();

		if (transaction != null) {

			newTransaction.setId(transaction.getId());
			newTransaction.setTransactionGroup(transaction.getTransactionGroup());

			if (transaction.getOriginatingAccount() != null)
				newTransaction.setOriginatingAccountId(transaction.getOriginatingAccount().getId());
			else
				newTransaction.setOriginatingAccountId(0L);

			newTransaction.setOriginatingCardNumber(transaction.getOriginatingCardNumber());

			if (transaction.getDestinationAccount() != null)
				newTransaction.setDestinationAccountId(transaction.getDestinationAccount().getId());
			else
				newTransaction.setDestinationAccountId(0L);

			newTransaction.setDestinationCardNumber(transaction.getDestinationCardNumber());
			newTransaction.setProcessingBankId(transaction.getProcessingBank().getId());

			if (transaction.getCustomerBank() != null)
				newTransaction.setCustomerBankId(transaction.getCustomerBank().getId());
			else
				newTransaction.setCustomerBankId(0l);
			newTransaction.setExternalReference(transaction.getExternalReference());
			newTransaction.setAmount(transaction.getAmount());
			newTransaction.setReference(transaction.getReference());
			newTransaction.setTransactionType(transaction.getTransactionType());
			newTransaction.setProcessingType(transaction.getProcessingType());
			newTransaction.setStatus(transaction.getStatus());
			newTransaction.setError(transaction.getError());
			newTransaction.setDate(transaction.getDate());
		}
		return newTransaction;
	}

	public static List<TransactionDto> convertEntitiesToDtos(List<Transaction> transactions) {
		// @TODO Map fields
		List<TransactionDto> dtos = new ArrayList<>();
		TransactionDto dto;

		for (Transaction transaction : transactions) {
			dto = convertEntityToDto(transaction);
			dtos.add(dto);
		}
		return dtos;
	}

	public static Transaction convertDtoToEntity(TransactionDto transaction) {
		// @TODO Map fields
		Transaction newTransaction = new Transaction();

		// if (transaction != null) {

		newTransaction.setId(transaction.getId());
		newTransaction.setTransactionGroup(transaction.getTransactionGroup());

		Account originatingAccount = new Account();
		if (transaction.getOriginatingAccountId() != null)
			originatingAccount.setId(transaction.getOriginatingAccountId());
		newTransaction.setOriginatingAccount(originatingAccount);

		newTransaction.setOriginatingCardNumber(transaction.getOriginatingCardNumber());

		Account destinationAccount = new Account();
		if (transaction.getDestinationAccountId() != null)
			destinationAccount.setId(transaction.getDestinationAccountId());
		newTransaction.setDestinationAccount(destinationAccount);

		newTransaction.setDestinationCardNumber(transaction.getDestinationCardNumber());

		Bank processingBank = new Bank();
		if (transaction.getProcessingBankId() != null)
			processingBank.setId(transaction.getProcessingBankId());
		newTransaction.setProcessingBank(processingBank);

		Bank customerBank = new Bank();
		if (transaction.getCustomerBankId() != null)
			customerBank.setId(transaction.getCustomerBankId());
		newTransaction.setCustomerBank(customerBank);

		newTransaction.setExternalReference(transaction.getExternalReference());
		newTransaction.setAmount(transaction.getAmount());
		newTransaction.setReference(transaction.getReference());
		newTransaction.setTransactionType(transaction.getTransactionType());
		newTransaction.setProcessingType(transaction.getProcessingType());
		newTransaction.setStatus(transaction.getStatus());
		newTransaction.setError(transaction.getError());
		newTransaction.setDate(transaction.getDate());
		return newTransaction;
	}

	public static List<Transaction> convertDtosToEntities(List<TransactionDto> transactions) {

		List<Transaction> entities = new ArrayList<>();
		Transaction entity;

		for (TransactionDto transaction : transactions) {
			entity = convertDtoToEntity(transaction);
			entities.add(entity);
		}
		return entities;
	}

}