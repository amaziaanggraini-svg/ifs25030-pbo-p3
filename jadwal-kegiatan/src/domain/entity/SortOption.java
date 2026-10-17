package domain.entity;

import domain.exception.ValidationException;
import java.util.Comparator;

public enum SortOption {
    DAY(1, Comparator.comparingInt((Activity a) -> getDayIndex(a.getDay()))
            .thenComparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))),
    TIME(2, Comparator.comparingInt((Activity a) -> parseTimeMinutes(a.getTime()))
            .thenComparingInt(Activity::getId)),
    TITLE_ASC(3, Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER)
            .thenComparingInt(Activity::getId)),
    TITLE_DESC(4, Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER).reversed()
            .thenComparingInt(Activity::getId));

    private final int code;
    private final Comparator<Activity> comparator;

    SortOption(int code, Comparator<Activity> comparator) {
        this.code = code;
        this.comparator = comparator;
    }

    /** Nomor pilihan yang ditampilkan di menu pengurutan. */
    public int code() {
        return code;
    }

    public Comparator<Activity> comparator() {
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

    private static int getDayIndex(String day) {
        if (day == null) return 99;
        return switch (day.trim().toLowerCase()) {
            case "senin" -> 1;
            case "selasa" -> 2;
            case "rabu" -> 3;
            case "kamis" -> 4;
            case "jumat", "jum'at" -> 5;
            case "sabtu" -> 6;
            case "minggu" -> 7;
            default -> 99;
        };
    }

    private static int parseTimeMinutes(String time) {
        if (time == null) return 0;
        try {
            String clean = time.trim();
            if (!clean.contains(":")) return 0;
            String[] parts = clean.split(":");
            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());
            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }
}