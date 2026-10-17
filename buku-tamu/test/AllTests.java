import adapter.repository.GuestRepository;
import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import usecase.GuestUseCase;

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
        GuestRepository repo = new GuestRepository();
        Guest a = repo.save("Budi", "Rapat");
        Guest b = repo.save("Ani", "Kunjungan");
        check("repo: ID bertambah otomatis", a.getId() == 1 && b.getId() == 2);
        check("repo: findAll berisi 2 data", repo.findAll().size() == 2);
        check("repo: findById ditemukan", repo.findById(2).isPresent());
        check("repo: findById tidak ditemukan", repo.findById(99).isEmpty());

        Guest replacement = new Guest(1, "Budi S", "Interview");
        repo.update(replacement);
        check("repo: update mengganti entity berdasarkan ID", repo.findById(1).get() == replacement);
        check("repo: update tidak mengubah urutan", repo.findAll().get(0).getId() == 1);
        check("repo: update ID tak dikenal tidak menambah data", sizeAfterUnknownUpdate(repo) == 2);

        check("repo: findBy memfilter", repo.findBy(g -> g.getName().startsWith("A")).size() == 1);
        check("repo: deleteById berhasil", repo.deleteById(1));
        check("repo: deleteById ID tak ada = false", !repo.deleteById(1));
    }

    private static int sizeAfterUnknownUpdate(GuestRepository repo) {
        repo.update(new Guest(99, "X", "Y"));
        return repo.findAll().size();
    }

    private static void useCaseTests() throws Exception {
        GuestUseCase useCase = new GuestUseCase(new GuestRepository());
        Guest g = useCase.registerGuest("Budi", "Rapat");
        check("usecase: registerGuest menyimpan data", g.getId() == 1 && useCase.getAllGuests().size() == 1);
        check("usecase: nama kosong ditolak", throwsType(ValidationException.class, () -> useCase.registerGuest("  ", "Rapat")));
        check("usecase: tujuan kosong ditolak", throwsType(ValidationException.class, () -> useCase.registerGuest("Ani", "")));
        check("usecase: nama null ditolak", throwsType(ValidationException.class, () -> useCase.registerGuest(null, "Rapat")));

        check("usecase: search tidak sensitif huruf besar", useCase.searchGuests("BUD").size() == 1);
        check("usecase: search tanpa hasil", useCase.searchGuests("zzz").isEmpty());

        check("usecase: remove ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.removeGuest(99)));
        useCase.removeGuest(1);
        check("usecase: remove berhasil", useCase.getAllGuests().isEmpty());
    }

    private static void finish() {
        System.out.println("Lulus: " + passed + ", Gagal: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
