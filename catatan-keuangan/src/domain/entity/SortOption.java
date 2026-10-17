package domain.entity;

import domain.exception.ValidationException;
import java.util.Comparator;

public enum SortOption {
    AMOUNT_ASC(1, Comparator.comparingLong(Transaction::getAmount)),
    AMOUNT_DESC(2, Comparator.comparingLong(Transaction::getAmount).reversed()),
    INCOME_FIRST(3, Comparator.comparing((Transaction t) -> t.getType() == TransactionType.INCOME ? 0 : 1)),
    EXPENSE_FIRST(4, Comparator.comparing((Transaction t) -> t.getType() == TransactionType.EXPENSE ? 0 : 1));

    private final int code;
    private final Comparator<Transaction> comparator;

    SortOption(int code, Comparator<Transaction> comparator) {
        this.code = code;
        this.comparator = comparator;
    }

    /** Nomor pilihan yang ditampilkan di menu pengurutan. */
    public int code() {
        return code;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }

    /** Mencari opsi berdasarkan nomor pilihan; melempar ValidationException jika tidak ada. */
    public static SortOption fromCode(int code) throws ValidationException {
        for (SortOption option : values()) {
            if (option.code == code) {
                return option;
            }
        }
        throw new ValidationException("Pilihan tidak valid");
    }
}