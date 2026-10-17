import adapter.repository.ItemRepository;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import usecase.ItemUseCase;

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
        ItemRepository repo = new ItemRepository();
        Item a = repo.save("Mouse", 10, "Elektronik");
        Item b = repo.save("Keyboard", 5, "Elektronik");
        check("repo: ID bertambah otomatis", a.getId() == 1 && b.getId() == 2);
        check("repo: findById ditemukan", repo.findById(2).isPresent());
        check("repo: findById tidak ditemukan", repo.findById(99).isEmpty());

        Item replacement = new Item(1, "Mouse X", 7, "Aksesoris");
        repo.update(replacement);
        check("repo: update mengganti entity berdasarkan ID", repo.findById(1).get() == replacement);
        check("repo: update tidak mengubah urutan", repo.findAll().get(0).getId() == 1);
        repo.update(new Item(99, "X", 1, "Y"));
        check("repo: update ID tak dikenal tidak menambah data", repo.findAll().size() == 2);

        check("repo: findBy memfilter", repo.findBy(i -> i.getQuantity() > 5).size() == 1);
        check("repo: deleteById berhasil", repo.deleteById(1));
        check("repo: deleteById ID tak ada = false", !repo.deleteById(1));
    }

    private static void useCaseTests() throws Exception {
        ItemUseCase useCase = new ItemUseCase(new ItemRepository());
        Item item = useCase.addItem("Mouse", 10, "Elektronik");
        useCase.addItem("Keyboard", 5, "Elektronik");
        check("usecase: addItem menyimpan data", item.getId() == 1 && useCase.getAllItems().size() == 2);
        check("usecase: nama kosong ditolak", throwsType(ValidationException.class, () -> useCase.addItem(" ", 1, "A")));
        check("usecase: jumlah <= 0 ditolak", throwsType(ValidationException.class, () -> useCase.addItem("A", 0, "A")));
        check("usecase: kategori kosong ditolak", throwsType(ValidationException.class, () -> useCase.addItem("A", 1, "")));

        useCase.updateItemStock(1, 25);
        check("usecase: update stok berhasil", useCase.getAllItems().get(0).getQuantity() == 25);
        useCase.updateItemStock(1, null);
        check("usecase: update null = stok tidak berubah", useCase.getAllItems().get(0).getQuantity() == 25);
        check("usecase: update stok <= 0 ditolak", throwsType(ValidationException.class, () -> useCase.updateItemStock(1, 0)));
        check("usecase: update ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.updateItemStock(99, 5)));

        check("usecase: search tidak sensitif huruf besar", useCase.searchItems("KEY").size() == 1);
        check("usecase: sort NAME_ASC", useCase.sortItems(SortOption.NAME_ASC).get(0).getName().equals("Keyboard"));
        check("usecase: sort QUANTITY_DESC", useCase.sortItems(SortOption.QUANTITY_DESC).get(0).getName().equals("Mouse"));

        check("usecase: remove ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.removeItem(99)));
        useCase.removeItem(1);
        check("usecase: remove berhasil", useCase.getAllItems().size() == 1);
    }

    private static void finish() {
        System.out.println("Lulus: " + passed + ", Gagal: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
