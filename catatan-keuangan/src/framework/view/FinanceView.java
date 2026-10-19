package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.FinanceUseCase;

/**
 * Tampilan konsol aplikasi catatan keuangan.
 * Bertugas menerima input, memanggil use case, dan menangani exception domain.
 * Tidak berisi logika bisnis maupun format output (diserahkan ke presenter).
 */
public class FinanceView {

    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;
    }

    /** Menjalankan loop menu utama sampai pengguna memilih keluar. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showTransactions(financeUseCase.getAllTransactions(), financeUseCase.getBalance());
            presenter.showMenu();

            String choice = InputUtil.input("Pilih");
            presenter.showBlankLine();

            switch (choice) {
                case "1" -> addTransaction(TransactionType.INCOME);
                case "2" -> addTransaction(TransactionType.EXPENSE);
                case "3" -> searchTransaction();
                case "4" -> sortTransaction();
                case "5" -> presenter.showBalance(financeUseCase.getBalance());
                case "6" -> removeTransaction();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                presenter.showBlankLine();
            }
        }
    }

    private void addTransaction(TransactionType type) {
        presenter.showSectionTitle(type == TransactionType.INCOME ? "Tambah Pemasukan" : "Tambah Pengeluaran");

        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (InputUtil.isCancel(description)) {
            return;
        }
        String rawAmount = InputUtil.input("Jumlah");
        if (InputUtil.isCancel(rawAmount)) {
            return;
        }

        try {
            long amount = InputUtil.parseLong(rawAmount, "Jumlah");
            presenter.showAddSuccess(financeUseCase.addTransaction(description, amount, type));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void searchTransaction() {
        presenter.showSectionTitle("Cari Transaksi");

        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (InputUtil.isCancel(keyword)) {
            return;
        }

        presenter.showSearchResults(financeUseCase.searchTransactions(keyword), keyword);
    }

    private void sortTransaction() {
        presenter.showSectionTitle("Urutkan Transaksi");
        presenter.showSortMenu();

        String input = InputUtil.input("Pilih");
        presenter.showBlankLine(); 
        if (InputUtil.isCancel(input)) {
            return;
        }

        try {
            SortOption option = InputUtil.parseChoice(input, SortOption.values(), "Pilihan");
            presenter.showSortedTransactions(financeUseCase.sortTransactions(option));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void removeTransaction() {
        presenter.showSectionTitle("Hapus Transaksi");

        String rawId = InputUtil.input("ID Transaksi (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        try {
            financeUseCase.removeTransaction(InputUtil.parseInt(rawId, "ID"));
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}