package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;

import java.util.List;
import java.util.Optional;

public class ActivityUseCase {
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity addActivity(String title, String day, String time) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Judul kegiatan tidak boleh kosong");
        }
        if (day == null || day.trim().isEmpty()) {
            throw new ValidationException("Hari tidak boleh kosong");
        }
        if (time == null || time.trim().isEmpty()) {
            throw new ValidationException("Waktu tidak boleh kosong");
        }
        return activityRepository.save(title, day, time);
    }

    public void updateActivity(int id, String title, String day, String time) throws EntityNotFoundException {
        Optional<Activity> found = activityRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        Activity activity = found.get();
        if (hasValue(title)) {
            activity.changeTitle(title);
        }
        if (hasValue(day)) {
            activity.changeDay(day);
        }
        if (hasValue(time)) {
            activity.changeTime(time);
        }
        activityRepository.update(activity);
    }

    public void removeActivity(int id) throws EntityNotFoundException {
        boolean deleted = activityRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
        return activityRepository.findBy(a -> a.getTitle().toLowerCase().contains(lowerKeyword));
    }

    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Field opsional: null atau kosong berarti "tidak diubah". Aturan ini hanya ada di use case. */
    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
