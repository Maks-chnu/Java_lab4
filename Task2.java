package Java_lab4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Лабораторна робота №4, завдання 2, варіант 4.
 *
 * Додаток підраховує кількість символів у кожному текстовому файлі.
 * Пробіл, повернення каретки (\r), перехід на новий рядок (\n) і табуляція (\t)
 * не рахуються. Виводиться назва файлу і кількість символів.
 *
 * Можна вводити шлях до файлу або до каталогу (тоді обробляються всі .txt файли в ньому).
 * Порожній рядок завершує роботу.
 */
public class Task2 {

    public static void main(String[] args) {
        IOHelper.println("ЛР4, завдання 2 (варіант 4): кількість символів у текстових файлах");
        IOHelper.println("Пробіли, \\r, \\n і табуляція не враховуються.");
        IOHelper.println("Введіть шлях до файлу або каталогу (порожній рядок — завершити).");
        IOHelper.println();

        int filesProcessed = 0;
        long totalChars = 0;

        try {
            while (true) {
                String path = IOHelper.readLine("Шлях: ");
                if (path.isEmpty()) {
                    break;
                }

                File target = new File(path);
                if (!target.exists()) {
                    IOHelper.println("Не знайдено: " + target.getAbsolutePath() + ". Спробуйте ще раз.");
                    continue;
                }

                List<File> files = collectFiles(target);
                if (files.isEmpty()) {
                    IOHelper.println("У каталозі немає .txt файлів. Спробуйте інший шлях.");
                    continue;
                }

                for (File file : files) {
                    try {
                        long count = countChars(file);
                        IOHelper.println(String.format("  %-30s %d", file.getName(), count));
                        filesProcessed++;
                        totalChars += count;
                    } catch (IOException e) {
                        IOHelper.println("  Не вдалося прочитати " + file.getName() + ": " + e.getMessage());
                    }
                }
                IOHelper.println();
            }
        } catch (IllegalStateException e) {
            IOHelper.println();
            IOHelper.println(e.getMessage());
        }

        IOHelper.println("Оброблено файлів: " + filesProcessed + ", усього символів: " + totalChars);
    }

    // Файл -> список з одного файлу; каталог -> усі .txt файли в ньому (за алфавітом)
    private static List<File> collectFiles(File target) {
        List<File> result = new ArrayList<>();
        if (target.isFile()) {
            result.add(target);
            return result;
        }
        File[] txtFiles = target.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (txtFiles != null) {
            Arrays.sort(txtFiles);
            result.addAll(Arrays.asList(txtFiles));
        }
        return result;
    }

    // Послідовно читає файл посимвольно і рахує все, крім пробілу, \r, \n і \t
    static long countChars(File file) throws IOException {
        long count = 0;
        try (BufferedReader reader = IOHelper.openReader(file)) {
            int code;
            while ((code = reader.read()) != -1) {  // read() повертає int, -1 — кінець файлу
                char c = (char) code;               // явне приведення до char
                if (c != ' ' && c != '\r' && c != '\n' && c != '\t') {
                    count++;
                }
            }
        }
        return count;
    }
}