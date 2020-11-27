package mx.letmethink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import mx.letmethink.graph.Edge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;

public class EdgeTest {
    @Test
    @DisplayName("should set non-null colors")
    void setStrokeColor() {
        val edge = new Edge(5, 7, "edge");
        edge.setStrokeColor(Color.BLUE);
        assertEquals(Color.BLUE, edge.getStrokeColor());
    }


    @Test
    @DisplayName("should NOT set null colors")
    void setStrokeColor_nullColor() {
        val edge = new Edge(5, 7, "edge");
        edge.setStrokeColor(null);
        assertEquals(Color.BLACK, edge.getStrokeColor());
    }
}
