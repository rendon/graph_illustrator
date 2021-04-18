package mx.letmethink.graph;

import lombok.val;
import mx.letmethink.InvalidOperationException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class Graph {
    // Keys are non-negative integers. The first key is reserved for internal use.
    private static final int FIRST_KEY = 0;

    private final HashMap<Integer, Vertex> verticesByKey;
    private final HashMap<String, Integer> keys;
    private Integer nextKey;

    private Graph() {
        verticesByKey = new HashMap<>();
        keys = new HashMap<>();
        nextKey = 1;
    }

    public static Graph create() {
        return new Graph();
    }

    public Integer addDummyVertex(Vertex v) {
        v.setKey(FIRST_KEY);
        verticesByKey.put(FIRST_KEY, v);
        return FIRST_KEY;
    }

    public void removeDummyVertex() {
        verticesByKey.remove(FIRST_KEY);
    }

    public Integer addVertex(Integer key, Vertex vertex)throws InvalidOperationException
    {
        String label = vertex.getLabel();
        if (keys.containsKey(label)) {
            String message = "A node with the same label already exists.";
            throw new InvalidOperationException(message);
        }

        if (verticesByKey.containsKey(key)) {
            String message = "A node with the same key already exists.";
            throw new InvalidOperationException(message);
        }

        vertex.setKey(key);
        keys.put(label, key);
        verticesByKey.put(key, vertex);
        return key;
    }

    public Integer addVertex(Vertex vertex) throws InvalidOperationException
    {
        String label = vertex.getLabel();
        if (keys.containsKey(label)) {
            String message = "A node with the same label already exists.";
            throw new InvalidOperationException(message);
        }

        int key = nextKey++;
        vertex.setKey(key);
        keys.put(label, key);
        verticesByKey.put(key, vertex);

        return vertex.getKey();
    }

    public Vertex getVertex(Integer key) {
        return verticesByKey.get(key);
    }

    public void removeVertex(Integer key) {
        if (key == null || !verticesByKey.containsKey(key)) {
            return;
        }

        // Disconnect the vertex to delete from the rest of the vertices.
        for (Vertex v : getVertices()) {
            if (v.getKey().equals(key)) {
                continue;
            }
            if (v.contains(key)) {
                v.removeNeighbor(key);
            }
        }

        String label = getVertex(key).getLabel();
        keys.remove(label);
        verticesByKey.remove(key);
    }

    public boolean containsVertexWithLabel(String label) {
        return keys.containsKey(label);
    }

    public Vertex getVertexWithLabel(String label) {
        return verticesByKey.get(keys.get(label));
    }

    public void setNextKey(Integer key) {
        nextKey = key;
    }

    public Iterable<Vertex> getVertices() {
        LinkedList<Vertex> list = new LinkedList<>();
        for (Entry<Integer, Vertex> entry : verticesByKey.entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    public Iterable<Edge> getEdges() {
        LinkedList<Edge> list = new LinkedList<>();
        for (Entry<Integer, Vertex> entryVertex : verticesByKey.entrySet()) {
            Vertex v = entryVertex.getValue();
            for (Edge e : v.neighbors()) {
                list.add(e);
            }
        }

        return list;
    }

    public void updateLabel(Integer key, String currentLabel, String newLabel) {
        val vertex = verticesByKey.get(key);
        vertex.setLabel(newLabel);
        keys.remove(currentLabel);
        keys.put(vertex.getLabel(), key);
    }
}
