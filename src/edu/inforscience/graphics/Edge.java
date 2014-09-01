package edu.inforscience.graphics;

import java.awt.Color;

public class Edge {
    // Attributes
    private Integer start, end;
    private String label;    // label for edge
    private Color color;
    private float stroke;
    private Point2D labelCenter;

    //Constructors
    public Edge(Integer start, Integer end, String label)
    {
        setStart(start);
        setEnd(end);

        setLabel(label);
        setColor(Color.BLACK);
        stroke = 1.0f;
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

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public float getStroke()
    {
        return stroke;
    }

    public void setStroke(float stroke)
    {
        this.stroke = stroke;
    }

    public Point2D getLabelCenter()
    {
        return labelCenter;
    }

    public void setLabelCenter(Point2D labelCenter)
    {
        this.labelCenter = labelCenter;
    }
}

