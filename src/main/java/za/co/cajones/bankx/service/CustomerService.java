package za.co.cajones.bankx.service;

import java.util.List;

import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.entity.Customer;

public interface CustomerService {

	Customer onboardCustomer(Customer customer);

	Customer createCustomer(Customer customer);

	Customer getCustomerById(Long customerId);

	List<Account> getAccountsByCustomerId(Long id);

	List<Customer> getAllCustomers();

	Customer updateCustomer(Customer customer);

	void deleteCustomer(Long customerId);
}