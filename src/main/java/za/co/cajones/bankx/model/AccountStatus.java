package za.co.cajones.bankx.model;

public enum AccountStatus {
    ACTIVE("Active"),
    ON_HOLD("On Hold");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}