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

public class Dimension {
  private double x;		  // X coordinate x for the shape
  private double y;		  // Y coordinate x for the shape
  private double radius; // Shape radius

  public Dimension(double x, double y, double radius)
  {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  public double getX()
  {
    return x;
  }

  public void setX(double x)
  {
    this.x = x;
  }

  public double getRadius()
  {
    return radius;
  }

  public void setRadius(double radius)
  {
    this.radius = radius;
  }

  public double getY()
  {
    return y;
  }

  public void setY(double y)
  {
    this.y = y;
  }

  public Point2D getCenter()
  {
    return new Point2D(getX(), getY());
  }
}

