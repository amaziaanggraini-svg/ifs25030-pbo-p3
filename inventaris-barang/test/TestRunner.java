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
    static Item saveOne(ItemRepository repo, String label) { return repo.save(label, 5, "Kategori"); }
    static String key(Item e) { return e.getName(); }
    static Item copyWith(int id, String label) { return new Item(id, label, 5, "Kategori"); }
    static Item ucAdd(ItemUseCase uc, String label) throws Exception { return uc.addItem(label, 5, "Kategori"); }
    static void ucAddBlank(ItemUseCase uc) throws Exception { uc.addItem("  ", 5, "Kategori"); }
    static void ucRemove(ItemUseCase uc, int id) throws Exception { uc.removeItem(id); }
    static List<Item> ucSearch(ItemUseCase uc, String kw) { return uc.searchItems(kw); }
    static List<Item> ucAll(ItemUseCase uc) { return uc.getAllItems(); }
    static List<String> keys(List<Item> list) { return list.stream().map(TestRunner::key).toList(); }

    public static void main(String[] args) {
        // ===== Repository =====
        test("repository: save memberi ID berurutan mulai dari 1", () -> {
            ItemRepository repo = new ItemRepository();
            assertEquals(1, saveOne(repo, "A").getId());
            assertEquals(2, saveOne(repo, "B").getId());
            assertEquals(3, saveOne(repo, "C").getId());
        });

        test("repository: findById menemukan dan tidak menemukan", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "A");
            assertTrue(repo.findById(1).isPresent(), "ID 1 harus ditemukan");
            assertTrue(repo.findById(99).isEmpty(), "ID 99 tidak boleh ditemukan");
        });

        test("repository: findAll mengembalikan salinan defensif", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "A");
            repo.findAll().clear();
            assertEquals(1, repo.findAll().size());
        });

        test("repository: deleteById true lalu false", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "A");
            assertTrue(repo.deleteById(1), "hapus pertama harus berhasil");
            assertTrue(!repo.deleteById(1), "hapus kedua harus gagal");
            assertEquals(0, repo.findAll().size());
        });

        test("repository: findBy memakai predikat", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "Apel");
            saveOne(repo, "Jeruk");
            saveOne(repo, "Anggur");
            assertEquals(2, repo.findBy(e -> key(e).startsWith("A")).size());
        });

        test("repository: update mengganti entity berdasarkan ID", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "Lama");
            saveOne(repo, "Lain");
            Item replacement = copyWith(1, "Baru");
            repo.update(replacement);
            assertTrue(repo.findById(1).get() == replacement, "objek pada ID 1 harus diganti");
            assertEquals("Baru", key(repo.findById(1).get()));
            assertEquals(2, repo.findAll().size());
        });

        test("repository: update dengan ID tidak dikenal tidak menambah data", () -> {
            ItemRepository repo = new ItemRepository();
            saveOne(repo, "A");
            repo.update(copyWith(99, "Hantu"));
            assertEquals(1, repo.findAll().size());
        });

        // ===== Use case =====
        test("usecase: tambah data valid tersimpan", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            Item created = ucAdd(uc, "Alpha");
            assertEquals(1, created.getId());
            assertEquals(1, ucAll(uc).size());
        });

        test("usecase: tambah data kosong melempar ValidationException", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            assertThrows(ValidationException.class, () -> ucAddBlank(uc));
            assertEquals(0, ucAll(uc).size());
        });

        test("usecase: hapus data ada berhasil, tidak ada melempar EntityNotFoundException", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            ucAdd(uc, "Alpha");
            ucRemove(uc, 1);
            assertEquals(0, ucAll(uc).size());
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> ucRemove(uc, 1));
            assertEquals(1, ex.getId());
            assertTrue(!ex.getMessage().endsWith("."), "pesan exception tidak diakhiri titik");
        });

        test("usecase: pencarian case-insensitive, null dan kosong tidak error", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
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

        test("usecase: stok <= 0 ditolak saat tambah", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            assertThrows(ValidationException.class, () -> uc.addItem("Pensil", 0, "Alat Tulis"));
            assertThrows(ValidationException.class, () -> uc.addItem("Pensil", -1, "Alat Tulis"));
        });

        test("usecase: ubah stok parsial (null = tidak diubah)", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            uc.addItem("Pensil", 10, "Alat Tulis");
            uc.updateItemStock(1, null);
            assertEquals(10, uc.getAllItems().get(0).getQuantity());
            uc.updateItemStock(1, 25);
            assertEquals(25, uc.getAllItems().get(0).getQuantity());
            assertThrows(ValidationException.class, () -> uc.updateItemStock(1, 0));
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> uc.updateItemStock(99, 5));
            assertEquals(99, ex.getId());
        });

        test("usecase: urutkan berdasarkan nama dan jumlah (stabil untuk nilai sama)", () -> {
            ItemUseCase uc = new ItemUseCase(new ItemRepository());
            uc.addItem("Zebra", 5, "K");
            uc.addItem("Apple", 10, "K");
            uc.addItem("Mouse", 10, "K");
            assertEquals(List.of("Apple", "Mouse", "Zebra"), keys(uc.sortItems(SortOption.NAME_ASC)));
            assertEquals(List.of("Zebra", "Mouse", "Apple"), keys(uc.sortItems(SortOption.NAME_DESC)));
            assertEquals(List.of("Zebra", "Apple", "Mouse"), keys(uc.sortItems(SortOption.QUANTITY_ASC)));
            assertEquals(List.of("Apple", "Mouse", "Zebra"), keys(uc.sortItems(SortOption.QUANTITY_DESC)));
        });

        System.out.println();
        System.out.println("Hasil: " + passed + " lulus, " + failed + " gagal");
        System.exit(failed == 0 ? 0 : 1);
    }
}
