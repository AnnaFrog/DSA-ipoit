
package by.it.group510971.timofeeva.lesson12;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyAvlMap implements Map<Integer, String> {

    // Узел АВЛ-дерева
    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        int height = 1;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size = 0;

    // Получаем высоту узла
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Обновляем высоту после изменения дерева
    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    // Вычисляем разницу высот поддеревьев
    private int balanceFactor(Node node) {
        return height(node.right) - height(node.left);
    }

    // Правый поворот дерева
    private Node rotateRight(Node node) {
        Node leftChild = node.left;
        node.left = leftChild.right;
        leftChild.right = node;

        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    // Левый поворот дерева
    private Node rotateLeft(Node node) {
        Node rightChild = node.right;
        node.right = rightChild.left;
        rightChild.left = node;

        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    // Восстанавливаем баланс узла
    private Node balance(Node node) {
        if (node == null) {
            return null;
        }

        updateHeight(node);

        if (balanceFactor(node) == 2) {
            if (balanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        if (balanceFactor(node) == -2) {
            if (balanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        return node;
    }

    // Вставляем новый узел в дерево
    private Node insert(Node node, Integer key, String value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        if (key < node.key) {
            node.left = insert(node.left, key, value);
        } else if (key > node.key) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        return balance(node);
    }

    // Ищем узел с минимальным ключом
    private Node findMin(Node node) {
        return node.left == null ? node : findMin(node.left);
    }

    // Удаляем минимальный узел
    private Node removeMin(Node node) {
        if (node.left == null) {
            return node.right;
        }

        node.left = removeMin(node.left);
        return balance(node);
    }

    // Удаляем узел по ключу
    private Node removeNode(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = removeNode(node.left, key);
        } else if (key > node.key) {
            node.right = removeNode(node.right, key);
        } else {
            Node left = node.left;
            Node right = node.right;

            size--;

            if (right == null) {
                return left;
            }

            Node min = findMin(right);
            min.right = removeMin(right);
            min.left = left;

            return balance(min);
        }

        return balance(node);
    }

    // Находим узел по ключу
    private Node findNode(Integer key) {
        if (key == null) {
            return null;
        }

        Node current = root;

        while (current != null) {
            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }

        Node existing = findNode(key);
        String oldValue = existing == null ? null : existing.value;

        root = insert(root, key, value);

        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        Integer intKey = (Integer) key;
        Node existing = findNode(intKey);

        if (existing == null) {
            return null;
        }

        String oldValue = existing.value;
        root = removeNode(root, intKey);

        return oldValue;
    }

    @Override
    public String get(Object key) {
        Node node = key instanceof Integer ? findNode((Integer) key) : null;
        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Integer && findNode((Integer) key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Полностью очищаем дерево
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Обход дерева по возрастанию ключей
    private void appendInOrder(Node node, StringBuilder result) {
        if (node == null) {
            return;
        }

        appendInOrder(node.left, result);

        if (result.length() > 1) {
            result.append(", ");
        }

        result.append(node.key).append("=").append(node.value);

        appendInOrder(node.right, result);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        appendInOrder(root, result);
        result.append("}");

        return result.toString();
    }

    // Остальные методы интерфейса Map

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, value);
    }

    private boolean containsValue(Node node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null ? node.value == null : value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value)
                || containsValue(node.right, value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> map) {
        for (Entry<? extends Integer, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
