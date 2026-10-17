package framework.view;

import adapter.presenter.ItemPresenter;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.ItemUseCase;

/**
 * Tampilan konsol inventaris barang.
 * Menerima input, memanggil use case, dan menangani exception domain.
 */
public class ItemView {
    private final ItemUseCase itemUseCase;
    private final ItemPresenter presenter;

    public ItemView(ItemUseCase itemUseCase, ItemPresenter presenter) {
        this.itemUseCase = itemUseCase;
        this.presenter = presenter;
    }

    public void show() {
        boolean running = true;
        while (running) {
            presenter.showItems(itemUseCase.getAllItems());
            presenter.showMenu();

            String choice = InputUtil.input("Pilih").trim();
            presenter.showBlankLine();

            switch (choice) {
                case "1" -> addItem();
                case "2" -> updateStock();
                case "3" -> searchItem();
                case "4" -> sortItem();
                case "5" -> removeItem();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                presenter.showBlankLine();
            }
        }
    }

    private void addItem() {
        presenter.showSectionTitle("Menambah Barang");

        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        // Jumlah divalidasi segera, sebelum menanyakan kategori
        int quantity;
        try {
            quantity = parseQuantity(InputUtil.input("Jumlah"));
        } catch (ValidationException e) {
            presenter.showInvalidQuantity();
            return;
        }

        String category = InputUtil.input("Kategori (x Jika Batal)");
        if (InputUtil.isCancel(category)) {
            return;
        }

        try {
            Item item = itemUseCase.addItem(name, quantity, category);
            presenter.showAddSuccess(item);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void updateStock() {
        presenter.showSectionTitle("Mengubah Stok");

        String rawId = InputUtil.input("ID Barang yang diubah (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        // ID divalidasi segera, sebelum menanyakan jumlah baru
        int id;
        try {
            id = parseId(rawId);
        } catch (ValidationException e) {
            presenter.showInvalidId();
            return;
        }

        String rawQty = InputUtil.input("Jumlah Baru (Kosongkan jika tidak ingin mengubah)");
        // null berarti stok tidak diubah
        Integer quantity;
        try {
            quantity = rawQty.isBlank() ? null : parseQuantity(rawQty);
        } catch (ValidationException e) {
            presenter.showInvalidQuantity();
            return;
        }

        try {
            itemUseCase.updateItemStock(id, quantity);
            presenter.showUpdateStockSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void searchItem() {
        presenter.showSectionTitle("Mencari Barang");

        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (InputUtil.isCancel(keyword)) {
            return;
        }

        presenter.showSearchResults(itemUseCase.searchItems(keyword), keyword);
    }

    private void sortItem() {
        presenter.showSectionTitle("Mengurutkan Barang");
        presenter.showSortMenu();

        String input = InputUtil.input("Pilih");
        presenter.showBlankLine();
        if (InputUtil.isCancel(input)) {
            return;
        }

        SortOption option = mapSortOption(input.trim());
        if (option == null) {
            presenter.showInvalidSortOption();
            return;
        }
        presenter.showSortedItems(itemUseCase.sortItems(option));
    }

    private void removeItem() {
        presenter.showSectionTitle("Menghapus Barang");

        String rawId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        try {
            int id = parseId(rawId);
            itemUseCase.removeItem(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    // ---------- Parsing input ----------

    /** Mengonversi string menjadi ID; melempar ValidationException jika bukan angka. */
    private int parseId(String value) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("ID harus berupa angka numerik");
        }
    }

    /** Mengonversi string menjadi jumlah stok (numerik dan > 0). */
    private int parseQuantity(String value) throws ValidationException {
        try {
            int quantity = Integer.parseInt(value.trim());
            if (quantity <= 0) {
                throw new ValidationException("Jumlah stok tidak valid");
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah stok tidak valid");
        }
    }

    /** Memetakan pilihan menu (1-4) ke SortOption; null jika tidak valid. */
    private SortOption mapSortOption(String input) {
        return switch (input) {
            case "1" -> SortOption.NAME_ASC;
            case "2" -> SortOption.NAME_DESC;
            case "3" -> SortOption.QUANTITY_ASC;
            case "4" -> SortOption.QUANTITY_DESC;
            default -> null;
        };
    }
}
