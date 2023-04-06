package za.co.cajones.bankx.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.entity.Customer;
import za.co.cajones.bankx.service.CustomerService;


@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Get all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers(){
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get Customer by ID
    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") Long customerId){
        Customer customer = customerService.getCustomerById(customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    // Get Accounts by Customer ID
    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable("id") Long id){
        List<Account> accounts = customerService.getAccountsByCustomerId(id);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Onboard a customer
    @PostMapping
    public ResponseEntity<Customer> onboardCustomer(@RequestBody Customer customer){
        Customer savedCustomer = customerService.onboardCustomer(customer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    //Update Customer
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Long customerId,
                                           @RequestBody Customer customer){
        customer.setId(customerId);
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long customerId){
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>("Customer successfully deleted!", HttpStatus.OK);
    }
}