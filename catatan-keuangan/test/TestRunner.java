import adapter.repository.*;
import domain.entity.*;
import domain.exception.*;
import framework.util.InputUtil;
import usecase.*;
import java.util.*;

/**
 * Unit test untuk repository, use case, dan InputUtil.
 * Tanpa library eksternal; dijalankan lewat main().
 *
 * Compile & jalankan (dari folder proyek):
 *   javac -d output-test -sourcepath src test/TestRunner.java test/TestCaseRunner.java
 *   java -cp output-test TestRunner
 */
public class TestRunner {

    // ---------- Mini test harness (tanpa dependensi eksternal) ----------
    interface Body { void run() throws Exception; }

    private static int passed = 0;
    private static int failed = 0;

    static void test(String name, Body body) {
        try {
            body.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("FAIL  " + name + " -> " + t);
        }
    }

    static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static <T extends Throwable> T assertThrows(Class<T> type, Body body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but nothing was thrown");
    }

    // ---------- Helper spesifik modul ----------
    static Transaction saveOne(TransactionRepository repo, String label) { return repo.save(label, 100L, TransactionType.INCOME); }
    static String key(Transaction e) { return e.getDescription(); }
    static Transaction copyWith(int id, String label) { return new Transaction(id, label, 100L, TransactionType.INCOME); }
    static Transaction ucAdd(FinanceUseCase uc, String label) throws Exception { return uc.addTransaction(label, 100L, TransactionType.INCOME); }
    static void ucAddBlank(FinanceUseCase uc) throws Exception { uc.addTransaction("  ", 100L, TransactionType.INCOME); }
    static void ucRemove(FinanceUseCase uc, int id) throws Exception { uc.removeTransaction(id); }
    static List<Transaction> ucSearch(FinanceUseCase uc, String kw) { return uc.searchTransactions(kw); }
    static List<Transaction> ucAll(FinanceUseCase uc) { return uc.getAllTransactions(); }

    static List<String> keys(List<Transaction> list) { return list.stream().map(TestRunner::key).toList(); }

