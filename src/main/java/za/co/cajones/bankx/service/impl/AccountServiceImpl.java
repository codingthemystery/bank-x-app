package za.co.cajones.bankx.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import za.co.cajones.bankx.components.Utils;
import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.model.AccountStatus;
import za.co.cajones.bankx.repository.AccountRepository;
import za.co.cajones.bankx.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@Override
	public Account getAccountById(Long accountId) {
		Optional<Account> optionalAccount = accountRepository.findById(accountId);
		return optionalAccount.get();
	}

	@Override
	public List<Account> getAccountsByCustomerId(Long customerId) {
		return accountRepository.getAccountsByCustomerId(customerId);
	}


	@Override
	public Account getAccountByCardNumber(Long cardNumber) {
		return accountRepository.getAccountByCardNumber(cardNumber);
	}

	@Override
	public Account createAccount(Account account) {
		if (account.getCardNumber() == null)
			account.setCardNumber(Utils.generateAccountNumber());
		if (account.getBalance() == null)
			account.setBalance(new BigDecimal("0.00"));
		if (account.getStatus() == null)
			account.setStatus(AccountStatus.ACTIVE);
		return accountRepository.save(account);
	}

	@Override
	public Account updateAccount(Account account) {
		Account existingAccount = accountRepository.findById(account.getId()).get();
		if (account.getCardNumber() != null)
			existingAccount.setCardNumber(account.getCardNumber());
		if (account.getType() != null)
			existingAccount.setType(account.getType());
		if (account.getBalance() != null)
			existingAccount.setBalance(account.getBalance());
		if (account.getStatus() != null)
			existingAccount.setStatus(account.getStatus());
		if (account.getCustomer() != null && account.getCustomer().getId() != null)
			existingAccount.setCustomer(account.getCustomer());
		Account updatedAccount = accountRepository.save(existingAccount);
		return updatedAccount;
	}

	@Override
	public void deleteAccount(Long accountId) {
		accountRepository.deleteById(accountId);
	}

}