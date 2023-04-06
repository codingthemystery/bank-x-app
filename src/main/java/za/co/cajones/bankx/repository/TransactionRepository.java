package za.co.cajones.bankx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import za.co.cajones.bankx.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	//@Query(value = "SELECT id FROM banks WHERE name = :name", nativeQuery = true)

	@Query("SELECT t FROM Transaction t WHERE t.externalReference = ?1")
	List<Transaction> findByExternalReference(String externalReference);

	// Many to one
	@Query("SELECT t FROM Transaction t WHERE t.processingBank.id = ?1")
	List<Transaction> findByProcessingBankOrdersByExternalReferenceAsc(Long id);

	
	@Query("SELECT t FROM Transaction t WHERE t.originatingAccount.id= ?1")
	List<Transaction> getOriginatingTransactionsByAccountIdOrderByGroupIdAsc(Long id);

	//@Query("SELECT t FROM Transaction t JOIN processingBanks b WHERE t.processingBank.id = ?1")
	//List<Transaction> findByProcessingBankOrderByExternalReferenceAsc(Long id);
}