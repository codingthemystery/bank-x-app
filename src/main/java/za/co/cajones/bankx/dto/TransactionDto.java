package za.co.cajones.bankx.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.cajones.bankx.model.ProcessingType;
import za.co.cajones.bankx.model.TransactionStatus;
import za.co.cajones.bankx.model.TransactionType;


@NoArgsConstructor
@Getter
@Setter
public class TransactionDto {

	private Long id;

	private Long transactionGroup;

	private Long originatingAccountId;

	private Long originatingCardNumber;

	private Long destinationAccountId;

	private Long destinationCardNumber;

	private Long processingBankId;

	private Long customerBankId;

	private String externalReference;

	private BigDecimal amount;

	private String reference;

	private TransactionType transactionType;

	private ProcessingType processingType;

	private TransactionStatus status;

	private String error;

	private LocalDateTime date;

	public TransactionDto(@JsonProperty Long id, @JsonProperty Long transactionGroup,
			@JsonProperty Long originatingAccountId, @JsonProperty Long originatingCardNumber,
			@JsonProperty Long destinationAccountId, @JsonProperty Long destinationCardNumber,
			@JsonProperty Long processingBankId, @JsonProperty Long customerBankId,
			@JsonProperty String externalReference, @JsonProperty BigDecimal amount, @JsonProperty String reference,
			@JsonProperty TransactionType transactionType, @JsonProperty ProcessingType processingType,
			@JsonProperty TransactionStatus status, @JsonProperty String error, @JsonProperty LocalDateTime date) {

		this.id = id;
		this.transactionGroup = transactionGroup;
		this.originatingAccountId = originatingAccountId;
		this.originatingCardNumber = originatingCardNumber;
		this.destinationAccountId = destinationAccountId;
		this.destinationCardNumber = destinationCardNumber;
		this.processingBankId = processingBankId;
		this.customerBankId = customerBankId;
		this.externalReference = externalReference;
		this.amount = amount;
		this.reference = reference;
		this.transactionType = transactionType;
		this.processingType = processingType;
		this.status = status;
		this.error = error;
		this.date = date;

	}

	public static TransactionDto clone(TransactionDto transaction) {
		TransactionDto newTransaction = new TransactionDto();

		newTransaction.setId(transaction.getId());
		newTransaction.setTransactionGroup(transaction.getTransactionGroup());
		newTransaction.setOriginatingAccountId(transaction.getOriginatingAccountId());
		newTransaction.setOriginatingCardNumber(transaction.getOriginatingCardNumber());
		newTransaction.setDestinationAccountId(transaction.getDestinationAccountId());
		newTransaction.setDestinationCardNumber(transaction.getDestinationCardNumber());
		newTransaction.setProcessingBankId(transaction.getProcessingBankId());
		newTransaction.setCustomerBankId(transaction.getCustomerBankId());
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

	public static List<TransactionDto> cloneDtos(List<TransactionDto> transactions) {
		// @TODO Map fields
		List<TransactionDto> dtos = new ArrayList<>();
		TransactionDto dto;

		for (TransactionDto transaction : transactions) {
			dto = clone(transaction);
			dtos.add(dto);
		}
		return dtos;
	}

}