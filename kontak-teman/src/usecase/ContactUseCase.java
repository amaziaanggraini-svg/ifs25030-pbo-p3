package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;

import java.util.List;
import java.util.Optional;

public class ContactUseCase {
    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact addContact(String name, String phone, String email) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama kontak tidak boleh kosong");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Nomor telepon tidak boleh kosong");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email tidak boleh kosong");
        }
        return contactRepository.save(name, phone, email);
    }

    public void updateContact(int id, String name, String phone, String email) throws EntityNotFoundException {
        Optional<Contact> found = contactRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        Contact contact = found.get();
        if (hasValue(name)) {
            contact.changeName(name);
        }
        if (hasValue(phone)) {
            contact.changePhone(phone);
        }
        if (hasValue(email)) {
            contact.changeEmail(email);
        }
        contactRepository.update(contact);
    }

    public void removeContact(int id) throws EntityNotFoundException {
        boolean deleted = contactRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
        return contactRepository.findBy(c -> c.getName().toLowerCase().contains(lowerKeyword));
    }

    public List<Contact> sortContacts(SortOption option) {
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Field opsional: null atau kosong berarti "tidak diubah". Aturan ini hanya ada di use case. */
    private static boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
