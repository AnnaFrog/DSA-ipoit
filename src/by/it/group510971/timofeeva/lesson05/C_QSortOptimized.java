package by.it.group510971.timofeeva.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Видеорегистраторы и площадь 2.
Условие то же что и в задаче А.

        По сравнению с задачей A доработайте алгоритм так, чтобы
        1) он оптимально использовал время и память:
            - за стек отвечает элиминация хвостовой рекурсии
            - за сам массив отрезков - сортировка на месте
            - рекурсивные вызовы должны проводиться на основе 3-разбиения

        2) при поиске подходящих отрезков для точки реализуйте метод бинарного поиска
        для первого отрезка решения, а затем найдите оставшуюся часть решения
        (т.е. отрезков, подходящих для точки, может быть много)

    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/



public class C_QSortOptimized {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);

        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory2(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        int n = scanner.nextInt();
        int m = scanner.nextInt();

        Segment[] segments = new Segment[n];
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            segments[i] = new Segment(scanner.nextInt(), scanner.nextInt());
        }

        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        scanner.close();

        quickSort(segments, 0, n - 1);

        for (int i = 0; i < m; i++) {
            int point = points[i];
            int right = upperBound(segments, point);

            int count = 0;

            for (int j = 0; j < right; j++) {
                if (segments[j].stop >= point) {
                    count++;
                }
            }

            result[i] = count;
        }

        return result;
    }

    // Бинарный поиск первого отрезка, у которого start > point
    int upperBound(Segment[] segments, int point) {
        int left = 0;
        int right = segments.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (segments[middle].start <= point) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }

    // Быстрая сортировка с трёхчастным разбиением
    void quickSort(Segment[] a, int left, int right) {
        while (left < right) {
            Segment pivot = a[left + (right - left) / 2];

            int lt = left;
            int i = left;
            int gt = right;

            while (i <= gt) {
                int cmp = a[i].compareTo(pivot);

                if (cmp < 0) {
                    swap(a, lt, i);
                    lt++;
                    i++;
                } else if (cmp > 0) {
                    swap(a, i, gt);
                    gt--;
                } else {
                    i++;
                }
            }

            // Рекурсивно сортируем меньшую часть,
            // большую обрабатываем циклом
            if (lt - left < right - gt) {
                quickSort(a, left, lt - 1);
                left = gt + 1;
            } else {
                quickSort(a, gt + 1, right);
                right = lt - 1;
            }
        }
    }

    void swap(Segment[] a, int i, int j) {
        Segment temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = Math.min(start, stop);
            this.stop = Math.max(start, stop);
        }

        @Override
        public int compareTo(Segment o) {
            int cmp = Integer.compare(this.start, o.start);

            if (cmp == 0) {
                return Integer.compare(this.stop, o.stop);
            }

            return cmp;
        }
    }
}

