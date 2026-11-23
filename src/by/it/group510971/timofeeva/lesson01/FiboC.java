package by.it.group510971.timofeeva.lesson01;

/*
 * Даны целые числа 1<=n<=1E18 и 2<=m<=1E5,
 * необходимо найти остаток от деления n-го числа Фибоначчи на m
 * время расчета должно быть не более 2 секунд
 */

public class FiboC {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        int n = 55555;
        int m = 1000;
        System.out.printf("fasterC(%d)=%d \n\t time=%d \n\n", n, fibo.fasterC(n, m), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    long fasterC(long n, int m) {
        long previous = 0;
        long current = 1;
        long period = 0;

        // Находим период Пизано
        for (long i = 0; i < 6L * m; i++) {
            long next = (previous + current) % m;
            previous = current;
            current = next;

            if (previous == 0 && current == 1) {
                period = i + 1;
                break;
            }
        }

        // Уменьшаем n до значения внутри периода
        long index = n % period;

        // Вычисляем число Фибоначчи по модулю m
        previous = 0;
        current = 1;

        if (index == 0) {
            return 0;
        }

        for (long i = 2; i <= index; i++) {
            long next = (previous + current) % m;
            previous = current;
            current = next;
        }

        return current;
    }


}

