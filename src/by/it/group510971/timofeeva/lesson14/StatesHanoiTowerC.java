
package by.it.group510971.timofeeva.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {

    private static int[] parent;
    private static int[] size;

    // Высота каждой пирамиды
    private static int[] heights;

    // Первый шаг, у которого встретилась определённая максимальная высота
    private static int[] firstByHeight;

    private static int step = 0;

    // Поиск корня с сокращением пути
    private static int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Объединение по размеру
    private static void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return;
        }

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
    }

    // Запоминаем очередной шаг и объединяем его с шагами
    // с такой же максимальной высотой пирамид
    private static void addStep() {
        step++;

        parent[step] = step;
        size[step] = 1;

        int maxHeight = Math.max(
                heights[0],
                Math.max(heights[1], heights[2])
        );

        if (firstByHeight[maxHeight] == -1) {
            firstByHeight[maxHeight] = step;
        } else {
            union(step, firstByHeight[maxHeight]);
        }
    }

    // Рекурсивное решение задачи Ханойских башен
    private static void hanoi(int n, int from, int to, int auxiliary) {
        if (n == 0) {
            return;
        }

        hanoi(n - 1, from, auxiliary, to);

        // Перемещаем одно кольцо
        heights[from]--;
        heights[to]++;

        // Стартовое состояние не учитывается
        addStep();

        hanoi(n - 1, auxiliary, to, from);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        step = 0; // Сбрасываем счётчик при каждом запуске

        if (n <= 0) {
            return;
        }

        // Всего ходов: 2^N - 1
        int totalSteps = (1 << n) - 1;

        parent = new int[totalSteps + 1];
        size = new int[totalSteps + 1];

        heights = new int[]{n, 0, 0};
        firstByHeight = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            firstByHeight[i] = -1;
        }

        // Переносим пирамиду с A на B через C
        hanoi(n, 0, 1, 2);

        // Собираем размеры получившихся компонент DSU
        int[] result = new int[n];
        int count = 0;

        for (int i = 1; i <= step; i++) {
            if (parent[i] == i) {
                result[count++] = size[i];
            }
        }

        // Сортировка размеров по возрастанию без коллекций
        for (int i = 1; i < count; i++) {
            int current = result[i];
            int j = i - 1;

            while (j >= 0 && result[j] > current) {
                result[j + 1] = result[j];
                j--;
            }

            result[j + 1] = current;
        }

        // Вывод результата
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(result[i]);
        }
    }
}
