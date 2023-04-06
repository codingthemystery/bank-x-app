package za.co.cajones.bankx.model;

public enum BankStatus {
    ACTIVE("Active"),
    PENDING_DELETION("Pending deletion");

    private final String description;

    BankStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}