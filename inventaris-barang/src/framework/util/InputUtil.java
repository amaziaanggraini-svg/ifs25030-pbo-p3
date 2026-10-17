package framework.util;

import java.util.Scanner;

/**
 * Utility untuk membaca input string dari keyboard.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 */
public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    /** Menampilkan prompt dan membaca satu baris input. Jika input habis, dianggap "x". */
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
}
