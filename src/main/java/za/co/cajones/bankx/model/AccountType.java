package za.co.cajones.bankx.model;

public enum AccountType {
    SAVINGS("Savings"),
    CURRENT("Current"),
    CREDIT("Credit");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}