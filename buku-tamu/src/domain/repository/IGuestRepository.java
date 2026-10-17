package domain.repository;

import domain.entity.Guest;

public interface IGuestRepository extends IRepository<Guest, Integer> {
    Guest save(String name, String purpose);
}