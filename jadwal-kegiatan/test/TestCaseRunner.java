import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menjalankan aplikasi untuk setiap test-cases/TC-XX.tc (input) dan memastikan
 * seluruh baris pada TC-XX.expected (cuplikan output penting dari soal) muncul
 * berurutan pada output aplikasi.
 *
 * Pemakaian (dari folder proyek):
 *   java -cp output-test TestCaseRunner output
 * dengan "output" = folder hasil compile aplikasi.
 */
public class TestCaseRunner {
    public static void main(String[] args) throws Exception {
        String classes = args.length > 0 ? args[0] : "output";
        File[] inputs = new File("test-cases").listFiles((dir, name) -> name.endsWith(".tc"));
        if (inputs == null || inputs.length == 0) {
            System.out.println("Folder test-cases tidak ditemukan / kosong.");
            System.exit(1);
        }
        Arrays.sort(inputs);

        int failed = 0;
        for (File tc : inputs) {
            String name = tc.getName().replace(".tc", "");
            Path expectedFile = new File(tc.getParentFile(), name + ".expected").toPath();
            String output = runApp(classes, tc);
            List<String> missing = findMissing(output, Files.readAllLines(expectedFile, StandardCharsets.UTF_8));
            if (missing.isEmpty()) {
                System.out.println("PASS  " + name);
            } else {
                failed++;
                System.out.println("FAIL  " + name + " -> baris tidak ditemukan / urutan salah: " + missing.get(0));
            }
        }
        System.out.println();
        System.out.println("Hasil: " + (inputs.length - failed) + " lulus, " + failed + " gagal");
        System.exit(failed == 0 ? 0 : 1);
    }

    private static String runApp(String classes, File input) throws IOException, InterruptedException {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        Process process = new ProcessBuilder(java, "-cp", classes, "App")
                .redirectInput(input)
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        process.waitFor();
        return output.replace("\r\n", "\n");
    }

    /** Setiap baris expected harus ada (sebagai potongan teks) setelah posisi baris sebelumnya. */
    private static List<String> findMissing(String output, List<String> expectedLines) {
        List<String> missing = new ArrayList<>();
        int position = 0;
        for (String line : expectedLines) {
            if (line.isBlank()) {
                continue;
            }
            int found = output.indexOf(line, position);
            if (found < 0) {
                missing.add(line);
                break;
            }
            position = found + line.length();
        }
        return missing;
    }
}
