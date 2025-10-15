package ru.nsu.tokarev.Matrixs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface Graph<T> {

    /**
     * Добавляет новую вершину в граф.
     *
     * @param vertex Вершина для добавления.
     */
    void addVertex(T vertex);

    /**
     * Удаляет вершину и все связанные с ней ребра.
     *
     * @param vertex Вершина для удаления.
     */
    void removeVertex(T vertex);

    /**
     * Добавляет ориентированное ребро между двумя вершинами.
     *
     * @param from Исходная вершина.
     * @param to   Конечная вершина.
     */
    void addEdge(T from, T to);

    /**
     * Удаляет ребро между двумя вершинами.
     * 
     * @param from Исходная вершина.
     * @param to   Конечная вершина.
     */
    void removeEdge(T from, T to);

    /**
     * Возвращает список всех "соседей" (смежных вершин) для данной вершины.
     *
     * @param vertex Вершина для поиска соседей.
     * @return Список смежных вершин.
     */
    List<T> getNeighbors(T vertex);

    /**
     * Возвращает множество всех вершин графа.
     *
     * @return Множество вершин.
     */
    Set<T> getAllVertices();

    /**
     * Загружает граф из файла.
     * Формат файла: каждая строка "vertex1 vertex2" представляет ребро.
     *
     * @param filePath Путь к файлу.
     *
     * @throws IOException Если возникает ошибка чтения файла.
     */
    default void readFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    T from = (T) parts[0];
                    T to = (T) parts[1];
                    addEdge(from, to);
                }
            }
        }
    }

    /**
     * Проверяет, существует ли вершина в графе.
     *
     * @param vertex Вершина для проверки.
     *
     * @return true, если вершина существует, иначе false.
     */
    boolean hasVertex(T vertex);

    /**
     * Проверяет, существует ли ребро в графе.
     *
     * @param from Исходная вершина.
     * @param to Конечная вершина.
     *
     * @return true, если ребро существует, иначе false.
     */
    boolean hasEdge(T from, T to);

    /**
     * Сравнивает два графа независимо от их реализации.
     * Два графа считаются равными, если у них одинаковые множества вершин и ребер.
     *
     * @param obj Объект для сравнения.
     *
     * @return true, если графы равны, иначе false.
     */
    default boolean graphEquals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Graph)) return false;

        Graph other = (Graph) obj;

        if (!this.getAllVertices().equals(other.getAllVertices())) {
            return false;
        }

        for (Object vertex1 : this.getAllVertices()) {
            for (Object vertex2 : this.getAllVertices()) {
                boolean thisHasEdge = this.hasEdge((T) vertex1, (T) vertex2);
                boolean otherHasEdge = other.hasEdge(vertex1, vertex2);
                if (thisHasEdge != otherHasEdge) {
                    return false;
                }
            }
        }

        return true;
    }

    int hashCode();
}
