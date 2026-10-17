package adapter.repository;

import domain.entity.Activity;
import domain.repository.IActivityRepository;

public class ActivityRepository extends InMemoryRepository<Activity, Integer> implements IActivityRepository {
    private int idCounter = 0;

    public ActivityRepository() {
        super(Activity::getId);
    }

    @Override
    public Activity save(String title, String day, String time) {
        Activity activity = new Activity(nextId(), title, day, time);
        return save(activity);
    }

    private int nextId() {
        return ++idCounter;
    }
}