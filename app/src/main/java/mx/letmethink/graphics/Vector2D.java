package mx.letmethink.graphics;

/**
 * A 2D Vector class.
 */
public class Vector2D {

    private double xComponent;
    private double yComponent;

    public Vector2D()
    {
        setX(0);
        setY(0);
    }

    public Vector2D(double x, double y)
    {
        setX(x);
        setY(y);
    }

    public Vector2D(Point2D p)
    {
        setX(p.x());
        setY(p.y());
    }

    public void setX(double x)
    {
        xComponent = x;
    }

    public void setY(double y)
    {
        yComponent = y;
    }

    public double x()
    {
        return xComponent;
    }

    public double y()
    {
        return yComponent;
    }

    public Point2D point()
    {
        return new Point2D(x(), y());
    }

    // Operations with vectors
    public double length()
    {
        return Math.sqrt(x() * x() + y() * y());
    }

    public Vector2D unit()
    {
        return new Vector2D(x() / length(), y() / length());
    }

    public void sum(Vector2D u)
    {
        setX(x() + u.x());
        setY(y() + u.y());
    }

    public void sum(Point2D p)
    {
        setX(x() + p.x());
        setY(y() + p.y());
    }

    public void sub(Vector2D u)
    {
        setX(x() - u.x());
        setY(y() - u.y());
    }

    public void product(double factor)
    {
        setX(factor * x());
        setY(factor * y());
    }

    public void dotProduct(Vector2D u)
    {
        setX(x() * u.x());
        setY(y() * u.y());
    }

    public void div(double divisor)
    {
        setX(x() / divisor);
        setY(y() / divisor);
    }

// Static methods

    public static Vector2D sum(Vector2D u, Vector2D v)
    {
        return new Vector2D(u.x() + v.x(), u.y() + v.y());
    }

    public static Vector2D sub(Vector2D u, Vector2D v)
    {
        return new Vector2D(u.x() - v.x(), u.y() - v.y());
    }

    public static Vector2D div(Vector2D u, double denominator)
    {
        return new Vector2D(u.x() / denominator, u.y() / denominator);
    }

    public static Vector2D mult(Vector2D u, double factor)
    {
        return new Vector2D(u.x() * factor, u.y() * factor);
    }

    public static double dotProduct(Vector2D u, Vector2D v)
    {
        return u.x() * v.x() + u.y() * v.y();
    }


    /**
     * Actualy the result of cross product  is another vector, but also the
     * cross product allows to find the area of a triangle formed by the two
     * vectors and the oriantation of a set of points in the plane: positive
     * value means a counter-clockwise arrangement, a negative value means
     * clockwise arrangement.
     * This method returns the double of the area formed by two vectors.
     */
    public static double area2(Point2D p, Point2D q, Point2D r)
    {
        double a1 = q.x() - p.x();
        double a2 = q.y() - p.y();

        double b1 = r.x() - p.x();
        double b2 = r.y() - p.y();

        return a1 * b2 - a2 * b1;
    }

}
