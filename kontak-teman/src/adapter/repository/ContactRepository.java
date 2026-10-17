package adapter.repository;

import domain.entity.Contact;
import domain.repository.IContactRepository;

public class ContactRepository extends InMemoryRepository<Contact, Integer> implements IContactRepository {
    private int idCounter = 0;

    public ContactRepository() {
        super(Contact::getId);
    }

    @Override
    public Contact save(String name, String phone, String email) {
        Contact contact = new Contact(nextId(), name, phone, email);
        return save(contact);
    }

    private int nextId() {
        return ++idCounter;
    }
}