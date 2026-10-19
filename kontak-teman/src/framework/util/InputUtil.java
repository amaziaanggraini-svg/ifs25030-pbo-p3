package framework.util;

import domain.exception.ValidationException;
import java.util.Scanner;

/**
 * Utility pembacaan dan konversi input keyboard.
 * Berada di layer framework karena bergantung langsung pada {@link System#in}.
 * Parsing dan validasi format input dipusatkan di sini agar view tidak
 * menduplikasi logika yang sama.
 */
public final class InputUtil {

    /** Kata kunci yang dipakai pengguna untuk membatalkan suatu operasi. */
    private static final String CANCEL_KEYWORD = "x";

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputUtil() {
    }

    /**
     * Menampilkan prompt lalu membaca satu baris input.
     * Jika input sudah habis (EOF), dikembalikan kata kunci batal agar aplikasi berhenti dengan aman.
     */
    public static String input(String info) {
        System.out.print(info + " : ");
        return SCANNER.hasNextLine() ? SCANNER.nextLine() : CANCEL_KEYWORD;
    }

    /** Menentukan apakah input pengguna berarti membatalkan operasi. */
    public static boolean isCancel(String value) {
        return value != null && CANCEL_KEYWORD.equalsIgnoreCase(value.trim());
    }

    /**
     * Mengonversi teks menjadi int.
     *
     * @param fieldName nama field untuk pesan error (mis. "ID")
     * @throws ValidationException jika teks bukan angka bulat
     */
    public static int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " tidak valid");
        }
    }

    /**
     * Memilih satu nilai enum berdasarkan nomor menu (dimulai dari 1).
     *
     * @param options   nilai enum sesuai urutan tampil di menu
     * @param fieldName nama field untuk pesan error (mis. "Pilihan")
     * @throws ValidationException jika bukan angka atau di luar rentang menu
     */
    public static <E extends Enum<E>> E parseChoice(String value, E[] options, String fieldName) throws ValidationException {
        int index = parseInt(value, fieldName) - 1;
        if (index < 0 || index >= options.length) {
            throw new ValidationException(fieldName + " tidak valid");
        }
        return options[index];
    }
}
