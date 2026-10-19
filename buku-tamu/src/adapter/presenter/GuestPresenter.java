package adapter.presenter;

import domain.entity.Guest;
import java.util.List;

/**
 * Presenter yang mengubah hasil proses aplikasi menjadi teks tampilan konsol.
 * Seluruh format output ada di sini sehingga entity, use case, dan view bebas dari urusan tampilan.
 */
public class GuestPresenter {

    private static final String EMPTY_MESSAGE = "- Data tamu belum tersedia!";
    private static final String NOT_FOUND_MESSAGE = "- Tamu tidak ditemukan!";

    // ---------- Format dasar ----------

    private String format(Guest guest) {
        return String.format("[%d] %s | Tujuan: %s",
                guest.getId(),
                guest.getName(),
                guest.getPurpose());
    }

    private void printList(List<Guest> guests, String header, String emptyMessage) {
        System.out.println(header);
        if (guests.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        guests.forEach(guest -> System.out.println(format(guest)));
    }

    // ---------- Menu dan judul ----------

    public void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Daftarkan");
        System.out.println("2. Cari");
        System.out.println("3. Hapus");
        System.out.println("x. Keluar");
    }

    public void showSectionTitle(String title) {
        System.out.printf("[%s]%n", title);
    }

    public void showBlankLine() {
        System.out.println();
    }

    // ---------- Daftar ----------

    public void showGuests(List<Guest> guests) {
        printList(guests, "Daftar Tamu:", EMPTY_MESSAGE);
    }

    public void showSearchResults(List<Guest> guests, String keyword) {
        printList(guests, "Hasil Pencarian: \"" + keyword + "\"", NOT_FOUND_MESSAGE);
    }

    // ---------- Pesan hasil operasi ----------

    public void showAddSuccess(Guest guest) {
        System.out.printf("Berhasil mendaftarkan tamu: %s%n", format(guest));
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus tamu.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus tamu dengan ID: %d.%n", id);
    }

    // ---------- Pesan error ----------

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    /** Menampilkan pesan dari ValidationException, mis. "ID tidak valid" menjadi "[!] ID tidak valid!". */
    public void showError(String message) {
        System.out.printf("[!] %s!%n", message);
    }
}
