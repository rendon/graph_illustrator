package mx.letmethink.graph;

import mx.letmethink.InvalidOperationException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

;

public class Graph {
    private HashMap<Integer, Vertex> V;
    private HashMap<String, Integer> keys;
    private HashMap<String, Integer> labelKeys;
    private Integer nextKey;

    public Graph()
    {
        V = new HashMap<Integer, Vertex>();
        keys = new HashMap<String, Integer>();
        labelKeys = new HashMap<String, Integer>();
        nextKey = 1;
    }

    public void addDummyVertex(Vertex v)
    {
        v.setKey(0);
        V.put(0, v);
    }

    public void removeDummyVertex()
    {
        V.remove(0);
    }

    public void addVertex(Integer key, Vertex v)throws InvalidOperationException
    {
        String label = v.getLabel();
        if (keys.containsKey(label)) {
            String message = "A node with the same label already exists.";
            throw new InvalidOperationException(message);
        }

        if (V.containsKey(key)) {
            String message = "A node with the same key already exists.";
            throw new InvalidOperationException(message);
        }

        v.setKey(key);
        keys.put(label, key);
        V.put(key, v);
    }

    public Integer addVertex(Vertex v) throws InvalidOperationException
    {
        String label = v.getLabel();
        if (keys.containsKey(label)) {
            String message = "A node with the same label already exists.";
            throw new InvalidOperationException(message);
        }

        int key = nextKey++;
        v.setKey(key);
        keys.put(label, key);
        V.put(key, v);

        return v.getKey();
    }

    public Vertex getVertex(Integer key)
    {
        return V.get(key);
    }

    public void removeVertex(Integer key)
    {
        if (key == null || !V.containsKey(key)) {
            return;
        }

        // Disconnect the vertex to delete from the rest of the vertices.
        for (Vertex v : vertices()) {
            boolean test1 = v.getKey().equals(key);
            boolean test2 = v.contains(key);
            if (!test1 && test2) {
                v.removeNeighbor(key);
            }
        }

        String label = getVertex(key).getLabel();
        keys.remove(label);
        V.remove(key);
    }

    public boolean containsVertexWithLabel(String label)
    {
        return keys.containsKey(label);
    }

    public Vertex getVertexWithLabel(String label)
    {
        return V.get(keys.get(label));
    }

    public void setNextKey(Integer key)
    {
        nextKey = key;
    }

    public Iterable<Vertex> vertices()
    {
        LinkedList<Vertex> list = new LinkedList<Vertex>();
        for (Entry<Integer, Vertex> entry : V.entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    public Iterable<Edge> edges()
    {
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (Entry<Integer, Vertex> entryVertex : V.entrySet()) {
            Vertex v = entryVertex.getValue();
            for (Edge e : v.neighbors()) {
                list.add(e);
            }
        }

        return list;
    }

    public void updateLabelKey(String oldLabel, String newLabel, Integer key)
    {
        keys.remove(oldLabel);
        keys.put(newLabel, key);
    }
}
