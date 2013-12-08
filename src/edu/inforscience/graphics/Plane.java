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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.MathContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.graphics2d.svg.SVGGraphics2D;

public class Plane extends JPanel implements MouseListener,
        MouseWheelListener,
        MouseMotionListener {
    private HashMap<Integer, Vertex> graph;
    private HashMap<String, Integer> ids;
    private Vertex edgeStart;
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
    private boolean drawOnlyVertexLabel;

    private boolean directed;

    private Point startDrag;
    private int vertexToDragIndex;

    private int fontSize;

    private final PrintStream log; // Utility

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
        drawOnlyVertexLabel = false;
        setCurrentOperation(DEFAULT_OPERATION);
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

        if (firstTime) {
            initGraphics();
            firstTime = false;
        }

        g2d.setColor(Color.LIGHT_GRAY);

        drawAxis(g2d);
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

        if (getCurrentOperation() == DRAW_NEW_EDGE && edgeStart != null) {
            double x1 = edgeStart.getCenter().x();
            double y1 = edgeStart.getCenter().y();
            if (getMousePosition() != null) {
                int x2 = (int) getMousePosition().getX();
                int y2 = (int) getMousePosition().getY();
                g2d.drawLine(ix(x1), iy(y1), x2, y2);
            }
        }

        fontSize = -1;
    }

    public void saveToFile(File file) throws IOException
    {
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fw);

        writer.write("[EDGES]" + "\n");
        for (Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            for (Entry<Integer, Edge> e : u.getNeighbors().entrySet()) {
                Edge edge = e.getValue();
                Vertex v = graph.get(edge.getEnd());
                writer.write(u.getLabel() + ","
                             + v.getLabel() + ","
                             + edge.getLabel()+ "\n");
            }
        }

        writer.write("[VERTICES]" + "\n");
        for (Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            double x = u.getCenter().x();
            double y = u.getCenter().y();
            writer.write(u.getLabel() + ":" + x + "," + y + "\n");
        }

        writer.close();
    }

    public void exportToSvg(File file) throws IOException
    {
        SVGGraphics2D g2d = new SVGGraphics2D(getWidth(), getHeight());

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

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(g2d.getSVGDocument());
        bw.close();
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

        if (pixelWidth > 0.2 || pixelHeight > 0.2)
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

    }// End of drawAxis()

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

        if (getCurrentOperation() == DRAW_NEW_EDGE) {
            int x = (int) event.getPoint().getX();
            int y = (int) event.getPoint().getY();
            Point2D p = new Point2D(fx(x), fy(y));

            for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
                double distance = p.distanceTo(entry.getValue().getCenter());
                if (edgeStart.getId() == entry.getValue().getId())
                    continue;
                if (distance <= entry.getValue().getRadius()) {
                    String label = JOptionPane.showInputDialog(null, "Label");
                    if (label != null) {
                        edgeStart.addNeighbor(entry.getValue().getId(), label);
                        break;
                    }
                }
            }
        }

        edgeStart = null;
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
        if (getCurrentOperation() == DRAW_NEW_VERTEX) {
            String label = JOptionPane.showInputDialog(null, "Enter label");
            if (label != null) {
                Vertex v = new Vertex(nodeId, label, p);
                graph.put(nodeId, v);
                nodeId++;
                repaint();
            }
        } else if (getCurrentOperation() == ERASE_OBJECT) {
            int delId = -1;
            for (Entry<Integer, Vertex> entry : graph.entrySet()) {
                Vertex v = entry.getValue();
                if (v.getCenter().distanceTo(p) <= v.getRadius()) {
                    int op = JOptionPane
                            .showConfirmDialog(null, "Are you sure?", "Confirm",
                                               JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        delId = v.getId();
                        break;
                    }
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
                    double a1 = b.x() - a.x();
                    double b1 = b.y() - a.y();
                    double a2 = p.x() - a.x();
                    double b2 = p.y() - a.y();

                    double alpha = Math.atan2(b1, a1);
                    double beta = Math.atan2(b2, a2);
                    double theta = Math.abs(alpha - beta);
                    double dist = a.distanceTo(p) * Math.sin(theta);
                    double eps = fx(3) - fx(0);
                    log.println("dist = " + dist);
                    log.println("eps = " + eps);
                    if (dist < eps) {
                        int op = JOptionPane.showConfirmDialog(null,
                                                               "Are you sure?");
                        if (op == JOptionPane.YES_OPTION) {
                            v.getNeighbors().remove(edge.getKey());
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
                    double distance = click.distanceTo(vertex.getCenter());
                    if (distance <= vertex.getRadius()) {
                        vertexToDragIndex = entry.getKey();
                        break;
                    }
                }

                log.println("HERE....");
            } else if (getCurrentOperation() == DRAW_NEW_EDGE) {
                int x = (int) event.getPoint().getX();
                int y = (int) event.getPoint().getY();
                Point2D click = new Point2D(fx(x), fy(y));

                boolean found = false;
                for (Map.Entry<Integer, Vertex> entry : graph.entrySet()) {
                    Vertex vertex = entry.getValue();
                    double distance = click.distanceTo(vertex.getCenter());
                    if (distance <= vertex.getRadius()) {
                        edgeStart = entry.getValue();
                        found = true;
                        break;
                    }
                }

                if (!found)
                    edgeStart = null;
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

    private void calculateFontSize(Graphics2D g2d, int desiredSize)
    {
        if (fontSize == -1) {
            // Search a font size(in points) such that its height in pixels
            // best approximates radius
            int size = 1;
            int bestSuited = 1;
            int minDiff = 1000;
            while (size <= 300) {
                Font test = new Font("Monospace", Font.BOLD, size);
                FontMetrics metrics = g2d.getFontMetrics(test);
                int fontHeight = metrics.getHeight();

                int diff = Math.abs(desiredSize - fontHeight);
                if (diff < minDiff) {
                    bestSuited = size;
                    minDiff = diff;
                }
                size += 10;
            }

            fontSize = bestSuited;
        }
    }

    void drawVertex(Graphics2D g2d, Vertex vertex)
    {
        g2d.setStroke(new BasicStroke(2f));
        Color tmp = g2d.getColor();

        int radius = ix(vertex.getRadius()) - ix(0);
        int width = 2 * radius;
        int height = 2 * radius;
        int x = ix(vertex.getCenter().x());
        int y = iy(vertex.getCenter().y());

        if (!drawOnlyVertexLabel) {
            g2d.setColor(vertex.getBorderColor());
            g2d.drawOval(x - radius, y - radius, width, height);

            g2d.setColor(vertex.getBackgroundColor());
            g2d.fillOval(x + 1 - radius, y + 1 - radius, width - 2, width - 2);
        }

        Font tmpFont = g2d.getFont();
        calculateFontSize(g2d, radius / 2);

        Font font = new Font(Font.MONOSPACED, Font.PLAIN,(int)(fontSize * 0.9));
        g2d.setFont(font);

        FontMetrics metrics = g2d.getFontMetrics();
        int fontHeight = metrics.getHeight();
        int fontWidth = metrics.stringWidth(vertex.getLabel());

        g2d.setColor(Color.BLACK);
        g2d.drawString(vertex.getLabel(),
                x - fontWidth / 2,
                y + fontHeight / 4
        );
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

        double radius = start.getRadius();
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
        double ca = 0;
        double co = 0;

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

            ctrlX = pxStart + 3 * radius;
            ctrlY = pyStart + 2 * radius;

            ctrlX2 = pxStart + 3 * radius;
            ctrlY2 = pyStart - 2 * radius;

            pxStart += radius * Math.cos(Math.PI * 0.25);
            pyStart += radius * Math.sin(Math.PI * 0.25);

            pxEnd += radius * Math.cos(-Math.PI * 0.25);
            pyEnd += radius * Math.sin(-Math.PI * 0.25);
        }

        // Draw edge
        if (direction == 1 || direction == -1) {
            if (direction == 1) {
                angle = Math.atan2(pyStart - pyEnd, pxEnd - pxStart);
                double beta = angle + 0.1;
                double halfRatio = Math.sqrt(
                        Math.pow(pxEnd - pxStart, 2) +
                                 Math.pow(pyEnd - pyStart, 2)
                );
                halfRatio *= 0.5;

                ctrlX = ctrlX2 = pxStart + halfRatio * Math.cos(beta);
                ctrlY = ctrlY2 = pyStart - halfRatio * Math.sin(beta);
            } else {
                angle = Math.atan2(pyStart - pyEnd, pxEnd - pxStart);
                double beta = angle + 0.1;

                double halfRatio = Math.sqrt(
                        Math.pow(pxEnd - pxStart, 2) +
                                 Math.pow(pyStart - pyEnd, 2)
                );
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
            ca = radius * Math.cos(angle);
            co = radius * Math.sin(angle);
            curve.setCurve(ix(pxStart + ca), iy(pyStart + co),
                           ix(ctrlX), iy(ctrlY), ix(ctrlX), iy(ctrlY),
                           ix(pxEnd - ca), iy(pyEnd - co));
        }

        g2d.setStroke(new BasicStroke(edge.getStroke()));
        g2d.draw(curve);
        g2d.setStroke(new BasicStroke(1));
        //Ends draw edge

        // Draw label
        int desiredSize = ix(radius) - ix(0);
        calculateFontSize(g2d, desiredSize);
        Font tmpFont = g2d.getFont();
        Font font = new Font(Font.MONOSPACED, Font.PLAIN,(int)(fontSize * 0.7));
        g2d.setFont(font);
        if (direction == Integer.MAX_VALUE)
            g2d.drawString(edge.getLabel(), ix(pxStart + 2), iy(pyStart));
        else
            g2d.drawString(edge.getLabel(), ix(ctrlX), iy(ctrlY));

        g2d.setFont(tmpFont);
        // Ends draw label

        if (isDirected()) {
            Line2D.Double line;
            if (direction == Integer.MAX_VALUE) {
                line = new Line2D.Double(ix(ctrlX2), iy(ctrlY2),
                                         ix(pxEnd), iy(pyEnd));
                angle = Math.atan2(line.y2 - iy(ctrlY2), line.x2 - ix(ctrlX2));
            } else {
                line = new Line2D.Double(ix(pxStart + ca), iy(pyStart + co),
                        ix(pxEnd - ca), iy(pyEnd - co));
                angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            }

            int length = (ix(radius) - ix(0)) / 5;
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, length);
            arrowHead.addPoint(-(length), -(length));
            arrowHead.addPoint(length, -(length));

            AffineTransform transform = new AffineTransform();
            transform.setToIdentity();

            // Adjust arrow head to circle outline
            double tmpHypotenuse = length;
            double tmpOpposite = tmpHypotenuse * Math.sin(angle);
            double tmpAdjacent = tmpHypotenuse * Math.cos(angle);

            AffineTransform tmp = g2d.getTransform();
            transform.translate(line.x2 - tmpAdjacent, line.y2 - tmpOpposite);
            transform.rotate((angle - Math.PI / 2.0d));

            g2d.setTransform(transform);
            g2d.fill(arrowHead);
            g2d.setTransform(tmp);
        }

        g2d.setColor(tmpColor);
    }

    public void setVertexColor(int id, Color color)
    {
        graph.get(id).setBackgroundColor(color);
        repaint();
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

    public HashMap<String, Integer> getIds()
    {
        return ids;
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

    public int getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    public void toggleDrawOnlyText()
    {
        drawOnlyVertexLabel = !drawOnlyVertexLabel;
        repaint();
    }
}

