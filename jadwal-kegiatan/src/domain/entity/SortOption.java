package domain.entity;

import java.util.Comparator;

public enum SortOption {
    DAY(Comparator.comparingInt((Activity a) -> getDayIndex(a.getDay()))
            .thenComparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))),
    TIME(Comparator.comparingInt((Activity a) -> parseTimeMinutes(a.getTime()))
            .thenComparingInt(Activity::getId)),
    TITLE_ASC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER)
            .thenComparingInt(Activity::getId)),
    TITLE_DESC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER).reversed()
            .thenComparingInt(Activity::getId));

    private final Comparator<Activity> comparator;

    SortOption(Comparator<Activity> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Activity> comparator() {
        return comparator;
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