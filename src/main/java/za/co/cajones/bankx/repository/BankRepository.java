package za.co.cajones.bankx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import za.co.cajones.bankx.entity.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {

	@Query("SELECT b FROM Bank b WHERE name LIKE ?1")
	Bank getBankByName(String name);

}
