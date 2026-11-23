
package by.it.group510971.timofeeva.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Даны события events
реализуйте метод calcStartTimes, так, чтобы число включений регистратора на
заданный период времени (1) было минимальным, а все события events
были зарегистрированы.
Алгоритм жадный. Для реализации обдумайте надежный шаг.
*/

public class A_VideoRegistrator {

    public static void main(String[] args) {
        A_VideoRegistrator instance = new A_VideoRegistrator();
        double[] events = new double[]{1, 1.1, 1.6, 2.2, 2.4, 2.7, 3.9, 8.1, 9.1, 5.5, 3.7};
        List<Double> starts = instance.calcStartTimes(events, 1);
        System.out.println(starts);
    }

    //модификаторы доступа опущены для возможности тестирования
    List<Double> calcStartTimes(double[] events, double workDuration) {
        List<Double> result = new ArrayList<>();

        // Сортируем события по возрастанию
        double[] sortedEvents = events.clone();
        Arrays.sort(sortedEvents);

        int i = 0;

        // Пока есть незарегистрированные события
        while (i < sortedEvents.length) {

            // Получаем самое раннее событие
            double start = sortedEvents[i];

            // Запоминаем время старта видеокамеры
            result.add(start);

            // Вычисляем момент окончания работы видеокамеры
            double end = start + workDuration;

            // Пропускаем все события, которые попадут в запись
            while (i < sortedEvents.length && sortedEvents[i] <= end) {
                i++;
            }
        }

        return result;
    }
}
