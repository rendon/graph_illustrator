package mx.letmethink.graphics;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.awt.Point;

/**
 * A 2D Point class to use with logical coordinates.
 */
@EqualsAndHashCode
@ToString
public class Point2D {
    public static final Point2D ORIGIN = new Point2D(0, 0);
    private double px;
    private double py;

    private Point2D(Point p, GraphicsContext gc) {
        px = gc.fx((int) p.getX());
        py = gc.fy((int) p.getY());
    }

    public static Point2D from(Point p, GraphicsContext gc) {
        return new Point2D(p, gc);
    }

    private Point2D(double x, double y) {
        px = x;
        py = y;
    }

    public static Point2D of(double x, double y) {
        return new Point2D(x, y);
    }

    private Point2D(Point2D point) {
        px = point.getX();
        py = point.getY();
    }

    public static Point2D from(Point2D point) {
        return new Point2D(point);
    }

    public void setX(double x) {
        px = x;
    }

    public void setY(double y) {
        py = y;
    }

    public double getX() {
        return px;
    }

    public double getY() {
        return py;
    }

    /**
     * Computes distance between two points, this and point.
     *
     * @param point Point2D object
     * @return double, distance between this and point
     */
    public double distanceTo(Point2D point) {
        return Math.sqrt((px - point.getX()) * (px - point.getX()) +
                (py - point.getY()) * (py - point.getY()));
    }

    /**
     * Returns the distance from origin to this point
     */
    public double distance() {
        return Math.sqrt(getX() * getX() + getY() * getY());
    }
}

