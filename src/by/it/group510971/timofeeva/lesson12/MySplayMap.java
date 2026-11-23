
package by.it.group510971.timofeeva.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MySplayMap implements NavigableMap<Integer, String> {

    // Узел splay-дерева
    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    // Левый поворот
    private void rotateLeft(Node node) {
        Node right = node.right;
        if (right == null) return;

        node.right = right.left;
        if (right.left != null) {
            right.left.parent = node;
        }

        right.parent = node.parent;

        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }

        right.left = node;
        node.parent = right;
    }

    // Правый поворот
    private void rotateRight(Node node) {
        Node left = node.left;
        if (left == null) return;

        node.left = left.right;
        if (left.right != null) {
            left.right.parent = node;
        }

        left.parent = node.parent;

        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.left) {
            node.parent.left = left;
        } else {
            node.parent.right = left;
        }

        left.right = node;
        node.parent = left;
    }

    // Перемещение узла в корень дерева
    private void splay(Node node) {
        if (node == null) return;

        while (node.parent != null) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (grandparent == null) {
                // Zig: один поворот
                if (node == parent.left) {
                    rotateRight(parent);
                } else {
                    rotateLeft(parent);
                }
            } else if (node == parent.left && parent == grandparent.left) {
                // Zig-Zig: два правых поворота
                rotateRight(grandparent);
                rotateRight(parent);
            } else if (node == parent.right && parent == grandparent.right) {
                // Zig-Zig: два левых поворота
                rotateLeft(grandparent);
                rotateLeft(parent);
            } else if (node == parent.right && parent == grandparent.left) {
                // Zig-Zag: левый, затем правый
                rotateLeft(parent);
                rotateRight(grandparent);
            } else {
                // Zig-Zag: правый, затем левый
                rotateRight(parent);
                rotateLeft(grandparent);
            }
        }
    }

    // Поиск узла. Последний посещенный узел тоже поднимается в корень,
    // если искомого ключа нет.
    private Node findNode(Integer key) {
        Node current = root;
        Node last = null;

        while (current != null) {
            last = current;

            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                splay(current);
                return current;
            }
        }

        if (last != null) {
            splay(last);
        }
        return null;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }

        if (root == null) {
            root = new Node(key, value, null);
            size++;
            return null;
        }

        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;

            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            }
        }

        Node newNode = new Node(key, value, parent);

        if (key < parent.key) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;
        splay(newNode);
        return null;
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
        if (!(key instanceof Integer)) {
            return false;
        }
        return findNode((Integer) key) != null;
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

        // Удаляем корень: его левое и правое поддеревья разделяются
        Node left = node.left;
        Node right = node.right;

        if (left != null) {
            left.parent = null;
        }
        if (right != null) {
            right.parent = null;
        }

        node.left = null;
        node.right = null;

        if (left == null) {
            root = right;
        } else {
            root = left;

            // Поднимаем максимальный узел левого поддерева
            Node max = left;
            while (max.right != null) {
                max = max.right;
            }

            splay(max);
            root.right = right;

            if (right != null) {
                right.parent = root;
            }
        }

        size--;
        return oldValue;
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

    // Обход дерева по возрастанию ключей
    private void appendInOrder(Node node, StringBuilder result) {
        if (node == null) return;

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

    // Поиск ближайшего ключа с учетом условия
    private Integer navigationKey(Integer key, int mode) {
        if (key == null) {
            throw new NullPointerException();
        }

        Node current = root;
        Node candidate = null;
        Node last = null;

        while (current != null) {
            last = current;

            if (mode == 0) {
                // lower: строго меньше
                if (current.key < key) {
                    candidate = current;
                    current = current.right;
                } else {
                    current = current.left;
                }
            } else if (mode == 1) {
                // floor: меньше или равно
                if (current.key <= key) {
                    candidate = current;
                    current = current.right;
                } else {
                    current = current.left;
                }
            } else if (mode == 2) {
                // ceiling: больше или равно
                if (current.key >= key) {
                    candidate = current;
                    current = current.left;
                } else {
                    current = current.right;
                }
            } else {
                // higher: строго больше
                if (current.key > key) {
                    candidate = current;
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
        }

        if (candidate != null) {
            Integer result = candidate.key;
            splay(candidate);
            return result;
        }

        if (last != null) {
            splay(last);
        }
        return null;
    }

    @Override
    public Integer lowerKey(Integer key) {
        return navigationKey(key, 0);
    }

    @Override
    public Integer floorKey(Integer key) {
        return navigationKey(key, 1);
    }

    @Override
    public Integer ceilingKey(Integer key) {
        return navigationKey(key, 2);
    }

    @Override
    public Integer higherKey(Integer key) {
        return navigationKey(key, 3);
    }

    // Копирование элементов с ключами меньше указанного
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException();
        }

        MySplayMap result = new MySplayMap();
        copyHeadMap(root, toKey, result);
        return result;
    }

    private void copyHeadMap(Node node, Integer toKey, MySplayMap result) {
        if (node == null) return;

        if (node.key < toKey) {
            copyHeadMap(node.left, toKey, result);
            result.put(node.key, node.value);
            copyHeadMap(node.right, toKey, result);
        } else {
            copyHeadMap(node.left, toKey, result);
        }
    }

    // Копирование элементов с ключами больше или равными указанному
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException();
        }

        MySplayMap result = new MySplayMap();
        copyTailMap(root, fromKey, result);
        return result;
    }

    private void copyTailMap(Node node, Integer fromKey, MySplayMap result) {
        if (node == null) return;

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

        Node current = root;
        while (current.left != null) {
            current = current.left;
        }

        Integer result = current.key;
        splay(current);
        return result;
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

        Integer result = current.key;
        splay(current);
        return result;
    }

    // Остальные методы интерфейса NavigableMap.
    // В обязательную часть задания они не входят.

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(
            Integer fromKey, boolean fromInclusive,
            Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> headMap(
            Integer toKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> tailMap(
            Integer fromKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> pollLastEntry() {
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
    public Set<Map.Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> map) {
        for (Map.Entry<? extends Integer, ? extends String> entry
                : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
