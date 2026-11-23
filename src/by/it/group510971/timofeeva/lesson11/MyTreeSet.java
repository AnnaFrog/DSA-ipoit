
package by.it.group510971.timofeeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyTreeSet<E> implements java.util.Set<E> {

    // Массив для хранения отсортированных элементов
    private E[] elements = (E[]) new Object[10];

    private int size = 0;

    // Сравниваем два элемента
    @SuppressWarnings("unchecked")
    private int compare(E first, E second) {
        return ((Comparable<E>) first).compareTo(second);
    }

    // Увеличиваем вместимость массива
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements = (E[]) new Object[elements.length * 2];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }

            elements = newElements;
        }
    }

    // Бинарный поиск позиции элемента
    // Если элемент найден, возвращаем его индекс.
    // Если не найден, возвращаем отрицательную позицию вставки.
    private int binarySearch(Object element) {
        int left = 0;
        int right = size - 1;

        @SuppressWarnings("unchecked")
        E value = (E) element;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int comparison = compare(elements[middle], value);

            if (comparison < 0) {
                left = middle + 1;
            } else if (comparison > 0) {
                right = middle - 1;
            } else {
                return middle;
            }
        }

        return -(left + 1);
    }

    @Override
    public String toString() {
        String result = "[";

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result += ", ";
            }

            result += elements[i];
        }

        return result + "]";
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Удаляем все элементы
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }

        size = 0;
    }

    // Добавляем элемент в отсортированную позицию
    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        int position = binarySearch(element);

        // Такой элемент уже есть
        if (position >= 0) {
            return false;
        }

        // Получаем позицию, куда нужно вставить элемент
        position = -position - 1;

        ensureCapacity();

        // Освобождаем место для нового элемента
        for (int i = size; i > position; i--) {
            elements[i] = elements[i - 1];
        }

        elements[position] = element;
        size++;

        return true;
    }

    // Удаляем элемент и сдвигаем оставшиеся элементы
    @Override
    public boolean remove(Object element) {
        if (element == null) {
            return false;
        }

        int position = binarySearch(element);

        if (position < 0) {
            return false;
        }

        for (int i = position; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
        size--;

        return true;
    }

    // Проверяем наличие элемента
    @Override
    public boolean contains(Object element) {
        if (element == null) {
            return false;
        }

        return binarySearch(element) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    // Добавляем элементы, сохраняя сортировку
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Object[] values = collection.toArray();
        boolean changed = false;

        for (Object value : values) {
            @SuppressWarnings("unchecked")
            E element = (E) value;

            if (add(element)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;

        for (int i = 0; i < size; ) {
            if (collection.contains(elements[i])) {
                for (int j = i; j < size - 1; j++) {
                    elements[j] = elements[j + 1];
                }

                elements[size - 1] = null;
                size--;
                changed = true;
            } else {
                i++;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        int newSize = 0;

        // Оставляем только элементы, которые есть в коллекции
        for (int i = 0; i < size; i++) {
            if (collection.contains(elements[i])) {
                elements[newSize++] = elements[i];
            } else {
                changed = true;
            }
        }

        // Очищаем освободившиеся ячейки
        for (int i = newSize; i < size; i++) {
            elements[i] = null;
        }

        size = newSize;
        return changed;
    }

    // Итератор проходит по массиву в порядке возрастания
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = cursor;
                return elements[cursor++];
            }

            @Override
            public void remove() {
                if (lastReturned < 0) {
                    throw new IllegalStateException();
                }

                MyTreeSet.this.remove(elements[lastReturned]);
                cursor = lastReturned;
                lastReturned = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        if (array.length < size) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        for (int i = 0; i < size; i++) {
            array[i] = (T) elements[i];
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
}
