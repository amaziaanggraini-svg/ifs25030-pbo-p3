package domain.repository;

import domain.entity.Activity;

public interface IActivityRepository extends IRepository<Activity, Integer> {
    Activity save(String title, String day, String time);
}