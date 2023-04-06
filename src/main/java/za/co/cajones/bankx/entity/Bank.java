package za.co.cajones.bankx.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.cajones.bankx.model.BankStatus;

@Data
@NoArgsConstructor
@Entity
@Table(name = "banks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bank {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column(nullable = false)
	String name;

	@Column(nullable = false)
	String address;

	@Column(name = "swift_code", unique = true, nullable = false, length = 20)
	String swiftCode;

	@Column(nullable = false)
	String email;

	@Column(name = "phone_number", nullable = false)
	String phoneNumber;

	@Column
	BankStatus status;

	@JsonIgnore
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customer> customers;

	@JsonIgnore
    @OneToMany(mappedBy = "processingBank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> processingBanks;

	@JsonIgnore
    @OneToMany(mappedBy = "customerBank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> customerBanks;


	public Bank(String name, String address, String swiftCode, String email, String phoneNumber, BankStatus status) {
		this.name = name;
		this.address = address;
		this.swiftCode = swiftCode;
		this.email = email;
		this.phoneNumber = phoneNumber;
		if (status == null)
			this.status = BankStatus.ACTIVE;
		else this.status = status;
		this.status = status;
	}

}
