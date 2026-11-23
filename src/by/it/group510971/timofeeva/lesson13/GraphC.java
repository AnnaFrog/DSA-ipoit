
package by.it.group510971.timofeeva.lesson13;

import java.util.*;

public class GraphC {

    // Первый DFS: запоминаем порядок завершения вершин
    private static void dfs1(
            char vertex,
            Map<Character, Set<Character>> graph,
            Set<Character> visited,
            List<Character> order) {

        visited.add(vertex);

        for (char next : graph.get(vertex)) {
            if (!visited.contains(next)) {
                dfs1(next, graph, visited, order);
            }
        }

        order.add(vertex);
    }

    // Второй DFS: собираем компоненту сильной связности
    private static void dfs2(
            char vertex,
            Map<Character, Set<Character>> reverseGraph,
            Set<Character> visited,
            List<Character> component) {

        visited.add(vertex);
        component.add(vertex);

        for (char next : reverseGraph.get(vertex)) {
            if (!visited.contains(next)) {
                dfs2(next, reverseGraph, visited, component);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<Character, Set<Character>> graph = new TreeMap<>();
        Map<Character, Set<Character>> reverseGraph = new TreeMap<>();

        // Создаем обычный и обратный графы
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("\\s*->\\s*");

            char from = vertices[0].trim().charAt(0);
            char to = vertices[1].trim().charAt(0);

            graph.putIfAbsent(from, new TreeSet<>());
            graph.putIfAbsent(to, new TreeSet<>());

            reverseGraph.putIfAbsent(from, new TreeSet<>());
            reverseGraph.putIfAbsent(to, new TreeSet<>());

            graph.get(from).add(to);
            reverseGraph.get(to).add(from);
        }

        Set<Character> visited = new HashSet<>();
        List<Character> order = new ArrayList<>();

        // Первый проход DFS по исходному графу
        for (char vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                dfs1(vertex, graph, visited, order);
            }
        }

        visited.clear();

        // Находим все компоненты сильной связности
        List<List<Character>> components = new ArrayList<>();

        for (int i = order.size() - 1; i >= 0; i--) {
            char vertex = order.get(i);

            if (!visited.contains(vertex)) {
                List<Character> component = new ArrayList<>();
                dfs2(vertex, reverseGraph, visited, component);

                // Сортируем вершины внутри компоненты
                Collections.sort(component);
                components.add(component);
            }
        }

        // Определяем номер компоненты для каждой вершины
        Map<Character, Integer> componentIndex = new HashMap<>();

        for (int i = 0; i < components.size(); i++) {
            for (char vertex : components.get(i)) {
                componentIndex.put(vertex, i);
            }
        }

        // Строим граф компонент сильной связности
        List<Set<Integer>> componentGraph = new ArrayList<>();
        int[] inDegree = new int[components.size()];

        for (int i = 0; i < components.size(); i++) {
            componentGraph.add(new HashSet<>());
        }

        for (char from : graph.keySet()) {
            for (char to : graph.get(from)) {
                int fromComponent = componentIndex.get(from);
                int toComponent = componentIndex.get(to);

                // Добавляем ребро только между разными компонентами
                if (fromComponent != toComponent
                        && componentGraph.get(fromComponent).add(toComponent)) {
                    inDegree[toComponent]++;
                }
            }
        }

        // Топологическая сортировка компонент
        PriorityQueue<Integer> queue = new PriorityQueue<>(
                (a, b) -> {
                    String first = componentToString(components.get(a));
                    String second = componentToString(components.get(b));
                    return first.compareTo(second);
                }
        );

        for (int i = 0; i < components.size(); i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        // Выводим компоненты: сначала истоки, затем стоки
        while (!queue.isEmpty()) {
            int current = queue.poll();

            System.out.println(componentToString(components.get(current)));

            for (int next : componentGraph.get(current)) {
                inDegree[next]--;

                if (inDegree[next] == 0) {
                    queue.add(next);
                }
            }
        }
    }

    // Преобразуем компоненту в строку без разделителей
    private static String componentToString(List<Character> component) {
        StringBuilder result = new StringBuilder();

        for (char vertex : component) {
            result.append(vertex);
        }

        return result.toString();
    }
}
