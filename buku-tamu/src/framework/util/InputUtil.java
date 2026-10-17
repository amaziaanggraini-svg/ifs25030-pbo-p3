package framework.util;

import domain.exception.ValidationException;
import java.util.Scanner;

/**
 * Utility untuk membaca dan mem-parsing input dari keyboard.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 * Seluruh parsing angka dipusatkan di sini agar view tidak menduplikasi logika.
 */
public class InputUtil {
    /** Scanner bersama untuk seluruh aplikasi (satu instance cukup). */
    private static final Scanner scanner = new Scanner(System.in);

    /** Menampilkan prompt dan membaca satu baris input. Jika input habis, dianggap "x" (batal/keluar). */
    public static String input(String info) {
        System.out.print(info + " : ");
        if (!scanner.hasNextLine()) {
            return "x";
        }
        return scanner.nextLine();
    }

    /** True jika input berupa perintah batal ("x"). */
    public static boolean isCancel(String value) {
        return value != null && value.trim().equalsIgnoreCase("x");
    }

    /**
     * Mengonversi string menjadi int; melempar ValidationException ("[field] tidak valid") jika bukan angka.
     * Khusus modul ini input tidak di-trim: ID yang diapit spasi dianggap tidak valid (sama seperti perilaku awal).
     */
    public static int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " tidak valid");
        }
    }

    /** Mengonversi string menjadi int positif (> 0); melempar ValidationException jika bukan angka atau <= 0. */
    public static int parsePositiveInt(String value, String fieldName) throws ValidationException {
        int parsed = parseInt(value, fieldName);
        if (parsed <= 0) {
            throw new ValidationException(fieldName + " tidak valid");
        }
        return parsed;
    }

    /** Mengonversi string menjadi long positif (> 0); melempar ValidationException jika bukan angka atau <= 0. */
    public static long parsePositiveLong(String value, String fieldName) throws ValidationException {
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed <= 0) {
                throw new ValidationException(fieldName + " tidak valid");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " tidak valid");
        }
    }

    /** Memetakan pilihan angka (mulai dari 1) ke nilai enum; melempar ValidationException jika di luar rentang. */
    public static <E extends Enum<E>> E parseChoice(String value, E[] enumValues, String fieldName) throws ValidationException {
        try {
            int index = Integer.parseInt(value.trim()) - 1;
            if (index >= 0 && index < enumValues.length) {
                return enumValues[index];
            }
        } catch (NumberFormatException ignored) {
            // diabaikan: input non-angka diperlakukan sama dengan angka di luar rentang
        }
        throw new ValidationException("Pilihan tidak valid");
    }
}
