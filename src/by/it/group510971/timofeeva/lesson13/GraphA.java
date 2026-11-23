
package by.it.group510971.timofeeva.lesson13;

import java.util.*;

public class GraphA {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Список смежности: от каждой вершины храним исходящие ребра
        Map<String, List<String>> graph = new HashMap<>();

        // Количество входящих ребер для каждой вершины
        Map<String, Integer> inDegree = new HashMap<>();

        // Разбираем входную строку
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("\\s*->\\s*");

            String from = vertices[0].trim();
            String to = vertices[1].trim();

            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());

            graph.get(from).add(to);

            inDegree.putIfAbsent(from, 0);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }

        // В очередь добавляем вершины без входящих ребер.
        // PriorityQueue обеспечивает лексикографический порядок.
        PriorityQueue<String> queue = new PriorityQueue<>();

        for (String vertex : graph.keySet()) {
            if (inDegree.getOrDefault(vertex, 0) == 0) {
                queue.add(vertex);
            }
        }

        List<String> result = new ArrayList<>();

        // Алгоритм Кана
        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            result.add(vertex);

            for (String next : graph.get(vertex)) {
                int degree = inDegree.get(next) - 1;
                inDegree.put(next, degree);

                if (degree == 0) {
                    queue.add(next);
                }
            }
        }

        // Выводим топологическую сортировку
        System.out.println(String.join(" ", result));
    }
}
