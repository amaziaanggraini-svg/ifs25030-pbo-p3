package domain.exception;

/** Induk semua checked exception di layer domain. */
public class DomainException extends Exception {
    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
