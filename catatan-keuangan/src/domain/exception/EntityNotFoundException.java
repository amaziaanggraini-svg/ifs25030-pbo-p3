package domain.exception;

/** Dilempar ketika entity dengan ID tertentu tidak ditemukan. */
public class EntityNotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    private final int id;

    public EntityNotFoundException(int id) {
        super("Transaksi dengan ID " + id + " tidak ditemukan");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
