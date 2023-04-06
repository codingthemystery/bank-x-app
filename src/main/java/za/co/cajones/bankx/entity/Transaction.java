package za.co.cajones.bankx.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.cajones.bankx.model.ProcessingType;
import za.co.cajones.bankx.model.TransactionStatus;
import za.co.cajones.bankx.model.TransactionType;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "transaction_group", nullable = false)
	Long transactionGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "originating_account_id")
	Account originatingAccount;

	@Column(name = "originating_card_number")
	Long originatingCardNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_account_id")
	Account destinationAccount;

	@Column(name = "destination_card_number")
	Long destinationCardNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processing_bank_id")
	Bank processingBank;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_bank_id")
	Bank customerBank;

	@Column(name = "external_reference", length = 30)
	String externalReference;

	@Column
	BigDecimal amount;

	@Column(nullable = false)
	String reference;

	@Column(name = "transaction_type", nullable = false)
	TransactionType transactionType;

	@Column(name = "processing_type", nullable = false)
	ProcessingType processingType;

	@Column(nullable = false)
	TransactionStatus status;

	@Column
	String error;

	@CreationTimestamp
	LocalDateTime date;

	public Transaction(Long transactionGroup, Account originatingAccount, Long originatingCardNumber, Account destinationAccount, Long destinationCardNumber, Bank processingBank,
			 Bank customerBank, String externalReference, TransactionType transactionType,
			BigDecimal amount, String reference, ProcessingType processingType, TransactionStatus status,
			String error) {
		this.originatingAccount = originatingAccount;
		this.originatingCardNumber = originatingCardNumber;
		this.processingBank = processingBank;
		this.destinationAccount = destinationAccount;
		this.destinationCardNumber = destinationCardNumber;
		this.customerBank = customerBank;
		this.transactionType = transactionType;
		this.amount = amount;
		this.reference = reference;
		this.processingType = processingType;
		this.status = status;
		if (error == null)
			this.error = "";
		else
			this.error = error;
	}

}