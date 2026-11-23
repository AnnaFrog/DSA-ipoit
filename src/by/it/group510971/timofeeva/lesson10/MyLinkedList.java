
package by.it.group510971.timofeeva.lesson10;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    // Узел двусвязного списка
    private class Node {
        E data;
        Node next;
        Node prev;

        Node(E data) {
            this.data = data;
        }
    }

    // Первый и последний узлы списка
    private Node head;
    private Node tail;

    // Количество элементов
    private int size = 0;

    // Добавляем узел в начало списка
    private void linkFirst(E element) {
        Node newNode = new Node(element);
        Node oldHead = head;

        newNode.next = oldHead;
        head = newNode;

        if (oldHead == null) {
            tail = newNode;
        } else {
            oldHead.prev = newNode;
        }

        size++;
    }

    // Добавляем узел в конец списка
    private void linkLast(E element) {
        Node newNode = new Node(element);
        Node oldTail = tail;

        newNode.prev = oldTail;
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        size++;
    }

    // Удаляем конкретный узел, обновляя связи соседей
    private E unlink(Node node) {
        E element = node.data;
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.data = null;
        size--;

        return element;
    }

    // Находим узел по индексу, начиная с ближайшего конца
    private Node getNode(int index) {
        checkIndex(index);

        if (index < size / 2) {
            Node current = head;

            for (int i = 0; i < index; i++) {
                current = current.next;
            }

            return current;
        } else {
            Node current = tail;

            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }

            return current;
        }
    }

    // Проверяем индекс существующего элемента
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    // Проверяем позицию для вставки
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public String toString() {
        String result = "[";
        Node current = head;

        while (current != null) {
            if (current != head) {
                result += ", ";
            }

            result += current.data;
            current = current.next;
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

    // Добавление в конец
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        linkFirst(element);
    }

    @Override
    public void addLast(E element) {
        linkLast(element);
    }

    // Удаление по индексу (дополнительный метод из задания)
    public E remove(int index) {
        return unlink(getNode(index));
    }

    // Удаление первого найденного элемента
    @Override
    public boolean remove(Object element) {
        return removeFirstOccurrence(element);
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (head == null) {
            throw new NoSuchElementException();
        }

        return head.data;
    }

    @Override
    public E getLast() {
        if (tail == null) {
            throw new NoSuchElementException();
        }

        return tail.data;
    }

    // Удаление первого элемента
    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (head == null) {
            return null;
        }

        return unlink(head);
    }

    @Override
    public E pollLast() {
        if (tail == null) {
            return null;
        }

        return unlink(tail);
    }

    // Остальные методы интерфейса Deque

    @Override
    public boolean offerFirst(E element) {
        addFirst(element);
        return true;
    }

    @Override
    public boolean offerLast(E element) {
        addLast(element);
        return true;
    }

    @Override
    public boolean offer(E element) {
        return offerLast(element);
    }

    @Override
    public E removeFirst() {
        if (head == null) {
            throw new NoSuchElementException();
        }

        return unlink(head);
    }

    @Override
    public E removeLast() {
        if (tail == null) {
            throw new NoSuchElementException();
        }

        return unlink(tail);
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
    public void push(E element) {
        addFirst(element);
    }

    @Override
    public E peekFirst() {
        return head == null ? null : head.data;
    }

    @Override
    public E peekLast() {
        return tail == null ? null : tail.data;
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        Node current = head;

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                unlink(current);
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object element) {
        Node current = tail;

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                unlink(current);
                return true;
            }

            current = current.prev;
        }

        return false;
    }

    @Override
    public boolean contains(Object element) {
        Node current = head;

        while (current != null) {
            if (element == null ? current.data == null : element.equals(current.data)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public void clear() {
        Node current = head;

        while (current != null) {
            Node next = current.next;
            current.data = null;
            current.next = null;
            current.prev = null;
            current = next;
        }

        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Object[] elements = collection.toArray();

        for (Object element : elements) {
            @SuppressWarnings("unchecked")
            E value = (E) element;
            addLast(value);
        }

        return elements.length > 0;
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

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (collection.contains(current.data)) {
                unlink(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (!collection.contains(current.data)) {
                unlink(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    // Обычный итератор: движение от начала к концу
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node current = head;
            private Node lastReturned = null;

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
                current = current.next;

                return lastReturned.data;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }

                unlink(lastReturned);
                lastReturned = null;
            }
        };
    }

    // Обратный итератор: движение от конца к началу
    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private Node current = tail;
            private Node lastReturned = null;

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
                current = current.prev;

                return lastReturned.data;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }

                unlink(lastReturned);
                lastReturned = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        Node current = head;

        for (int i = 0; i < size; i++) {
            result[i] = current.data;
            current = current.next;
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

        Node current = head;

        for (int i = 0; i < size; i++) {
            array[i] = (T) current.data;
            current = current.next;
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
}
