package adapter.presenter;

import domain.entity.Activity;
import domain.entity.SortOption;
import java.util.List;

public class ActivityPresenter {

    private static final String EMPTY_MESSAGE = "- Data kegiatan belum tersedia!";
    private static final String NOT_FOUND_MESSAGE = "- Kegiatan tidak ditemukan!";

    private String format(Activity activity) {
        return String.format("[%d] %s | Hari: %s | Pukul: %s",
                activity.getId(),
                activity.getTitle(),
                activity.getDay(),
                activity.getTime());
    }

    private String sortLabel(SortOption option) {
        return switch (option) {
            case DAY -> "Hari (Senin -> Minggu)";
            case TIME -> "Waktu (Awal -> Akhir)";
            case TITLE_ASC -> "Judul (A-Z)";
            case TITLE_DESC -> "Judul (Z-A)";
        };
    }

    private void printList(List<Activity> activities, String header, String emptyMessage) {
        System.out.println(header);
        if (activities.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        activities.forEach(activity -> System.out.println(format(activity)));
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

    public void showActivities(List<Activity> activities) {
        printList(activities, "Daftar Kegiatan:", EMPTY_MESSAGE);
    }

    public void showSearchResults(List<Activity> activities, String keyword) {
        printList(activities, "Hasil Pencarian: \"" + keyword + "\"", NOT_FOUND_MESSAGE);
    }

    public void showSortedActivities(List<Activity> activities) {
        printList(activities, "Daftar Kegiatan (Terurut):", EMPTY_MESSAGE);
    }

    public void showAddSuccess(Activity activity) {
        System.out.println("Berhasil menambah kegiatan: " + format(activity));
    }

    public void showUpdateSuccess() {
        System.out.println("Berhasil mengubah kegiatan.");
    }

    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah kegiatan dengan ID: %d.%n", id);
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus kegiatan.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus kegiatan dengan ID: %d.%n", id);
    }

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    public void showError(String message) {
        System.out.printf("[!] %s!%n", message);
    }
}