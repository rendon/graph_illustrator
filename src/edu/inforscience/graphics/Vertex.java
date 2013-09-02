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

import java.awt.*;
import java.util.Vector;

public class Vertex {
    // Attributes
    private Dimension dimensions;	// Dimensions of the shape

    private Color 	border;		  // Vertex's outline color
    private Color background;	  // Vertex's background color
    private String label;       // Label for vertex

    private Vector<Vertex> neighbors;

    // This variable indicates the control point direction of the curve
// that goes from this vertex to another(-1 = down, 0 = straight, 1 = up).
    private int edgeDirection;

    public Vertex(Dimension measures, String text)
    {
        setDimensions(measures);
        setBorderColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setLabel(text);

        edgeDirection = 0;
        neighbors = new Vector<Vertex>();
    }

    public Vertex(Dimension measures, String text, Color border, Color background)
    {
        setDimensions(measures);
        setBorderColor(border);
        setBackgroundColor(background);
        setLabel(text);

        edgeDirection = 0;
        neighbors = new Vector<Vertex>();
    }

    public Vertex(String nodeName, double x, double y)
    {
        setDimensions(new Dimension(x, y, 5));
        setBorderColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setLabel(nodeName);
        edgeDirection = 0;
        neighbors = new Vector<Vertex>();
    }


    // Accessor Methods
    public void setDimensions(Dimension dimension)
    {
        dimensions = dimension;
    }
    public Dimension getDimensions() { return dimensions; }

    public void  setBorderColor(Color value) { border = value; }
    public Color getBorderColor() { return border; }

    public void setBackgroundColor(Color value) { background = value; }
    public Color getBackgroundColor() { return background; }

    public void   setLabel(String value) { label = value; }
    public String getLabel() { return label; }


    public void setEdgeDirection(int direction) { edgeDirection = direction; }
    public int getEdgeDirection() { return edgeDirection; }


    public void addNeighbor(Vertex neighbor) { neighbors.add(neighbor); }
    public void removeNeighbor(Vertex neighbor) { neighbors.remove(neighbor); }
    public boolean contains(Vertex v) { return neighbors.contains(v); }

    public Point2D getVertexCenter()
    {
        return dimensions.getCenter();
    }

    public void setVertexCenter(double x, double y)
    {
        dimensions.setX(x);
        dimensions.setY(y);
    }

    public double getVertexRadius()
    {
        return dimensions.getRadius();
    }
}


