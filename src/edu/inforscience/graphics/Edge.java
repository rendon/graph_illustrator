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

import java.awt.Color;

public class Edge {
    // Attributes
    private int start, end;
    private String label;    // label for edge
    private Color color;
    private float stroke;
    private Point2D labelPosition;

    //Constructors
    public Edge(int start, int end, String label)
    {
        setStart(start);
        setEnd(end);

        setLabel(label);
        setColor(Color.BLACK);
        stroke = 1.0f;
    }

    public int getStart()
    {
        return start;
    }

    void setStart(int start)
    {
        this.start = start;
    }

    public int getEnd()
    {
        return end;
    }

    void setEnd(int end)
    {
        this.end = end;
    }

    public String getLabel()
    {
        return label;
    }

    void setLabel(String label)
    {
        this.label = label;
    }

    public Color getColor()
    {
        return color;
    }

    void setColor(Color color)
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

    public Point2D getLabelPosition()
    {
        return labelPosition;
    }

    public void setLabelPosition(Point2D labelPosition)
    {
        this.labelPosition = labelPosition;
    }
}

