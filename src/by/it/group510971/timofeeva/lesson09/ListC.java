package by.it.group510971.timofeeva.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListC<E> implements List<E> {

    //Создайте аналог списка БЕЗ использования других классов СТАНДАРТНОЙ БИБЛИОТЕКИ

    // Узел двусвязного списка
    private class Node {
        E data;
        Node next;
        Node prev;

        Node(E data) {
            this.data = data;
        }
    }

    // Первый и последний элементы списка
    private Node head;
    private Node tail;

    // Количество элементов
    private int size = 0;

    // Находим узел, начиная с ближайшего конца списка
    private Node getNode(int index) {
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

    // Проверяем индекс позиции для вставки
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
    }

    // Удаляем узел, связывая его соседей между собой
    private E removeNode(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        size--;
        return node.data;
    }
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        String result = "[";
        Node current = head;

        while (current != null) {
            result += current.data;

            if (current.next != null) {
                result += ", ";
            }

            current = current.next;
        }

        return result + "]";
    }

    @Override
    public boolean add(E e) {
        Node newNode = new Node(e);

        // Новый элемент становится последним
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }

        size++;
        return true;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        return removeNode(getNode(index));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);

        // Вставка в конец
        if (index == size) {
            add(element);
            return;
        }

        Node nextNode = getNode(index);
        Node newNode = new Node(element);

        // Связываем новый узел с соседями
        newNode.next = nextNode;
        newNode.prev = nextNode.prev;

        if (nextNode.prev == null) {
            head = newNode;
        } else {
            nextNode.prev.next = newNode;
        }

        nextNode.prev = newNode;
        size++;
    }

    @Override
    public boolean remove(Object o) {
        Node current = head;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                removeNode(current);
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);

        Node node = getNode(index);
        E oldValue = node.data;

        node.data = element;
        return oldValue;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public int indexOf(Object o) {
        Node current = head;
        int index = 0;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                return index;
            }

            current = current.next;
            index++;
        }

        return -1;
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return getNode(index).data;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        Node current = tail;
        int index = size - 1;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                return index;
            }

            current = current.prev;
            index--;
        }

        return -1;
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
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;

        for (E element : c) {
            add(element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        boolean changed = false;

        for (E element : c) {
            add(index++, element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (c.contains(current.data)) {
                removeNode(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (!c.contains(current.data)) {
                removeNode(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    //////               Опциональные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    ////////        Эти методы имплементировать необязательно    ////////////
    ////////        но они будут нужны для корректной отладки    ////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    @Override
    public Iterator<E> iterator() {
        return null;
    }

}
