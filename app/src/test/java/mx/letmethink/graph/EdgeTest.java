package mx.letmethink.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import lombok.val;
import mx.letmethink.graphics.Point2D;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EdgeTest {
    private Edge makeEdge() {
        return Edge.create(5, 7, "edge");
    }

    @Test
    @DisplayName("should set and get stroke color")
    void strokeColor() {
        val edge = makeEdge();
        edge.setStrokeColor(null);
        assertEquals(Color.BLACK, edge.getStrokeColor());

        edge.setStrokeColor(Color.BLUE);
        assertEquals(Color.BLUE, edge.getStrokeColor());
    }

    @Test
    @DisplayName("should set and get backEdge flag")
    void backEdge() {
        val edge = makeEdge();
        edge.setIsBackEdge(true);
        assertTrue(edge.isBackEdge());

        edge.setIsBackEdge(false);
        assertFalse(edge.isBackEdge());
    }

    @Test
    @DisplayName("should return the start value")
    void start() {
        val edge = makeEdge();
        assertEquals(5, edge.getStart());
    }

    @Test
    @DisplayName("should return the end value")
    void end() {
        val edge = makeEdge();
        assertEquals(7, edge.getEnd());
    }

    @Test
    @DisplayName("should return the label")
    void label() {
        val edge = makeEdge();
        assertEquals("edge", edge.getLabel());
    }

    @Test
    @DisplayName("should set and get foreground")
    void foreground() {
        val edge = makeEdge();
        edge.setForegroundColor(null);
        assertNotNull(edge.getForegroundColor());

        edge.setForegroundColor(Color.RED);
        assertEquals(Color.RED, edge.getForegroundColor());
    }

    @Test
    @DisplayName("should set and get label center")
    void labelCenter() {
        val edge = makeEdge();
        edge.setLabelCenter(null);
        assertNotNull(edge.getLabelCenter());

        edge.setLabelCenter(Point2D.of(1.0, 1.0));
        assertEquals(Point2D.of(1.0, 1.0), edge.getLabelCenter());
    }

    @Test
    @DisplayName("should set and get directed flag")
    void directed() {
        val edge = makeEdge();
        assertTrue(edge.isDirected());

        edge.setIsDirected(false);
        assertFalse(edge.isDirected());
    }

    @Test
    @DisplayName("should set and get selected flag")
    void selected() {
        val edge = makeEdge();
        assertFalse(edge.isSelected());

        edge.setSelected(true);
        assertTrue(edge.isSelected());
    }


    @Test
    @DisplayName("should set and get highlighted flag")
    void highlighted() {
        val edge = makeEdge();
        assertFalse(edge.isHighlighted());

        edge.setHighlighted(true);
        assertTrue(edge.isHighlighted());
    }
}
