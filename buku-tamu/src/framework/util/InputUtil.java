package framework.util;

import domain.exception.ValidationException;
import java.util.Scanner;

/**
 * Utility input/parsing konsol.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 * Seluruh parsing dan validasi format input dipusatkan di sini agar view tidak
 * menduplikasi logika yang sama. Pesan ValidationException ditulis tanpa tanda
 * baca akhir karena presenter yang menambahkan format "[!] ... !".
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

    /** Mengonversi string menjadi ID; harus berupa angka bulat. */
    public static int parseId(String value) throws ValidationException {
        return parseInt(value, "ID");
    }

    /** Mengonversi string menjadi int; melempar ValidationException jika bukan angka. */
    public static int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " tidak valid");
        }
    }

    /** Mengonversi string menjadi int positif (> 0); melempar ValidationException jika tidak valid. */
    public static int parsePositiveInt(String value, String fieldName) throws ValidationException {
        int parsed = parseInt(value, fieldName);
        if (parsed <= 0) {
            throw new ValidationException(fieldName + " tidak valid");
        }
        return parsed;
    }

    /** Mengonversi string menjadi long positif (> 0); melempar ValidationException jika tidak valid. */
    public static long parseLong(String value, String fieldName) throws ValidationException {
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
            // jatuh ke ValidationException di bawah
        }
        throw new ValidationException(fieldName + " tidak valid");
    }
}
