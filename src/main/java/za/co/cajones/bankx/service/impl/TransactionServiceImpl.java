package za.co.cajones.bankx.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessagingException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.components.Utils;
import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.model.AccountType;
import za.co.cajones.bankx.model.Note;
import za.co.cajones.bankx.model.ReconciliationError;
import za.co.cajones.bankx.model.TransactionStatus;
import za.co.cajones.bankx.model.TransactionType;
import za.co.cajones.bankx.repository.TransactionRepository;
import za.co.cajones.bankx.service.AccountService;
import za.co.cajones.bankx.service.BankService;
import za.co.cajones.bankx.service.MessageService;
import za.co.cajones.bankx.service.TransactionService;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private BankService bankService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private MessageService messageService;

	@Override
	public List<Transaction> getAllTransactions() {
		return transactionRepository.findAll();
	}

	@Override
	public Transaction getTransactionById(Long transactionId) {
		Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
		return optionalTransaction.get();
	}
	
	@Override
	public List<Transaction> getOriginatingTransactionsByAccountId(Long id) {
		return transactionRepository.getOriginatingTransactionsByAccountIdOrderByGroupIdAsc(id);
	}


	@Override
	public List<Transaction> addTransactions(List<Transaction> transactions) {
		List<Transaction> processedTransactions = new ArrayList<>();
		Transaction processedTransaction = new Transaction();

		for (Transaction transaction : transactions) {
			processedTransaction = createTransaction(transaction);
			processedTransactions.add(processedTransaction);
		}

		return processedTransactions;
	}

	@Override
	public Transaction createTransaction(Transaction transaction) {

		Transaction savedTransaction = new Transaction();

		log.debug("**** Starting Transaction");

		if (transaction.getTransactionType() == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Transaction Type is null. Not persisted.");
			return transaction;
		}

		if (transaction.getStatus() != null && (transaction.getStatus().equals(TransactionStatus.PROCESSED)
				|| transaction.getStatus().equals(TransactionStatus.RECONCILED))) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Transaction has already been completed. Not persisted.");
			return transaction;
		}

		if (transaction.getStatus() != null && !transaction.getStatus().equals(TransactionStatus.INITIALIZED)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Transaction Status is not INITIALIZED.");
			return transaction;
		}

		if (transaction.getAmount() == null || transaction.getAmount().doubleValue() <= 0) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Transaction value must be greater than zero. Transaction not persisted.");
			return transaction;
		}

		TransactionType type = transaction.getTransactionType();
		switch (type) {
		case TRANSFER:
			savedTransaction = this.processBankLevelTransaction(transaction);
			break;
		case PAYMENT:
			savedTransaction = this.processPaymentTransaction(transaction);
			break;
		case CREDIT:
		case DEBIT:
		case FEE:
		case INTEREST:
			savedTransaction = this.processBankLevelTransaction(transaction);
			break;
		default:
			log.debug("**** Invalid transaction type");
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("**** Unknown transaction type. Not persisted.");
			return transaction;
		}

		// Send notifications to customer/s.

		try {

			if (savedTransaction != null && !savedTransaction.getStatus().equals(TransactionStatus.ERROR)) {
				String transactionType = savedTransaction.getTransactionType().getDescription();
				Note newNote = new Note();

				newNote.setSubject(transactionType + " Transaction, ID: " + savedTransaction.getId().toString());

				log.debug("**** Notification Subject: " + newNote.getSubject());

				if (savedTransaction.getOriginatingAccount() != null) {

					newNote.setContent(
							"**** Account Number: " + savedTransaction.getOriginatingAccount().getId().toString() + ": "
									+ transactionType + " for " + savedTransaction.getAmount().toPlainString());

					log.debug("**** Notification Content: " + newNote.getContent());

					try {
						String result = messageService.sendNotification(newNote,
								savedTransaction.getOriginatingAccount().getCustomer().getToken().toString());
						log.info("**** Notification result for transaction, originating account "
								+ savedTransaction.getId().toString() + " " + result);

					} catch (FirebaseMessagingException ex) {
						log.error("**** Could not send notification to "
								+ savedTransaction.getOriginatingAccount().getCustomer().getFirstName() + " "
								+ savedTransaction.getOriginatingAccount().getCustomer().getLastName()
								+ ", Transaction Id: " + savedTransaction.getId().toString());
					}
				}
				if (savedTransaction.getDestinationAccount() != null) {
					newNote.setContent(
							"**** Account Number: " + savedTransaction.getDestinationAccount().getId().toString() + ": "
									+ transactionType + " for " + savedTransaction.getAmount().toPlainString());

					log.debug("**** Notification Content: " + newNote.getContent());
				}
				try {

					String result = messageService.sendNotification(newNote,
							savedTransaction.getDestinationAccount().getCustomer().getToken().toString());
					log.info("**** Notification result for transaction, destination account "
							+ savedTransaction.getId().toString() + " " + result);

				} catch (FirebaseMessagingException ex) {
					log.error("****Could not send notification to "
							+ savedTransaction.getDestinationAccount().getCustomer().getFirstName() + " "
							+ savedTransaction.getDestinationAccount().getCustomer().getLastName()
							+ ", Transaction Id: " + savedTransaction.getId().toString());
				}
			}
		} catch (Exception Ex) {
			log.debug("**** Error sending notification. Transfer WAS completed successfully.");
			savedTransaction.setStatus(TransactionStatus.PROCESSED);
			savedTransaction.setError("Transaction processed but notification/s failed.");
			return savedTransaction;
		}
		return savedTransaction;

	}

	@Override
	@Transactional
	public Transaction processTransferTransaction(Transaction transaction) {

		log.debug("**** Creating Transfer: ");
		if (transaction.getOriginatingAccount() == null || transaction.getOriginatingAccount().getId() == null
				|| transaction.getDestinationAccount() == null || transaction.getDestinationAccount().getId() == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: One or more accounts are missing. Transaction not persisted.");

			return transaction;
		}

		Account savedOriginatingAccount;
		Account savedDestinationAccount;
		try {
			savedOriginatingAccount = accountService.getAccountById(transaction.getOriginatingAccount().getId());
		} catch (Exception Ex) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Cannot access originating account. Transaction not persisted.");
			return transaction;
		}
		try {
			savedDestinationAccount = accountService.getAccountById(transaction.getDestinationAccount().getId());
		} catch (Exception Ex) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Cannot access destination account. Transaction not persisted.");
			return transaction;
		}

		if (savedOriginatingAccount.getCustomer().getBank().getId() != Utils.getBankXId()
				|| savedOriginatingAccount.getCustomer().getBank().getId() != Utils.getBankXId()) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Only BankX customers can make transfers. Transaction not persisted.");
			return transaction;
		}

		if (Utils.isSameAccount(savedOriginatingAccount, savedDestinationAccount)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Can't transfer to same account. Transaction not persisted.");
			return transaction;
		}

		if (!Utils.isSameCustomer(savedOriginatingAccount.getCustomer(), savedDestinationAccount.getCustomer())) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Accounts belong to different customers. Transaction not persisted.");
			return transaction;
		}

		// From account.

		BigDecimal originatingBalance = savedOriginatingAccount.getBalance();
		BigDecimal debitBalance = savedOriginatingAccount.getBalance().subtract(transaction.getAmount());

		if (debitBalance.doubleValue() < 0) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Insufficient funds. Balance in account is: "
					+ originatingBalance.toPlainString() + ". Transaction not persisted.");
			return transaction;
		}
		// Decrease balance in originating account.
		savedOriginatingAccount.setBalance(originatingBalance.subtract(transaction.getAmount()));

		log.debug("**** Transfer from account: " + savedOriginatingAccount.getId().toString() + " Balance: "
				+ originatingBalance.toPlainString() + ", New Balance: "
				+ savedOriginatingAccount.getBalance().toPlainString());

		// To account.
		BigDecimal destinationBalance = savedDestinationAccount.getBalance();

		savedDestinationAccount.setBalance(destinationBalance.add(transaction.getAmount()));

		log.debug("**** Transfer to account: " + savedDestinationAccount.getId().toString() + " Balance: "
				+ destinationBalance.toPlainString() + ", New Balance: "
				+ savedDestinationAccount.getBalance().toPlainString());

		savedOriginatingAccount = accountService.updateAccount(savedOriginatingAccount);
		savedDestinationAccount = accountService.updateAccount(savedDestinationAccount);

		// Create a record of this transaction.

		Transaction savedTransaction = new Transaction();
		savedTransaction.setOriginatingAccount(savedOriginatingAccount);
		savedTransaction.setOriginatingCardNumber(savedOriginatingAccount.getCardNumber());

		savedTransaction.setDestinationAccount(savedDestinationAccount);
		savedTransaction.setDestinationCardNumber(savedDestinationAccount.getCardNumber());

		savedTransaction.setProcessingBank(transaction.getProcessingBank());
		savedTransaction.setCustomerBank(savedOriginatingAccount.getCustomer().getBank());

		savedTransaction.setExternalReference(transaction.getExternalReference());
		savedTransaction.setAmount(transaction.getAmount());
		savedTransaction.setReference(transaction.getReference());

		savedTransaction.setTransactionType(transaction.getTransactionType());
		savedTransaction.setProcessingType(transaction.getProcessingType()); // Real-time
		savedTransaction.setStatus(TransactionStatus.PROCESSED); // Processed
		savedTransaction.setError("");
		savedTransaction.setTransactionGroup(Utils.generateTransactionGroup());

		log.info("**** Persisting Transfer Transaction");
		return transactionRepository.save(savedTransaction);

	}

	@Override
	@Transactional
	public Transaction processPaymentTransaction(Transaction transaction) {
		log.debug("**** Creating Payment");
		if (transaction.getOriginatingAccount() == null || transaction.getOriginatingAccount().getId() == null
				|| transaction.getDestinationAccount() == null || transaction.getDestinationAccount().getId() == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: One or more accounts are missing. Transaction not persisted.");

			return transaction;
		}

		Account savedOriginatingAccount;
		Account savedDestinationAccount;
		try {
			savedOriginatingAccount = accountService.getAccountById(transaction.getOriginatingAccount().getId());
		} catch (Exception Ex) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Cannot access originating account. Transaction not persisted.");
			return transaction;
		}
		try {
			savedDestinationAccount = accountService.getAccountById(transaction.getDestinationAccount().getId());
		} catch (Exception Ex) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Cannot access destination account. Transaction not persisted.");
			return transaction;
		}

		if (Utils.isSameAccount(savedOriginatingAccount, savedDestinationAccount)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Can't make a payment to same account. Transaction not persisted.");
			return transaction;
		}

		if (!savedOriginatingAccount.getType().equals(AccountType.CURRENT)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Payments can only be made from current accounts. Transaction not persisted.");
			return transaction;
		}

		// From account.

		BigDecimal originatingBalance = savedOriginatingAccount.getBalance();

		// Calculate Debit Fee.
		BigDecimal debitFee = Utils.calcDrFee(transaction.getAmount());
		BigDecimal totalDebit = transaction.getAmount().add(debitFee);

		BigDecimal debitBalance = savedOriginatingAccount.getBalance().subtract(totalDebit);

		if (debitBalance.doubleValue() < 0) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("Error: Insufficient funds. Balance in account is: "
					+ originatingBalance.toPlainString() + ". Transaction not persisted.");
			return transaction;
		}
		// Decrease balance in originating account.
		savedOriginatingAccount.setBalance(originatingBalance.subtract(totalDebit));

		log.debug("**** Payment from account: " + savedOriginatingAccount.getId().toString() + " Balance: "
				+ originatingBalance.toPlainString() + ", New Balance: "
				+ savedOriginatingAccount.getBalance().toPlainString() + ", Debit: " + totalDebit);

		// To account.
		BigDecimal destinationBalance = savedDestinationAccount.getBalance();
		BigDecimal creditInt = new BigDecimal("0");

		if (savedDestinationAccount.getType().equals(AccountType.SAVINGS)) {
			creditInt = Utils.calcCrFee(destinationBalance).add(transaction.getAmount());
		}

		savedDestinationAccount.setBalance(destinationBalance.add(creditInt).add(transaction.getAmount()));

		log.debug("**** Payment to account: " + savedDestinationAccount.getId().toString() + " Balance: "
				+ destinationBalance.toPlainString() + ", New Balance: "
				+ savedDestinationAccount.getBalance().toPlainString() + ", Total Credit: "
				+ creditInt.add(transaction.getAmount()));

		savedOriginatingAccount = accountService.updateAccount(savedOriginatingAccount);
		savedDestinationAccount = accountService.updateAccount(savedDestinationAccount);

		// Create a record of this transaction.

		Transaction savedTransaction = new Transaction();

		savedTransaction.setOriginatingAccount(savedOriginatingAccount);
		savedTransaction.setOriginatingCardNumber(savedOriginatingAccount.getCardNumber());

		savedTransaction.setDestinationAccount(savedDestinationAccount);
		savedTransaction.setDestinationCardNumber(savedDestinationAccount.getCardNumber());

		if (transaction.getProcessingBank() != null && transaction.getProcessingBank().getId() != null)
			savedTransaction.setProcessingBank(transaction.getProcessingBank());
		else
			savedTransaction.setProcessingBank(bankService.getBankById(Utils.getBankXId()));
		savedTransaction.setCustomerBank(savedDestinationAccount.getCustomer().getBank());

		savedTransaction.setReference(transaction.getExternalReference());
		savedTransaction.setAmount(transaction.getAmount());
		savedTransaction.setReference(transaction.getReference());

		savedTransaction.setTransactionType(transaction.getTransactionType());
		savedTransaction.setProcessingType(transaction.getProcessingType()); // Real-time
		savedTransaction.setStatus(TransactionStatus.PROCESSED); // Processed
		savedTransaction.setError("");
		Long transactionGroup = Utils.generateTransactionGroup();
		savedTransaction.setTransactionGroup(transactionGroup);

		log.info("**** Persisting Payment Transaction");

		Transaction mainTransaction = transactionRepository.save(savedTransaction);

		Transaction debitTransaction = new Transaction();
		// Add a transaction for origination account debit.

		debitTransaction.setOriginatingCardNumber(null);
		debitTransaction.setOriginatingAccount(null);

		debitTransaction.setDestinationAccount(savedOriginatingAccount);
		debitTransaction.setDestinationCardNumber(savedOriginatingAccount.getCardNumber());

		debitTransaction.setProcessingBank(bankService.getBankById(Utils.getBankXId()));
		debitTransaction.setCustomerBank(savedOriginatingAccount.getCustomer().getBank());

		debitTransaction.setExternalReference(transaction.getExternalReference());
		debitTransaction.setAmount(debitFee);
		debitTransaction
				.setReference("Debit Charge for payment transaction: " + mainTransaction.getId().toString() + ".");

		debitTransaction.setProcessingType(transaction.getProcessingType());
		debitTransaction.setTransactionType(TransactionType.DEBIT);
		debitTransaction.setStatus(TransactionStatus.PROCESSED);
		debitTransaction.setError("");
		debitTransaction.setTransactionGroup(transactionGroup);

		log.info("**** Persisting Payment Dr Fee");
		debitTransaction = this.processBankLevelTransaction(debitTransaction);

		// Add a transaction for destination account credit.
		if (savedDestinationAccount.getType().equals(AccountType.SAVINGS)) {

			Transaction creditTransaction = new Transaction();

			creditTransaction.setOriginatingCardNumber(null);
			creditTransaction.setOriginatingAccount(null);

			creditTransaction.setDestinationAccount(savedDestinationAccount);
			creditTransaction.setDestinationCardNumber(savedDestinationAccount.getCardNumber());

			creditTransaction.setProcessingBank(bankService.getBankById(Utils.getBankXId()));
			creditTransaction.setCustomerBank(savedDestinationAccount.getCustomer().getBank());

			creditTransaction.setExternalReference(transaction.getExternalReference());
			creditTransaction.setAmount(creditInt);
			creditTransaction.setReference("Interest on Savings account: " + mainTransaction.getId().toString() + " .");

			creditTransaction.setProcessingType(transaction.getProcessingType());
			creditTransaction.setTransactionType(TransactionType.CREDIT);
			creditTransaction.setStatus(TransactionStatus.PROCESSED);
			creditTransaction.setError("");
			creditTransaction.setTransactionGroup(transactionGroup);

			log.info("**** Persisting PaymentCredit Fee");
			creditTransaction = this.processBankLevelTransaction(creditTransaction);
		}

		return mainTransaction;
	}

	@Override
	@Transactional
	public Transaction processBankLevelTransaction(Transaction transaction) {

		log.debug("**** Creating Single Customer Transaction. ");
		if (transaction.getDestinationAccount() == null || transaction.getDestinationAccount().getId() == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(
					"**** Error: Customer Account is missing (Destination Account). Transaction not persisted.");
			return transaction;
		}
		
		if (transaction.getExternalReference() == null && transaction.getProcessingBank().getId() != Utils.getBankXId()) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(
					"**** Error: External References must be provided for external bank transactions. Transaction not persisted.");
			return transaction;
		}

		Account savedAccount;

		try {
			savedAccount = accountService.getAccountById(transaction.getDestinationAccount().getId());
		} catch (Exception Ex) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(
					"**** Error: Cannot access customer account (destination account). Transaction not persisted.");
			return transaction;
		}

		BigDecimal oldBalance = new BigDecimal("0.00");
		BigDecimal newBalance = new BigDecimal("0.00");
		oldBalance = savedAccount.getBalance();

		// Debit
		if (transaction.getTransactionType().equals(TransactionType.DEBIT)
				|| transaction.getTransactionType().equals(TransactionType.FEE)) {
			newBalance = oldBalance.subtract(transaction.getAmount());

			if (newBalance.doubleValue() < 0) {
				transaction.setStatus(TransactionStatus.ERROR);
				transaction.setError("**** Error: Insufficient funds. Balance in account is: "
						+ oldBalance.toPlainString() + ". Transaction not persisted.");
				return transaction;
			}
		}

		// Credit
		if (transaction.getTransactionType().equals(TransactionType.CREDIT)
				|| transaction.getTransactionType().equals(TransactionType.INTEREST)) {

			newBalance = oldBalance.add(transaction.getAmount());
		}

		log.debug(
				"**** Dr/Cr Transaction: " + savedAccount.getId().toString() + " Balance: " + oldBalance.toPlainString()
						+ ", New Balance: " + newBalance.toPlainString() + ", Debit: " + transaction.getAmount());

		// Decrease balance in originating account.
		savedAccount.setBalance(newBalance);

		savedAccount = accountService.updateAccount(savedAccount);

		// Create Transaction.

		Transaction savedTransaction = new Transaction();
		savedTransaction.setOriginatingAccount(null);
		if (transaction.getOriginatingCardNumber() != null)
			savedTransaction.setOriginatingCardNumber(savedAccount.getCardNumber());

		savedTransaction.setDestinationAccount(savedAccount);
		savedTransaction.setDestinationCardNumber(savedAccount.getCardNumber());

		if (transaction.getProcessingBank() != null)
			savedTransaction.setProcessingBank(transaction.getProcessingBank());
		else
			// Default Processing Bank to BankX.
			savedTransaction.setProcessingBank(bankService.getBankById(Utils.getBankXId()));
		
		savedTransaction.setCustomerBank(savedAccount.getCustomer().getBank());

		savedTransaction.setExternalReference(transaction.getExternalReference());
		savedTransaction.setAmount(transaction.getAmount());
		savedTransaction.setReference(transaction.getReference());

		savedTransaction.setTransactionType(transaction.getTransactionType());
		savedTransaction.setProcessingType(transaction.getProcessingType());
		savedTransaction.setStatus(TransactionStatus.PROCESSED);
		savedTransaction.setError("");
		savedTransaction.setTransactionGroup(Utils.generateTransactionGroup());
		log.debug("**** Persisting Dr/Cr transaction");
		return transactionRepository.save(savedTransaction);

	}

	@Override
	public List<Transaction> reconcileTransactions(List<Transaction> transactions) {
		List<Transaction> processedTransactions = new ArrayList<>();
		Transaction processedTransaction = new Transaction();

		for (Transaction transaction : transactions) {
			processedTransaction = reconcileTransaction(transaction);
			processedTransactions.add(processedTransaction);
		}

		return processedTransactions;
	}

	@Override
	public Transaction reconcileTransaction(Transaction transaction) {

		log.debug("**** Starting Reconcile transaction");

		if (transaction.getStatus() != null && !transaction.getStatus().equals(TransactionStatus.RECONCILING)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.INVALID_STATUS + ":" + ReconciliationError.INVALID_STATUS.getDescription());
			return transaction;
		}

		if (transaction.getProcessingBank() == null || transaction.getProcessingBank().getId() ==null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.PROC_BANK_NULL + ":" + ReconciliationError.PROC_BANK_NULL.getDescription());
			return transaction;
		}


		if (transaction.getExternalReference() == null || transaction.getExternalReference().isEmpty()) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.EXT_REF_NULL + ":" + ReconciliationError.EXT_REF_NULL.getDescription());
			return transaction;
		}

		// Lookup external reference

		List<Transaction> savedTransactions = transactionRepository
				.findByExternalReference(transaction.getExternalReference());

		if (savedTransactions == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.TRANS_NOT_FOUND + ":" + ReconciliationError.TRANS_NOT_FOUND.getDescription());
			return transaction;
		}

		if (savedTransactions.size() > 1) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.DUPLICATE_TRANS + ":" + ReconciliationError.DUPLICATE_TRANS.getDescription());
			return transaction;
		}

		Transaction savedTransaction = savedTransactions.get(0);

		if (savedTransaction == null) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError("");
			return transaction;
		}

		if (savedTransaction.getProcessingBank() != null && !transaction.getProcessingBank().equals(savedTransaction.getProcessingBank())) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.PROC_BANKS_DIFFER + ":" + ReconciliationError.PROC_BANKS_DIFFER.getDescription());
			return transaction;
		}

		if (savedTransaction.getStatus().equals(TransactionStatus.RECONCILED)) {
			transaction.setStatus(TransactionStatus.RECONCILED);
			transaction.setError(ReconciliationError.ALREADY_RECONCILED + ":" + ReconciliationError.ALREADY_RECONCILED.getDescription());
			return transaction;
		}

		if (!savedTransaction.getStatus().equals(TransactionStatus.PROCESSED)) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(ReconciliationError.INVALID_TRANS_STATUS + ":" + ReconciliationError.INVALID_TRANS_STATUS.getDescription());
			return transaction;
		}

		String error = new String();

		if (savedTransaction.getAmount() != transaction.getAmount()) {
			error.concat(ReconciliationError.AMTS_DIFFER + ":" + ReconciliationError.AMTS_DIFFER.getDescription());
		}

		if ((transaction.getTransactionType() != null && savedTransaction.getTransactionType() != null)
				&& !transaction.getTransactionType().equals(savedTransaction.getTransactionType())) {
			error.concat(", " + ReconciliationError.TRANS_TYPES_DIFFER + ":" + ReconciliationError.TRANS_TYPES_DIFFER.getDescription());
		}

		if ((transaction.getOriginatingCardNumber() != null && savedTransaction.getOriginatingCardNumber() != null)
				&& !transaction.getOriginatingCardNumber().equals(savedTransaction.getOriginatingCardNumber())) {
			error.concat(", " + ReconciliationError.ORIGINATING_CARDS_DIFFER + ":" + ReconciliationError.ORIGINATING_CARDS_DIFFER.getDescription());
		}

		if ((transaction.getDestinationCardNumber() != null && savedTransaction.getDestinationCardNumber() != null)
				&& !transaction.getDestinationCardNumber().equals(savedTransaction.getDestinationCardNumber())) {
			error.concat(", " + ReconciliationError.DESTINATION_CARDS_DIFFER + ":" + ReconciliationError.DESTINATION_CARDS_DIFFER.getDescription());
		}

		if (!error.isBlank()) {
			transaction.setStatus(TransactionStatus.ERROR);
			transaction.setError(error);
			return transaction;
		}
		transaction.setStatus(TransactionStatus.RECONCILED);
		return transaction;
	}

	@Override
	public Transaction updateTransaction(Transaction transaction) {
		// Get transaction for data store and update.
		Transaction existingTransaction = transactionRepository.findById(transaction.getId()).get();
		if (transaction.getTransactionGroup() != null) {
			existingTransaction.setTransactionGroup(transaction.getTransactionGroup());
		}

		if (transaction.getOriginatingAccount() != null && transaction.getOriginatingAccount().getId() != null)
			existingTransaction.setOriginatingAccount(transaction.getOriginatingAccount());
		if (transaction.getOriginatingCardNumber() != null)
			existingTransaction.setOriginatingCardNumber(transaction.getOriginatingCardNumber());

		if (transaction.getDestinationAccount() != null && transaction.getDestinationAccount().getId() != null)
			existingTransaction.setDestinationAccount(transaction.getDestinationAccount());
		if (transaction.getDestinationCardNumber() != null)
			existingTransaction.setDestinationCardNumber(transaction.getDestinationCardNumber());

		if (transaction.getProcessingBank() != null && transaction.getProcessingBank().getId() != null)
			existingTransaction.setProcessingBank(transaction.getProcessingBank());
		if (transaction.getCustomerBank() != null && transaction.getCustomerBank().getId() != null)
			existingTransaction.setCustomerBank(transaction.getCustomerBank());

		if (transaction.getExternalReference() != null)
			existingTransaction.setExternalReference(transaction.getExternalReference());
		if (transaction.getAmount() != null)
			existingTransaction.setAmount(transaction.getAmount());
		if (transaction.getReference() != null)
			existingTransaction.setReference(transaction.getReference());

		if (transaction.getTransactionType() != null)
			existingTransaction.setTransactionType(transaction.getTransactionType());
		if (transaction.getProcessingType() != null)
			existingTransaction.setProcessingType(transaction.getProcessingType());
		if (transaction.getStatus() != null)
			existingTransaction.setStatus(transaction.getStatus());
		if (transaction.getError() != null)
			existingTransaction.setError(transaction.getError());
		return transactionRepository.save(existingTransaction);
	}

	@Override
	public void deleteTransaction(Long transactionId) {
		transactionRepository.deleteById(transactionId);
	}

}