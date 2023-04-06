package za.co.cajones.bankx.api.service;

import java.util.List;

import za.co.cajones.bankx.dto.TransactionDto;


public interface ApiTransactionService {

	List<TransactionDto> getAllTransactionsByBankId(Long id);
	
	TransactionDto getTransactionsById(Long id);
	
    List<TransactionDto> getTransactionByExternalReference(String externalReference);
	
	//Boolean addTransactions(List<TransactionDto> transactions);
	
	//Boolean addTransaction(TransactionDto transaction);
	
	//Boolean addReconciliations(List<TransactionDto> transactions);
	

}