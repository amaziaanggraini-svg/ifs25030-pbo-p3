package adapter.presenter;

import domain.entity.Guest;
import java.util.List;

public class GuestPresenter {

    public String format(Guest guest) {
        return String.format("[%d] %s | Tujuan: %s",
                guest.getId(),
                guest.getName(),
                guest.getPurpose());
    }

    public void showGuests(List<Guest> guests) {
        System.out.println("Daftar Tamu:");
        if (guests.isEmpty()) {
            System.out.println("- Data tamu belum tersedia!");
        } else {
            for (Guest g : guests) {
                System.out.println(format(g));
            }
        }
    }

    public void showSearchResults(List<Guest> guests, String keyword) {
        System.out.println("Hasil Pencarian: \"" + keyword + "\"");
        if (guests.isEmpty()) {
            System.out.println("- Tamu tidak ditemukan!");
        } else {
            for (Guest g : guests) {
                System.out.println(format(g));
            }
        }
    }

    public void showAddSuccess(Guest guest) {
        System.out.printf("Berhasil mendaftarkan tamu: %s%n", format(guest));
    }

    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus tamu.");
    }

    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus tamu dengan ID: %d.%n", id);
    }

    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    public void showInvalidId() {
        System.out.println("[!] ID tidak valid!");
    }
}