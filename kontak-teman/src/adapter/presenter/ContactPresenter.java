package adapter.presenter;

import domain.entity.Contact;
import domain.entity.SortOption;
import java.util.List;

public class ContactPresenter {

    private static final String EMPTY_MESSAGE = "- Data kontak belum tersedia!";
    private static final String NOT_FOUND_MESSAGE = "- Kontak tidak ditemukan!";

    private String format(Contact contact) {
        return String.format("[%d] %s | Telp: %s | Email: %s",
                contact.getId(),
                contact.getName(),
                contact.getPhone(),
                contact.getEmail());
    }

    private String sortLabel(SortOption option) {
        return switch (option) {
            case NAME_ASC -> "Nama (A-Z)";
            case NAME_DESC -> "Nama (Z-A)";
        };
    }

    private void printList(List<Contact> contacts, String header, String emptyMessage) {
        System.out.println(header);
        if (contacts.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        contacts.forEach(c -> System.out.println(format(c)));
    }

    public void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah");
        System.out.println("2. Ubah");
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

    public void showContacts(List<Contact> contacts) {
        printList(contacts, "Daftar Kontak:", EMPTY_MESSAGE);
    }

    public void showSearchResults(List<Contact> contacts, String keyword) {
        printList(contacts, "Hasil Pencarian: \"" + keyword + "\"", NOT_FOUND_MESSAGE);
    }

    public void showSortedContacts(List<Contact> contacts) {
        printList(contacts, "Daftar Kontak (Terurut):", EMPTY_MESSAGE);
    }

    public void showAddSuccess(Contact contact) {
        System.out.println("Berhasil menambah kontak: " + format(contact));
    }

    public void showUpdateSuccess() {
        System.out.println("Berhasil mengubah kontak.");
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus kontak.");
    }

    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah kontak dengan ID: %d.%n", id);
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus kontak dengan ID: %d.%n", id);
    }

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    public void showError(String message) {
        System.out.printf("[!] %s!%n", message);
    }
}