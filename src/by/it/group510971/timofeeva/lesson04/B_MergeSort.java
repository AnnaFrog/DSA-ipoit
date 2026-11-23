package by.it.group510971.timofeeva.lesson04;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Реализуйте сортировку слиянием для одномерного массива.
Сложность алгоритма должна быть не хуже, чем O(n log n)

Первая строка содержит число 1<=n<=10000,
вторая - массив A[1…n], содержащий натуральные числа, не превосходящие 10E9.
Необходимо отсортировать полученный массив.

Sample Input:
5
2 3 9 2 9
Sample Output:
2 2 3 9 9
*/
public class B_MergeSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_MergeSort.class.getResourceAsStream("dataB.txt");
        B_MergeSort instance = new B_MergeSort();
        //long startTime = System.currentTimeMillis();
        int[] result = instance.getMergeSort(stream);
        //long finishTime = System.currentTimeMillis();
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getMergeSort(InputStream stream) throws FileNotFoundException {
        //подготовка к чтению данных
        Scanner scanner = new Scanner(stream);
        //!!!!!!!!!!!!!!!!!!!!!!!!!     НАЧАЛО ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!

        // Размер массива
        int n = scanner.nextInt();

        // Читаем массив
        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        // Запускаем сортировку слиянием
        mergeSort(a, 0, n - 1);

        scanner.close();
        return a;
    }

    // Рекурсивное разделение массива
    void mergeSort(int[] a, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        // Сортируем левую половину
        mergeSort(a, left, middle);

        // Сортируем правую половину
        mergeSort(a, middle + 1, right);

        // Объединяем отсортированные половины
        merge(a, left, middle, right);
    }

    // Слияние двух отсортированных частей
    void merge(int[] a, int left, int middle, int right) {
        int[] temp = new int[right - left + 1];

        int i = left;
        int j = middle + 1;
        int k = 0;

        // Сравниваем элементы обеих частей
        while (i <= middle && j <= right) {
            if (a[i] <= a[j]) {
                temp[k++] = a[i++];
            } else {
                temp[k++] = a[j++];
            }
        }

        // Копируем оставшиеся элементы левой части
        while (i <= middle) {
            temp[k++] = a[i++];
        }

        // Копируем оставшиеся элементы правой части
        while (j <= right) {
            temp[k++] = a[j++];
        }

        // Возвращаем результат в исходный массив
        for (int index = 0; index < temp.length; index++) {
            a[left + index] = temp[index];
        }
    }


}
