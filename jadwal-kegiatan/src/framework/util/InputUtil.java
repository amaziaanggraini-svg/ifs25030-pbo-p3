package framework.util;

import domain.exception.ValidationException;
import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String input(String info) {
        System.out.print(info + " : ");
        if (!scanner.hasNextLine()) {
            return "x";
        }
        return scanner.nextLine();
    }

    public static boolean isCancel(String value) {
        return value != null && value.trim().equalsIgnoreCase("x");
    }

    public static int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " tidak valid");
        }
    }

    public static <E extends Enum<E>> E parseChoice(String value, E[] enumValues, String fieldName) throws ValidationException {
        try {
            int index = Integer.parseInt(value.trim()) - 1;
            if (index >= 0 && index < enumValues.length) {
                return enumValues[index];
            }
        } catch (NumberFormatException ignored) {
        }
        throw new ValidationException("Pilihan tidak valid");
    }
}