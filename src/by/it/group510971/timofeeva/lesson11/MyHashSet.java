
package by.it.group510971.timofeeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    // Узел односвязного списка для хранения элементов с одинаковым индексом
    private class Node {
        E data;
        Node next;

        Node(E data) {
            this.data = data;
        }
    }

    // Массив, в котором хранятся цепочки элементов
    private Node[] buckets = (Node[]) new MyHashSet.Node[16];

    // Количество уникальных элементов
    private int size = 0;

    // Вычисляем индекс ячейки по хеш-коду
    private int getIndex(Object element) {
        int hash = element == null ? 0 : element.hashCode();
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

    // Удаляем все элементы
    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }

        size = 0;
    }

    // Добавляем элемент, если его ещё нет в множестве
    @Override
    public boolean add(E element) {
        int index = getIndex(element);
        Node current = buckets[index];

        // Проверяем, нет ли уже такого элемента
        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                return false;
            }

            current = current.next;
        }

        // Добавляем новый узел в начало цепочки
        Node newNode = new Node(element);
        newNode.next = buckets[index];
        buckets[index] = newNode;

        size++;
        return true;
    }

    // Проверяем наличие элемента
    @Override
    public boolean contains(Object element) {
        int index = getIndex(element);
        Node current = buckets[index];

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    // Удаляем элемент из соответствующей цепочки
    @Override
    public boolean remove(Object element) {
        int index = getIndex(element);
        Node current = buckets[index];
        Node previous = null;

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }

                size--;
                return true;
            }

            previous = current;
            current = current.next;
        }

        return false;
    }

    // Формируем строку в стандартном формате коллекции
    @Override
    public String toString() {
        String result = "[";
        boolean first = true;

        for (Node bucket : buckets) {
            Node current = bucket;

            while (current != null) {
                if (!first) {
                    result += ", ";
                }

                result += current.data;
                first = false;
                current = current.next;
            }
        }

        return result + "]";
    }

    // Остальные методы интерфейса Set

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

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

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;

        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];
            Node previous = null;

            while (current != null) {
                if (collection.contains(current.data)) {
                    if (previous == null) {
                        buckets[i] = current.next;
                    } else {
                        previous.next = current.next;
                    }

                    size--;
                    changed = true;
                    current = previous == null ? buckets[i] : previous.next;
                } else {
                    previous = current;
                    current = current.next;
                }
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;

        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];
            Node previous = null;

            while (current != null) {
                if (!collection.contains(current.data)) {
                    if (previous == null) {
                        buckets[i] = current.next;
                    } else {
                        previous.next = current.next;
                    }

                    size--;
                    changed = true;
                    current = previous == null ? buckets[i] : previous.next;
                } else {
                    previous = current;
                    current = current.next;
                }
            }
        }

        return changed;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int bucketIndex = 0;
            private Node current = null;
            private Node nextNode = findNext();

            // Ищем следующий непустой узел в таблице
            private Node findNext() {
                if (current != null && current.next != null) {
                    return current.next;
                }

                while (bucketIndex < buckets.length) {
                    Node node = buckets[bucketIndex++];

                    if (node != null) {
                        return node;
                    }
                }

                return null;
            }

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if (nextNode == null) {
                    throw new java.util.NoSuchElementException();
                }

                current = nextNode;
                nextNode = findNext();

                return current.data;
            }

            @Override
            public void remove() {
                if (current == null) {
                    throw new IllegalStateException();
                }

                MyHashSet.this.remove(current.data);
                current = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;

        for (Node bucket : buckets) {
            Node current = bucket;

            while (current != null) {
                result[index++] = current.data;
                current = current.next;
            }
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

        int index = 0;

        for (Node bucket : buckets) {
            Node current = bucket;

            while (current != null) {
                array[index++] = (T) current.data;
                current = current.next;
            }
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
}
