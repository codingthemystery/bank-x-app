package za.co.cajones.bankx.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.cajones.bankx.model.CustomerStatus;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "first_name", nullable = false)
	String firstName;

	@Column(name = "last_name", nullable = false)
	String lastName;

	@Column(nullable = false)
	String address;

	@Column(nullable = false)
	String email;

	@Column(name = "phone_number", nullable = false)
	String phoneNumber;

	@Column(name = "token")
	String token;

	@Column
	CustomerStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id")
	Bank bank;

	@JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

	public Customer(String firstName, String lastName, String address, String email, String phoneNumber, String token, CustomerStatus status, Bank bank) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.token = token;
		if (status == null)
			this.status = CustomerStatus.ACTIVE;
		else this.status = status;
		this.status = status;
		this.bank = bank;
	}

}