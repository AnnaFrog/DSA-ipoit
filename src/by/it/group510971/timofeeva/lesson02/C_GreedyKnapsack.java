package by.it.group510971.timofeeva.lesson02;
/*
Даны
1) объем рюкзака 4
2) число возможных предметов 60
3) сам набор предметов
    100 50
    120 30
    100 50
Все это указано в файле (by/it/a_khmelev/lesson02/greedyKnapsack.txt)

Необходимо собрать наиболее дорогой вариант рюкзака для этого объема
Предметы можно резать на кусочки (т.е. алгоритм будет жадным)
 */

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Arrays;

public class C_GreedyKnapsack {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = C_GreedyKnapsack.class.getResourceAsStream("greedyKnapsack.txt");
        double costFinal = new C_GreedyKnapsack().calc(inputStream);
        long finishTime = System.currentTimeMillis();
        System.out.printf("Общая стоимость %f (время %d)", costFinal, finishTime - startTime);
    }


    double calc(InputStream inputStream) throws FileNotFoundException {
        Scanner input = new Scanner(inputStream);

        int n = input.nextInt();      // количество предметов
        int W = input.nextInt();      // вместимость рюкзака

        Item[] items = new Item[n];

        for (int i = 0; i < n; i++) {
            items[i] = new Item(input.nextInt(), input.nextInt());
        }

        // Показываем предметы
        for (Item item : items) {
            System.out.println(item);
        }

        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);

        // Сортируем предметы по стоимости единицы веса
        Arrays.sort(items);

        double result = 0;
        double remainingWeight = W;

        // Набираем рюкзак от самых выгодных предметов
        for (Item item : items) {

            if (remainingWeight <= 0) {
                break;
            }

            // Сколько веса предмета можно взять
            double takenWeight = Math.min(remainingWeight, item.weight);

            // Добавляем стоимость взятой части предмета
            result += takenWeight * item.cost / item.weight;

            // Уменьшаем оставшуюся вместимость
            remainingWeight -= takenWeight;
        }

        System.out.printf("Удалось собрать рюкзак на сумму %f\n", result);

        input.close();
        return result;
    }


    private static class Item implements Comparable<Item> {
        int cost;
        int weight;

        Item(int cost, int weight) {
            this.cost = cost;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Item{" +
                   "cost=" + cost +
                   ", weight=" + weight +
                   '}';
        }

        @Override
        public int compareTo(Item o) {
            return Long.compare(
                    (long) o.cost * this.weight,
                    (long) this.cost * o.weight
            );
        }
    }
}