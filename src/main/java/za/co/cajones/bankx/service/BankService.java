package za.co.cajones.bankx.service;

import java.util.List;

import za.co.cajones.bankx.entity.Bank;

public interface BankService {

	Bank createBank(Bank bank);

	Bank getBankById(Long bankId);

	Bank getBankByName(String name);

	List<Bank> getAllBanks();

	Bank updateBank(Bank bank);

	void deleteBank(Long bankId);
}