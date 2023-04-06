package za.co.cajones.bankx.service;

import java.util.List;

import za.co.cajones.bankx.entity.Account;

public interface AccountService {

	Account createAccount(Account account);

	Account getAccountById(Long accountId);

	List<Account> getAccountsByCustomerId(Long id);

	Account getAccountByCardNumber (Long cardNumber);

	// Account getBycustomerId(Long customerId);

	// Account getBycustomerAccount(Long customerAccount);

	List<Account> getAllAccounts();

	Account updateAccount(Account account);

	void deleteAccount(Long accountId);

}