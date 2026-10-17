package framework.view;

import adapter.presenter.GuestPresenter;
import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.GuestUseCase;

/**
 * Tampilan konsol buku tamu.
 * Menerima input, memanggil use case, dan menangani exception domain.
 * Layer ini tidak mengandung logika bisnis maupun format output.
 */
public class GuestView {
    private final GuestUseCase guestUseCase;
    private final GuestPresenter presenter;

    public GuestView(GuestUseCase guestUseCase, GuestPresenter presenter) {
        this.guestUseCase = guestUseCase;
        this.presenter = presenter;
    }

    public void show() {
        boolean running = true;
        while (running) {
            presenter.showGuests(guestUseCase.getAllGuests());
            presenter.showMenu();

            String choice = InputUtil.input("Pilih");
            presenter.showBlankLine();

            switch (choice) {
                case "1" -> registerGuest();
                case "2" -> searchGuest();
                case "3" -> removeGuest();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                presenter.showBlankLine();
            }
        }
    }

    private void registerGuest() {
        presenter.showSectionTitle("Mendaftarkan Tamu");

        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        String purpose = InputUtil.input("Tujuan Kunjungan (x Jika Batal)");
        if (InputUtil.isCancel(purpose)) {
            return;
        }

        try {
            Guest guest = guestUseCase.registerGuest(name, purpose);
            presenter.showAddSuccess(guest);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void searchGuest() {
        presenter.showSectionTitle("Mencari Tamu");

        String keyword = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(keyword)) {
            return;
        }

        presenter.showSearchResults(guestUseCase.searchGuests(keyword), keyword);
    }

    private void removeGuest() {
        presenter.showSectionTitle("Menghapus Tamu");

        String rawId = InputUtil.input("[ID Tamu] yang dihapus (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(rawId, "ID");
            guestUseCase.removeGuest(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showInvalidId();
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
