package za.co.cajones.bankx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import za.co.cajones.bankx.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {


	@Query("SELECT a FROM Account a WHERE a.cardNumber = ?1")
	Account getAccountByCardNumber(Long cardNumber);

	// Many to one
	@Query("SELECT a FROM Account a WHERE a.customer.id = ?1")
	List<Account> getAccountsByCustomerId(Long id);

}