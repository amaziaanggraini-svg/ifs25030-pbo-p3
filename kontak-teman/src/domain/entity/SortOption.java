package domain.entity;

import domain.exception.ValidationException;
import java.util.Comparator;

public enum SortOption {
    NAME_ASC(1, Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER)),
    NAME_DESC(2, Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER).reversed());

    private final int code;
    private final Comparator<Contact> comparator;

    SortOption(int code, Comparator<Contact> comparator) {
        this.code = code;
        this.comparator = comparator;
    }

    /** Nomor pilihan yang ditampilkan di menu pengurutan. */
    public int code() {
        return code;
    }

    public Comparator<Contact> comparator() {
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