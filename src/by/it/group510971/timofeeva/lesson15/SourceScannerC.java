
package by.it.group510971.timofeeva.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SourceScannerC {

    private static final int LIMIT = 9;
    private static final int INF = 1000;

    private static class SourceInfo {
        String path;
        String text;
        List<Integer> copies = new ArrayList<>();

        SourceInfo(String path, String text) {
            this.path = path;
            this.text = text;
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir")
                + File.separator + "src" + File.separator;

        List<SourceInfo> files = new ArrayList<>();

        // Собираем и обрабатываем исходные файлы
        scanDirectory(new File(src), src, files);

        // Сортируем файлы по относительному пути
        files.sort(Comparator.comparing(info -> info.path));

        // Сравниваем тексты попарно
        for (int i = 0; i < files.size(); i++) {
            for (int j = i + 1; j < files.size(); j++) {

                String first = files.get(i).text;
                String second = files.get(j).text;

                boolean hasFibo = files.get(i).path.endsWith("FiboA.java")
                        || files.get(j).path.endsWith("FiboA.java");

// Выводим информацию обо всех сравнениях с FiboA
                if (hasFibo) {
                    System.out.println("DEBUG: сравниваем "
                            + files.get(i).path + " <-> "
                            + files.get(j).path);
                    System.out.println("Длины: " + first.length()
                            + " и " + second.length());
                }

                if (Math.abs(first.length() - second.length()) > LIMIT) {
                    if (hasFibo) {
                        System.out.println("Разница длин больше 9 — пропуск");
                    }
                    continue;
                }

                int distance = levenshtein(first, second);

                if (hasFibo) {
                    System.out.println("Расстояние: " + distance);
                }

                if (distance < 10) {
                    files.get(i).copies.add(j);
                    files.get(j).copies.add(i);
                }
            }
        }

        // Выводим только файлы, у которых есть копии
        for (SourceInfo file : files) {
            if (file.copies.isEmpty()) {
                continue;
            }

            System.out.println(file.path);

            for (int copyIndex : file.copies) {
                System.out.println(files.get(copyIndex).path);
            }
        }
    }

    // Рекурсивно обходим src и все подкаталоги
    private static void scanDirectory(
            File directory,
            String src,
            List<SourceInfo> files
    ) {
        File[] entries = directory.listFiles();

        if (entries == null) {
            return;
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                scanDirectory(entry, src, files);
            } else if (entry.isFile()
                    && entry.getName().endsWith(".java")) {
                processFile(entry, src, files);
            }
        }
    }

    // Читаем и обрабатываем один файл
    private static void processFile(
            File file,
            String src,
            List<SourceInfo> files
    ) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            // Некорректные последовательности UTF-8 игнорируются
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .onUnmappableCharacter(CodingErrorAction.IGNORE);

            String text = decoder.decode(ByteBuffer.wrap(bytes)).toString();

            // Пропускаем тестовые файлы
            if (text.contains("@Test") || text.contains("org.junit.Test")) {
                return;
            }

            // Удаляем комментарии
            text = removeComments(text);

            // Удаляем package и import
            text = removePackageAndImports(text);

            // Заменяем последовательности символов с кодом < 33
            // одним пробелом
            text = normalizeWhitespace(text);

            // Относительный путь от src
            String relativePath = file.getAbsolutePath()
                    .substring(src.length());

            files.add(new SourceInfo(relativePath, text));

        } catch (IOException e) {
            // Пропускаем файлы, которые не удалось прочитать
        }
    }

    // Удаление строк package и import
    private static String removePackageAndImports(String text) {
        StringBuilder result = new StringBuilder();

        int start = 0;

        while (start < text.length()) {
            int end = text.indexOf('\n', start);

            if (end == -1) {
                end = text.length();
            }

            String line = text.substring(start, end);
            String trimmed = line.trim();

            // Удаляем объявления package и import
            if (!trimmed.startsWith("package ")
                    && !trimmed.startsWith("import ")) {
                result.append(line);
                result.append('\n');
            }

            start = end + 1;
        }

        return result.toString();
    }

    // Удаление комментариев с сохранением строк и литералов
    private static String removeComments(String text) {
        StringBuilder result = new StringBuilder();

        int i = 0;
        int length = text.length();

        while (i < length) {
            char c = text.charAt(i);

            // Обычная строка
            if (c == '"') {
                // Проверяем, не является ли это началом text block
                if (i + 2 < length
                        && text.charAt(i + 1) == '"'
                        && text.charAt(i + 2) == '"') {

                    result.append("\"\"\"");
                    i += 3;

                    // Читаем содержимое text block до закрывающих """
                    while (i < length) {
                        if (i + 2 < length
                                && text.charAt(i) == '"'
                                && text.charAt(i + 1) == '"'
                                && text.charAt(i + 2) == '"') {
                            result.append("\"\"\"");
                            i += 3;
                            break;
                        }

                        result.append(text.charAt(i));
                        i++;
                    }
                } else {
                    result.append(c);
                    i++;

                    while (i < length) {
                        char current = text.charAt(i);
                        result.append(current);
                        i++;

                        if (current == '\\' && i < length) {
                            result.append(text.charAt(i));
                            i++;
                        } else if (current == '"') {
                            break;
                        }
                    }
                }
            }

            // Символьный литерал
            else if (c == '\'') {
                result.append(c);
                i++;

                while (i < length) {
                    char current = text.charAt(i);
                    result.append(current);
                    i++;

                    if (current == '\\' && i < length) {
                        result.append(text.charAt(i));
                        i++;
                    } else if (current == '\'') {
                        break;
                    }
                }
            }

            // Однострочный комментарий
            else if (c == '/' && i + 1 < length
                    && text.charAt(i + 1) == '/') {

                // Вместо комментария оставляем пробел
                result.append(' ');
                i += 2;

                while (i < length && text.charAt(i) != '\n') {
                    i++;
                }
            }

            // Многострочный комментарий
            else if (c == '/' && i + 1 < length
                    && text.charAt(i + 1) == '*') {

                result.append(' ');
                i += 2;

                while (i + 1 < length
                        && !(text.charAt(i) == '*'
                        && text.charAt(i + 1) == '/')) {
                    i++;
                }

                if (i + 1 < length) {
                    i += 2;
                }
            }

            // Обычный символ
            else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    // Все последовательности символов с кодом < 33 заменяются пробелом
    private static String normalizeWhitespace(String text) {
        StringBuilder result = new StringBuilder();

        boolean previousWasSpace = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c < 33) {
                if (!previousWasSpace) {
                    result.append(' ');
                    previousWasSpace = true;
                }
            } else {
                result.append(c);
                previousWasSpace = false;
            }
        }

        return result.toString().trim();
    }

    // Расстояние Левенштейна с ограничением в 9 правок
    private static int levenshtein(String a, String b) {
        int n = a.length();
        int m = b.length();

        if (Math.abs(n - m) > LIMIT) {
            return LIMIT + 1;
        }

        // Используем только две строки матрицы
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            previous[j] = j <= LIMIT ? j : INF;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                current[j] = INF;
            }

            if (i <= LIMIT) {
                current[0] = i;
            }

            int rowMin = INF;

            // Проверяем только полосу шириной 19 вокруг диагонали
            int from = Math.max(1, i - LIMIT);
            int to = Math.min(m, i + LIMIT);

            for (int j = from; j <= to; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1)
                        ? 0 : 1;

                int deletion = previous[j] + 1;
                int insertion = current[j - 1] + 1;
                int substitution = previous[j - 1] + cost;

                current[j] = Math.min(
                        deletion,
                        Math.min(insertion, substitution)
                );

                rowMin = Math.min(rowMin, current[j]);
            }

            // Если даже минимальное значение строки больше 9,
            // дальнейшие вычисления не нужны
            if (rowMin > LIMIT) {
                return LIMIT + 1;
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[m];
    }
}
