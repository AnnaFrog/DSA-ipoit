
package by.it.group510971.timofeeva.lesson10;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {

    // Массив для хранения элементов
    private E[] elements = (E[]) new Object[10];

    // Индекс первого элемента
    private int head = 0;

    // Количество элементов
    private int size = 0;

    // Получаем физический индекс элемента по его позиции в деке
    private int index(int position) {
        return (head + position) % elements.length;
    }

    // Увеличиваем массив, сохраняя порядок элементов
    private void ensureCapacity() {
        if (size == elements.length) {
            E[] newElements = (E[]) new Object[elements.length * 2];

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[index(i)];
            }

            elements = newElements;
            head = 0;
        }
    }

    // Удаляем элемент по логической позиции
    private E removeAt(int position) {
        E removed = elements[index(position)];

        // Сдвигаем меньшую часть массива
        if (position < size / 2) {
            for (int i = position; i > 0; i--) {
                elements[index(i)] = elements[index(i - 1)];
            }

            elements[head] = null;
            head = (head + 1) % elements.length;
        } else {
            for (int i = position; i < size - 1; i++) {
                elements[index(i)] = elements[index(i + 1)];
            }

            elements[index(size - 1)] = null;
        }

        size--;
        return removed;
    }

    @Override
    public String toString() {
        String result = "[";

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result += ", ";
            }

            result += elements[index(i)];
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

    // Добавление элемента в конец
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        ensureCapacity();

        // Перемещаем начало массива на одну позицию назад
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        ensureCapacity();

        elements[index(size)] = element;
        size++;
    }

    // Возвращаем первый элемент без удаления
    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return elements[head];
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return elements[index(size - 1)];
    }

    // Удаляем и возвращаем первый элемент
    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        E removed = elements[head];
        elements[head] = null;

        head = (head + 1) % elements.length;
        size--;

        return removed;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        int lastIndex = index(size - 1);
        E removed = elements[lastIndex];

        elements[lastIndex] = null;
        size--;

        return removed;
    }

    // Остальные методы интерфейса Deque

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return pollFirst();
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return pollLast();
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E peekFirst() {
        return size == 0 ? null : getFirst();
    }

    @Override
    public E peekLast() {
        return size == 0 ? null : getLast();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (int i = 0; i < size; i++) {
            E current = elements[index(i)];

            if (o == null ? current == null : o.equals(current)) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            E current = elements[index(i)];

            if (o == null ? current == null : o.equals(current)) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            E current = elements[index(i)];

            if (o == null ? current == null : o.equals(current)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[index(i)] = null;
        }

        head = 0;
        size = 0;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;

        for (E element : c) {
            addLast(element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (int i = 0; i < size; ) {
            if (c.contains(elements[index(i)])) {
                removeAt(i);
                changed = true;
            } else {
                i++;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;

        for (int i = 0; i < size; ) {
            if (!c.contains(elements[index(i)])) {
                removeAt(i);
                changed = true;
            } else {
                i++;
            }
        }

        return changed;
    }

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
                return elements[index(cursor++)];
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                removeAt(lastReturned);

                if (lastReturned < cursor) {
                    cursor--;
                }

                lastReturned = -1;
            }
        };
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private int cursor = size - 1;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return cursor >= 0;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = cursor;
                return elements[index(cursor--)];
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                removeAt(lastReturned);
                lastReturned = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = elements[index(i)];
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(
                    a.getClass().getComponentType(), size);
        }

        for (int i = 0; i < size; i++) {
            a[i] = (T) elements[index(i)];
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }
}