    public static void main(String[] args) {
        // ===== Repository =====
        test("repository: save memberi ID berurutan mulai dari 1", () -> {
            TransactionRepository repo = new TransactionRepository();
            assertEquals(1, saveOne(repo, "A").getId());
            assertEquals(2, saveOne(repo, "B").getId());
            assertEquals(3, saveOne(repo, "C").getId());
        });

        test("repository: findById menemukan dan tidak menemukan", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "A");
            assertTrue(repo.findById(1).isPresent(), "ID 1 harus ditemukan");
            assertTrue(repo.findById(99).isEmpty(), "ID 99 tidak boleh ditemukan");
        });

        test("repository: findAll mengembalikan salinan defensif", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "A");
            repo.findAll().clear();
            assertEquals(1, repo.findAll().size());
        });

        test("repository: deleteById true lalu false", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "A");
            assertTrue(repo.deleteById(1), "hapus pertama harus berhasil");
            assertTrue(!repo.deleteById(1), "hapus kedua harus gagal");
            assertEquals(0, repo.findAll().size());
        });

        test("repository: findBy memakai predikat", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "Apel");
            saveOne(repo, "Jeruk");
            saveOne(repo, "Anggur");
            assertEquals(2, repo.findBy(e -> key(e).startsWith("A")).size());
        });

        test("repository: update mengganti entity berdasarkan ID", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "Lama");
            saveOne(repo, "Lain");
            Transaction replacement = copyWith(1, "Baru");
            repo.update(replacement);
            assertTrue(repo.findById(1).get() == replacement, "objek pada ID 1 harus diganti");
            assertEquals("Baru", key(repo.findById(1).get()));
            assertEquals(2, repo.findAll().size());
        });

        test("repository: update dengan ID tidak dikenal tidak menambah data", () -> {
            TransactionRepository repo = new TransactionRepository();
            saveOne(repo, "A");
            repo.update(copyWith(99, "Hantu"));
            assertEquals(1, repo.findAll().size());
        });

        // ===== Use case =====
        test("usecase: tambah data valid tersimpan", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            Transaction created = ucAdd(uc, "Alpha");
            assertEquals(1, created.getId());
            assertEquals(1, ucAll(uc).size());
        });

        test("usecase: tambah data kosong melempar ValidationException", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            assertThrows(ValidationException.class, () -> ucAddBlank(uc));
            assertEquals(0, ucAll(uc).size());
        });

        test("usecase: hapus data ada berhasil, tidak ada melempar EntityNotFoundException", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            ucAdd(uc, "Alpha");
            ucRemove(uc, 1);
            assertEquals(0, ucAll(uc).size());
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> ucRemove(uc, 1));
            assertEquals(1, ex.getId());
            assertTrue(!ex.getMessage().endsWith("."), "pesan exception tidak diakhiri titik");
        });

        test("usecase: pencarian case-insensitive, null dan kosong tidak error", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            ucAdd(uc, "Alpha");
            ucAdd(uc, "beta");
            assertEquals(1, ucSearch(uc, "ALP").size());
            assertEquals(1, ucSearch(uc, "BETA").size());
            assertEquals(2, ucSearch(uc, null).size());
            assertEquals(2, ucSearch(uc, "").size());
            assertEquals(0, ucSearch(uc, "zzz").size());
        });

        // ===== InputUtil (parsing & validasi terpusat) =====
        test("InputUtil.parseId: angka valid, spasi di-trim, bukan angka ditolak", () -> {
            assertEquals(12, InputUtil.parseId("12"));
            assertEquals(7, InputUtil.parseId(" 7 "));
            assertEquals(-1, InputUtil.parseId("-1")); // angka bulat tetap "ID valid"; tidak ditemukan ditangani use case
            ValidationException ex = assertThrows(ValidationException.class, () -> InputUtil.parseId("abc"));
            assertEquals("ID tidak valid", ex.getMessage());
        });

        test("InputUtil.parsePositiveInt dan parseLong: hanya > 0", () -> {
            assertEquals(5, InputUtil.parsePositiveInt("5", "Jumlah"));
            assertThrows(ValidationException.class, () -> InputUtil.parsePositiveInt("0", "Jumlah"));
            assertThrows(ValidationException.class, () -> InputUtil.parsePositiveInt("-3", "Jumlah"));
            assertThrows(ValidationException.class, () -> InputUtil.parsePositiveInt("x", "Jumlah"));
            assertEquals(9000000000L, InputUtil.parseLong("9000000000", "Jumlah"));
            assertThrows(ValidationException.class, () -> InputUtil.parseLong("-1", "Jumlah"));
        });

        test("InputUtil.isCancel: x / X dengan spasi dianggap batal", () -> {
            assertTrue(InputUtil.isCancel("x"), "x");
            assertTrue(InputUtil.isCancel(" X "), "X");
            assertTrue(!InputUtil.isCancel("xx"), "xx");
            assertTrue(!InputUtil.isCancel(null), "null");
        });

        test("usecase: jumlah <= 0 ditolak", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            ValidationException ex = assertThrows(ValidationException.class, () -> uc.addTransaction("Gaji", 0L, TransactionType.INCOME));
            assertEquals("Jumlah tidak valid", ex.getMessage());
            assertThrows(ValidationException.class, () -> uc.addTransaction("Gaji", -5L, TransactionType.INCOME));
        });

        test("usecase: urutkan berdasarkan jumlah dan tipe", () -> {
            FinanceUseCase uc = new FinanceUseCase(new TransactionRepository());
            uc.addTransaction("A", 300L, TransactionType.INCOME);
            uc.addTransaction("B", 100L, TransactionType.EXPENSE);
            uc.addTransaction("C", 200L, TransactionType.INCOME);
            assertEquals(List.of("B", "C", "A"), keys(uc.sortTransactions(SortOption.AMOUNT_ASC)));
            assertEquals(List.of("A", "C", "B"), keys(uc.sortTransactions(SortOption.AMOUNT_DESC)));
            assertEquals(List.of("A", "C", "B"), keys(uc.sortTransactions(SortOption.INCOME_FIRST)));
            assertEquals(List.of("B", "A", "C"), keys(uc.sortTransactions(SortOption.EXPENSE_FIRST)));
        });

        System.out.println();
        System.out.println("Hasil: " + passed + " lulus, " + failed + " gagal");
        System.exit(failed == 0 ? 0 : 1);
    }
}
