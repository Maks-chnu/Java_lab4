package Java_lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Лабораторна робота №4, завдання 1, варіант 4.
 *
 * Кожен рядок файлу задає ціле або дробове число:
 *  - рядок містить лише цифри -> ціле число;
 *  - інакше -> дробове число;
 *  - якщо число записане неправильно -> рядок відкидається.
 * Порахувати суму дробових чисел і добуток усіх цілих (вивести на консоль).
 * Відсортовані цілі та дробові числа записати у файли, задані користувачем.
 */
public class Task1 {

    private static final Pattern INTEGER = Pattern.compile("\\d+");

    private static final Pattern DECIMAL =
            Pattern.compile("[+-]?(\\d+([.,]\\d*)?|[.,]\\d+)([eE][+-]?\\d+)?");

    public static void main(String[] args) {
        IOHelper.println("ЛР4, завдання 1 (варіант 4): цілі та дробові числа з файлу");
        IOHelper.println();

        try {

            File input = IOHelper.askExistingFile("Введіть шлях до вхідного файлу: ");

            List<String> lines = readLines(input);

            //Розбір рядків
            List<BigInteger> integers = new ArrayList<>();
            List<BigDecimal> decimals = new ArrayList<>();
            List<String> rejected = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String raw = lines.get(i);
                String s = raw.trim();

                if (INTEGER.matcher(s).matches()) {
                    integers.add(new BigInteger(s));
                } else if (DECIMAL.matcher(s).matches()) {
                    decimals.add(new BigDecimal(s.replace(',', '.')));
                } else {
                    String shown = s.isEmpty() ? "(порожній рядок)" : "\"" + raw + "\"";
                    rejected.add("рядок " + (i + 1) + ": " + shown);
                }
            }

            //Сортування за зростанням
            Collections.sort(integers);
            Collections.sort(decimals);

            //Обчислення
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal d : decimals) {
                sum = sum.add(d);
            }
            BigInteger product = BigInteger.ONE;
            for (BigInteger n : integers) {
                product = product.multiply(n);
            }

            //Виведення на консоль
            IOHelper.println();
            IOHelper.println("Прочитано рядків: " + lines.size());
            IOHelper.println("Цілих чисел: " + integers.size() + " -> " + formatIntegers(integers));
            IOHelper.println("Дробових чисел: " + decimals.size() + " -> " + formatDecimals(decimals));
            IOHelper.println("Відкинуто рядків: " + rejected.size());
            for (String r : rejected) {
                IOHelper.println("   " + r);
            }
            IOHelper.println();

            if (decimals.isEmpty()) {
                IOHelper.println("Дробових чисел немає, сума = 0");
            } else {
                IOHelper.println("Сума дробових чисел: " + sum.toPlainString());
            }
            if (integers.isEmpty()) {
                IOHelper.println("Цілих чисел немає, добуток не обчислюється");
            } else {
                IOHelper.println("Добуток цілих чисел: " + product);
            }
            IOHelper.println();

            //Запис у файли (кожен файл запитується окремо)
            File[] used = {input};
            try (BufferedWriter writer = IOHelper.askOutputWriter(
                    "Введіть шлях до файлу для ЦІЛИХ чисел: ", used)) {
                for (BigInteger n : integers) {
                    writer.write(n.toString());
                    writer.newLine();
                }
            }
            IOHelper.println("Цілі числа записано.");

            try (BufferedWriter writer = IOHelper.askOutputWriter(
                    "Введіть шлях до файлу для ДРОБОВИХ чисел: ", used)) {
                for (BigDecimal d : decimals) {
                    writer.write(d.toPlainString());
                    writer.newLine();
                }
            }
            IOHelper.println("Дробові числа записано.");
            IOHelper.println("Готово!");

        } catch (IOException e) {
            IOHelper.println("Помилка роботи з файлом: " + e.getMessage());
        } catch (IllegalStateException e) {
            IOHelper.println(e.getMessage());
        }
    }

    // Послідовно читає всі рядки файлу у список
    private static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = IOHelper.openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static String formatIntegers(List<BigInteger> list) {
        return list.toString();
    }

    private static String formatDecimals(List<BigDecimal> list) {
        List<String> parts = new ArrayList<>();
        for (BigDecimal d : list) {
            parts.add(d.toPlainString());
        }
        return parts.toString();
    }
}