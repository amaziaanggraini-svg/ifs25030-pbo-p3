package domain.repository;

import domain.entity.Contact;

public interface IContactRepository extends IRepository<Contact, Integer> {
    Contact save(String name, String phone, String email);
}