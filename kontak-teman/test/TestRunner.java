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
    static Contact saveOne(ContactRepository repo, String label) { return repo.save(label, "0811", "a@x.com"); }
    static String key(Contact e) { return e.getName(); }
    static Contact copyWith(int id, String label) { return new Contact(id, label, "0811", "a@x.com"); }
    static Contact ucAdd(ContactUseCase uc, String label) throws Exception { return uc.addContact(label, "0811", "a@x.com"); }
    static void ucAddBlank(ContactUseCase uc) throws Exception { uc.addContact("  ", "0811", "a@x.com"); }
    static void ucRemove(ContactUseCase uc, int id) throws Exception { uc.removeContact(id); }
    static List<Contact> ucSearch(ContactUseCase uc, String kw) { return uc.searchContacts(kw); }
    static List<Contact> ucAll(ContactUseCase uc) { return uc.getAllContacts(); }
    static List<String> keys(List<Contact> list) { return list.stream().map(TestRunner::key).toList(); }

    public static void main(String[] args) {
        // ===== Repository =====
        test("repository: save memberi ID berurutan mulai dari 1", () -> {
            ContactRepository repo = new ContactRepository();
            assertEquals(1, saveOne(repo, "A").getId());
            assertEquals(2, saveOne(repo, "B").getId());
            assertEquals(3, saveOne(repo, "C").getId());
        });

        test("repository: findById menemukan dan tidak menemukan", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "A");
            assertTrue(repo.findById(1).isPresent(), "ID 1 harus ditemukan");
            assertTrue(repo.findById(99).isEmpty(), "ID 99 tidak boleh ditemukan");
        });

        test("repository: findAll mengembalikan salinan defensif", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "A");
            repo.findAll().clear();
            assertEquals(1, repo.findAll().size());
        });

        test("repository: deleteById true lalu false", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "A");
            assertTrue(repo.deleteById(1), "hapus pertama harus berhasil");
            assertTrue(!repo.deleteById(1), "hapus kedua harus gagal");
            assertEquals(0, repo.findAll().size());
        });

        test("repository: findBy memakai predikat", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "Apel");
            saveOne(repo, "Jeruk");
            saveOne(repo, "Anggur");
            assertEquals(2, repo.findBy(e -> key(e).startsWith("A")).size());
        });

        test("repository: update mengganti entity berdasarkan ID", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "Lama");
            saveOne(repo, "Lain");
            Contact replacement = copyWith(1, "Baru");
            repo.update(replacement);
            assertTrue(repo.findById(1).get() == replacement, "objek pada ID 1 harus diganti");
            assertEquals("Baru", key(repo.findById(1).get()));
            assertEquals(2, repo.findAll().size());
        });

        test("repository: update dengan ID tidak dikenal tidak menambah data", () -> {
            ContactRepository repo = new ContactRepository();
            saveOne(repo, "A");
            repo.update(copyWith(99, "Hantu"));
            assertEquals(1, repo.findAll().size());
        });

        // ===== Use case =====
        test("usecase: tambah data valid tersimpan", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            Contact created = ucAdd(uc, "Alpha");
            assertEquals(1, created.getId());
            assertEquals(1, ucAll(uc).size());
        });

        test("usecase: tambah data kosong melempar ValidationException", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            assertThrows(ValidationException.class, () -> ucAddBlank(uc));
            assertEquals(0, ucAll(uc).size());
        });

        test("usecase: hapus data ada berhasil, tidak ada melempar EntityNotFoundException", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            ucAdd(uc, "Alpha");
            ucRemove(uc, 1);
            assertEquals(0, ucAll(uc).size());
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> ucRemove(uc, 1));
            assertEquals(1, ex.getId());
            assertTrue(!ex.getMessage().endsWith("."), "pesan exception tidak diakhiri titik");
        });

        test("usecase: pencarian case-insensitive, null dan kosong tidak error", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
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

        test("usecase: telepon atau email kosong ditolak saat tambah", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            assertThrows(ValidationException.class, () -> uc.addContact("Budi", " ", "b@x.com"));
            assertThrows(ValidationException.class, () -> uc.addContact("Budi", "0811", ""));
        });

        test("usecase: ubah parsial (null/kosong = tidak diubah)", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            uc.addContact("Ani", "0822", "ani@x.com");
            uc.updateContact(1, "Ani Updated", "0899", "");
            Contact c = uc.getAllContacts().get(0);
            assertEquals("Ani Updated", c.getName());
            assertEquals("0899", c.getPhone());
            assertEquals("ani@x.com", c.getEmail());
            uc.updateContact(1, null, "  ", null);
            c = uc.getAllContacts().get(0);
            assertEquals("Ani Updated", c.getName());
            assertEquals("0899", c.getPhone());
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> uc.updateContact(999, "X", null, null));
            assertEquals(999, ex.getId());
        });

        test("usecase: urutkan nama A-Z dan Z-A", () -> {
            ContactUseCase uc = new ContactUseCase(new ContactRepository());
            uc.addContact("budi", "1", "b@x");
            uc.addContact("Ani", "2", "a@x");
            uc.addContact("Citra", "3", "c@x");
            assertEquals(List.of("Ani", "budi", "Citra"), keys(uc.sortContacts(SortOption.NAME_ASC)));
            assertEquals(List.of("Citra", "budi", "Ani"), keys(uc.sortContacts(SortOption.NAME_DESC)));
        });

        System.out.println();
        System.out.println("Hasil: " + passed + " lulus, " + failed + " gagal");
        System.exit(failed == 0 ? 0 : 1);
    }
}
