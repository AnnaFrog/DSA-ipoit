
package by.it.group510971.timofeeva.lesson13;

import java.util.*;

public class GraphB {

    // Поиск цикла с помощью DFS
    private static boolean hasCycle(
            String vertex,
            Map<String, List<String>> graph,
            Map<String, Integer> state) {

        // Помечаем вершину как находящуюся в обработке
        state.put(vertex, 1);

        for (String next : graph.get(vertex)) {
            int nextState = state.getOrDefault(next, 0);

            // Возврат в вершину, которая уже обрабатывается, означает цикл
            if (nextState == 1) {
                return true;
            }

            // Если вершина ещё не посещалась, запускаем DFS
            if (nextState == 0 && hasCycle(next, graph, state)) {
                return true;
            }
        }

        // Вершина полностью обработана
        state.put(vertex, 2);
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();

        // Создаём граф из входной строки
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("\\s*->\\s*");

            String from = vertices[0].trim();
            String to = vertices[1].trim();

            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());

            graph.get(from).add(to);
        }

        Map<String, Integer> state = new HashMap<>();
        boolean cycleFound = false;

        // Проверяем все вершины, включая несвязанные компоненты
        for (String vertex : graph.keySet()) {
            if (state.getOrDefault(vertex, 0) == 0) {
                if (hasCycle(vertex, graph, state)) {
                    cycleFound = true;
                    break;
                }
            }
        }

        System.out.println(cycleFound ? "yes" : "no");
    }
}
