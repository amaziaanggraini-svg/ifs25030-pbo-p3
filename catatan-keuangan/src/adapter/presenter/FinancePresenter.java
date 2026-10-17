package adapter.presenter;

import domain.entity.SortOption;
import domain.entity.Transaction;
import domain.entity.TransactionType;
import java.util.List;

/**
 * Presenter yang mengubah hasil proses aplikasi menjadi teks tampilan konsol.
 * Seluruh format output ada di sini sehingga entity, use case, dan view bebas dari urusan tampilan.
 */
public class FinancePresenter {

    private static final String EMPTY_MESSAGE = "- Belum ada transaksi!";
    private static final String NOT_FOUND_MESSAGE = "- Transaksi tidak ditemukan!";

    // ---------- Format dasar ----------

    private String format(Transaction transaction) {
        return String.format("[%d] %s | Rp %d | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                typeLabel(transaction.getType()));
    }

    private String typeLabel(TransactionType type) {
        return switch (type) {
            case INCOME -> "Pemasukan";
            case EXPENSE -> "Pengeluaran";
        };
    }

    private String sortLabel(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah (Terkecil)";
            case AMOUNT_DESC -> "Jumlah (Terbesar)";
            case INCOME_FIRST -> "Pemasukan Dulu";
            case EXPENSE_FIRST -> "Pengeluaran Dulu";
        };
    }

    private void printList(List<Transaction> transactions, String header, String emptyMessage) {
        System.out.println(header);
        if (transactions.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        transactions.forEach(transaction -> System.out.println(format(transaction)));
    }

    // ---------- Menu dan judul ----------

    public void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    public void showSortMenu() {
        SortOption[] options = SortOption.values();
        for (SortOption option : options) {
            System.out.printf("%d. %s%n", option.code(), sortLabel(option));
        }
        System.out.println("x. Batal");
    }

    public void showSectionTitle(String title) {
        System.out.printf("[%s]%n", title);
    }

    public void showBlankLine() {
        System.out.println();
    }

    // ---------- Daftar dan saldo ----------

    /** Menampilkan seluruh transaksi beserta saldo terkini. */
    public void showTransactions(List<Transaction> transactions, long balance) {
        printList(transactions, "Daftar Transaksi:", EMPTY_MESSAGE);
        System.out.printf("Saldo: Rp %d%n", balance);
    }

    public void showSearchResults(List<Transaction> transactions, String keyword) {
        printList(transactions, "Hasil Pencarian: \"" + keyword + "\"", NOT_FOUND_MESSAGE);
    }

    public void showSortedTransactions(List<Transaction> transactions) {
        printList(transactions, "Daftar Transaksi (Terurut):", EMPTY_MESSAGE);
    }

    public void showBalance(long balance) {
        System.out.printf("Saldo saat ini: Rp %d%n", balance);
    }

    // ---------- Pesan hasil operasi ----------

    public void showAddSuccess(Transaction transaction) {
        System.out.println("Berhasil menambah transaksi: " + format(transaction));
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus transaksi.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus transaksi dengan ID: %d.%n", id);
    }

    // ---------- Pesan error ----------

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    /** Menampilkan pesan dari ValidationException, mis. "Jumlah tidak valid" -> "[!] Jumlah tidak valid!". */
    public void showError(String message) {
        System.out.printf("[!] %s!%n", message);
    }
}