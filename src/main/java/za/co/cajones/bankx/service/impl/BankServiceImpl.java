package za.co.cajones.bankx.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import za.co.cajones.bankx.entity.Bank;
import za.co.cajones.bankx.model.BankStatus;
import za.co.cajones.bankx.repository.BankRepository;
import za.co.cajones.bankx.service.BankService;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BankRepository bankRepository;

	@Override
	public List<Bank> getAllBanks() {
		return bankRepository.findAll();
	}

	@Override
	public Bank getBankById(Long bankId) {
		Optional<Bank> optionalBank = bankRepository.findById(bankId);
		return optionalBank.get();
	}

	@Override
	public Bank getBankByName(String name) {
		return bankRepository.getBankByName(name);
	}

	@Override
	public Bank createBank(Bank bank) {
		bank.setStatus(BankStatus.ACTIVE);
		return bankRepository.save(bank);
	}

	@Override
	public Bank updateBank(Bank bank) {
		Bank existingBank = bankRepository.findById(bank.getId()).get();
		if (bank.getName() != null)
			existingBank.setName(bank.getName());
		if (bank.getAddress() != null)
		existingBank.setAddress(bank.getAddress());
		if (bank.getSwiftCode() != null)
		existingBank.setSwiftCode(bank.getSwiftCode());
		if (bank.getEmail() != null)
		existingBank.setEmail(bank.getEmail());
		if (bank.getPhoneNumber() != null)
		existingBank.setPhoneNumber(bank.getPhoneNumber());
		if (bank.getStatus() != null)
		existingBank.setStatus(bank.getStatus());
		Bank updatedBank = bankRepository.save(existingBank);
		return updatedBank;
	}

	@Override
	public void deleteBank(Long bankId) {
		Bank existingBank = bankRepository.findById(bankId).get();
		bankRepository.delete(existingBank);
	}
}