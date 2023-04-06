package za.co.cajones.bankx.model;

public enum CustomerStatus {
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String description;

    CustomerStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}