package mx.letmethink.graph;

import lombok.EqualsAndHashCode;
import mx.letmethink.graphics.GraphicsContext;
import mx.letmethink.graphics.Point2D;

import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

@EqualsAndHashCode
public class Vertex {
    public static final double BASE_VERTEX_RADIUS = 1;

    // Attributes
    private Integer key;
    private Color foregroundColor;  // Vertex's label color
    private Color borderColor;      // Vertex's outline color
    private Color backgroundColor;  // Vertex's background color
    private String label;           // Label for vertex
    private int labelAlignment;
    private Point2D center;         // Position in the plane
    private double radius;
    private boolean labelChanged;
    private boolean selected;

    private final HashMap<Integer, Edge> neighborMap;


    // This variable indicates the control point direction of the curve
    // that goes from this vertex to another(-1 = down, 0 = straight, 1 = up).
    private int edgeDirection;

    public static Vertex create(final String label) {
        return new Vertex(label, null);
    }
    private Vertex(String label, Point2D pos) {
        setForegroundColor(Color.BLACK);
        setBorderColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setLabel(label);
        setCenter(pos);
        setRadius(BASE_VERTEX_RADIUS);

        edgeDirection = 0;
        neighborMap = new HashMap<>();

        Random random = new Random();
        double[] signs = new double[]{-1, 1};
        int width = (int) GraphicsContext.DEFAULT_REAL_WIDTH / 2 - 10;
        int height = (int) GraphicsContext.DEFAULT_REAL_HEIGHT / 2 - 10;

        if (getCenter() == null) {
            int a = random.nextInt(width);
            int b = random.nextInt(height);
            double x = a * signs[random.nextInt(2)];
            double y = b * signs[random.nextInt(2)];
            setCenter(Point2D.of(x, y));
        }
        setLabelAlignment(StyleConstants.ALIGN_LEFT);

        setLabelChanged(true);
        setSelected(false);
    }

    public static Vertex from(String label, Point2D pos) {
        return new Vertex(label, pos);
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setEdgeDirection(int direction) {
        edgeDirection = direction;
    }

    public int getEdgeDirection() {
        return edgeDirection;
    }

    public void addNeighbor(Edge edge) {
        if (edge != null && edge.getEnd() != null) {
            neighborMap.put(edge.getEnd(), edge);
        }
    }

    public Edge addNeighbor(Integer neighborKey, String edgeLabel) {
        if (!neighborMap.containsKey(neighborKey)) {
            Edge e = new Edge(getKey(), neighborKey, edgeLabel);
            neighborMap.put(neighborKey, e);

            return e;
        } else {
            return null;
        }
    }

    public Edge getNeighbor(Integer key) {
        return neighborMap.get(key);
    }

    public boolean contains(Integer key) {
        return neighborMap.containsKey(key);
    }

    public Point2D getCenter() {
        return center;
    }

    public void setCenter(Point2D center) {
        this.center = center;
    }

    public void setCenter(double x, double y) {
        center.setX(x);
        center.setY(y);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getLabelAlignment() {
        return labelAlignment;
    }

    public void setLabelAlignment(int labelAlignment) {
        this.labelAlignment = labelAlignment;
    }

    public boolean hasLabelChanged() {
        return labelChanged;
    }

    public void setLabelChanged(boolean value) {
        labelChanged = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        selected = value;
    }

    public void setForegroundColor(Color color) {
        if (color != null) {
            foregroundColor = color;
        }
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setBorderColor(Color color) {
        if (color != null) {
            borderColor = color;
        }
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBackgroundColor(Color color) {
        if (color != null) {
            backgroundColor = color;
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Iterable<Edge> neighbors() {
        LinkedList<Edge> list = new LinkedList<>();
        for (Entry<Integer, Edge> entry : neighborMap.entrySet()) {
            list.add(entry.getValue());
        }

        return list;
    }

    public Edge removeNeighbor(Integer key) {
        return neighborMap.remove(key);
    }
}

