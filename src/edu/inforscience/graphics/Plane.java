/*
Copyright (C) 2013 Rafael Rendón Pablo <rafaelrendonpablo@gmail.com>

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.MathContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Map.Entry;

import edu.inforscience.util.Editor;
import edu.inforscience.util.StringUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D;

public class Plane extends JPanel implements MouseListener,
        MouseWheelListener,
        MouseMotionListener {
    private HashMap<Integer, Vertex> graph;
    private HashMap<String, Integer> ids;
    private Vertex startVertex;
    private int nodeId;
    private int maxX;
    private int maxY;
    private int centerX;
    private int centerY;
    private int factorIndexX;
    private int factorIndexY;

    private double pixelWidth;
    private double pixelHeight;
    private double gridIntervalX;
    private double gridIntervalY;
    private final double[] factors;

    public static final double DEFAULT_REAL_WIDTH   = 100;
    public static final double DEFAULT_REAL_HEIGHT  = 100;
    public static final int DEFAULT_OPERATION   = 1;
    public static final int DRAW_NEW_VERTEX     = 2;
    public static final int DRAW_NEW_EDGE       = 3;
    public static final int ERASE_OBJECT        = 4;
    public static final int SHAPE_CIRCLE        = 5;
    public static final int SHAPE_RECTANGLE     = 6;
    public static final int SHAPE_NONE          = 7;
    public static final int RECTANGLE_PADDING   = 3;
    private double realWidth;
    private double realHeight;
    private double scaleInX;
    private double scaleInY;

    private int currentOperation;

    private boolean firstTime;
    private boolean showAxis;
    private boolean showGrid;
    private boolean dragPlane;
    private boolean dragVertex;

    private boolean directed;

    private Point startDrag;
    private int vertexToDragIndex;

    private int fontSize;
    private int shapeType;

    private Graphics2D graphics2D;
    private final PrintStream log; // Utility
    private boolean exportingToSVG;

    // Editions since the last saving
    private int changes;

    public Plane()
    {
        graph = new HashMap<Integer, Vertex>();

        addMouseListener(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        firstTime = true;

        setRealWidth(DEFAULT_REAL_WIDTH);
        setRealHeight(DEFAULT_REAL_HEIGHT);

        // Grid drawing settings
        gridIntervalX = 0.5;
        gridIntervalY = 0.5;

        factors = new double[]{2, 2, 2.5};
        factorIndexX = 0;
        factorIndexY = 0;
        setShowGrid(true);
        setShowAxis(false);
        log = System.out;

        vertexToDragIndex = -1;

        setDirected(true);
        setCurrentOperation(DEFAULT_OPERATION);
        setShapeType(SHAPE_CIRCLE);
        exportingToSVG = false;

        changes = 0;
    }

    double getRealWidth()
    {
        return realWidth;
    }

    public double getRealHeight()
    {
        return realHeight;
    }

    void setRealWidth(double rw)
    {
        realWidth = rw;
    }

    void setRealHeight(double rh)
    {
        realHeight = rh;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        graphics2D = g2d;

        if (firstTime) {
            initGraphics();
            firstTime = false;
        }

        g2d.setColor(Color.LIGHT_GRAY);

        if (isShowGrid())
            drawGrid(g2d);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            drawVertex(g2d, u);

            for (Map.Entry<Integer, Edge> v : u.getNeighbors().entrySet()) {
                Edge e = v.getValue();
                drawEdge(g2d, e);
            }
        }

        if (getCurrentOperation() == DRAW_NEW_EDGE && startVertex != null) {
            double x1 = startVertex.getCenter().x();
            double y1 = startVertex.getCenter().y();
            if (getMousePosition() != null) {
                int x2 = (int) getMousePosition().getX();
                int y2 = (int) getMousePosition().getY();
                g2d.drawLine(ix(x1), iy(y1), x2, y2);
            }
        }

        fontSize = -1;
    }


    public String exportToSVG()
    {
        SVGGraphics2D g2d = new SVGGraphics2D(getWidth(), getHeight());
        exportingToSVG = true;

        //initGraphics();
        if (firstTime) {
            initGraphics();
            //setScale();
            firstTime = false;
        }

        g2d.setColor(Color.LIGHT_GRAY);
        //if (isShowAxis()) drawAxis(g2d);
        //if (isShowGrid()) drawGrid(g2d);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(3f));

        for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            drawVertex(g2d, u);

            for (Map.Entry<Integer, Edge> v : u.getNeighbors().entrySet()) {
                Edge e = v.getValue();
                drawEdge(g2d, e);
            }
        }

        fontSize = -1;

        exportingToSVG = false;
        return g2d.getSVGDocument();
    }

    /**
     * Returns whether to draw or not the axis.
     *
     * @return true if axis should be drawn.
     */
    boolean isShowAxis()
    {
        return showAxis;
    }

    /**
     * Sets the showAxes property.
     *
     * @param showAxis the new setting
     */
    void setShowAxis(boolean showAxis)
    {
        this.showAxis = showAxis;
        repaint();
    }

    /**
     * Toggle showAxis state.
     */
    public void toggleShowAxis()
    {
        showAxis = !showAxis;
        repaint();
    }

    /**
     * Returns true if to draw grid or false if not.
     *
     * @return true or false, draw or not draw grid
     */
    boolean isShowGrid()
    {
        return showGrid;
    }

    /**
     * Sets if to draw grid or not.
     *
     * @param showGrid boolean, true to draw grid, false if not.
     */
    void setShowGrid(boolean showGrid)
    {
        this.showGrid = showGrid;
        repaint();
    }

    /**
     * Toggle showGrid state.
     */
    public void toggleShowGrid()
    {
        showGrid = !showGrid;
        repaint();
    }

    /**
     Initialize the variables needed to use isotropic mapping mode(Computer
     Graphics for Java Programmers, 2nd. Edition, Leen Ammeraaland, Kang Zhang).
     */
    void initGraphics()
    {
        maxX = getWidth() - 1;
        maxY = getHeight() - 1;

        centerX = maxX / 2;
        centerY = maxY / 2;

        pixelWidth = realWidth / Math.max(maxX, maxY);
        pixelHeight = realHeight / Math.max(maxX, maxY);

        factorIndexX = 0;
        factorIndexY = 0;

        gridIntervalX = 0.5;
        int w = ix(gridIntervalX) - ix(0);
        while (w < 50 || w > 150) {
            if (w < 50) {
                gridIntervalX *= factors[factorIndexX];
                factorIndexX = (factorIndexX + 1) % factors.length;
            } else if (w > 150) {
                factorIndexX = (factorIndexX - 1 + factors.length)
                             % factors.length;
                gridIntervalX /= factors[factorIndexX];
            }

            w = ix(gridIntervalX) - ix(0);
        }

        gridIntervalY = 0.5;
        int h = iy(0) - iy(gridIntervalY);
        while (h < 50 || h > 150) {
            if (h < 50) {
                gridIntervalY *= factors[factorIndexY];
                factorIndexY = (factorIndexY + 1) % factors.length;
            } else if (h > 150) {
                factorIndexY = (factorIndexY - 1 + factors.length)
                             % factors.length;
                gridIntervalY /= factors[factorIndexY];
            }
            h = iy(0) - iy(gridIntervalY);
        }
    }

    /**
     * Returns n rounded to the nearest integer.
     *
     * @param n a double number
     * @return an integer rounded to the nearest integer
     */
    int round(double n)
    {
        return (int) Math.floor(n + 0.5);
    }

    /**
     * Returns the device-coordinate of x.
     *
     * @param x x-coordinate in logical-coordinates
     * @return an integer with the device-coordinate of x
     */
    int ix(double x)
    {
        return round(centerX + x / pixelWidth);
    }

    /**
     * Returns the device-coordinate of y.
     *
     * @param y y-coordinate in logical-coordinates
     * @return an integer with the device-coordinate of y
     */
    int iy(double y)
    {
        return round(centerY - y / pixelHeight);
    }

    /**
     * Returns the device-coordinate of x using a particular pixel size.
     *
     * @param x  x-coordinate in logical-coordinates
     * @param ps pixel size
     * @return an integer with the device-coordinate of x
     */
    int ix(double x, double ps)
    {
        return round(centerX + x / ps);
    }

    /**
     * Returns the device-coordinate of y using a particular pixel size.
     *
     * @param y  y-coordinate in logical-coordinates
     * @param ps pixel size
     * @return an integer with the device-coordinate of y
     */
    int iy(double y, double ps)
    {
        return round(centerY - y / ps);
    }

    /**
     * Returns the logical-coordinate of x.
     *
     * @param x x-coordinate in device-coordinates
     * @return double, logical coordinate of x
     */
    double fx(int x)
    {
        return (double) (x - centerX) * pixelWidth;
    }

    /**
     * Returns the logical-coordinate of y.
     *
     * @param y y-coordinate in device-coordinates
     * @return double, logical coordinate of y
     */
    double fy(int y)
    {
        return (double) (centerY - y) * pixelHeight;
    }

    /**
     * Returns real with the specified precision.
     *
     * @param real      a real number
     * @param precision precision in digits of the output
     * @return real with the specified precision
     */
    double setPrecision(double real, int precision)
    {
        BigDecimal decimal = new BigDecimal(real, new MathContext(precision));
        return decimal.doubleValue();
    }

    /**
     * Zooms out the plane ten percent with origin in mouse click.
     *
     * @param mx X coordinate of mouse click.
     * @param my Y coordinate of mouse click.
     */
    void zoomOut(int mx, int my)
    {
        double psx = pixelWidth;
        double psy = pixelHeight;
        Point2D previous = new Point2D(fx(mx), fy(my));

        if (pixelWidth > 1 || pixelHeight > 1)
            return;

        pixelWidth += pixelWidth / 10;
        pixelHeight += pixelHeight / 10;

        int dx = ix(previous.x()) - ix(previous.x(), psx);
        int dy = iy(previous.y()) - iy(previous.y(), psy);

        centerX -= dx;
        centerY -= dy;

        repaint();
    }

    /**
     * Zooms in the plane ten percent with origin in mouse click.
     *
     * @param mx X coordinate of mouse click.
     * @param my Y coordinate of mouse click.
     */
    void zoomIn(int mx, int my)
    {
        double psx = pixelWidth;
        double psy = pixelHeight;
        Point2D previous = new Point2D(fx(mx), fy(my));

        if (pixelWidth < 1e-2 || pixelHeight < 1e-2)
            return;

        pixelWidth -= pixelWidth / 10;
        pixelHeight -= pixelHeight / 10;

        int dx = ix(previous.x()) - ix(previous.x(), psx);
        int dy = iy(previous.y()) - iy(previous.y(), psy);

        centerX -= dx;
        centerY -= dy;

        repaint();
    }

    /**
     * Restore the original scale.
     */
    void resetZoom()
    {
        setRealWidth(DEFAULT_REAL_WIDTH);
        setRealHeight(DEFAULT_REAL_HEIGHT);
        initGraphics();

        repaint();
    }

    /**
     * This methods set and get the values for scale of the plane.
     */
    public void setScaleInX(double scale)
    {
        scaleInX = scale;
    }

    public void setScaleInY(double scale)
    {
        scaleInY = scale;
    }

    double getScaleInX()
    {
        return scaleInX;
    }

    double getScaleInY()
    {
        return scaleInY;
    }

    /**
     * Set the scale of the plane in the form a:b.
     * Calling this method reset the zoom too.
     */
    private void setScale()
    {
        double a = getScaleInX();
        double b = getScaleInY();
        resetZoom();
        double factor = a / b;
        setRealWidth(getRealWidth() * factor);
        initGraphics();
    }

    /**
     * Translate the plane and set point (x, y) as the center of the viewport.
     *
     * @param x the new x coordinate.
     * @param y the new y coordinate.
     */
    public void translate(double x, double y)
    {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int dx = ix(x) - cx;
        int dy = iy(y) - cy;

        centerX -= dx;
        centerY -= dy;

        repaint();
    }

    /**
     * Draw grid in the plane.
     *
     * @param g2d Graphics2D object
     */
    void drawGrid(Graphics2D g2d)
    {
        double left = fx(0);
        double top = fy(0);
        double right = fx(getWidth() - 1);
        double bottom = fy(getHeight() - 1);

        int w = ix(gridIntervalX) - ix(0);
        if (w < 50) {
            gridIntervalX *= factors[factorIndexX];
            factorIndexX = (factorIndexX + 1) % factors.length;
        } else if (w > 150) {
            factorIndexX = (factorIndexX - 1 + factors.length) % factors.length;
            gridIntervalX /= factors[factorIndexX];
        }

        int cX = ix(0);
        int interval = java.lang.Math.max(1, ix(gridIntervalX) - cX);
        int mod = cX % interval;
        double startX = fx(mod) - (fx(mod) % gridIntervalX) - gridIntervalX;

        Stroke dash = new BasicStroke(0.3f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 10,
                new float[]{8, 4}, 0);

        g2d.setStroke(dash);
        g2d.setColor(Color.LIGHT_GRAY);
        for (double i = startX; i <= right; i += gridIntervalX)
            if (ix(i) != ix(0) || !isShowAxis())
                g2d.drawLine(ix(i), iy(top), ix(i), iy(bottom));

        int h = iy(0) - iy(gridIntervalY);
        if (h < 50) {
            gridIntervalY *= factors[factorIndexY];
            factorIndexY = (factorIndexY + 1) % factors.length;
        } else if (h > 150) {
            factorIndexY = (factorIndexY - 1 + factors.length) % factors.length;
            gridIntervalY /= factors[factorIndexY];
        }

        int cY = iy(0);
        interval = java.lang.Math.max(1, iy(gridIntervalY) - cY);
        mod = cY % interval;
        double startY = fy(mod) - (fy(mod) % gridIntervalY) + gridIntervalY;

        for (double i = startY; i >= bottom; i -= gridIntervalY)
            if (iy(i) != iy(0) || !isShowAxis())
                g2d.drawLine(ix(left), iy(i), ix(right), iy(i));

    } // End of drawGrid()

    /**
     * Draw axes in the plane.
     *
     * @param g2d a Graphics2D object
     */
    void drawAxis(Graphics2D g2d)
    {
        double left = fx(0);
        double top = fy(0);
        double right = fx(getWidth() - 1);
        double bottom = fy(getHeight() - 1);

        int w = ix(gridIntervalX) - ix(0);
        if (w < 50) {
            gridIntervalX *= factors[factorIndexX];
            factorIndexX = (factorIndexX + 1) % factors.length;
        } else if (w > 150) {
            factorIndexX = (factorIndexX - 1 + factors.length) % factors.length;
            gridIntervalX /= factors[factorIndexX];
        }

        int cX = ix(0);
        int interval = java.lang.Math.max(1, ix(gridIntervalX) - cX);
        int mod = cX % interval;
        double startX = fx(mod) - (fx(mod) % gridIntervalX) - gridIntervalX;

        g2d.setStroke(new BasicStroke(1f));
        g2d.setColor(Color.BLACK);

        for (double i = startX; i <= right; i += gridIntervalX) {
            if (ix(i) == ix(0)) {
                g2d.drawLine(ix(i), iy(top), ix(i), iy(bottom));

                //Draws arrows of y axis
                g2d.drawLine(ix(i) - 7, iy(top) + 7, ix(i), iy(top));
                g2d.drawLine(ix(i) + 7, iy(top) + 7, ix(i), iy(top));
                g2d.drawString("y", ix(i) - 20, iy(top) + 10);

                g2d.drawLine(ix(i) - 7, iy(bottom) - 7, ix(i), iy(bottom));
                g2d.drawLine(ix(i) + 7, iy(bottom) - 7, ix(i), iy(bottom));
                g2d.drawString("-y", ix(i) - 20, iy(bottom) - 10);
            } else {
                g2d.drawString("" + setPrecision(i, 5), ix(i) + 5, iy(0) + 15);
            }
            g2d.drawLine(ix(i), iy(0), ix(i), iy(0) + 12);
        }

        int h = iy(0) - iy(gridIntervalY);
        if (h < 50) {
            gridIntervalY *= factors[factorIndexY];
            factorIndexY = (factorIndexY + 1) % factors.length;
        } else if (w > 150) {
            factorIndexY = (factorIndexY - 1 + factors.length) % factors.length;
            gridIntervalY /= factors[factorIndexY];
        }

        int cY = iy(0);
        interval = java.lang.Math.max(1, iy(gridIntervalY) - cY);
        mod = cY % interval;
        double startY = fy(mod) - (fy(mod) % gridIntervalY) + gridIntervalY;

        for (double i = startY; i >= bottom; i -= gridIntervalY) {
            if (iy(i) == iy(0)) {
                g2d.drawLine(ix(left), iy(i), ix(right), iy(i));

                //Draw arrows of x axis
                g2d.drawLine(ix(left) + 7, iy(i) - 7, ix(left), iy(i));
                g2d.drawLine(ix(left) + 7, iy(i) + 7, ix(left), iy(i));
                g2d.drawString("-x", ix(left) + 5, iy(i) - 10);

                g2d.drawLine(ix(right) - 7, iy(i) - 7, ix(right), iy(i));
                g2d.drawLine(ix(right) - 7, iy(i) + 7, ix(right), iy(i));
                g2d.drawString("x", ix(right) - 5, iy(i) - 10);
            } else {
                g2d.drawString("" + setPrecision(i, 5), ix(0) + 10, iy(i) - 5);
            }
            g2d.drawLine(ix(0), iy(i), ix(0) + 12, iy(i));
        }

    } // End of drawAxis()

    boolean isDragPlane()
    {
        return dragPlane;
    }

    void setDragPlane(boolean dragPlane)
    {
        this.dragPlane = dragPlane;
    }

    boolean isDragVertex()
    {
        return dragVertex;
    }

    void setDragVertex(boolean dragVertex)
    {
        this.dragVertex = dragVertex;
    }

    /* MouseListener methods. */
    @Override
    public void mouseReleased(MouseEvent event)
    {
        setDragPlane(false);
        setDragVertex(false);
        vertexToDragIndex = -1;

        if (getCurrentOperation() == DRAW_NEW_EDGE && startVertex != null) {
            int x = (int) event.getPoint().getX();
            int y = (int) event.getPoint().getY();
            Point2D p = new Point2D(fx(x), fy(y));

            for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                int id = vertex.getId();
                if (startVertex.getId() == id)
                    continue;

                boolean found = false;
                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = p.distanceTo(vertex.getCenter());
                    if (distance <= entry.getValue().getRadius())
                        found = true;
                } else {
                    Polygon box = createBox(vertex);
                    if (box.contains(event.getPoint()))
                        found = true;
                }
                if (!found) continue;

                if (startVertex.getNeighbors().containsKey(id)) {
                    JOptionPane.showMessageDialog(null,
                            "An edge already exists!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                }

                String label = JOptionPane.showInputDialog(null, "Label");
                if (label != null) {
                    startVertex.addNeighbor(id, label);
                    break;
                }
            }
        }

        startVertex = null;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        Point point = event.getPoint();
        Point2D p = new Point2D(fx((int) point.getX()), fy((int) point.getY()));
        int operation = getCurrentOperation();
        if (event.getClickCount() == 2 && operation == DEFAULT_OPERATION) {
            for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                boolean found = false;

                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = vertex.getCenter().distanceTo(p);
                    if (distance <= vertex.getRadius())
                        found = true;
                } else {
                    Polygon box = createBox(vertex);
                    if (box.contains(event.getPoint()))
                        found = true;
                }

                if (!found) continue;
                Editor editor = new Editor(vertex.getLabel(),
                                           vertex.getLabelAlignment());
                int res  = JOptionPane.showConfirmDialog(null, editor,
                            "New label", JOptionPane.OK_CANCEL_OPTION);

                if (res == JOptionPane.OK_OPTION) {
                    String label = editor.getText();

                    if (label != null && !label.isEmpty()) {
                        vertex.setLabel(label);
                        vertex.setLabelAlignment(editor.getTextAlignment());
                        vertex.setLabelChanged(true);
                        changes++;
                        repaint();
                        return;
                    }
                }
            }

            for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                HashMap<Integer, Edge> set = vertex.getNeighbors();
                for (Entry<Integer, Edge> e : set.entrySet()) {
                    Edge edge = e.getValue();
                    Polygon box = createBox(edge.getLabelPosition(),
                                            edge.getLabel(),
                                            vertex.getRadius());
                    if (box.contains(event.getPoint())) {
                        String label = JOptionPane
                                        .showInputDialog(null, "New label",
                                                         edge.getLabel());
                        if (label != null) {
                            edge.setLabel(label);
                            changes++;
                            repaint();
                            return;
                        }
                    }
                }
            }

            return;
        }


        if (getCurrentOperation() == DRAW_NEW_VERTEX) {
            Editor editor = new Editor();
            int res  = JOptionPane.showConfirmDialog(null, editor,
                    "Label (required)", JOptionPane.OK_CANCEL_OPTION);

            if (res == JOptionPane.OK_OPTION) {
                String label = editor.getText();
                if (label != null && !label.isEmpty()) {
                    Vertex v = new Vertex(nodeId, label, p);
                    v.setLabelAlignment(editor.getTextAlignment());
                    graph.put(nodeId, v);
                    nodeId++;
                    changes++;
                    repaint();
                }
            }
        } else if (getCurrentOperation() == ERASE_OBJECT) {
            int delId = -1;
            for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                boolean found = false;

                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = vertex.getCenter().distanceTo(p);
                    if (distance <= vertex.getRadius())
                        found = true;
                } else {
                    Polygon box = createBox(vertex);
                    if (box.contains(event.getPoint()))
                        found = true;
                }

                if (found) {
                    int op = JOptionPane
                            .showConfirmDialog(null, "Are you sure?", "Confirm",
                                               JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        delId = vertex.getId();
                    } else {
                        return;
                    }

                    break;
                }
            }

            if (delId != -1) {
                for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    if (v.getId() != delId &&
                        v.getNeighbors().containsKey(delId)) {
                        v.getNeighbors().remove(delId);
                    }
                }

                graph.remove(delId);
                changes++;
                repaint();
                return;
            }

            for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex v = entry.getValue();
                for (Entry<Integer, Edge> edge : v.getNeighbors().entrySet()) {
                    int startId = edge.getValue().getStart();
                    int endId = edge.getValue().getEnd();
                    Point2D a = graph.get(startId).getCenter();
                    Point2D b = graph.get(endId).getCenter();
                    if (inLine(a, b, p)) {
                        int op = JOptionPane
                                .showConfirmDialog(null, "Are you sure?",
                                                   "Confirm",
                                                   JOptionPane.YES_NO_OPTION);
                        if (op == JOptionPane.YES_OPTION) {
                            v.getNeighbors().remove(edge.getKey());
                            changes++;
                            repaint();
                        }
                        return;
                    }
                }
            }

        }
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        if (event.getButton() == MouseEvent.BUTTON3) {        // Drag plane
            setDragPlane(true);
            setDragVertex(false);
        } else if (event.getButton() == MouseEvent.BUTTON1) { // Drag vertex
            if (getCurrentOperation() == DEFAULT_OPERATION) {
                setDragVertex(true);
                setDragPlane(false);

                int x = (int) event.getPoint().getX();
                int y = (int) event.getPoint().getY();
                Point2D click = new Point2D(fx(x), fy(y));

                for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
                    Vertex vertex = entry.getValue();

                    if (getShapeType() == SHAPE_CIRCLE) {
                        double distance = click.distanceTo(vertex.getCenter());
                        if (distance <= vertex.getRadius()) {
                            vertexToDragIndex = entry.getKey();
                            break;
                        }
                    } else {
                        Polygon box = createBox(vertex);
                        if (box.contains(event.getPoint())) {
                            vertexToDragIndex = entry.getKey();
                            break;
                        }
                    }
                }
            } else if (getCurrentOperation() == DRAW_NEW_EDGE) {
                int x = (int) event.getPoint().getX();
                int y = (int) event.getPoint().getY();
                Point2D click = new Point2D(fx(x), fy(y));

                boolean found = false;
                for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
                    Vertex vertex = entry.getValue();
                    if (getShapeType() == SHAPE_CIRCLE) {
                        double distance = vertex.getCenter().distanceTo(click);
                        if (distance <= vertex.getRadius()) {
                            startVertex = vertex;
                            found = true;
                            break;
                        }
                    } else {
                        Polygon box = createBox(vertex);
                        if (box.contains(event.getPoint())) {
                            startVertex = entry.getValue();
                            found = true;
                            break;
                        }
                    }
                }

                if (!found)
                    startVertex = null;
            }

        }

        startDrag = event.getPoint();
    }

    /* MouseMotionListener methods. */
    /**
     * Performs plane dragging.
     *
     * @param e MouseEvent with coordinates of mouse position
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        int dx = e.getX() - (int) startDrag.getX();
        int dy = e.getY() - (int) startDrag.getY();

        if (dx * dx + dy * dy < 50)
            return;

        startDrag = e.getPoint();

        if (getCurrentOperation() == DEFAULT_OPERATION) {
            if (isDragPlane()) {
                centerX += dx;
                centerY += dy;
            } else if (isDragVertex() && vertexToDragIndex != -1) {
                Vertex vertex = graph.get(vertexToDragIndex);
                double x = vertex.getCenter().x();
                double y = vertex.getCenter().y();

                double dx1 = fx(dx) - fx(0);
                double dy1 = fy(dy) - fy(0);

                x += dx1;
                y += dy1;
                vertex.setCenter(x, y);
                changes++;
            }
        }

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    /* MouseWheelListener methods. */
    /**
     * Controls zoom direction.
     *
     * @param event a MouseWheelEvent object
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent event)
    {
        int rotation = event.getWheelRotation();
        int x = event.getX();
        int y = event.getY();

        if (rotation > 0)
            zoomIn(x, y);
        else
            zoomOut(x, y);
    }

    private void calculateFontSize(Graphics2D g2d)
    {
        int desiredHeight = ix((int) Vertex.BASE_VERTEX_RADIUS) - ix(0);
        if (fontSize == -1) {
            // Search a font size(in points) such that its height in pixels
            // best approximates radius
            int bestSuited = 5;
            int low = 5, high = 512;
            while (low < high) {
                int size = (low + high) / 2;
                Font test = new Font(Font.MONOSPACED, Font.PLAIN, size);
                FontMetrics metrics = g2d.getFontMetrics(test);
                int fontHeight = metrics.getAscent() + metrics.getDescent();

                if (fontHeight > desiredHeight) {
                    high = size;
                    bestSuited = high - 1;
                } else if (fontHeight < desiredHeight) {
                    low = size + 1;
                } else {
                    bestSuited = size;
                    break;
                }
            }

            fontSize = bestSuited;
        }

    }

    void drawVertex(Graphics2D g2d, Vertex vertex)
    {
        g2d.setStroke(new BasicStroke(2f));
        Color tmp = g2d.getColor();

        if (vertex.hasLabelChanged()) {
            ArrayList<Point2D> box = createBox2(vertex.getCenter(),
                                                vertex.getLabel(),
                                                vertex.getRadius());
            double d1 = vertex.getCenter().distanceTo(box.get(0));
            double d2 = vertex.getCenter().distanceTo(box.get(1));
            double d3 = vertex.getCenter().distanceTo(box.get(2));
            double d4 = vertex.getCenter().distanceTo(box.get(3));

            double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));
            vertex.setRadius(Math.max(max, Vertex.BASE_VERTEX_RADIUS));

            vertex.setLabelChanged(false);
        }

        int padding = 3 * RECTANGLE_PADDING;
        int radius = ix(vertex.getRadius()) - ix(0) + padding;
        int width = 2 * radius;
        int height = 2 * radius;
        int x = ix(vertex.getCenter().x());
        int y = iy(vertex.getCenter().y());

        Font tmpFont = g2d.getFont();
        calculateFontSize(g2d);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        int fontHeight = metrics.getAscent() + metrics.getDescent();

        String label = StringUtils.align(vertex.getLabel(),
                                         vertex.getLabelAlignment());

        String[] lines = label.split("\n");
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }
        int fontWidth = metrics.stringWidth(largest) + padding;
        int stringHeight = fontHeight * lines.length + padding;

        if (getShapeType() == SHAPE_CIRCLE) {
            g2d.setColor(vertex.getBorderColor());
            g2d.drawOval(x - radius, y - radius, width, height);
            g2d.setColor(vertex.getBackgroundColor());
            g2d.fillOval(x + 1 - radius, y + 1 - radius, width - 2, width - 2);
        } else if (getShapeType() == SHAPE_RECTANGLE) {
            g2d.setColor(vertex.getBorderColor());
            g2d.drawRoundRect(x - fontWidth / 2,
                              y - stringHeight / 2,
                              fontWidth,
                              stringHeight, 10, 10);
        }

        g2d.setColor(Color.BLACK);
        y = y - stringHeight / 2 - metrics.getDescent();
        x = x - fontWidth / 2 + RECTANGLE_PADDING;
        for (int i = 0; i < lines.length; i++) {
            y += fontHeight;
            g2d.drawString(lines[i], x, y);
        }
        g2d.setFont(tmpFont);
        g2d.setColor(tmp);
    }

    void drawEdge(Graphics2D g2d, Edge edge)
    {
        Vertex start = graph.get(edge.getStart());
        Vertex end = graph.get(edge.getEnd());

        Color tmpColor = g2d.getColor();
        g2d.setColor(edge.getColor());
        CubicCurve2D curve = new CubicCurve2D.Float();

        double startRadius = start.getRadius();
        double endRadius = end.getRadius();
        double pxStart = start.getCenter().x();
        double pyStart = start.getCenter().y();
        double pxEnd = end.getCenter().x();
        double pyEnd = end.getCenter().y();

        //By default the edge is a straight line
        double angle;
        double ctrlX = (pxEnd + pxStart) * 0.5;
        double ctrlY = (pyEnd + pyStart) * 0.5;
        double ctrlX2 = 0;
        double ctrlY2 = 0;

        int direction = 0;    //Straight line
        if (end.contains(start.getId()) && start != end) {
            if (start.getEdgeDirection() == end.getEdgeDirection()) {
                start.setEdgeDirection(1);
                end.setEdgeDirection(-1);
                direction = 1;
            } else {
                direction = start.getEdgeDirection();
            }
        } else if (start == end) {
            direction = Integer.MAX_VALUE;

            ctrlX = pxStart + 3 * startRadius;
            ctrlY = pyStart + 2 * startRadius;

            ctrlX2 = pxStart + 3 * endRadius;
            ctrlY2 = pyStart - 2 * endRadius;

            pxStart += startRadius * Math.cos(Math.PI * 0.25);
            pyStart += startRadius * Math.sin(Math.PI * 0.25);

            pxEnd += endRadius * Math.cos(-Math.PI * 0.25);
            pyEnd += endRadius * Math.sin(-Math.PI * 0.25);
        }

        calculateFontSize(g2d);
        Font tmpFont = g2d.getFont();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        g2d.setFont(font);

        // Draw edge
        if (direction == 1 || direction == -1) {
            if (direction == 1) {
                angle = Math.atan2(pyStart - pyEnd, pxEnd - pxStart);
                double beta = angle + 0.1;
                double halfRatio = Math.sqrt(Math.pow(pxEnd - pxStart, 2) +
                                             Math.pow(pyEnd - pyStart, 2));
                halfRatio *= 0.5;

                ctrlX = ctrlX2 = pxStart + halfRatio * Math.cos(beta);
                ctrlY = ctrlY2 = pyStart - halfRatio * Math.sin(beta);
            } else {
                angle = Math.atan2(pyStart - pyEnd, pxEnd - pxStart);
                double beta = angle + 0.1;

                double halfRatio = Math.sqrt(Math.pow(pxEnd - pxStart, 2) +
                                             Math.pow(pyStart - pyEnd, 2));
                halfRatio *= 0.5;

                ctrlX = ctrlX2 = pxStart + halfRatio * Math.cos(beta);
                ctrlY = ctrlY2 = pyStart - halfRatio * Math.sin(beta);
            }
            curve.setCurve(pxStart, pyStart, ctrlX, ctrlY,
                           ctrlX2, ctrlY2, pxEnd, pyEnd);

        }

        if (direction == Integer.MAX_VALUE) {
            curve.setCurve(ix(pxStart), iy(pyStart), ix(ctrlX), iy(ctrlY),
                           ix(ctrlX2), iy(ctrlY2), ix(pxEnd), iy(pyEnd));
        } else {
            angle = Math.atan2(pyEnd - pyStart, pxEnd - pxStart);
            if (getShapeType() == SHAPE_CIRCLE) {
                // AS = Adjacent Size
                // OS = Opposite Size
                double startAS = startRadius * Math.cos(angle);
                double startOS = startRadius * Math.sin(angle);
                pxStart += startAS;
                pyStart += startOS;

                double endAS = endRadius * Math.cos(angle);
                double endOS = endRadius * Math.sin(angle);
                pxEnd -= endAS;
                pyEnd -= endOS;

                Point2D tmp = new Point2D(pxEnd - pxStart, pyEnd - pyStart);
                double xx = tmp.x() * 0.5;
                double yy = tmp.y() * 0.5;
                ctrlX = pxStart + xx;
                ctrlY =  pyStart + yy;

                curve.setCurve(ix(pxStart), iy(pyStart),
                               ix(ctrlX), iy(ctrlY),
                               ix(ctrlX), iy(ctrlY),
                               ix(pxEnd), iy(pyEnd));
            } else if (getShapeType() == SHAPE_RECTANGLE ||
                       getShapeType() == SHAPE_NONE) {

                FontMetrics metrics = g2d.getFontMetrics();
                Vertex u = graph.get(edge.getStart());
                Vertex v = graph.get(edge.getEnd());
                Point2D startPoint = computeEndPoint(u, v, metrics);
                Point2D endPoint = computeEndPoint(v, u, metrics);

                pxStart = startPoint.x();
                pyStart = startPoint.y();
                pxEnd = endPoint.x();
                pyEnd = endPoint.y();
                Point2D tmp = new Point2D(pxEnd - pxStart, pyEnd - pyStart);
                double xx = tmp.x() * 0.5;
                double yy = tmp.y() * 0.5;
                ctrlX = pxStart + xx;
                ctrlY =  pyStart + yy;

                curve.setCurve(ix(pxStart), iy(pyStart),
                        ix(ctrlX), iy(ctrlY), ix(ctrlX), iy(ctrlY),
                        ix(pxEnd), iy(pyEnd));
            }
        }

        g2d.setStroke(new BasicStroke(edge.getStroke()));
        g2d.draw(curve);
        g2d.setStroke(new BasicStroke(1));
        //Ends draw edge

        // Draw label
        FontMetrics m = g2d.getFontMetrics();
        double width = Math.abs(fx(m.stringWidth(edge.getLabel())) - fx(0));
        double height = Math.abs(fy(m.getAscent() + m.getDescent()) - fy(0));
        if (direction == Integer.MAX_VALUE) {
            g2d.drawString(edge.getLabel(), ix(pxStart + 2), iy(pyStart));
            edge.setLabelPosition(new Point2D(pxStart + 0.5 * width,
                                              pyStart + 0.5 * height));
        } else {
            Point2D c = new Point2D(ctrlX, ctrlY + 0.5 * height);
            int x = ix(c.x());
            int y = iy(c.y());
            int fontHeight = m.getAscent() + m.getDescent();
            int fontWidth = m.stringWidth(edge.getLabel());
            g2d.drawString(edge.getLabel(),
                           x - fontWidth / 2,
                           y + fontHeight / 4);
            edge.setLabelPosition(c);
            if (!exportingToSVG && edge.getLabel().isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.fillOval(ix(c.x()), iy(c.y()), 4, 4);
                g2d.setColor(Color.BLACK);
            }
        }

        g2d.setFont(tmpFont);
        // Ends draw label

        if (isDirected()) {
            Line2D.Double line;
            if (direction == Integer.MAX_VALUE) {
                line = new Line2D.Double(ix(ctrlX2), iy(ctrlY2),
                                         ix(pxEnd), iy(pyEnd));
                angle = Math.atan2(line.y2 - iy(ctrlY2), line.x2 - ix(ctrlX2));
            } else {
                line = new Line2D.Double(ix(pxStart), iy(pyStart),
                                         ix(pxEnd), iy(pyEnd));
                angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            }

            int length = (ix(endRadius) - ix(0)) / 8;
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, length);
            arrowHead.addPoint(-length / 2, -length);
            arrowHead.addPoint(0, length);
            arrowHead.addPoint(length / 2, -(length));
            arrowHead.addPoint(0, length);

            AffineTransform transform = new AffineTransform();
            transform.setToIdentity();

            // Adjust arrow head to circle outline
            double tmpHypotenuse = length;
            double tmpOpposite = tmpHypotenuse * Math.sin(angle);
            double tmpAdjacent = tmpHypotenuse * Math.cos(angle);

            AffineTransform tmp = g2d.getTransform();
            transform.translate(line.x2 - tmpAdjacent, line.y2 - tmpOpposite);
            transform.rotate((angle - Math.PI * 0.5));

            g2d.setTransform(transform);
            g2d.draw(arrowHead);
            g2d.setTransform(tmp);
        }

        g2d.setColor(tmpColor);
    }

    boolean isDirected()
    {
        return directed;
    }

    void setDirected(boolean directed)
    {
        this.directed = directed;
    }

    public void setGraph(HashMap<Integer, Vertex> graph)
    {
        this.graph = graph;
        repaint();
    }

    public HashMap<Integer, Vertex> getGraph()
    {
        return graph;
    }

    public void setIds(HashMap<String, Integer> ids)
    {
        this.ids = ids;
    }

    int getCurrentOperation()
    {
        return currentOperation;
    }

    public void setCurrentOperation(int currentOperation)
    {
        this.currentOperation = currentOperation;
    }

    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    public int getShapeType()
    {
        return shapeType;
    }

    public void setShapeType(int shapeType)
    {
        this.shapeType = shapeType;
    }

    boolean inLine(Point2D a, Point2D b, Point2D p)
    {
        double a1 = b.x() - a.x();
        double b1 = b.y() - a.y();
        double a2 = p.x() - a.x();
        double b2 = p.y() - a.y();

        double alpha = Math.atan2(b1, a1);
        double beta = Math.atan2(b2, a2);
        double theta = Math.abs(alpha - beta);
        double dist = Math.abs(a.distanceTo(p) * Math.sin(theta));
        double eps = Math.abs(fx(3) - fx(0));

        return dist < eps;
    }

    boolean segInt(Point2D a, Point2D b, Point2D c, Point2D d)
    {
        double test1 = ccw(a, b, c) * ccw(a, b, d);
        double test2 = ccw(c, d, a) * ccw(c, d, b);
        return test1 <= 0 && test2 <= 0;
    }

    Point2D intersection(Point2D a, Point2D b, Point2D c, Point2D d)
    {
        double a1 = a.y() - b.y();
        double b1 = b.x() - a.x();
        double c1 = a1 * a.x() + b1 * a.y();

        double a2 = c.y() - d.y();
        double b2 = d.x() - c.x();
        double c2 = a2 * c.x() + b2 * c.y();

        double det = a1 * b2 - a2 * b1;
        if (det == 0) {
            return null;
        }

        double d1 = b2 * c1 - b1 * c2;
        double d2 = a1 * c2 - a2 * c1;

        return new Point2D(d1 / det, d2 / det);
    }

    double ccw(Point2D a, Point2D b, Point2D c)
    {
        double a1 = b.x() - a.x();
        double b1 = b.y() - a.y();
        double a2 = c.x() - a.x();
        double b2 = c.y() - a.y();

        return a1 * b2 - a2 * b1;
    }

    Point2D computeEndPoint(Vertex u, Vertex v, FontMetrics m)
    {
        Point2D cu = u.getCenter();
        Point2D cv = v.getCenter();
        int padding = 3 * RECTANGLE_PADDING;

        String label = StringUtils.align(u.getLabel(), u.getLabelAlignment());
        String[] lines = label.split("\n");
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }

        int w = m.stringWidth(largest) + padding;
        int h = (m.getAscent() + m.getDescent()) * lines.length + padding;
        double width = fx(w) - fx(0);
        double height = fy(h) - fy(0);
        double x = cu.x();
        double y = cu.y();
        Point2D a = new Point2D(x - width * 0.5, y + height * 0.5);
        Point2D b = new Point2D(x + width * 0.5, y + height * 0.5);
        Point2D c = new Point2D(x + width * 0.5, y - height * 0.5);
        Point2D d = new Point2D(x - width * 0.5, y - height * 0.5);

        Point2D endPoint = cv;
        if (segInt(a, b, cu, cv)) {
            endPoint = intersection(a, b, cu, cv);
        } else if (segInt(b, c, cu, cv)) {
            endPoint = intersection(b, c, cu, cv);
        } else if (segInt(c, d, cu, cv)) {
            endPoint = intersection(c, d, cu, cv);
        } else if (segInt(d, a, cu, cv)) {
            endPoint = intersection(d, a, cu, cv);
        }

        return endPoint;
    }

    public Polygon createBox(Vertex v) {
        String label = StringUtils.align(v.getLabel(), v.getLabelAlignment());
        return createBox(v.getCenter(), label, v.getRadius());
    }

    public Polygon createBox(Point2D center, String label, double r)
    {
        Polygon box = new Polygon();
        ArrayList<Point2D> box2 = createBox2(center, label, r);
        box.addPoint(ix(box2.get(0).x()), iy(box2.get(0).y()));
        box.addPoint(ix(box2.get(1).x()), iy(box2.get(1).y()));
        box.addPoint(ix(box2.get(2).x()), iy(box2.get(2).y()));
        box.addPoint(ix(box2.get(3).x()), iy(box2.get(3).y()));

        return box;
    }

    public ArrayList<Point2D> createBox2(Point2D center, String label, double r)
    {
        int radius = ix(r) - ix(0);
        int x2 = ix(center.x());
        int y2 = iy(center.y());

        calculateFontSize(graphics2D);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        graphics2D.setFont(font);
        FontMetrics metrics = graphics2D.getFontMetrics();

        String[] lines = label.split("\n");
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }

        int fw = metrics.stringWidth(largest);
        int fh = (metrics.getAscent() + metrics.getDescent()) * lines.length;
        int pad = RECTANGLE_PADDING;
        ArrayList<Point2D> box = new ArrayList<Point2D>();
        box.add(new Point2D(fx(x2 - (fw / 2 + pad)), fy(y2 - (fh / 2 + pad))));
        box.add(new Point2D(fx(x2 + (fw / 2 + pad)), fy(y2 - (fh / 2 + pad))));
        box.add(new Point2D(fx(x2 + (fw / 2 + pad)), fy(y2 + (fh / 2 + pad))));
        box.add(new Point2D(fx(x2 - (fw / 2 + pad)), fy(y2 + (fh / 2 + pad))));
        box.add(new Point2D(fx(x2 - (fw / 2 + pad)), fy(y2 - (fh / 2 + pad))));

        return box;
    }

    public boolean hasChanges()
    {
        return changes > 0;
    }

    public void setChanges(int value)
    {
        changes = value;
    }
}

