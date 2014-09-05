package edu.inforscience.graph;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.LinkedList;;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.inforscience.graphics.*;

public class Vertex {
    public static final double BASE_VERTEX_RADIUS = 1;

    // Attributes
    private Integer key;
    private Color labelColor;       // Vertex's label color
    private Color borderColor;      // Vertex's outline color
    private Color backgroundColor;  // Vertex's background color
    private String label;           // Label for vertex
    private int labelAlignment;
    private Point2D center;         // Position in the plane
    private double radius;
    private boolean labelChanged;
    private boolean selected;

    private HashMap<Integer, Edge> neighborMap;


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
        neighborMap = new HashMap<Integer, Edge>();

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

    public void addNeighbor(Edge edge)
    {
        if (edge != null && edge.getEnd() != null) {
            neighborMap.put(edge.getEnd(), edge);
        }
    }

    public Edge addNeighbor(Integer neighborKey, String label)
    {
        if (!neighborMap.containsKey(neighborKey)) {
            Edge e = new Edge(getKey(), neighborKey, label);
            neighborMap.put(neighborKey, e);
            return e;
        } else {
            return null;
        }
    }

    public Edge getNeighbor(Integer key)
    {
        return neighborMap.get(key);
    }

    public boolean contains(Integer k)
    {
        return neighborMap.containsKey(k);
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

    public Iterable<Edge> neighbors()
    {
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (Entry<Integer, Edge> entry : neighborMap.entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    public Edge removeNeighbor(Integer key)
    {
        return neighborMap.remove(key);
    }
}

