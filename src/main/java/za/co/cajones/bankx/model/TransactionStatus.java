package za.co.cajones.bankx.model;

public enum TransactionStatus {
    INITIALIZED("Initialized"),
    INPROGRESS("In Progress"),
    PROCESSED("Processed"),
    RECONCILED("Reconciled"),
    RECONCILING("Reonciling"),
    ERROR("Error");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}