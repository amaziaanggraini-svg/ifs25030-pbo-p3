package adapter.repository;

import domain.entity.Guest;
import domain.repository.IGuestRepository;

public class GuestRepository extends InMemoryRepository<Guest, Integer> implements IGuestRepository {
    private int idCounter = 0;

    public GuestRepository() {
        super(Guest::getId);
    }

    @Override
    public Guest save(String name, String purpose) {
        Guest guest = new Guest(nextId(), name, purpose);
        return save(guest);
    }

    private int nextId() {
        return ++idCounter;
    }
}