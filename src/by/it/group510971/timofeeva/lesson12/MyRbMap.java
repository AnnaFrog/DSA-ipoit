
package by.it.group510971.timofeeva.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {

    // Цвета узлов дерева
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Узел красно-черного дерева
    private static class Node {
        Integer key;
        String value;
        boolean color;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    private boolean colorOf(Node node) {
        return node == null ? BLACK : node.color;
    }

    private void setColor(Node node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    private Node findNode(Integer key) {
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

    // Левый поворот вокруг узла
    private void rotateLeft(Node node) {
        Node rightChild = node.right;
        node.right = rightChild.left;

        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.parent = node.parent;

        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        rightChild.left = node;
        node.parent = rightChild;
    }

    // Правый поворот вокруг узла
    private void rotateRight(Node node) {
        Node leftChild = node.left;
        node.left = leftChild.right;

        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.parent = node.parent;

        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }

        leftChild.right = node;
        node.parent = leftChild;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }

        Node parent = null;
        Node current = root;

        // Ищем место для нового узла
        while (current != null) {
            parent = current;

            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        Node newNode = new Node(key, value, RED, parent);

        if (parent == null) {
            root = newNode;
        } else if (key < parent.key) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;
        fixAfterInsertion(newNode);
        return null;
    }

    // Восстановление свойств красно-черного дерева после добавления
    private void fixAfterInsertion(Node node) {
        node.color = RED;

        while (node != root && colorOf(node.parent) == RED) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;

                if (colorOf(uncle) == RED) {
                    setColor(node.parent, BLACK);
                    setColor(uncle, BLACK);
                    setColor(node.parent.parent, RED);
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }

                    setColor(node.parent, BLACK);
                    setColor(node.parent.parent, RED);
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;

                if (colorOf(uncle) == RED) {
                    setColor(node.parent, BLACK);
                    setColor(uncle, BLACK);
                    setColor(node.parent.parent, RED);
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }

                    setColor(node.parent, BLACK);
                    setColor(node.parent.parent, RED);
                    rotateLeft(node.parent.parent);
                }
            }
        }

        setColor(root, BLACK);
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        Node node = findNode((Integer) key);
        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Integer && findNode((Integer) key) != null;
    }

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
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        Node node = findNode((Integer) key);

        if (node == null) {
            return null;
        }

        String oldValue = node.value;
        deleteNode(node);
        return oldValue;
    }

    // Минимальный узел поддерева
    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Замена одного узла другим в дереве
    private void transplant(Node oldNode, Node newNode) {
        if (oldNode.parent == null) {
            root = newNode;
        } else if (oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }

        if (newNode != null) {
            newNode.parent = oldNode.parent;
        }
    }

    // Удаление узла
    private void deleteNode(Node node) {
        Node movedNode = node;
        boolean originalColor = movedNode.color;
        Node replacement;
        Node replacementParent;

        if (node.left == null) {
            replacement = node.right;
            replacementParent = node.parent;
            transplant(node, node.right);
        } else if (node.right == null) {
            replacement = node.left;
            replacementParent = node.parent;
            transplant(node, node.left);
        } else {
            movedNode = minimum(node.right);
            originalColor = movedNode.color;
            replacement = movedNode.right;

            if (movedNode.parent == node) {
                replacementParent = movedNode;
                if (replacement != null) {
                    replacement.parent = movedNode;
                }
            } else {
                replacementParent = movedNode.parent;
                transplant(movedNode, movedNode.right);
                movedNode.right = node.right;
                movedNode.right.parent = movedNode;
            }

            transplant(node, movedNode);
            movedNode.left = node.left;
            movedNode.left.parent = movedNode;
            movedNode.color = node.color;
        }

        size--;

        if (originalColor == BLACK) {
            fixAfterDeletion(replacement, replacementParent);
        }
    }

    // Восстановление свойств дерева после удаления
    private void fixAfterDeletion(Node node, Node parent) {
        while (node != root && colorOf(node) == BLACK) {
            if (parent == null) {
                break;
            }

            if (node == parent.left) {
                Node sibling = parent.right;

                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(parent, RED);
                    rotateLeft(parent);
                    sibling = parent.right;
                }

                if (sibling == null) {
                    node = parent;
                    parent = node.parent;
                } else if (colorOf(sibling.left) == BLACK
                        && colorOf(sibling.right) == BLACK) {
                    setColor(sibling, RED);
                    node = parent;
                    parent = node.parent;
                } else {
                    if (colorOf(sibling.right) == BLACK) {
                        setColor(sibling.left, BLACK);
                        setColor(sibling, RED);
                        rotateRight(sibling);
                        sibling = parent.right;
                    }

                    setColor(sibling, colorOf(parent));
                    setColor(parent, BLACK);
                    setColor(sibling.right, BLACK);
                    rotateLeft(parent);
                    node = root;
                    parent = null;
                }
            } else {
                Node sibling = parent.left;

                if (colorOf(sibling) == RED) {
                    setColor(sibling, BLACK);
                    setColor(parent, RED);
                    rotateRight(parent);
                    sibling = parent.left;
                }

                if (sibling == null) {
                    node = parent;
                    parent = node.parent;
                } else if (colorOf(sibling.right) == BLACK
                        && colorOf(sibling.left) == BLACK) {
                    setColor(sibling, RED);
                    node = parent;
                    parent = node.parent;
                } else {
                    if (colorOf(sibling.left) == BLACK) {
                        setColor(sibling.right, BLACK);
                        setColor(sibling, RED);
                        rotateLeft(sibling);
                        sibling = parent.left;
                    }

                    setColor(sibling, colorOf(parent));
                    setColor(parent, BLACK);
                    setColor(sibling.left, BLACK);
                    rotateRight(parent);
                    node = root;
                    parent = null;
                }
            }
        }

        setColor(node, BLACK);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Обход дерева слева направо дает ключи по возрастанию
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

    // Все ключи строго меньше указанного
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException();
        }

        MyRbMap result = new MyRbMap();
        copyHeadMap(root, toKey, result);
        return result;
    }

    private void copyHeadMap(Node node, Integer toKey, MyRbMap result) {
        if (node == null) {
            return;
        }

        if (node.key < toKey) {
            copyHeadMap(node.left, toKey, result);
            result.put(node.key, node.value);
            copyHeadMap(node.right, toKey, result);
        } else {
            copyHeadMap(node.left, toKey, result);
        }
    }

    // Все ключи больше или равны указанному
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException();
        }

        MyRbMap result = new MyRbMap();
        copyTailMap(root, fromKey, result);
        return result;
    }

    private void copyTailMap(Node node, Integer fromKey, MyRbMap result) {
        if (node == null) {
            return;
        }

        if (node.key >= fromKey) {
            copyTailMap(node.left, fromKey, result);
            result.put(node.key, node.value);
            copyTailMap(node.right, fromKey, result);
        } else {
            copyTailMap(node.right, fromKey, result);
        }
    }

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        return minimum(root).key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }

        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.key;
    }

    // Остальные методы интерфейса SortedMap / Map
    @Override
    public Comparator<? super Integer> comparator() {
        return null; // Используется естественный порядок Integer
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
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

    @Override
    public void putAll(Map<? extends Integer, ? extends String> map) {
        for (Entry<? extends Integer, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
