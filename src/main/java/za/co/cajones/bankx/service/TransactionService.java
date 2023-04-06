package za.co.cajones.bankx.service;

import java.util.List;

import za.co.cajones.bankx.entity.Transaction;


public interface TransactionService {

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(Long transactionId);

    //List<Transaction> findByProcessingBankOrdersByExternalReferenceAsc(Long id);

    List<Transaction> getOriginatingTransactionsByAccountId(Long accountId);

	List<Transaction> addTransactions(List<Transaction> transactions);

    Transaction createTransaction(Transaction transaction);

    Transaction processTransferTransaction(Transaction transaction);

    Transaction processPaymentTransaction(Transaction transaction);

    Transaction processBankLevelTransaction(Transaction transaction);

	List<Transaction> reconcileTransactions(List<Transaction> transactions);

	Transaction reconcileTransaction(Transaction transaction);

    Transaction updateTransaction(Transaction transaction);

    //Transaction updateTransactionStatus(String status);

    void deleteTransaction(Long transactionId);


}