/*
    Copyright (C) 2013 Rafael Rendón Pablo <smart.rendon@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package edu.inforscience.graphics;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class Vertex {
    // Attributes
    private Color border;           // Vertex's outline color
    private Color background;       // Vertex's background color
    private String label;           // Label for vertex
    private int labelAlignment;
    private int id;
    private Point2D center;            // Position in the plane
    private double radius;
    private boolean labelChanged;

    private HashMap<Integer, Edge> neighbors;

    public static final double BASE_VERTEX_RADIUS = 5;

    // This variable indicates the control point direction of the curve
    // that goes from this vertex to another(-1 = down, 0 = straight, 1 = up).
    private int edgeDirection;

    public Vertex(int id, String text)
    {
        this(id, text, null);
    }

    public Vertex(int id, String text, Point2D pos)
    {
        this.id = id;
        setBorderColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setLabel(text);
        setCenter(pos);
        setRadius(BASE_VERTEX_RADIUS);

        edgeDirection = 0;
        neighbors = new HashMap<Integer, Edge>();

        Random random = new Random();
        double[] signs = new double[]{-1, 1};
        int width = (int) Plane.DEFAULT_REAL_WIDTH / 2 - 10;
        int height = (int) Plane.DEFAULT_REAL_HEIGHT / 2 - 10;

        if (getCenter() == null) {
            int a = random.nextInt(width);
            int b = random.nextInt(height);
            double x = a * signs[random.nextInt(2)];
            double y = b * signs[random.nextInt(2)];
            setCenter(new Point2D(x, y));
        }
        setLabelAlignment(StyleConstants.ALIGN_LEFT);

        setLabelChanged(true);
    }

    void setBorderColor(Color value)
    {
        border = value;
    }

    public Color getBorderColor()
    {
        return border;
    }

    public void setBackgroundColor(Color value)
    {
        background = value;
    }

    public Color getBackgroundColor()
    {
        return background;
    }

    void setLabel(String value)
    {
        label = value;
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

    public void addNeighbor(int neighbor, String label)
    {
        Edge e = new Edge(getId(), neighbor, label);
        if (!neighbors.containsKey(neighbor)) {
            neighbors.put(neighbor, e);
        }
    }

    public void removeNeighbor(int neighbor)
    {
        neighbors.remove(neighbor);
    }

    public boolean contains(int v)
    {
        return neighbors.containsKey(v);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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

    void setRadius(double radius)
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
}

