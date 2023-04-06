package za.co.cajones.bankx.model;

public enum TransactionType {
    CREDIT("Credit"),
    DEBIT("Debit"),
    PAYMENT("Payment"),
    INTEREST("Interest"),
    FEE("Fee"),
    TRANSFER("Transfer");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}