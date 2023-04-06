package za.co.cajones.bankx.model; 

public enum ProcessingType {
    REALTIME("Real-time"),
    BATCH("Batch");

    private final String description;

    ProcessingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}