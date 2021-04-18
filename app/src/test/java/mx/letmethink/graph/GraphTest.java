package mx.letmethink.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import mx.letmethink.InvalidOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GraphTest {
    @Test
    @DisplayName("should be able to add, get, and delete dummy vertices")
    void dummyVertex() {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        val key = graph.addDummyVertex(vertex);
        assertEquals(vertex, graph.getVertex(key));
        graph.removeDummyVertex();
        assertNull(graph.getVertex(key));
    }

    @Test
    @DisplayName("add vertex with with key")
    void addVertex_withKey() throws InvalidOperationException {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        val key = graph.addVertex(123, vertex);
        assertEquals(vertex, graph.getVertex(key));
    }

    @Test
    @DisplayName("add vertex with with key: should reject duplicate vertices")
    void addVertex_withKeyAndDuplicateVertex() throws InvalidOperationException {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        graph.addVertex(1, vertex);
        assertThrows(InvalidOperationException.class, () -> graph.addVertex(2, vertex));
    }

    @Test
    @DisplayName("add vertex with with key: should reject duplicate keys")
    void addVertex_withKeyAndDuplicateKey() throws InvalidOperationException {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        graph.addVertex(3, vertex);
        assertThrows(InvalidOperationException.class, () -> graph.addVertex(3, Vertex.create("b")));
    }

    @Test
    @DisplayName("should not accept vertices with duplicate labels/keys")
    void addVertex_duplicates() throws InvalidOperationException {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        graph.addVertex(vertex);
        assertThrows(InvalidOperationException.class, () -> graph.addVertex(vertex));
    }

    @Test
    @DisplayName("should be able to add, get, and delete vertices")
    void vertex() throws InvalidOperationException {
        val graph = Graph.create();
        val vertex = Vertex.create("abc");
        val key = graph.addVertex(vertex);
        assertEquals(vertex, graph.getVertex(key));
    }

    @Test
    @DisplayName("should remove isolated vertices")
    void removeVertex_isolatedVertices() throws InvalidOperationException {
        val graph = Graph.create();
        graph.removeVertex(null);
        graph.removeVertex(123);

        val key = graph.addVertex(Vertex.create("abc"));
        graph.removeVertex(key);
    }

    @Test
    @DisplayName("should remove connected vertices")
    void removeVertex_connectedVertices() throws InvalidOperationException {
        val graph = Graph.create();
        val a = Vertex.create("a");
        val b = Vertex.create("b");

        val key = graph.addVertex(a);
        graph.addVertex(b);

        b.addNeighbor(a.getKey(), "b -> a");
        graph.removeVertex(key);
    }

    @Test
    @DisplayName("vertex with label: should support look-ups and retrievals by label")
    void containsVertexWithLabel() throws InvalidOperationException {
       val graph = Graph.create();
        String label = "abc";
        assertFalse(graph.containsVertexWithLabel(label));

        val vertex = Vertex.create(label);
        graph.addVertex(vertex);
        assertTrue(graph.containsVertexWithLabel(label));
        assertEquals(vertex, graph.getVertexWithLabel(label));
    }

    @Test
    @DisplayName("set next key")
    void setNextKey() throws InvalidOperationException {
        val graph = Graph.create();
        int nextKey = 55;
        graph.setNextKey(nextKey);
        int key = graph.addVertex(Vertex.create("abc"));
        assertEquals(nextKey, key);
    }

    @Test
    @DisplayName("vertices iterator")
    void vertices() throws InvalidOperationException {
        val graph = Graph.create();
        graph.addVertex(Vertex.create("a"));
        graph.addVertex(Vertex.create("b"));
        val vertices = new ArrayList<Vertex>();
        for (val vertex : graph.getVertices()) {
            vertices.add(vertex);
        }
        assertEquals(2, vertices.size());
        assertEquals("a", vertices.get(0).getLabel());
        assertEquals("b", vertices.get(1).getLabel());
    }

    @Test
    @DisplayName("edges iterator")
    void edges() throws InvalidOperationException {
        val graph = Graph.create();
        val a = Vertex.create("a");
        val b = Vertex.create("b");
        graph.addVertex(a);
        graph.addVertex(b);
        a.addNeighbor(b.getKey(), b.getLabel());
        val edges = new ArrayList<Edge>();
        for (val edge : graph.getEdges()) {
            edges.add(edge);
        }
        assertEquals(1, edges.size());
        assertEquals(a.getKey(), edges.get(0).getStart());
        assertEquals(b.getKey(), edges.get(0).getEnd());
    }

    @Test
    @DisplayName("update label")
    void updateLabel() throws InvalidOperationException {
        val graph = Graph.create();
        val label = "abc";
        val vertex = Vertex.create(label);
        val key = graph.addVertex(vertex);
        assertEquals(vertex, graph.getVertexWithLabel(label));
        val newLabel = "xyz";
        graph.updateLabel(key, label, newLabel);
        assertEquals(newLabel, graph.getVertexWithLabel(newLabel).getLabel());
    }
}

