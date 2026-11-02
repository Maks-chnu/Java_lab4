package Java_lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Допоміжний клас для буферизованого консольного та файлового введення-виведення.
 * Тут зібрані всі вимоги з "Додаткових зауважень" лабораторної:
 *  1) шлях до файлу вводиться з консолі;
 *  2) якщо вхідного файлу немає — повідомлення і повторний запит;
 *  3) якщо вихідного файлу немає — повідомлення і запит на створення;
 *  4) якщо вихідний файл існує — вибір: перезаписати або дописати в кінець;
 *  5) буферизований консольний і файловий ввід-вивід.
 */
public final class IOHelper {

    // Буферизоване введення з консолі: байти System.in -> символи -> буфер
    private static final BufferedReader CONSOLE =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    // Буферизоване виведення на консоль. true — автоматичний flush() після println()
    private static final PrintWriter OUT =
            new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8)), true);

    // Клас лише з static-методами: створювати його об'єкти не потрібно
    private IOHelper() {
    }

    // ===================== Консоль =====================

    public static void println(String text) {
        OUT.println(text);  // flush виконується автоматично
    }

    public static void println() {
        OUT.println();
    }

    // print() не скидає буфер автоматично, тому flush() викликаємо вручну —
    // інакше підказка може не з'явитися на екрані до введення
    public static void print(String text) {
        OUT.print(text);
        OUT.flush();
    }

    // Читає рядок з консолі (без пробілів по краях)
    public static String readLine(String prompt) {
        print(prompt);
        try {
            String line = CONSOLE.readLine();
            if (line == null) {  // кінець введення (Ctrl+D / Ctrl+Z)
                throw new IllegalStateException("Введення з консолі завершено");
            }
            return line.trim();
        } catch (IOException e) {
            throw new IllegalStateException("Помилка читання з консолі: " + e.getMessage(), e);
        }
    }

    // Читає непорожній рядок
    public static String readNonEmpty(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isEmpty()) {
                return line;
            }
            println("Значення не може бути порожнім.");
        }
    }

    // Питання "так/ні"
    public static boolean askYesNo(String prompt) {
        while (true) {
            String answer = readLine(prompt).toLowerCase();
            if (answer.equals("т") || answer.equals("так") || answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("н") || answer.equals("ні") || answer.equals("n") || answer.equals("no")) {
                return false;
            }
            println("Введіть \"т\" (так) або \"н\" (ні).");
        }
    }

    // Вибір числа з діапазону [min; max]
    public static int askChoice(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // некоректне число — просто попросимо ще раз
            }
            println("Введіть число від " + min + " до " + max + ".");
        }
    }

    // ===================== Файли =====================

    /**
     * Запитує шлях до ІСНУЮЧОГО файлу. Поки файл не знайдено — повторний запит.
     */
    public static File askExistingFile(String prompt) {
        while (true) {
            File file = new File(readNonEmpty(prompt));
            if (!file.exists()) {
                println("Файл не знайдено: " + file.getAbsolutePath() + ". Спробуйте ще раз.");
            } else if (!file.isFile()) {
                println("Це не файл, а каталог: " + file.getAbsolutePath() + ". Спробуйте ще раз.");
            } else if (!file.canRead()) {
                println("Немає прав на читання файлу. Спробуйте інший.");
            } else {
                return file;
            }
        }
    }

    // Відкриває файл для буферизованого послідовного читання
    public static BufferedReader openReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
    }

    /**
     * Запитує вихідний файл і відкриває його для буферизованого запису.
     *  - файлу немає > питаємо, чи створити;
     *  - файл є > перезаписати / дописати в кінець / ввести іншу назву.
     * forbidden — файли, у які писати не можна (наприклад, вхідний файл).
     */
    public static BufferedWriter askOutputWriter(String prompt, File... forbidden) {
        while (true) {
            File file = new File(readNonEmpty(prompt));

            if (file.isDirectory()) {
                println("Це каталог, а не файл. Введіть іншу назву.");
                continue;
            }
            if (isOneOf(file, forbidden)) {
                println("Цей файл уже використовується в програмі (вхідний або інший вихідний). Введіть іншу назву.");
                continue;
            }

            boolean append = false;

            if (!file.exists()) {
                println("Файл не існує: " + file.getAbsolutePath());
                if (!askYesNo("Створити його? (т/н): ")) {
                    continue;
                }
                File parent = file.getAbsoluteFile().getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!askYesNo("Каталог " + parent + " не існує. Створити? (т/н): ")) {
                        continue;
                    }
                    if (!parent.mkdirs()) {
                        println("Не вдалося створити каталог. Введіть інший шлях.");
                        continue;
                    }
                }
                try {
                    if (!file.createNewFile()) {
                        println("Не вдалося створити файл. Введіть інший шлях.");
                        continue;
                    }
                    println("Файл створено.");
                } catch (IOException e) {
                    println("Помилка створення файлу: " + e.getMessage());
                    continue;
                }
            } else {
                int choice = askChoice("Файл уже існує. 1 — перезаписати, 2 — дописати в кінець, "
                        + "3 — ввести іншу назву: ", 1, 3);
                if (choice == 3) {
                    continue;
                }
                append = (choice == 2);
            }

            if (!file.canWrite()) {
                println("Немає прав на запис у цей файл. Введіть інший шлях.");
                continue;
            }

            try {
                // другий аргумент — кодування, третій: true = дописувати, false = перезаписати
                return new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, append));
            } catch (IOException e) {
                println("Не вдалося відкрити файл для запису: " + e.getMessage());
            }
        }
    }

    // Чи збігається файл з одним із заборонених (порівнюємо реальні шляхи)
    private static boolean isOneOf(File file, File... others) {
        try {
            String path = file.getCanonicalPath();
            for (File other : others) {
                if (other != null && other.getCanonicalPath().equals(path)) {
                    return true;
                }
            }
        } catch (IOException ignored) {
            // якщо шлях не вдалося визначити — не блокуємо
        }
        return false;
    }
}