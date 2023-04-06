package za.co.cajones.bankx.components;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.springframework.stereotype.Component;

import za.co.cajones.bankx.entity.Account;
import za.co.cajones.bankx.entity.Bank;
import za.co.cajones.bankx.entity.Customer;

@Component
public final class Utils {

	final static String BANKX_SWIFT = "BANKXZAJJ";
	final static Long BANKX_ID = 1L;
	final static BigDecimal PAYMENT_CR_INT = new BigDecimal("0.5");
	final static BigDecimal PAYMENT_DR_INT = new BigDecimal("0.05");

	final static BigDecimal JOINING_BONUS = new BigDecimal("500.00");

	private Utils() {}

	public static boolean banksIdsMatch(Long firstBank, Long secondBank) {
		// Return false
		if (firstBank == null || secondBank == null)
			return false;
		if (firstBank.equals(secondBank))
			return true;
		else
			return false;

	}

	public static Long getBankXId() {
		return  BANKX_ID;
	}

	public static BigDecimal getJoininingBonus() {
		return JOINING_BONUS;
	}

	public static boolean isBankX(Bank bank) {

		if (bank.getId() == null)
			return false;

		if (bank.getId().equals(BANKX_ID))
			return true;
		else
			return false;

	}

	public static boolean isSameAccount(Account firstAccount, Account secondAccount) {

		return (firstAccount.getId() != null && secondAccount.getId() != null)
				&& firstAccount.getId().equals(secondAccount.getId());

	}

	public static boolean isSameCustomer(Customer firstCustomer, Customer secondCustomer) {

		return (firstCustomer.getId() != null && secondCustomer.getId() != null)
				&& firstCustomer.getId().equals(secondCustomer.getId());

	}

	public static BigDecimal calcDrFee(BigDecimal amount) {

		// Calculate the fee by multiplying the amount by the percentage
		BigDecimal fee = amount.multiply(PAYMENT_DR_INT);

		// Round the fee to 2 decimal places
		return fee.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

	}

	public static BigDecimal calcCrFee(BigDecimal amount) {

		// Calculate the fee by multiplying the amount by the percentage
		BigDecimal fee = amount.multiply(PAYMENT_CR_INT);

		// Round the fee to 2 decimal places
		return fee.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

	}

	public static Long generateAccountNumber() {
		Random rand = new Random();
		long creditCardNumber = rand.nextLong() % 10000000000000000L;
		while (creditCardNumber < 0) {
			creditCardNumber += 10000000000000000L;
		}
		return creditCardNumber;
	}

	public static Long generateTransactionGroup() {
		Random rand = new Random();
		long transactionGroup = rand.nextLong() % 10000000000000000L;
		while (transactionGroup < 0) {
			transactionGroup += 10000000000000000L;
		}
		return transactionGroup;
	}

	public static BigDecimal calculatePercentage(BigDecimal amount, BigDecimal percentage) {
		BigDecimal result = amount.multiply(percentage);
		return result.setScale(2, RoundingMode.HALF_UP);
	}

}
