package edu.inforscience.graphics;

import java.awt.Color;

public class Edge {
    // Attributes
    private Integer start, end;
    private String label;    // label for edge
    private Color labelColor;
    private Color strokeColor;
    private float strokeSize;
    private Point2D labelCenter;

    //Constructors
    public Edge(Integer start, Integer end, String label)
    {
        setStart(start);
        setEnd(end);

        setLabel(label);
        labelColor = Color.BLACK;
        strokeColor = Color.BLACK;
        strokeSize = 1.0f;
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
}

