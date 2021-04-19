package mx.letmethink.util;

import mx.letmethink.graph.Vertex;
import mx.letmethink.graphics.GraphicsContext;
import mx.letmethink.graphics.Point2D;
import mx.letmethink.graphics.Vector2D;

import java.awt.FontMetrics;

public class GeometryUtils {
    public static boolean onSegment(Point2D a, Point2D b,
                                    Point2D p, GraphicsContext gc) {
        if (a == null || b == null || p == null || gc == null) {
            return false;
        }

        double a1 = b.getX() - a.getX();
        double b1 = b.getY() - a.getY();
        double a2 = p.getX() - a.getX();
        double b2 = p.getY() - a.getY();

        double alpha = Math.atan2(b1, a1);
        double beta = Math.atan2(b2, a2);
        double theta = Math.abs(alpha - beta);
        double dist = Math.abs(a.distanceTo(p) * Math.sin(theta));

        Vector2D A = new Vector2D(a1, b1);
        Vector2D B = new Vector2D(a2, b2);
        double dot = Vector2D.dotProduct(A, B);

        if (dot < 0)   { return false; }

        double ab2 = a1 * a1 + b1 * b1;
        if (dot > ab2) { return false; }

        double eps = Math.abs(gc.fx(3) - gc.fx(0));
        return dist < eps;
    }

    public static boolean segInt(Point2D a, Point2D b, Point2D c, Point2D d) {
        if (a == null || b == null || c == null || d == null) {
            return false;
        }

        double test1 = ccw(a, b, c) * ccw(a, b, d);
        double test2 = ccw(c, d, a) * ccw(c, d, b);
        return test1 <= 0 && test2 <= 0;
    }

    public static Point2D intersection(Point2D a, Point2D b,
                                       Point2D c, Point2D d) {
        if (a == null || b == null || c == null || d == null) {
            return null;
        }

        double a1 = a.getY() - b.getY();
        double b1 = b.getX() - a.getX();
        double c1 = a1 * a.getX() + b1 * a.getY();

        double a2 = c.getY() - d.getY();
        double b2 = d.getX() - c.getX();
        double c2 = a2 * c.getX() + b2 * c.getY();

        double det = a1 * b2 - a2 * b1;
        if (det == 0) {
            return null;
        }

        double d1 = b2 * c1 - b1 * c2;
        double d2 = a1 * c2 - a2 * c1;

        return Point2D.of(d1 / det, d2 / det);
    }

    public static double ccw(Point2D a, Point2D b, Point2D c) {
        if (a == null || b == null || c == null) {
            return Double.NaN;
        }

        double a1 = b.getX() - a.getX();
        double b1 = b.getY() - a.getY();
        double a2 = c.getX() - a.getX();
        double b2 = c.getY() - a.getY();

        return a1 * b2 - a2 * b1;
    }

    public static Point2D computeEndPoint(Vertex u, Vertex v,
                                          FontMetrics m,
                                          GraphicsContext gc) {
        if (u == null || v == null || m == null || gc == null) {
            return null;
        }

        Point2D cu = u.getCenter();
        Point2D cv = v.getCenter();
        int padding = 3 * m.getDescent();

        String label = StringUtils.align(u.getLabel(), u.getLabelAlignment());
        String[] lines = label.split("\n", -1);
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }

        int w = m.stringWidth(largest) + padding;
        int h = (m.getAscent() + m.getDescent()) * lines.length + padding;
        double width = gc.fx(w) - gc.fx(0);
        double height = gc.fy(h) - gc.fy(0);
        double x = cu.getX();
        double y = cu.getY();
        Point2D a = Point2D.of(x - width * 0.5, y + height * 0.5);
        Point2D b = Point2D.of(x + width * 0.5, y + height * 0.5);
        Point2D c = Point2D.of(x + width * 0.5, y - height * 0.5);
        Point2D d = Point2D.of(x - width * 0.5, y - height * 0.5);

        Point2D endPoint = cv;
        if (segInt(a, b, cu, cv)) {
            endPoint = intersection(a, b, cu, cv);
        } else if (segInt(b, c, cu, cv)) {
            endPoint = intersection(b, c, cu, cv);
        } else if (segInt(c, d, cu, cv)) {
            endPoint = intersection(c, d, cu, cv);
        } else if (segInt(d, a, cu, cv)) {
            endPoint = intersection(d, a, cu, cv);
        }

        return endPoint;
    }

    public static Point2D getMiddlePoint(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        return Point2D.of(a.getX() + 0.5 * dx, a.getY() + 0.5 * dy);
    }
}
