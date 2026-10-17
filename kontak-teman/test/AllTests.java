import adapter.repository.ContactRepository;
import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import usecase.ContactUseCase;

/**
 * Unit test sederhana tanpa library eksternal.
 * Jalankan dari folder modul: javac -d output $(find src test -name "*.java") && java -cp output AllTests
 */
public class AllTests {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("GAGAL: " + name);
        }
    }

    private interface Action {
        void run() throws Exception;
    }

    /** True jika action melempar exception bertipe expected. */
    private static boolean throwsType(Class<? extends Exception> expected, Action action) {
        try {
            action.run();
            return false;
        } catch (Exception e) {
            return expected.isInstance(e);
        }
    }

    public static void main(String[] args) throws Exception {
        repositoryTests();
        useCaseTests();
        finish();
    }

    private static void repositoryTests() {
        ContactRepository repo = new ContactRepository();
        Contact a = repo.save("Ani", "081", "ani@x");
        Contact b = repo.save("Budi", "082", "budi@x");
        check("repo: ID bertambah otomatis", a.getId() == 1 && b.getId() == 2);
        check("repo: findById ditemukan", repo.findById(2).isPresent());
        check("repo: findById tidak ditemukan", repo.findById(99).isEmpty());

        Contact replacement = new Contact(1, "Ani B", "083", "ani@y");
        repo.update(replacement);
        check("repo: update mengganti entity berdasarkan ID", repo.findById(1).get() == replacement);
        check("repo: update tidak mengubah urutan", repo.findAll().get(0).getId() == 1);
        repo.update(new Contact(99, "X", "1", "y"));
        check("repo: update ID tak dikenal tidak menambah data", repo.findAll().size() == 2);

        check("repo: findBy memfilter", repo.findBy(c -> c.getName().startsWith("B")).size() == 1);
        check("repo: deleteById berhasil", repo.deleteById(1));
        check("repo: deleteById ID tak ada = false", !repo.deleteById(1));
    }

    private static void useCaseTests() throws Exception {
        ContactUseCase useCase = new ContactUseCase(new ContactRepository());
        Contact c = useCase.addContact("Budi", "082", "budi@x");
        useCase.addContact("Ani", "081", "ani@x");
        check("usecase: addContact menyimpan data", c.getId() == 1 && useCase.getAllContacts().size() == 2);
        check("usecase: nama kosong ditolak", throwsType(ValidationException.class, () -> useCase.addContact(" ", "1", "a")));
        check("usecase: telepon kosong ditolak", throwsType(ValidationException.class, () -> useCase.addContact("A", "", "a")));
        check("usecase: email kosong ditolak", throwsType(ValidationException.class, () -> useCase.addContact("A", "1", null)));

        useCase.updateContact(1, "", "099", "  ");
        Contact updated = useCase.getAllContacts().get(0);
        check("usecase: update parsial hanya mengubah field terisi",
                updated.getName().equals("Budi") && updated.getPhone().equals("099") && updated.getEmail().equals("budi@x"));
        check("usecase: update ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.updateContact(99, "A", "B", "C")));

        check("usecase: search tidak sensitif huruf besar", useCase.searchContacts("ANI").size() == 1);
        check("usecase: sort NAME_ASC", useCase.sortContacts(SortOption.NAME_ASC).get(0).getName().equals("Ani"));
        check("usecase: sort NAME_DESC", useCase.sortContacts(SortOption.NAME_DESC).get(0).getName().equals("Budi"));

        check("usecase: remove ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.removeContact(99)));
        useCase.removeContact(1);
        check("usecase: remove berhasil", useCase.getAllContacts().size() == 1);
    }

    private static void finish() {
        System.out.println("Lulus: " + passed + ", Gagal: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
