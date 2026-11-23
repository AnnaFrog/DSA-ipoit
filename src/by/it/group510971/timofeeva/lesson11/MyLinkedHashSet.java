
package by.it.group510971.timofeeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;

public class MyLinkedHashSet<E> implements java.util.Set<E> {

    // Узел хранит элемент и две ссылки для разных цепочек
    private static class Node<E> {
        E data;

        // Связь с элементами, имеющими такой же индекс
        Node<E> bucketNext;

        // Связь в порядке добавления
        Node<E> orderNext;

        Node(E data) {
            this.data = data;
        }
    }

    // Массив для хранения цепочек коллизий
    @SuppressWarnings("unchecked")
    private Node<E>[] buckets = (Node<E>[]) new Node[16];

    // Начало и конец цепочки в порядке добавления
    private Node<E> head;
    private Node<E> tail;

    private int size = 0;

    // Вычисляем индекс ячейки по хеш-коду элемента
    private int getIndex(Object element) {
        int hash = element == null ? 0 : element.hashCode();
        hash ^= (hash >>> 16);
        return (hash & 0x7fffffff) % buckets.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Очищаем таблицу и цепочку порядка добавления
    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }

        head = null;
        tail = null;
        size = 0;
    }

    // Добавляем элемент, если его ещё нет в множестве
    @Override
    public boolean add(E element) {
        int index = getIndex(element);
        Node<E> current = buckets[index];

        // Проверяем наличие дубликата в цепочке коллизий
        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                return false;
            }

            current = current.bucketNext;
        }

        // Добавляем новый узел в хеш-таблицу
        Node<E> newNode = new Node<>(element);
        newNode.bucketNext = buckets[index];
        buckets[index] = newNode;

        // Одновременно добавляем его в конец порядка вставки
        if (tail == null) {
            head = newNode;
        } else {
            tail.orderNext = newNode;
        }

        tail = newNode;
        size++;

        return true;
    }

    // Проверяем наличие элемента
    @Override
    public boolean contains(Object element) {
        int index = getIndex(element);
        Node<E> current = buckets[index];

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                return true;
            }

            current = current.bucketNext;
        }

        return false;
    }

    // Удаляем элемент из обеих цепочек
    @Override
    public boolean remove(Object element) {
        int index = getIndex(element);
        Node<E> current = buckets[index];
        Node<E> previous = null;

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {

                // Удаляем из цепочки хеш-таблицы
                if (previous == null) {
                    buckets[index] = current.bucketNext;
                } else {
                    previous.bucketNext = current.bucketNext;
                }

                // Удаляем из цепочки порядка добавления
                Node<E> orderPrevious = null;
                Node<E> orderCurrent = head;

                while (orderCurrent != current) {
                    orderPrevious = orderCurrent;
                    orderCurrent = orderCurrent.orderNext;
                }

                if (orderPrevious == null) {
                    head = current.orderNext;
                } else {
                    orderPrevious.orderNext = current.orderNext;
                }

                if (tail == current) {
                    tail = orderPrevious;
                }

                size--;
                return true;
            }

            previous = current;
            current = current.bucketNext;
        }

        return false;
    }

    // Формируем строку в порядке добавления элементов
    @Override
    public String toString() {
        String result = "[";
        Node<E> current = head;

        while (current != null) {
            if (current != head) {
                result += ", ";
            }

            result += current.data;
            current = current.orderNext;
        }

        return result + "]";
    }

    // Проверяем, содержатся ли все элементы коллекции
    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    // Добавляем все элементы, сохраняя порядок их обхода
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Object[] elements = collection.toArray();
        boolean changed = false;

        for (Object element : elements) {
            @SuppressWarnings("unchecked")
            E value = (E) element;

            if (add(value)) {
                changed = true;
            }
        }

        return changed;
    }

    // Удаляем все элементы, которые есть в переданной коллекции
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Node<E> current = head;

        while (current != null) {
            Node<E> next = current.orderNext;

            if (collection.contains(current.data)) {
                remove(current.data);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    // Оставляем только элементы, которые есть в переданной коллекции
    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Node<E> current = head;

        while (current != null) {
            Node<E> next = current.orderNext;

            if (!collection.contains(current.data)) {
                remove(current.data);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    // Итератор проходит по элементам в порядке добавления
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            private Node<E> lastReturned = null;
            private boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }

                lastReturned = current;
                current = current.orderNext;
                canRemove = true;

                return lastReturned.data;
            }

            @Override
            public void remove() {
                if (!canRemove) {
                    throw new IllegalStateException();
                }

                MyLinkedHashSet.this.remove(lastReturned.data);
                canRemove = false;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<E> current = head;
        int index = 0;

        while (current != null) {
            result[index++] = current.data;
            current = current.orderNext;
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

        Node<E> current = head;
        int index = 0;

        while (current != null) {
            array[index++] = (T) current.data;
            current = current.orderNext;
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
}
