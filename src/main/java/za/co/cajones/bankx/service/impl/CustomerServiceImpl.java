package za.co.cajones.bankx.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.components.Utils;
import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.entity.Bank;
import za.co.cajones.bankx.entity.Customer;
import za.co.cajones.bankx.entity.Transaction;
import za.co.cajones.bankx.model.AccountType;
import za.co.cajones.bankx.model.CustomerStatus;
import za.co.cajones.bankx.model.ProcessingType;
import za.co.cajones.bankx.model.TransactionStatus;
import za.co.cajones.bankx.model.TransactionType;
import za.co.cajones.bankx.repository.AccountRepository;
import za.co.cajones.bankx.repository.CustomerRepository;
import za.co.cajones.bankx.repository.TransactionRepository;
import za.co.cajones.bankx.service.AccountService;
import za.co.cajones.bankx.service.BankService;
import za.co.cajones.bankx.service.CustomerService;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BankService bankService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionRepository transactionRepository;


	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Customer getCustomerById(Long customerId) {
		Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
		return optionalCustomer.get();
	}

    @Override
    public List<Account> getAccountsByCustomerId(Long customerId) {
    	return accountRepository.getAccountsByCustomerId(customerId);
    }

	@Override
	public Customer createCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	@Transactional
	public Customer onboardCustomer(Customer customer) {

		log.debug("Onboarding customer: " + customer.getFirstName());

		Customer savedCustomer = customerRepository.save(customer);
		// Create accounts for the new customer.

		Account savingsAccount = new Account(AccountType.SAVINGS, Utils.getJoininingBonus(), savedCustomer);
		Account savedSavingsAccount = accountService.createAccount(savingsAccount);

		Account currentAccount = new Account(AccountType.CURRENT, new BigDecimal("0"), savedCustomer);
		accountService.createAccount(currentAccount);

		// Credit a joining bonus credit transaction.
		Transaction transaction = new Transaction();

		log.debug("**** Bank Id: " + Utils.getBankXId().toString());
		Bank bankX = new Bank();
		bankX = bankService.getBankById(Utils.getBankXId());

		//if (bankX == null || bankX.getId() == null || bankX.getName() != "BankX")
			//bankX = bankService.getBankByName("BankX");

		transaction.setProcessingBank(bankX);
		transaction.setOriginatingCardNumber(null);
		transaction.setOriginatingAccount(null);

		transaction.setDestinationAccount(savedSavingsAccount);
		transaction.setCustomerBank(savedSavingsAccount.getCustomer().getBank());
		transaction.setDestinationCardNumber(savedSavingsAccount.getCardNumber());
		transaction.setExternalReference(transaction.getExternalReference());
		transaction.setAmount(Utils.getJoininingBonus());
		transaction.setReference("Joining Bonus");
		transaction.setProcessingType(ProcessingType.REALTIME);
		transaction.setTransactionType(TransactionType.CREDIT);
		transaction.setStatus(TransactionStatus.PROCESSED);
		transaction.setError("");
		transaction.setTransactionGroup(Utils.generateTransactionGroup());
		transactionRepository.save(transaction);


		return savedCustomer;
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		Customer existingCustomer = customerRepository.findById(customer.getId()).get();
		if (customer.getFirstName() != null)
			existingCustomer.setFirstName(customer.getFirstName());
		if (customer.getLastName() != null)
			existingCustomer.setLastName(customer.getLastName());
		if (customer.getAddress() != null)
			existingCustomer.setAddress(customer.getAddress());
		if (customer.getEmail() != null)
			existingCustomer.setEmail(customer.getEmail());
		if (customer.getPhoneNumber() != null)
			existingCustomer.setPhoneNumber(customer.getPhoneNumber());
		if (customer.getBank() != null && customer.getBank().getId() != null )
			existingCustomer.setBank(customer.getBank());
		if (customer.getStatus() != null)
			existingCustomer.setStatus(CustomerStatus.ACTIVE);
		if (customer.getToken() != null)
			existingCustomer.setToken(customer.getToken());
		Customer updatedCustomer = customerRepository.save(existingCustomer);
		return updatedCustomer;
	}

	@Override
	public void deleteCustomer(Long customerId) {
		customerRepository.deleteById(customerId);
	}


}