package domain.entity;

public class Transaction {
    private final int id;
    private String description;
    private long amount;
    private TransactionType type;

    public Transaction(int id, String description, long amount, TransactionType type) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeAmount(long amount) {
        this.amount = amount;
    }

    public void changeType(TransactionType type) {
        this.type = type;
    }
}