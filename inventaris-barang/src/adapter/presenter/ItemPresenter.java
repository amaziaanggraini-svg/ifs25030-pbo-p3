package adapter.presenter;

import domain.entity.Item;
import domain.entity.SortOption;
import java.util.List;

/**
 * Presenter yang mengubah hasil proses aplikasi menjadi teks tampilan konsol.
 * Seluruh format output ada di sini sehingga entity, use case, dan view bebas dari urusan tampilan.
 */
public class ItemPresenter {

    private static final String EMPTY_MESSAGE = "- Data barang belum tersedia!";
    private static final String NOT_FOUND_MESSAGE = "- Barang tidak ditemukan!";

    // ---------- Format dasar ----------

    private String format(Item item) {
        return String.format("[%d] %s | Stok: %d | Kategori: %s",
                item.getId(), item.getName(), item.getQuantity(), item.getCategory());
    }

    private String sortLabel(SortOption option) {
        return switch (option) {
            case NAME_ASC -> "Nama (A-Z)";
            case NAME_DESC -> "Nama (Z-A)";
            case QUANTITY_ASC -> "Jumlah (Terkecil -> Terbesar)";
            case QUANTITY_DESC -> "Jumlah (Terbesar -> Terkecil)";
        };
    }

    private void printList(List<Item> items, String header, String emptyMessage) {
        System.out.println(header);
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        items.forEach(item -> System.out.println(format(item)));
    }

    // ---------- Menu dan judul ----------

    public void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah");
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    public void showSortMenu() {
        System.out.println("Pilihan Pengurutan:");
        SortOption[] options = SortOption.values();
        for (int index = 0; index < options.length; index++) {
            System.out.printf("%d. %s%n", index + 1, sortLabel(options[index]));
        }
        System.out.println("x. Batal");
    }

    public void showSectionTitle(String title) {
        System.out.printf("[%s]%n", title);
    }

    public void showBlankLine() {
        System.out.println();
    }

    // ---------- Daftar ----------

    public void showItems(List<Item> items) {
        printList(items, "Daftar Barang:", EMPTY_MESSAGE);
    }

    public void showSearchResults(List<Item> items, String keyword) {
        printList(items, "Hasil Pencarian: \"" + keyword + "\"", NOT_FOUND_MESSAGE);
    }

    public void showSortedItems(List<Item> items) {
        printList(items, "Daftar Barang (Terurut):", EMPTY_MESSAGE);
    }

    // ---------- Pesan hasil operasi ----------

    public void showAddSuccess(Item item) {
        System.out.println("Berhasil menambah barang: " + format(item));
    }

    public void showUpdateStockSuccess() {
        System.out.println("Berhasil mengubah stok barang.");
    }

    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah stok barang dengan ID: %d.%n", id);
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus barang.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus barang dengan ID: %d.%n", id);
    }

    // ---------- Pesan error ----------

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    /** Menampilkan pesan dari ValidationException lain, mis. nama kosong. */
    public void showError(String message) {
        System.out.printf("[!] %s!%n", message);
    }
}
