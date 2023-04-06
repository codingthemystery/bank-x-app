package za.co.cajones.bankx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.cajones.bankx.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


}