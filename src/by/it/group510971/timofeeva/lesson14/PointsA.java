
package by.it.group510971.timofeeva.lesson14;

import java.util.*;

public class PointsA {

    // Структура DSU
    private static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];

            // Изначально каждая точка образует отдельный кластер
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        // Поиск корня с эвристикой сжатия пути
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // Объединение по размеру дерева
        void union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);

            if (rootA == rootB) {
                return;
            }

            // Меньшее дерево присоединяем к большему
            if (size[rootA] < size[rootB]) {
                int temp = rootA;
                rootA = rootB;
                rootB = temp;
            }

            parent[rootB] = rootA;
            size[rootA] += size[rootB];
        }
    }

    // Квадрат расстояния между двумя точками
    private static double distanceSquared(double[] a, double[] b) {
        double dx = a[0] - b[0];
        double dy = a[1] - b[1];
        double dz = a[2] - b[2];

        return dx * dx + dy * dy + dz * dz;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double d = scanner.nextDouble();
        int n = scanner.nextInt();

        double[][] points = new double[n][3];

        // Считываем координаты точек
        for (int i = 0; i < n; i++) {
            points[i][0] = scanner.nextDouble();
            points[i][1] = scanner.nextDouble();
            points[i][2] = scanner.nextDouble();
        }

        DSU dsu = new DSU(n);

        // Проверяем расстояние между каждой парой точек
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distanceSquared(points[i], points[j]) < d * d) {
                    dsu.union(i, j);
                }
            }
        }

        // Собираем размеры кластеров
        List<Integer> clusters = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (dsu.find(i) == i) {
                clusters.add(dsu.size[i]);
            }
        }

        // Сортируем размеры по возрастанию
        Collections.sort(clusters, Collections.reverseOrder());

        // Выводим результат
        for (int i = 0; i < clusters.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(clusters.get(i));
        }
    }
}
