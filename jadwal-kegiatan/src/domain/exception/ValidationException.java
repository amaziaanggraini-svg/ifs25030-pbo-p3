package domain.exception;

/** Dilempar ketika input atau data melanggar aturan validasi domain. */
public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
