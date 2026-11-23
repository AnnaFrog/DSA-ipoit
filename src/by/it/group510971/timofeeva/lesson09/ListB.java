package by.it.group510971.timofeeva.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListB<E> implements List<E> {


    //Создайте аналог списка БЕЗ использования других классов СТАНДАРТНОЙ БИБЛИОТЕКИ

    // Узел связного списка: значение и ссылка на следующий элемент
    private class Node {
        E data;
        Node next;

        Node(E data) {
            this.data = data;
        }
    }

    // Первый элемент списка
    private Node head;

    // Количество элементов
    private int size = 0;

    // Находим узел по индексу
    private Node getNode(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current;
    }

    // Проверяем индекс существующего элемента
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
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

        // Последовательно проходим по всем узлам
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

        // Если список пустой, новый узел становится первым
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;

            // Доходим до последнего узла
            while (current.next != null) {
                current = current.next;
            }

            current.next = newNode;
        }

        size++;
        return true;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);

        Node removed;

        // Если удаляем первый элемент, меняем голову списка
        if (index == 0) {
            removed = head;
            head = head.next;
        } else {
            Node previous = getNode(index - 1);
            removed = previous.next;

            // Связываем предыдущий узел со следующим
            previous.next = removed.next;
        }

        size--;
        return removed.data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        Node newNode = new Node(element);

        // Вставка в начало
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node previous = getNode(index - 1);

            // Вставляем новый узел между двумя существующими
            newNode.next = previous.next;
            previous.next = newNode;
        }

        size++;
    }

    @Override
    public boolean remove(Object o) {
        Node current = head;
        Node previous = null;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                if (previous == null) {
                    head = current.next;
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
        Node current = head;
        int index = 0;
        int result = -1;

        while (current != null) {
            if (o == null ? current.data == null : o.equals(current.data)) {
                result = index;
            }

            current = current.next;
            index++;
        }

        return result;
    }
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    //////               Опциональные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////


    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }


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
