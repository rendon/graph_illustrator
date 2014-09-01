package edu.inforscience.graphics;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class Vertex {
    public static final double BASE_VERTEX_RADIUS = 1;

    // Attributes
    private Integer key;
    private Color labelColor;        // Vertex's label color
    private Color borderColor;      // Vertex's outline color
    private Color backgroundColor;  // Vertex's background color
    private String label;           // Label for vertex
    private int labelAlignment;
    private Point2D center;         // Position in the plane
    private double radius;
    private boolean labelChanged;
    private boolean selected;

    private HashMap<Integer, Edge> neighbors;


    // This variable indicates the control point direction of the curve
    // that goes from this vertex to another(-1 = down, 0 = straight, 1 = up).
    private int edgeDirection;

    public Vertex(String label)
    {
        this(label, null);
    }

    public Vertex(String label, Point2D pos)
    {
        setLabelColor(Color.BLACK);
        setBorderColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setLabel(label);
        setCenter(pos);
        setRadius(BASE_VERTEX_RADIUS);

        edgeDirection = 0;
        neighbors = new HashMap<Integer, Edge>();

        Random random = new Random();
        double[] signs = new double[]{-1, 1};
        int width = (int) GraphicsContext.DEFAULT_REAL_WIDTH / 2 - 10;
        int height = (int) GraphicsContext.DEFAULT_REAL_HEIGHT / 2 - 10;

        if (getCenter() == null) {
            int a = random.nextInt(width);
            int b = random.nextInt(height);
            double x = a * signs[random.nextInt(2)];
            double y = b * signs[random.nextInt(2)];
            setCenter(new Point2D(x, y));
        }
        setLabelAlignment(StyleConstants.ALIGN_LEFT);

        setLabelChanged(true);
        setSelected(false);
    }

    public void setKey(Integer key)
    {
        this.key = key;
    }

    public Integer getKey()
    {
        return key;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setEdgeDirection(int direction)
    {
        edgeDirection = direction;
    }

    public int getEdgeDirection()
    {
        return edgeDirection;
    }

    public Edge addNeighbor(Integer neighborKey, String label)
    {
        if (!neighbors.containsKey(neighborKey)) {
            Edge e = new Edge(getKey(), neighborKey, label);
            neighbors.put(neighborKey, e);
            return e;
        } else {
            return null;
        }
    }

    public void removeNeighbor(Integer neighborKey)
    {
        neighbors.remove(neighborKey);
    }

    public boolean contains(Integer k)
    {
        return neighbors.containsKey(k);
    }

    public HashMap<Integer, Edge> getNeighbors()
    {
        return neighbors;
    }

    public Point2D getCenter()
    {
        return center;
    }

    public void setCenter(Point2D center)
    {
        this.center = center;
    }

    public void setCenter(double x, double y)
    {
        center.setX(x);
        center.setY(y);
    }

    public double getRadius()
    {
        return radius;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public int getLabelAlignment()
    {
        return labelAlignment;
    }

    public void setLabelAlignment(int labelAlignment)
    {
        this.labelAlignment = labelAlignment;
    }

    public boolean hasLabelChanged()
    {
        return labelChanged;
    }

    public void setLabelChanged(boolean value)
    {
        labelChanged = value;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean value)
    {
        selected = value;
    }

    public void setLabelColor(Color color)
    {
        if (color != null) {
            labelColor = color;
        }
    }

    public Color getLabelColor()
    {
        return labelColor;
    }

    public void setBorderColor(Color color)
    {
        if (color != null) {
            borderColor = color;
        }
    }

    public Color getBorderColor()
    {
        return borderColor;
    }

    public void setBackgroundColor(Color color)
    {
        if (color != null) {
            backgroundColor = color;
        }
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }
}

