package framework.view;

import adapter.presenter.ActivityPresenter;
import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.ActivityUseCase;

public class ActivityView {
    private final ActivityUseCase activityUseCase;
    private final ActivityPresenter presenter;

    public ActivityView(ActivityUseCase activityUseCase, ActivityPresenter presenter) {
        this.activityUseCase = activityUseCase;
        this.presenter = presenter;
    }

    public void show() {
        boolean running = true;
        while (running) {
            presenter.showActivities(activityUseCase.getAllActivities());
            presenter.showMenu();

            String choice = InputUtil.input("Pilih");
            presenter.showBlankLine();

            switch (choice) {
                case "1" -> addActivity();
                case "2" -> updateActivity();
                case "3" -> searchActivity();
                case "4" -> sortActivity();
                case "5" -> removeActivity();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                presenter.showBlankLine();
            }
        }
    }

    private void addActivity() {
        presenter.showSectionTitle("Menambah Kegiatan");

        String title = InputUtil.input("Judul (x Jika Batal)");
        if (InputUtil.isCancel(title)) {
            return;
        }

        String day = InputUtil.input("Hari (x Jika Batal)");
        if (InputUtil.isCancel(day)) {
            return;
        }

        String time = InputUtil.input("Waktu (x Jika Batal)");
        if (InputUtil.isCancel(time)) {
            return;
        }

        try {
            Activity activity = activityUseCase.addActivity(title, day, time);
            presenter.showAddSuccess(activity);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void updateActivity() {
        presenter.showSectionTitle("Mengubah Kegiatan");

        String rawId = InputUtil.input("ID Kegiatan yang diubah (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        int id;
        try {
            id = InputUtil.parseInt(rawId, "ID");
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        String title = InputUtil.input("Judul Baru (Kosongkan jika tidak ingin mengubah)");
        String day = InputUtil.input("Hari Baru (Kosongkan jika tidak ingin mengubah)");
        String time = InputUtil.input("Waktu Baru (Kosongkan jika tidak ingin mengubah)");

        try {
            activityUseCase.updateActivity(id, title, day, time);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        }
    }

    private void searchActivity() {
        presenter.showSectionTitle("Mencari Kegiatan");

        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (InputUtil.isCancel(keyword)) {
            return;
        }

        presenter.showSearchResults(activityUseCase.searchActivities(keyword), keyword);
    }

    private void sortActivity() {
        presenter.showSectionTitle("Mengurutkan Kegiatan");
        presenter.showSortMenu();

        String input = InputUtil.input("Pilih");
        presenter.showBlankLine();
        if (InputUtil.isCancel(input)) {
            return;
        }

        try {
            SortOption option = InputUtil.parseChoice(input, SortOption.values(), "Pilihan");
            presenter.showSortedActivities(activityUseCase.sortActivities(option));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }

    private void removeActivity() {
        presenter.showSectionTitle("Menghapus Kegiatan");

        String rawId = InputUtil.input("[ID Kegiatan] yang dihapus (x Jika Batal)");
        if (InputUtil.isCancel(rawId)) {
            return;
        }

        try {
            int id = InputUtil.parseInt(rawId, "ID");
            activityUseCase.removeActivity(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}