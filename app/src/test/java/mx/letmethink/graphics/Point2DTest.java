package mx.letmethink.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;

public class Point2DTest {
    @Test
    @DisplayName("from Point2D")
    void from() {
        val point = Point2D.from(Point2D.of(1.0, 2.0));
        assertEquals(0, Double.compare(point.getX(), 1.0));
        assertEquals(0, Double.compare(point.getY(), 2.0));
    }

    @Test
    @DisplayName("from Point2D and graphics context")
    void from_pointAndGraphicsContext() {
        val point = Point2D.from(new Point(1, 2), GraphicsContext.create());
        assertEquals(0, Double.compare(point.getX(), 0.0));
        assertEquals(0, Double.compare(point.getY(), -0.0));
    }

   @Test
   @DisplayName("set and get x")
   void setAndGetX() {
       val point = Point2D.of(0, 0);
       assertEquals(0, point.getX());
       point.setX(1.5);
       assertEquals(0, Double.compare(1.5, point.getX()));
   }

    @Test
    @DisplayName("set and get y")
    void setAndGetY() {
        val point = Point2D.of(0, 1);
        assertEquals(1, point.getY());
        point.setY(2.5);
        assertEquals(0, Double.compare(2.5, point.getY()));
    }

    @Test
    @DisplayName("distanceTo")
    void distanceTo() {
        val a = Point2D.of(0, 0);
        val b = Point2D.of(0, 7);
        assertEquals(0, Double.compare(7.0, a.distanceTo(b)));
        assertEquals(0, Double.compare(7.0, a.distanceTo(b)));

        assertEquals(0, Double.compare(0.0, a.distanceTo(a)));
        assertEquals(0, Double.compare(0.0, b.distanceTo(b)));
    }


    @Test
    @DisplayName("distance")
    void distance() {
        assertEquals(0, Double.compare(0.0, Point2D.of(0, 0).distance()));
        assertEquals(0, Double.compare(Math.sqrt(25 + 25), Point2D.of(5, 5).distance()));
    }
}
