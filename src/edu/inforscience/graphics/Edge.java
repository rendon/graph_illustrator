package edu.inforscience.graphics;

import java.awt.Color;

public class Edge {
    public static final int EDGE_TYPE_DIRECTED      = 1;
    public static final int EDGE_TYPE_UNDIRECTED    = 2;

    // Attributes
    private Integer start, end;
    private String label;    // label for edge
    private Color labelColor;
    private Color strokeColor;
    private float strokeSize;
    private Point2D labelCenter;
    private int type; // Directed or undirected
    boolean selected;

    //Constructors
    public Edge(Integer start, Integer end, String label)
    {
        setStart(start);
        setEnd(end);

        setLabel(label);
        labelColor = Color.BLACK;
        strokeColor = Color.BLACK;
        strokeSize = 1.0f;
        setType(EDGE_TYPE_DIRECTED);
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

    public float getStrokeSize()
    {
        return strokeSize;
    }

    public void setStrokeSize(float stroke)
    {
        strokeSize = stroke;
    }

    public Point2D getLabelCenter()
    {
        return labelCenter;
    }

    public void setLabelCenter(Point2D center)
    {
        labelCenter = center;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public void setSelected(boolean value)
    {
        selected = value;
    }

    public boolean isSelected()
    {
        return selected;
    }
}

