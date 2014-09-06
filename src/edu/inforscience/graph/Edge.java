package edu.inforscience.graph;

import java.awt.Color;

import edu.inforscience.graphics.*;
public class Edge {
    public static final int EDGE_TYPE_DIRECTED      = 1;
    public static final int EDGE_TYPE_UNDIRECTED    = 2;

    // Attributes
    private Integer start, end;
    private String label;    // label for edge
    private Color foregroundColor;
    private Color strokeColor;
    private Point2D labelCenter;
    private boolean directed;
    private boolean selected;
    private boolean highlighted;
    private boolean isBackEdge;

    //Constructors
    public Edge(Integer start, Integer end, String label)
    {
        setStart(start);
        setEnd(end);

        setLabel(label);
        foregroundColor = Color.BLACK;
        strokeColor = Color.BLACK;
        directed = true;
        isBackEdge = false;
    }

    public boolean isBackEdge()
    {
        return isBackEdge;
    }

    public void setIsBackEdge(boolean value)
    {
        isBackEdge = value;
    }

    public Integer getStart()
    {
        return start;
    }

    public void setStart(Integer start)
    {
        this.start = start;
    }

    public Integer getEnd()
    {
        return end;
    }

    public void setEnd(Integer end)
    {
        this.end = end;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setForegroundColor(Color color)
    {
        if (color != null) {
            foregroundColor = color;
        }
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public void setStrokeColor(Color color)
    {
        if (color != null) {
            strokeColor = color;
        }
    }

    public Color getStrokeColor()
    {
        return strokeColor;
    }

    public Point2D getLabelCenter()
    {
        return labelCenter;
    }

    public void setLabelCenter(Point2D center)
    {
        labelCenter = center;
    }

    public void setIsDirected(boolean value)
    {
        directed = value;
    }

    public boolean isDirected()
    {
        return directed;
    }

    public void setSelected(boolean value)
    {
        selected = value;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setHighlighted(boolean value)
    {
        highlighted = value;
    }

    public boolean isHighlighted()
    {
        return highlighted;
    }
}

