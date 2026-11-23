package by.it.group510971.timofeeva.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Видеорегистраторы и площадь.
На площади установлена одна или несколько камер.
Известны данные о том, когда каждая из них включалась и выключалась (отрезки работы)
Известен список событий на площади (время начала каждого события).
Вам необходимо определить для каждого события сколько камер его записали.

В первой строке задано два целых числа:
    число включений камер (отрезки) 1<=n<=50000
    число событий (точки) 1<=m<=50000.

Следующие n строк содержат по два целых числа ai и bi (ai<=bi) -
координаты концов отрезков (время работы одной какой-то камеры).
Последняя строка содержит m целых чисел - координаты точек.
Все координаты не превышают 10E8 по модулю (!).

Точка считается принадлежащей отрезку, если она находится внутри него или на границе.

Для каждой точки в порядке их появления во вводе выведите,
скольким отрезкам она принадлежит.
    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/

public class A_QSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_QSort.class.getResourceAsStream("dataA.txt");
        A_QSort instance = new A_QSort();
        int[] result = instance.getAccessory(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory(InputStream stream) throws FileNotFoundException {
        //подготовка к чтению данных
        Scanner scanner = new Scanner(stream);
        //!!!!!!!!!!!!!!!!!!!!!!!!!     НАЧАЛО ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        int[] starts = new int[n];
        int[] ends = new int[n];
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            int start = scanner.nextInt();
            int stop = scanner.nextInt();

            starts[i] = Math.min(start, stop);
            ends[i] = Math.max(start, stop);
        }

        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        scanner.close();

        java.util.Arrays.sort(starts);
        java.util.Arrays.sort(ends);

        for (int i = 0; i < m; i++) {
            int x = points[i];

            int started = upperBound(starts, x);
            int finished = lowerBound(ends, x);

            result[i] = started - finished;
        }

        return result;
    }

    int upperBound(int[] a, int value) {
        int left = 0;
        int right = a.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (a[middle] <= value) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }

    int lowerBound(int[] a, int value) {
        int left = 0;
        int right = a.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (a[middle] < value) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }

    //отрезок
    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
            //тут вообще-то лучше доделать конструктор на случай если
            //концы отрезков придут в обратном порядке
        }

        @Override
        public int compareTo(Segment o) {
            //подумайте, что должен возвращать компаратор отрезков

            return 0;
        }
    }

}
