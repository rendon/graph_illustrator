package edu.inforscience.util;
import edu.inforscience.graphics.*;
import edu.inforscience.graph.*;
import java.awt.FontMetrics;

public class GeometryUtils {
    public static boolean onSegment(Point2D a, Point2D b,
                                    Point2D p, GraphicsContext gc)
    {
        if (a == null || b == null || p == null || gc == null) {
            return false;
        }

        double a1 = b.x() - a.x();
        double b1 = b.y() - a.y();
        double a2 = p.x() - a.x();
        double b2 = p.y() - a.y();

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

    public static boolean segInt(Point2D a, Point2D b, Point2D c, Point2D d)
    {
        if (a == null || b == null || c == null || d == null) {
            return false;
        }

        double test1 = ccw(a, b, c) * ccw(a, b, d);
        double test2 = ccw(c, d, a) * ccw(c, d, b);
        return test1 <= 0 && test2 <= 0;
    }

    public static Point2D intersection(Point2D a, Point2D b,
                                       Point2D c, Point2D d)
    {
        if (a == null || b == null || c == null || d == null) {
            return null;
        }

        double a1 = a.y() - b.y();
        double b1 = b.x() - a.x();
        double c1 = a1 * a.x() + b1 * a.y();

        double a2 = c.y() - d.y();
        double b2 = d.x() - c.x();
        double c2 = a2 * c.x() + b2 * c.y();

        double det = a1 * b2 - a2 * b1;
        if (det == 0) {
            return null;
        }

        double d1 = b2 * c1 - b1 * c2;
        double d2 = a1 * c2 - a2 * c1;

        return new Point2D(d1 / det, d2 / det);
    }

    public static double ccw(Point2D a, Point2D b, Point2D c)
    {
        if (a == null || b == null || c == null) {
            return Double.NaN;
        }

        double a1 = b.x() - a.x();
        double b1 = b.y() - a.y();
        double a2 = c.x() - a.x();
        double b2 = c.y() - a.y();

        return a1 * b2 - a2 * b1;
    }

    public static Point2D computeEndPoint(Vertex u, Vertex v,
                                          FontMetrics m, GraphicsContext gc)
    {
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
        double x = cu.x();
        double y = cu.y();
        Point2D a = new Point2D(x - width * 0.5, y + height * 0.5);
        Point2D b = new Point2D(x + width * 0.5, y + height * 0.5);
        Point2D c = new Point2D(x + width * 0.5, y - height * 0.5);
        Point2D d = new Point2D(x - width * 0.5, y - height * 0.5);

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

    public static Point2D getMiddlePoint(Point2D a, Point2D b)
    {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        return new Point2D(a.x() + 0.5 * dx, a.y() + 0.5 * dy);
    }
}
