package za.co.cajones.bankx.model;

public enum ReconciliationError {
    INVALID_STATUS("Only RECONCILING is valid"),
    PROC_BANK_NULL("Processing bank not provided"),
    EXT_REF_NULL("External Reference not provided"),
    TRANS_NOT_FOUND("Transaction not in database"),
    DUPLICATE_TRANS("Duplicate Transactions found in database"),
    ALREADY_RECONCILED("Already reconciled"),
    INVALID_TRANS_STATUS("Status is not Processed or Reconciled"),
    PROC_BANKS_DIFFER("Processing banks differ"),
    AMTS_DIFFER("Transaction Amount Differs"),
    ORIGINATING_CARDS_DIFFER("Originating Card Numbers differ"),
    DESTINATION_CARDS_DIFFER("Destination Card Numbers differ"),
    TRANS_TYPES_DIFFER("Transaction Types Differs"),
    ERROR("Error");

    private final String description;

    ReconciliationError(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}