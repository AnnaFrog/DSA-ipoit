
package by.it.group510971.timofeeva.lesson14;

import java.util.*;

public class SitesB {

    // DSU: для каждого сайта храним его родителя и размер множества
    private static class DSU {
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> size = new HashMap<>();

        // Добавление нового сайта
        void add(String site) {
            if (!parent.containsKey(site)) {
                parent.put(site, site);
                size.put(site, 1);
            }
        }

        // Поиск корня со сжатием пути
        String find(String site) {
            if (!parent.get(site).equals(site)) {
                parent.put(site, find(parent.get(site)));
            }
            return parent.get(site);
        }

        // Объединение по размеру множества
        void union(String a, String b) {
            String rootA = find(a);
            String rootB = find(b);

            if (rootA.equals(rootB)) {
                return;
            }

            // Меньшее множество присоединяем к большему
            if (size.get(rootA) < size.get(rootB)) {
                String temp = rootA;
                rootA = rootB;
                rootB = temp;
            }

            parent.put(rootB, rootA);
            size.put(rootA, size.get(rootA) + size.get(rootB));
            size.remove(rootB);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();

        // Читаем пары сайтов до строки end
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.equals("end")) {
                break;
            }

            if (line.isEmpty()) {
                continue;
            }

            String[] sites = line.split("\\+");

            String first = sites[0].trim();
            String second = sites[1].trim();

            // Добавляем сайты и объединяем их
            dsu.add(first);
            dsu.add(second);
            dsu.union(first, second);
        }

        // Собираем размеры всех кластеров
        List<Integer> clusters = new ArrayList<>();

        for (String site : dsu.parent.keySet()) {
            if (dsu.find(site).equals(site)) {
                clusters.add(dsu.size.get(site));
            }
        }

        // Сортируем по убыванию, как в примере задания
        clusters.sort(Collections.reverseOrder());

        // Выводим размеры кластеров
        for (int i = 0; i < clusters.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(clusters.get(i));
        }
    }
}
