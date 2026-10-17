import adapter.repository.TransactionRepository;
import domain.entity.SortOption;
import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import usecase.FinanceUseCase;

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
        TransactionRepository repo = new TransactionRepository();
        Transaction a = repo.save("Gaji", 5000, TransactionType.INCOME);
        Transaction b = repo.save("Makan", 100, TransactionType.EXPENSE);
        check("repo: ID bertambah otomatis", a.getId() == 1 && b.getId() == 2);
        check("repo: findById ditemukan", repo.findById(2).isPresent());
        check("repo: findById tidak ditemukan", repo.findById(99).isEmpty());

        Transaction replacement = new Transaction(1, "Bonus", 700, TransactionType.INCOME);
        repo.update(replacement);
        check("repo: update mengganti entity berdasarkan ID", repo.findById(1).get() == replacement);
        check("repo: update tidak mengubah urutan", repo.findAll().get(0).getId() == 1);
        repo.update(new Transaction(99, "X", 1, TransactionType.EXPENSE));
        check("repo: update ID tak dikenal tidak menambah data", repo.findAll().size() == 2);

        check("repo: findBy memfilter", repo.findBy(t -> t.getType() == TransactionType.EXPENSE).size() == 1);
        check("repo: deleteById berhasil", repo.deleteById(1));
        check("repo: deleteById ID tak ada = false", !repo.deleteById(1));
    }

    private static void useCaseTests() throws Exception {
        FinanceUseCase useCase = new FinanceUseCase(new TransactionRepository());
        Transaction t = useCase.addTransaction("Gaji", 5000, TransactionType.INCOME);
        useCase.addTransaction("Makan", 100, TransactionType.EXPENSE);
        check("usecase: addTransaction menyimpan data", t.getId() == 1 && useCase.getAllTransactions().size() == 2);
        check("usecase: saldo = pemasukan - pengeluaran", useCase.getBalance() == 4900);
        check("usecase: keterangan kosong ditolak", throwsType(ValidationException.class, () -> useCase.addTransaction(" ", 10, TransactionType.INCOME)));
        check("usecase: jumlah <= 0 ditolak", throwsType(ValidationException.class, () -> useCase.addTransaction("A", 0, TransactionType.INCOME)));

        check("usecase: search tidak sensitif huruf besar", useCase.searchTransactions("GAJ").size() == 1);
        check("usecase: sort AMOUNT_ASC", useCase.sortTransactions(SortOption.AMOUNT_ASC).get(0).getAmount() == 100);
        check("usecase: sort INCOME_FIRST", useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).getType() == TransactionType.INCOME);
        check("usecase: sort EXPENSE_FIRST", useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).getType() == TransactionType.EXPENSE);

        check("usecase: remove ID tak ada -> EntityNotFoundException", throwsType(EntityNotFoundException.class, () -> useCase.removeTransaction(99)));
        useCase.removeTransaction(1);
        check("usecase: remove berhasil", useCase.getAllTransactions().size() == 1);
        check("usecase: saldo setelah hapus", useCase.getBalance() == -100);
    }

    private static void finish() {
        System.out.println("Lulus: " + passed + ", Gagal: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
