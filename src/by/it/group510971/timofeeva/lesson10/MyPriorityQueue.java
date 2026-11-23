
package by.it.group510971.timofeeva.lesson10;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyPriorityQueue<E> implements java.util.Queue<E> {

    // Массив, в котором хранится бинарная куча
    private E[] elements = (E[]) new Object[10];

    // Количество элементов в куче
    private int size = 0;

    // Сравниваем два элемента
    @SuppressWarnings("unchecked")
    private int compare(E first, E second) {
        return ((Comparable<E>) first).compareTo(second);
    }

    // Увеличиваем массив, если свободного места больше нет
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements = (E[]) new Object[elements.length * 2];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }

            elements = newElements;
        }
    }

    // Меняем два элемента местами
    private void swap(int first, int second) {
        E temp = elements[first];
        elements[first] = elements[second];
        elements[second] = temp;
    }

    // Поднимаем элемент вверх, пока он меньше родителя
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (compare(elements[index], elements[parent]) >= 0) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    // Перестраиваем кучу после массового удаления элементов
    private void heapify() {
        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    // Опускаем элемент вниз, пока он больше одного из детей
    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = index * 2 + 2;
            int smallest = index;

            if (left < size &&
                    compare(elements[left], elements[smallest]) < 0) {
                smallest = left;
            }

            if (right < size &&
                    compare(elements[right], elements[smallest]) < 0) {
                smallest = right;
            }

            if (smallest == index) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    // Удаляем элемент по индексу и восстанавливаем свойства кучи
    private E removeAt(int index) {
        E removed = elements[index];

        elements[index] = elements[size - 1];
        elements[size - 1] = null;
        size--;

        if (index < size) {
            int parent = (index - 1) / 2;

            if (index > 0 &&
                    compare(elements[index], elements[parent]) < 0) {
                siftUp(index);
            } else {
                siftDown(index);
            }
        }

        return removed;
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

    // Очищаем кучу
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }

        size = 0;
    }

    // Добавляем элемент в кучу
    @Override
    public boolean add(E element) {
        if (!offer(element)) {
            throw new IllegalStateException();
        }

        return true;
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        ensureCapacity();

        elements[size] = element;
        siftUp(size);
        size++;

        return true;
    }

    // Возвращаем минимальный элемент и удаляем его
    @Override
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return poll();
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        return removeAt(0);
    }

    // Получаем минимальный элемент без удаления
    @Override
    public E peek() {
        return size == 0 ? null : elements[0];
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return elements[0];
    }

    // Проверяем наличие элемента
    @Override
    public boolean contains(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == null ? elements[i] == null :
                    element.equals(elements[i])) {
                return true;
            }
        }

        return false;
    }

    // Удаляем первое совпадение с элементом
    @Override
    public boolean remove(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == null ? elements[i] == null :
                    element.equals(elements[i])) {
                removeAt(i);
                return true;
            }
        }

        return false;
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

    // Сначала создаём копию, чтобы корректно обработать addAll(this)
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Object[] values = collection.toArray();

        for (Object value : values) {
            @SuppressWarnings("unchecked")
            E element = (E) value;
            add(element);
        }

        return values.length > 0;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (collection == this) {
            boolean changed = size > 0;
            clear();
            return changed;
        }

        int oldSize = size;
        int newSize = 0;

        // Переносим в начало массива элементы, которых нет в коллекции
        for (int i = 0; i < oldSize; i++) {
            if (!collection.contains(elements[i])) {
                elements[newSize++] = elements[i];
            }
        }

        if (newSize == oldSize) {
            return false;
        }

        // Удаляем ссылки на старые элементы
        for (int i = newSize; i < oldSize; i++) {
            elements[i] = null;
        }

        size = newSize;

        heapify();

        return true;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        if (collection == this) {
            return false;
        }

        int oldSize = size;
        int newSize = 0;

        // Оставляем в массиве только нужные элементы
        for (int i = 0; i < oldSize; i++) {
            if (collection.contains(elements[i])) {
                elements[newSize++] = elements[i];
            }
        }

        if (newSize == oldSize) {
            return false;
        }

        // Очищаем освободившиеся ячейки
        for (int i = newSize; i < oldSize; i++) {
            elements[i] = null;
        }

        size = newSize;

        // Восстанавливаем свойства кучи один раз
        heapify();

        return true;
    }

    // Итератор проходит по внутреннему массиву кучи
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return elements[cursor++];
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
            array = (T[]) Array.newInstance(
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
