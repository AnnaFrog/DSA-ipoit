
package by.it.group510971.timofeeva.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class SourceScannerA {

    // Результат обработки одного файла
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

        File root = new File(src);

        List<SourceInfo> results = new ArrayList<>();

        // Обрабатываем все файлы в каталоге src и подкаталогах
        scanDirectory(root, src, results);

        // Сортировка по размеру, затем по пути
        results.sort(Comparator
                .comparingInt((SourceInfo info) -> info.size)
                .thenComparing(info -> info.path));

        // Вывод результата
        for (SourceInfo info : results) {
            System.out.println(info.size + " " + info.path);
        }
    }

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
            // Читаем файл, игнорируя некорректные последовательности символов
            String text = new String(
                    Files.readAllBytes(file.toPath()),
                    StandardCharsets.UTF_8
            );

            // Тестовые файлы пропускаем
            if (text.contains("@Test") || text.contains("org.junit.Test")) {
                return;
            }

            // Удаляем строки package и import
            text = Pattern.compile(
                    "(?m)^\\s*(package|import)\\s+[^;]*;[^\\r\\n]*"
            ).matcher(text).replaceAll("");

            // Удаляем символы с кодом меньше 33 в начале и конце
            int start = 0;
            int end = text.length();

            while (start < end && text.charAt(start) < 33) {
                start++;
            }

            while (end > start && text.charAt(end - 1) < 33) {
                end--;
            }

            text = text.substring(start, end);

            // Размер текста в байтах UTF-8
            int size = text.getBytes(StandardCharsets.UTF_8).length;

            // Относительный путь от src
            String relativePath = file.getAbsolutePath().substring(src.length());

            results.add(new SourceInfo(relativePath, size));

        } catch (MalformedInputException e) {
            // Некорректный файл пропускаем
        } catch (IOException e) {
            // Ошибки чтения не прерывают обработку остальных файлов
        }
    }
}
