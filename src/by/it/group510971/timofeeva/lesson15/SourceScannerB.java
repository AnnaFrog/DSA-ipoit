
package by.it.group510971.timofeeva.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class SourceScannerB {

    private static class SourceInfo {
        String path;
        int size;

        SourceInfo(String path, int size) {
            this.path = path;
            this.size = size;
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir")
                + File.separator + "src" + File.separator;

        List<SourceInfo> results = new ArrayList<>();

        scanDirectory(new File(src), src, results);

        // Сначала сортируем по размеру, затем по пути
        results.sort(Comparator
                .comparingInt((SourceInfo info) -> info.size)
                .thenComparing(info -> info.path));

        for (SourceInfo info : results) {
            System.out.println(info.size + " " + info.path);
        }
    }

    // Рекурсивный обход каталога и всех подкаталогов
    private static void scanDirectory(
            File directory,
            String src,
            List<SourceInfo> results
    ) {
        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, src, results);
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                processFile(file, src, results);
            }
        }
    }

    private static void processFile(
            File file,
            String src,
            List<SourceInfo> results
    ) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            // Декодер пропускает некорректные последовательности символов
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .onUnmappableCharacter(CodingErrorAction.IGNORE);

            String text = decoder.decode(ByteBuffer.wrap(bytes)).toString();

            // Исключаем тестовые файлы
            if (text.contains("@Test") || text.contains("org.junit.Test")) {
                return;
            }

            // Удаляем комментарии
            text = removeComments(text);

            // Удаляем package и import
            text = Pattern.compile(
                    "(?m)^\\s*(package|import)\\s+[^;]*;[^\\r\\n]*"
            ).matcher(text).replaceAll("");

            // Удаляем пустые строки
            text = Pattern.compile("(?m)^[\\t \\r\\f]*\\n")
                    .matcher(text).replaceAll("");

            // Удаляем символы с кодом меньше 33 по краям
            int start = 0;
            int end = text.length();

            while (start < end && text.charAt(start) < 33) {
                start++;
            }

            while (end > start && text.charAt(end - 1) < 33) {
                end--;
            }

            text = text.substring(start, end);

            // Вычисляем размер очищенного текста в байтах UTF-8
            int size = text.getBytes(StandardCharsets.UTF_8).length;

            // Получаем относительный путь от src
            String relativePath = file.getAbsolutePath()
                    .substring(src.length());

            results.add(new SourceInfo(relativePath, size));

        } catch (IOException e) {
            // Если файл не удалось прочитать, пропускаем его
        }
    }

    // Удаление однострочных и многострочных комментариев
    private static String removeComments(String text) {
        StringBuilder result = new StringBuilder();

        int i = 0;
        int length = text.length();

        while (i < length) {
            char c = text.charAt(i);

            // Строковый литерал — комментарии внутри него не удаляем
            if (c == '"') {
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

            // Однострочный комментарий //
            else if (c == '/' && i + 1 < length
                    && text.charAt(i + 1) == '/') {

                i += 2;

                // Пропускаем комментарий до конца строки
                while (i < length && text.charAt(i) != '\n') {
                    i++;
                }
            }

            // Многострочный комментарий /* */
            else if (c == '/' && i + 1 < length
                    && text.charAt(i + 1) == '*') {

                i += 2;

                // Пропускаем комментарий до закрывающего */
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
}
