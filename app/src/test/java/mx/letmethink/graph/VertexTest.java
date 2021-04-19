package mx.letmethink.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import lombok.val;
import mx.letmethink.graphics.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests {@link Vertex}.
 */
public class VertexTest {
    Vertex vertex;

    @BeforeEach
    void setUp() {
        vertex = Vertex.create("vertex");
    }

    @Test
    @DisplayName("setKey() should set values correctly")
    void setKey() {
        vertex.setKey(7);
        assertEquals(7, vertex.getKey());
    }

    @Test
    @DisplayName("setLabel() should set values correctly")
    void setLabel() {
        assertEquals("vertex", vertex.getLabel());
        vertex.setLabel("label");
        assertEquals("label", vertex.getLabel());
    }

    @Test
    @DisplayName("setEdgeDirection() should set values correctly")
    void setEdgeDirection() {
        assertEquals(0, vertex.getEdgeDirection());
        vertex.setEdgeDirection(1);
        assertEquals(1, vertex.getEdgeDirection());
    }

    @Test
    @DisplayName("addNeighbor() should add valid neighbors")
    void addNeighbor() {
        vertex.addNeighbor(Edge.create(1, 2, "-->"));
        assertTrue(vertex.contains(2));
    }

    @Test
    @DisplayName("addNeighbor() should NOT add null edges")
    void addNeighbor_nullEdge() {
        val vertex = Vertex.create("vertex");
        vertex.addNeighbor(null);
        assertFalse(vertex.contains(2));
    }

    @Test
    @DisplayName("addNeighbor() should NOT add edges without an end")
    void addNeighbor_edgesWithoutEnd() {
        val vertex = Vertex.create("vertex");
        vertex.addNeighbor(Edge.create(1, null, ""));
        assertFalse(vertex.contains(2));
    }

    @Test
    @DisplayName("addNeighbor() should return the edge to the new neighbor")
    void addNeighbor_newNeighbor() {
        val edge = vertex.addNeighbor(7, "seven");
        assertEquals(7, edge.getEnd());
        assertEquals("seven", edge.getLabel());
    }

    @Test
    @DisplayName("addNeighbor() should return null if the specified vertex is already a neighbor")
    void addNeighbor_attemptToAddExistingNeighbor() {
        vertex.addNeighbor(7, "seven");
        assertNull(vertex.addNeighbor(7, "label"));
    }

    @Test
    @DisplayName("getNeighbor() should return null when the requested key does not exist")
    void getNeighbor_nonExistent() {
        assertNull(vertex.getNeighbor(3));
    }

    @Test
    @DisplayName("getNeighbor() should return existing neighbor")
    void getNeighbor_existingNeighbor() {
        vertex.addNeighbor(3, "three");
        val edge = vertex.getNeighbor(3);
        assertEquals(3, edge.getEnd());
        assertEquals("three", edge.getLabel());
    }

    @Test
    @DisplayName("setCenter() should accept a point")
    void setCenter_fromPoint() {
        vertex.setCenter(Point2D.of(1, 2));
        assertEquals(1, vertex.getCenter().getX());
        assertEquals(2, vertex.getCenter().getY());
    }


    @Test
    @DisplayName("setCenter() should accept coordinates")
    void setCenter_fromTwoValues() {
        vertex.setCenter(1, 2);
        assertEquals(1, vertex.getCenter().getX());
        assertEquals(2, vertex.getCenter().getY());
    }

    @Test
    @DisplayName("setRadius() should set values correctly")
    void setRadius() {
        vertex.setRadius(5);
        assertEquals(5, vertex.getRadius());
    }

    @Test
    @DisplayName("setLabelAssignment() should set values correctly")
    void setLabelAlignment() {
        assertEquals(0, vertex.getLabelAlignment());
        vertex.setLabelAlignment(1);
        assertEquals(1, vertex.getLabelAlignment());
    }

    @Test
    @DisplayName("setLabelChanged() should set values correctly")
    void setLabelChanged() {
        vertex.setLabelChanged(false);
        assertFalse(vertex.hasLabelChanged());

        vertex.setLabelChanged(true);
        assertTrue(vertex.hasLabelChanged());
    }

    @Test
    @DisplayName("setSelected() should set values correctly")
    void setSelected() {
        vertex.setSelected(false);
        assertFalse(vertex.isSelected());

        vertex.setSelected(true);
        assertTrue(vertex.isSelected());
    }

    @Test
    @DisplayName("setForegroundColor() should not assign null colors")
    void setForegroundColor_nullColor() {
        assertNotNull(vertex.getForegroundColor());
        vertex.setForegroundColor(null);
        assertNotNull(vertex.getForegroundColor());
    }

    @Test
    @DisplayName("setForegroundColor() should assign non-null colors")
    void setForegroundColor_nonNullColor() {
        assertEquals(Color.BLACK, vertex.getForegroundColor());
        vertex.setForegroundColor(Color.BLUE);
        assertEquals(Color.BLUE, vertex.getForegroundColor());
    }

    @Test
    @DisplayName("setBorderColor() should not assign null colors")
    void setBorderColor_nullColor() {
        assertNotNull(vertex.getBorderColor());
        vertex.setBorderColor(null);
        assertNotNull(vertex.getBorderColor());
    }

    @Test
    @DisplayName("setBorderColor() should assign non-null colors")
    void setBorderColor_nonNullColor() {
        assertEquals(Color.BLACK, vertex.getBorderColor());
        vertex.setBorderColor(Color.BLUE);
        assertEquals(Color.BLUE, vertex.getBorderColor());
    }

    @Test
    @DisplayName("setBackgroundColor() should not assign null colors")
    void setBackgroundColor_nullColor() {
        assertNotNull(vertex.getBackgroundColor());
        vertex.setBackgroundColor(null);
        assertNotNull(vertex.getBackgroundColor());
    }

    @Test
    @DisplayName("setBackgroundColor() should assign non-null colors")
    void setBackgroundColor_nonNullColor() {
        assertEquals(Color.WHITE, vertex.getBackgroundColor());
        vertex.setBackgroundColor(Color.BLACK);
        assertEquals(Color.BLACK, vertex.getBackgroundColor());
    }

    @Test
    @DisplayName("removeNeighbor() should return null if neighbor does not exist")
    void removeNeighbor_nonExistentNeighbor() {
        assertNull(vertex.removeNeighbor(3));
    }

    @Test
    @DisplayName("removeNeighbor() should remove existing neighbor")
    void removeNeighbor() {
        vertex.addNeighbor(2, "two");
        val edge = vertex.removeNeighbor(2);
        assertEquals(2, edge.getEnd());
        assertEquals("two", edge.getLabel());
    }

    @Test
    @DisplayName("neighbors() should return iterator with all the neighbors")
    void neighbors() {
        vertex.addNeighbor(1, "one");
        vertex.addNeighbor(2, "two");
        vertex.addNeighbor(3, "three");

        val neighbors = new ArrayList<Integer>();
        for (val n : vertex.neighbors()) {
            neighbors.add(n.getEnd());
        }
        assertTrue(neighbors.contains(1));
        assertTrue(neighbors.contains(2));
        assertTrue(neighbors.contains(3));
    }
}
