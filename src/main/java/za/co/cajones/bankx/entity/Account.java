package za.co.cajones.bankx.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.model.AccountStatus;
import za.co.cajones.bankx.model.AccountType;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "card_number", unique = true, nullable = false, length = 20)
	Long cardNumber;

	@Column(nullable = false)
	AccountType type;

	@Column
	BigDecimal balance;

	@Column
	AccountStatus status;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	Customer customer;

	@JsonIgnore
    @OneToMany(mappedBy = "originatingAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> originatingAccounts;

	@JsonIgnore
    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> destinationAccounts;

	public Account(AccountType type, BigDecimal balance, Customer customer) {
		log.debug("Account creation: A/c Id:" + this.id + ", balance=" + balance + ", customer: " + customer.getId());
		this.type = type;
		this.balance = balance;
		this.customer = customer;
	}

	public Account(Long cardNumber, AccountType type, BigDecimal balance, AccountStatus status, Customer customer) {
		this.cardNumber = cardNumber;
		this.type = type;
		this.cardNumber = cardNumber;
		this.balance = balance;
		this.status = status;
		this.customer = customer;
	}
}