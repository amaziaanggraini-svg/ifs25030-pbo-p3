import adapter.repository.ActivityRepository;
import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import usecase.ActivityUseCase;

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
        ActivityRepository repo = new ActivityRepository();
        Activity a = repo.save("Rapat", "Senin", "10:00");
        Activity b = repo.save("Gym", "Selasa", "07:00");
        check("repo: ID bertambah otomatis", a.getId() == 1 && b.getId() == 2);
        check("repo: findById ditemukan", repo.findById(2).isPresent());
        check("repo: findById tidak ditemukan", repo.findById(99).isEmpty());

        Activity replacement = new Activity(1, "Rapat B", "Rabu", "11:00");
        repo.update(replacement);
        check("repo: update mengganti entity berdasarkan ID", repo.findById(1).get() == replacement);
        check("repo: update tidak mengubah urutan", repo.findAll().get(0).getId() == 1);
        repo.update(new Activity(99, "X", "Y", "Z"));
        check("repo: update ID tak dikenal tidak menambah data", repo.findAll().size() == 2);

        check("repo: findBy memfilter", repo.findBy(x -> x.getDay().equals("Selasa")).size() == 1);
        check("repo: deleteById berhasil", repo.deleteById(1));
        check("repo: deleteById ID tak ada = false", !repo.deleteById(1));
    }

    private static void useCaseTests() throws Exception {
        ActivityUseCase useCase = new ActivityUseCase(new ActivityRepository());
        Activity act = useCase.addActivity("Rapat", "Senin", "10:00");
        useCase.addActivity("Gym", "Selasa", "07:00");
        check("usecase: addActivity menyimpan data", act.getId() == 1 && useCase.getAllActivities().size() == 2);
        check("usecase: judul kosong ditolak", throwsType(ValidationException.class, () -> useCase.addActivity(" ", "Senin", "10:00")));
        check("usecase: hari kosong ditolak", throwsType(ValidationException.class, () -> useCase.addActivity("A", "", "10:00")));
        check("usecase: waktu kosong ditolak", throwsType(ValidationException.class, () -> useCase.addActivity("A", "Senin", null)));

        useCase.updateActivity(1, "", "Rabu", "  ");
        Activity updated = useCase.getAllActivities().get(0);
        check("usecase: update parsial hanya mengubah field terisi",
                updated.getTitle().equals("Rapat") && updated.getDay().equals("Rabu") && updated.getTime().equals("10:00"));
        check("usecase: update ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.updateActivity(99, "A", "B", "C")));

        check("usecase: search tidak sensitif huruf besar", useCase.searchActivities("GYM").size() == 1);
        check("usecase: sort menghasilkan semua data", useCase.sortActivities(SortOption.values()[0]).size() == 2);

        check("usecase: remove ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.removeActivity(99)));
        useCase.removeActivity(1);
        check("usecase: remove berhasil", useCase.getAllActivities().size() == 1);
    }

    private static void finish() {
        System.out.println("Lulus: " + passed + ", Gagal: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
