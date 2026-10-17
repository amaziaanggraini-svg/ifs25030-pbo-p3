package domain.entity;

import java.util.Comparator;

public enum SortOption {
    NAME_ASC(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER)),
    NAME_DESC(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER).reversed()),
    QUANTITY_ASC(Comparator.comparingInt(Item::getQuantity)),
    QUANTITY_DESC(Comparator.comparingInt(Item::getQuantity).reversed());

    private final Comparator<Item> comparator;

    SortOption(Comparator<Item> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Item> comparator() {
        return comparator;
    }
}