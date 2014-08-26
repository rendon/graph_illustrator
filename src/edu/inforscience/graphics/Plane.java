package edu.inforscience.graphics;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.MathContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Map.Entry;

import edu.inforscience.util.StringUtils;
import edu.inforscience.util.MathUtils;
import edu.inforscience.util.GeometryUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D;

public class Plane extends JPanel implements MouseListener,
                                             KeyListener,
                                             MouseWheelListener,
                                             MouseMotionListener {
    private HashMap<String, Vertex> graph;
    private HashMap<String, Integer> ids;

    private GraphicsContext gc;

    private Vertex startVertex;
    public static final int ACTION_DEFAULT              = 0x01;
    public static final int ACTION_CREATE_NEW_VERTEX    = 0x02;
    public static final int ACTION_DRAW_NEW_EDGE        = 0x03;
    public static final int ACTION_ERASE_OBJECT         = 0x04;
    public static final int ACTION_EDIT_NODE_LABEL      = 0x05;
    public static final int ACTION_EDIT_NEW_NODE_LABEL  = 0x06;
    public static final int ACTION_EDIT_EDGE_LABEL      = 0x07;
    public static final int ACTION_EDIT_NEW_EDGE_LABEL  = 0x08;
    public static final int ACTION_SELECTION            = 0x09;
    public static final int ACTION_DRAG_PLANE           = 0x10;
    public static final int ACTION_DRAG_VERTEX          = 0x11;
    public static final int SHAPE_CIRCLE                = 0x12;
    public static final int SHAPE_RECTANGLE             = 0x13;
    public static final int SHAPE_NONE                  = 0x14;

    private int currentAction;

    private boolean firstTime;
    private boolean showGrid;
    private boolean smoothLines;

    // True if the dragging really occured, i.e, there was a displacement.
    private boolean wasDragged;

    private boolean directed;

    private Point startDrag;
    private Point startSelection;
    private Point endSelection;
    private Vertex vertexToDrag;

    private Font currentFont;
    private int shapeType;

    private Graphics2D graphics2D;
    private final PrintStream log; // Utility
    private boolean exportingToSVG;

    // Editions since the last saving
    private int changes;
    private boolean pendingActions;

    private final JTextPane labelEditor;
    private Vertex vertexBeingEdited;
    private Edge edgeBeingEdited;

    private JToolBar mainToolBar;
    private JButton alignLeftButton;
    private JButton alignRightButton;
    private JButton alignCenterButton;
    private int textAlignment;
    private boolean alignButtonsEnabled;

    private boolean ctrlKeyStatus;

    public Plane(JToolBar toolBar)
    {
        setLayout(null);
        mainToolBar = toolBar;

        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        firstTime = true;

        graph = new HashMap<String, Vertex>();
        setShowGrid(false);
        setCurrentAction(ACTION_DEFAULT);
        setShapeType(SHAPE_CIRCLE);
        exportingToSVG = false;

        changes = 0;
        pendingActions = false;
        labelEditor = new JTextPane();
        labelEditor.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        labelEditor.addKeyListener(this);

        smoothLines = true;

        alignLeftButton = new JButton(getImage("align_left"));
        alignCenterButton = new JButton(getImage("align_center"));
        alignRightButton = new JButton(getImage("align_right"));
        ActionHandler actionHandler = new ActionHandler();
        alignLeftButton.addActionListener(actionHandler);
        alignCenterButton.addActionListener(actionHandler);
        alignRightButton.addActionListener(actionHandler);
        alignButtonsEnabled = false;

        ctrlKeyStatus = false;
        wasDragged = false;
        vertexToDrag = null;
        log = System.out;

        gc = new GraphicsContext();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        graphics2D = g2d;

        if (firstTime) {
            gc.init(getWidth(), getHeight());
            firstTime = false;
        }

        if (isShowGrid()) {
            drawGrid(g2d);
        }

        if (smoothLines) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
        }

        for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            for (Map.Entry<String, Edge> v : u.getNeighbors().entrySet()) {
                Edge e = v.getValue();
                drawEdge(g2d, e);
            }
        }

        for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
            drawVertex(g2d, entry.getValue());
        }

        if (getCurrentAction() == ACTION_DRAW_NEW_EDGE && startVertex != null) {
            double x1 = startVertex.getCenter().x();
            double y1 = startVertex.getCenter().y();
            if (getMousePosition() != null) {
                int x2 = (int) getMousePosition().getX();
                int y2 = (int) getMousePosition().getY();
                g2d.drawLine(gc.ix(x1), gc.iy(y1), x2, y2);
            }
        }

        if (getCurrentAction() == ACTION_SELECTION) {
            if (startSelection != null && endSelection != null) {
                int x1 = (int) startSelection.getX();
                int y1 = (int) startSelection.getY();
                int x2 = (int) endSelection.getX();
                int y2 = (int) endSelection.getY();
                int w = Math.abs(x2 - x1);
                int h = Math.abs(y2 - y1);
                g2d.drawRect(Math.min(x1, x2), Math.min(y2, y1), w, h);
            }
        }

        currentFont = null;
    }


    public String exportToSVG()
    {
        SVGGraphics2D g2d = new SVGGraphics2D(getWidth(), getHeight());
        exportingToSVG = true;

        if (firstTime) {
            gc.init(getWidth(), getHeight());
            firstTime = false;
        }

        g2d.setColor(Color.LIGHT_GRAY);
        if (isShowGrid()) drawGrid(g2d);

        if (smoothLines) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g2d.setStroke(new BasicStroke(3f));

        for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            for (Map.Entry<String, Edge> v : u.getNeighbors().entrySet()) {
                Edge e = v.getValue();
                drawEdge(g2d, e);
            }
        }

        for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
            drawVertex(g2d, entry.getValue());
        }

        currentFont = null;

        exportingToSVG = false;
        return g2d.getSVGDocument();
    }

    /**
     * Returns true if to draw grid or false if not.
     *
     * @return true or false, draw or not draw grid
     */
    public boolean isShowGrid()
    {
        return showGrid;
    }

    /**
     * Sets if to draw grid or not.
     *
     * @param showGrid boolean, true to draw grid, false if not.
     */
    public void setShowGrid(boolean showGrid)
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
     * Draw grid in the plane.
     *
     * @param g2d Graphics2D object
     */
    private void drawGrid(Graphics2D g2d)
    {
        Stroke dash = new BasicStroke(0.3f, BasicStroke.CAP_SQUARE,
                                      BasicStroke.JOIN_MITER, 10,
                                      new float[]{8, 4}, 0);

        Stroke tempStroke = g2d.getStroke();
        g2d.setStroke(dash);
        Color tempColor = g2d.getColor();
        g2d.setColor(Color.LIGHT_GRAY);

        double[] params = gc.computeGridParams(getWidth(), getHeight());
        double delta = params[0];
        double left = params[1];
        double right = params[2];
        double top = params[3];
        double bottom = params[4];
        double startX = params[5];
        double startY = params[6];
        for (double i = startX; i <= right; i += delta) {
            g2d.drawLine(gc.ix(i), gc.iy(top), gc.ix(i), gc.iy(bottom));
        }

        for (double i = startY; i >= bottom; i -= delta) {
            g2d.drawLine(gc.ix(left), gc.iy(i), gc.ix(right), gc.iy(i));
        }

        g2d.setColor(tempColor);
        g2d.setStroke(tempStroke);
    } // End of drawGrid()

    /* MouseListener methods. */
    @Override
    public void mouseReleased(MouseEvent event)
    {
        Point2D click = new Point2D(event.getPoint(), gc);
        int ca = getCurrentAction();

        if (ca == ACTION_DRAW_NEW_EDGE && startVertex != null) {
            for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                String id = vertex.getLabel();
                if (startVertex.getLabel().equals(id)) {
                    continue;
                }

                boolean found = false;
                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = click.distanceTo(vertex.getCenter());
                    if (distance <= entry.getValue().getRadius())
                        found = true;
                } else {
                    Polygon polygon = createPolygon(vertex);
                    if (polygon.contains(event.getPoint()))
                        found = true;
                }
                if (found) {
                    if (startVertex.getNeighbors().containsKey(id)) {
                        JOptionPane.showMessageDialog(null,
                                "An edge already exists!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    }

                    Edge edge = startVertex.addNeighbor(id, "");
                    Point2D a = startVertex.getCenter();
                    Point2D b = vertex.getCenter();
                    double dx = b.x() - a.x();
                    double dy = b.y() - a.y();
                    Point2D c = new Point2D(a.x() + 0.5 * dx, a.y() + 0.5 * dy);
                    edge.setLabelCenter(c);
                    edgeBeingEdited = edge;
                    setCurrentAction(ACTION_EDIT_NEW_EDGE_LABEL);
                    resizeLabelEditor(c, "");
                    labelEditor.setText("");
                    this.add(labelEditor);
                    labelEditor.grabFocus();

                    pendingActions = true;
                    break;
                }
            }
        } else if (ca == ACTION_SELECTION && wasDragged) {
            double x1 = Math.min(startSelection.getX(), endSelection.getX());
            double x2 = Math.max(startSelection.getX(), endSelection.getX());
            double y1 = Math.min(startSelection.getY(), endSelection.getY());
            double y2 = Math.max(startSelection.getY(), endSelection.getY());
            for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex v = entry.getValue();
                Point[] rect = createVertexRect(v);

                // Test if the current node is inside the selection rectangle
                boolean inside = true;
                for (int i = 0; i < 4; i++) {
                    double x = rect[i].getX();
                    double y = rect[i].getY();
                    if (x < x1 || x > x2 || y < y1 || y > y2) {
                        inside = false;
                        break;
                    }
                }

                if (inside) {
                    v.setSelected(true);
                } else {
                    v.setSelected(false);
                }
            }
            setCurrentAction(ACTION_DEFAULT);
        } else if (ca == ACTION_DRAG_PLANE) {
            setCurrentAction(ACTION_DEFAULT);
        } else {
            if (ca == ACTION_SELECTION || ca == ACTION_DRAG_PLANE ||
                ca == ACTION_DRAG_VERTEX) {

                for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    Point2D center = v.getCenter();
                    boolean selected = false;
                    if (getShapeType() == SHAPE_CIRCLE) {
                        if (center.distanceTo(click) <= v.getRadius()) {
                            selected = true;
                        }
                    } else {
                        Polygon polygon = createPolygon(v);
                        if (polygon.contains(event.getPoint())) {
                            selected = true;
                        }
                    }

                    int button = event.getButton();
                    boolean isRightButton = button == MouseEvent.BUTTON3;
                    boolean isLeftButton = button == MouseEvent.BUTTON1;
                    if (selected) {
                        //Use mouse's right button to flip selection in multiple
                        //selection mode (pressing Ctrl).
                        if (ctrlKeyStatus && isRightButton) {
                            v.setSelected(false);
                        } else {
                            v.setSelected(true);
                        }
                    } else if (!ctrlKeyStatus && !wasDragged && isLeftButton) {
                        v.setSelected(false);
                    }
                }

                setCurrentAction(ACTION_DEFAULT);
            }
        }

        vertexToDrag = null;
        wasDragged = false;
        startSelection = null;
        endSelection = null;

        startVertex = null;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent event) { }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        this.requestFocus();
        Point2D click = new Point2D(event.getPoint(), gc);
        int operation = getCurrentAction();
        if (event.getClickCount() == 2 && operation == ACTION_DEFAULT) {
            for (Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                boolean found = false;

                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = vertex.getCenter().distanceTo(click);
                    if (distance <= vertex.getRadius())
                        found = true;
                } else {
                    Polygon polygon = createPolygon(vertex);
                    if (polygon.contains(event.getPoint()))
                        found = true;
                }

                if (found) {
                    vertexBeingEdited = vertex;
                    setCurrentAction(ACTION_EDIT_NODE_LABEL);
                    resizeLabelEditor(vertex.getCenter(), vertex.getLabel());
                    labelEditor.setText(vertex.getLabel());
                    this.add(labelEditor);
                    labelEditor.grabFocus();
                    pendingActions = true;
                    return;
                }
            }

            for (Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                HashMap<String, Edge> set = vertex.getNeighbors();
                for (Entry<String, Edge> e : set.entrySet()) {
                    Edge edge = e.getValue();
                    Polygon polygon = createPolygon(edge.getLabelCenter(),
                                                    edge.getLabel());
                    if (polygon.contains(event.getPoint())) {
                        edgeBeingEdited = edge;
                        setCurrentAction(ACTION_EDIT_EDGE_LABEL);
                        resizeLabelEditor(edgeBeingEdited.getLabelCenter(),
                                          edge.getLabel());
                        labelEditor.setText(edge.getLabel());
                        this.add(labelEditor);
                        labelEditor.grabFocus();
                        pendingActions = true;
                        return;
                    }
                }
            }

        } else if (getCurrentAction() == ACTION_CREATE_NEW_VERTEX ||
                   getCurrentAction() == ACTION_EDIT_NEW_NODE_LABEL) {
            
            if (getCurrentAction() == ACTION_EDIT_NEW_NODE_LABEL) {
                finishPendingActions();
                setCurrentAction(ACTION_CREATE_NEW_VERTEX);
            }

            resizeLabelEditor(click, "");
            vertexBeingEdited = new Vertex("", click);
            graph.put("", vertexBeingEdited);
            labelEditor.setText("");
            this.add(labelEditor);
            labelEditor.grabFocus();
            setCurrentAction(ACTION_EDIT_NEW_NODE_LABEL);
            pendingActions = true;
        } else if (getCurrentAction() == ACTION_ERASE_OBJECT) {
            String delId = null;
            for (Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex vertex = entry.getValue();
                boolean found = false;

                if (getShapeType() == SHAPE_CIRCLE) {
                    double distance = vertex.getCenter().distanceTo(click);
                    if (distance <= vertex.getRadius())
                        found = true;
                } else {
                    Polygon polygon = createPolygon(vertex);
                    if (polygon.contains(event.getPoint()))
                        found = true;
                }

                if (found) {
                    int op = JOptionPane
                        .showConfirmDialog(null, "Are you sure?", "Confirm",
                                JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        delId = vertex.getLabel();
                    } else {
                        return;
                    }

                    break;
                }
            }

            if (delId != null) {
                for (Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    boolean test1 = v.getLabel().equals(delId);
                    boolean test2 = v.getNeighbors().containsKey(delId);
                    if (!test1 && test2) {
                        v.getNeighbors().remove(delId);
                    }
                }

                graph.remove(delId);
                changes++;
                repaint();
                return;
            }

            for (Entry<String, Vertex> entry : graph.entrySet()) {
                Vertex v = entry.getValue();
                for (Entry<String, Edge> edge : v.getNeighbors().entrySet()) {
                    String startId = edge.getValue().getStart();
                    String endId = edge.getValue().getEnd();
                    Point2D a = graph.get(startId).getCenter();
                    Point2D b = graph.get(endId).getCenter();
                    if (GeometryUtils.onSegment(a, b, click, gc)) {
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

        } else if (getCurrentAction() == ACTION_EDIT_NODE_LABEL) {
            finishPendingActions();
            setCurrentAction(ACTION_DEFAULT);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (getCurrentAction() == ACTION_EDIT_EDGE_LABEL) {
            finishPendingActions();
            setCurrentAction(ACTION_DEFAULT);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (getCurrentAction() == ACTION_EDIT_NEW_EDGE_LABEL) {
            finishPendingActions();
            setCurrentAction(ACTION_DRAW_NEW_EDGE);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) { }

    @Override
    public void mousePressed(MouseEvent event)
    {
        this.requestFocus();
        Point2D click = new Point2D(event.getPoint(), gc);
        if (event.getButton() == MouseEvent.BUTTON3) {
            if (ctrlKeyStatus) {
                for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    Point2D center = v.getCenter();
                    if (getShapeType() == SHAPE_CIRCLE) {
                        if (center.distanceTo(click) <= v.getRadius()) {
                            if (v.isSelected()) {
                                v.setSelected(false);
                                break;
                            }
                        }
                    } else {
                        Polygon polygon = createPolygon(v);
                        if (polygon.contains(event.getPoint())) {
                            if (v.isSelected()) {
                                v.setSelected(false);
                                break;
                            }
                        }
                    }
                }
            } else {
                setCurrentAction(ACTION_DRAG_PLANE);
            }
        } else if (event.getButton() == MouseEvent.BUTTON1) {
            if (getCurrentAction() == ACTION_DEFAULT) {
                wasDragged = false;
                vertexToDrag = null;
                for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    if (getShapeType() == SHAPE_CIRCLE) {
                        double distance = click.distanceTo(v.getCenter());
                        if (distance <= v.getRadius()) {
                            vertexToDrag = v;
                            break;
                        }
                    } else {
                        Polygon polygon = createPolygon(v);
                        if (polygon.contains(event.getPoint())) {
                            vertexToDrag = v;
                            break;
                        }
                    }
                }

                if (vertexToDrag != null) {
                    setCurrentAction(ACTION_DRAG_VERTEX);
                } else {
                    startSelection = event.getPoint();
                    endSelection = event.getPoint();
                    setCurrentAction(ACTION_SELECTION);
                }
            } else if (getCurrentAction() == ACTION_DRAW_NEW_EDGE ||
                       getCurrentAction() == ACTION_EDIT_NEW_EDGE_LABEL) {
                if (getCurrentAction() == ACTION_EDIT_NEW_EDGE_LABEL) {
                    finishPendingActions();
                    setCurrentAction(ACTION_DRAW_NEW_EDGE);
                }

                boolean found = false;
                for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex vertex = entry.getValue();
                    if (getShapeType() == SHAPE_CIRCLE) {
                        double distance = vertex.getCenter().distanceTo(click);
                        if (distance <= vertex.getRadius()) {
                            startVertex = vertex;
                            found = true;
                            break;
                        }
                    } else {
                        Polygon polygon = createPolygon(vertex);
                        if (polygon.contains(event.getPoint())) {
                            startVertex = entry.getValue();
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    startVertex = null;
                }
            }
        }

        startDrag = event.getPoint();
        repaint();
    }

    /* MouseMotionListener methods. */
    /**
     * Performs plane dragging.
     *
     * @param e MouseEvent with coordinates of mouse position
     */
    @Override
    public void mouseDragged(MouseEvent event)
    {
        int dx = event.getX() - (int) startDrag.getX();
        int dy = event.getY() - (int) startDrag.getY();

        // A tiny optimization
        if (dx * dx + dy * dy < 50) {
            return;
        }

        startDrag = event.getPoint();
        int ca = getCurrentAction();
        if (ca  == ACTION_DRAG_PLANE) {
                gc.setCenterX(gc.getCenterX() + dx);
                gc.setCenterY(gc.getCenterY() + dy);
        } else if (ca == ACTION_DRAG_VERTEX) {
            if (!ctrlKeyStatus) {
                double dx1 = gc.fx(dx) - gc.fx(0);
                double dy1 = gc.fy(dy) - gc.fy(0);
                double x, y;
                if (!vertexToDrag.isSelected()) {
                    x = vertexToDrag.getCenter().x();
                    y = vertexToDrag.getCenter().y();
                    vertexToDrag.setCenter(x + dx1, y + dy1);
                }

                for (Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex vertex = entry.getValue();
                    if (!vertexToDrag.isSelected()) {
                        vertex.setSelected(false);
                    } else if (vertex.isSelected()) {
                        x = vertex.getCenter().x();
                        y = vertex.getCenter().y();
                        vertex.setCenter(x + dx1, y + dy1);
                    }
                }
                changes++;
            }
        } else if (ca == ACTION_SELECTION) {
            endSelection = event.getPoint();
        }

        wasDragged = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent event) { }

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

        if (rotation > 0) {
            gc.zoomIn(x, y);
        } else {
            gc.zoomOut(x, y);
        }

        switch (getCurrentAction()) {
            case ACTION_EDIT_NODE_LABEL:
            case ACTION_EDIT_NEW_NODE_LABEL:
                resizeLabelEditor(vertexBeingEdited.getCenter(),
                                  labelEditor.getText());
                break;
            case ACTION_EDIT_EDGE_LABEL:
            case ACTION_EDIT_NEW_EDGE_LABEL:
                resizeLabelEditor(edgeBeingEdited.getLabelCenter(),
                                  labelEditor.getText());
                break;
        }
        repaint();
    }

    private void drawVertex(Graphics2D g2d, Vertex vertex)
    {
        Stroke tempStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        Color tempColor = g2d.getColor();

        if (vertex.hasLabelChanged()) {
            Point[] rect = createTextRect(vertex.getCenter(),
                                          vertex.getLabel(), true);
            Point2D c = vertex.getCenter();
            double d1 = c.distanceTo(new Point2D(rect[0], gc));
            double d2 = c.distanceTo(new Point2D(rect[1], gc));
            double d3 = c.distanceTo(new Point2D(rect[2], gc));
            double d4 = c.distanceTo(new Point2D(rect[3], gc));
            double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));
            vertex.setRadius(Math.max(max, Vertex.BASE_VERTEX_RADIUS));
            vertex.setLabelChanged(false);
        }

        int radius = gc.ix(vertex.getRadius()) - gc.ix(0);
        int width = 2 * radius;
        int height = 2 * radius;
        int x = gc.ix(vertex.getCenter().x());
        int y = gc.iy(vertex.getCenter().y());

        Font tempFont = g2d.getFont();
        calculateFontSize(g2d);
        g2d.setFont(currentFont);
        FontMetrics metrics = g2d.getFontMetrics();
        int fontHeight = metrics.getAscent() + metrics.getDescent();

        String label = StringUtils.align(vertex.getLabel(),
                vertex.getLabelAlignment());

        String[] lines = label.split("\n", -1);
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }

        int fontWidth = metrics.stringWidth(largest);
        int stringHeight = fontHeight * lines.length;

        if (getShapeType() == SHAPE_CIRCLE) {
            g2d.setColor(vertex.getBorderColor());
            g2d.drawOval(x - radius, y - radius, width, height);
            g2d.setColor(vertex.getBackgroundColor());
            g2d.fillOval(x + 1 - radius, y + 1 - radius, width - 2, width - 2);
            if (vertex.isSelected()) {
                g2d.setColor(new Color(0, 0, 255, 32));
                g2d.fillOval(x + 1 - radius, y + 1 - radius,
                             width - 2, width - 2);
            }
        } else if (getShapeType() == SHAPE_RECTANGLE) {
            int fw = fontWidth + 3 * metrics.getDescent();
            int fh = stringHeight + 3 * metrics.getDescent();
            g2d.setColor(vertex.getBorderColor());
            g2d.drawRoundRect(x - fw / 2, y - fh / 2, fw, fh, 10, 10);
            g2d.setColor(vertex.getBackgroundColor());
            g2d.fillRoundRect(x - fw / 2 + 1, y - fh / 2 + 1,
                              fw - 2, fh - 2, 10, 10);
            if (vertex.isSelected()) {
                g2d.setColor(new Color(0, 0, 255, 32));
                g2d.fillRoundRect(x - fw / 2 + 1, y - fh / 2 + 1,
                                  fw - 2, fh - 2, 10, 10);
            }
        }

        if (getCurrentAction() != ACTION_EDIT_NODE_LABEL ||
            vertex != vertexBeingEdited) {
            if (vertex.isSelected()) {
                g2d.setColor(new Color(0, 0, 255, 128));
            } else {
                g2d.setColor(Color.BLACK);
            }
            y = y - stringHeight / 2 + (fontHeight  * 3 / 4);
            x = x - fontWidth / 2;
            for (int i = 0; i < lines.length; i++) {
                g2d.drawString(lines[i], x, y);
                y += fontHeight;
            }
        }

        g2d.setFont(tempFont);
        g2d.setColor(tempColor);
        g2d.setStroke(tempStroke);
    }

    private void drawEdge(Graphics2D g2d, Edge edge)
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
        if (end.contains(start.getLabel()) && start != end) {
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

        Font tmpFont = g2d.getFont();
        calculateFontSize(g2d);
        g2d.setFont(currentFont);

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
            curve.setCurve(gc.ix(pxStart), gc.iy(pyStart), gc.ix(ctrlX),
                           gc.iy(ctrlY), gc.ix(ctrlX2), gc.iy(ctrlY2),
                           gc.ix(pxEnd), gc.iy(pyEnd));
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

                curve.setCurve(gc.ix(pxStart), gc.iy(pyStart),
                               gc.ix(ctrlX), gc.iy(ctrlY),
                               gc.ix(ctrlX), gc.iy(ctrlY),
                               gc.ix(pxEnd), gc.iy(pyEnd));
            } else if (getShapeType() == SHAPE_RECTANGLE ||
                    getShapeType() == SHAPE_NONE) {

                FontMetrics metrics = g2d.getFontMetrics();
                Vertex u = graph.get(edge.getStart());
                Vertex v = graph.get(edge.getEnd());
                Point2D startPoint = GeometryUtils.
                                     computeEndPoint(u, v, metrics, gc);
                Point2D endPoint = GeometryUtils.
                                   computeEndPoint(v, u, metrics, gc);

                pxStart = startPoint.x();
                pyStart = startPoint.y();
                pxEnd = endPoint.x();
                pyEnd = endPoint.y();
                Point2D tmp = new Point2D(pxEnd - pxStart, pyEnd - pyStart);
                double xx = tmp.x() * 0.5;
                double yy = tmp.y() * 0.5;
                ctrlX = pxStart + xx;
                ctrlY =  pyStart + yy;

                curve.setCurve(gc.ix(pxStart), gc.iy(pyStart),
                               gc.ix(ctrlX), gc.iy(ctrlY), gc.ix(ctrlX),
                               gc.iy(ctrlY), gc.ix(pxEnd), gc.iy(pyEnd));
            }
        }

        g2d.setStroke(new BasicStroke(edge.getStroke()));
        g2d.draw(curve);
        g2d.setStroke(new BasicStroke(1));
        //Ends draw edge

        // Draw label
        if (getCurrentAction() != ACTION_EDIT_EDGE_LABEL ||
            edgeBeingEdited != edge) {
            FontMetrics m = g2d.getFontMetrics();
            double width = gc.fx(m.stringWidth(edge.getLabel())) - gc.fx(0);
            double height = gc.fy(m.getAscent() + m.getDescent()) -gc.fy(0);
            width = Math.abs(width);
            height = Math.abs(height);
            if (direction == Integer.MAX_VALUE) {
                g2d.drawString(edge.getLabel(), gc.ix(pxStart + 2),
                               gc.iy(pyStart));
                edge.setLabelCenter(new Point2D(pxStart + 0.5 * width,
                                                pyStart + 0.5 * height));
            } else {
                Point2D c = new Point2D(ctrlX, ctrlY + 0.5 * height);
                int x = gc.ix(c.x());
                int y = gc.iy(c.y());
                int fontHeight = m.getAscent() + m.getDescent();
                int fontWidth = m.stringWidth(edge.getLabel());
                y = y - fontHeight / 2 + (fontHeight * 3 / 4);
                x = x - fontWidth / 2;
                g2d.drawString(edge.getLabel(), x, y);
                edge.setLabelCenter(c);
                if (!exportingToSVG && edge.getLabel().isEmpty()) {
                    g2d.setColor(Color.GRAY);
                    g2d.fillOval(gc.ix(c.x()), gc.iy(c.y()), 4, 4);
                    g2d.setColor(Color.BLACK);
                }
            }

            g2d.setFont(tmpFont);
        }
        // Ends draw label

        Line2D.Double line;
        if (direction == Integer.MAX_VALUE) {
            line = new Line2D.Double(gc.ix(ctrlX2), gc.iy(ctrlY2),
                                     gc.ix(pxEnd), gc.iy(pyEnd));
            angle = Math.atan2(line.y2 - gc.iy(ctrlY2),
                               line.x2 - gc.ix(ctrlX2));
        } else {
            line = new Line2D.Double(gc.ix(pxStart), gc.iy(pyStart),
                                     gc.ix(pxEnd), gc.iy(pyEnd));
            angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        }

        int length = (gc.ix(endRadius) - gc.ix(0)) / 8;
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

        g2d.setColor(tmpColor);
    }

    public void setGraph(HashMap<String, Vertex> graph)
    {
        this.graph = graph;
        repaint();
    }

    public HashMap<String, Vertex> getGraph()
    {
        return graph;
    }

    public void setIds(HashMap<String, Integer> ids)
    {
        this.ids = ids;
    }

    private int getCurrentAction()
    {
        return currentAction;
    }

    public void setCurrentAction(int currentAction)
    {
        if (pendingActions) {
            finishPendingActions();
        }

        this.currentAction = currentAction;

        if (currentAction == ACTION_EDIT_NEW_NODE_LABEL ||
            currentAction == ACTION_EDIT_NODE_LABEL) {
            mainToolBar.add(alignLeftButton);
            mainToolBar.add(alignCenterButton);
            mainToolBar.add(alignRightButton);
            alignButtonsEnabled = true;
        } else {
            if (alignButtonsEnabled) {
                mainToolBar.remove(alignRightButton);
                mainToolBar.remove(alignCenterButton);
                mainToolBar.remove(alignLeftButton);
                mainToolBar.updateUI();
                alignButtonsEnabled = false;
            }
        }
    }

    public int getShapeType()
    {
        return shapeType;
    }

    public void setShapeType(int shapeType)
    {
        this.shapeType = shapeType;
    }

    public Polygon createPolygon(Vertex v) {
        String label = StringUtils.align(v.getLabel(), v.getLabelAlignment());
        return createPolygon(v.getCenter(), label);
    }

    public Polygon createPolygon(Point2D center, String label)
    {
        Polygon polygon = new Polygon();
        Point[] rect = createTextRect(center, label, true);
        polygon.addPoint((int) rect[0].getX(), (int) rect[0].getY());
        polygon.addPoint((int) rect[1].getX(), (int) rect[1].getY());
        polygon.addPoint((int) rect[2].getX(), (int) rect[2].getY());
        polygon.addPoint((int) rect[3].getX(), (int) rect[3].getY());

        return polygon;
    }

    private Point[] createTextRect(Point2D center,
                                  String label,
                                  boolean withPadding)
    {
        int x2 = gc.ix(center.x());
        int y2 = gc.iy(center.y());

        calculateFontSize(graphics2D);
        graphics2D.setFont(currentFont);
        FontMetrics metrics = graphics2D.getFontMetrics();

        String[] lines = label.split("\n", -1);
        String largest = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > largest.length()) {
                largest = lines[i];
            }
        }

        int fw = metrics.stringWidth(largest);
        int fh = (metrics.getAscent() + metrics.getDescent()) * lines.length;
        int pad = withPadding ? 3 * metrics.getDescent() : 0;

        Point[] rect = new Point[4];
        rect[0] = new Point(x2 - (fw / 2 + pad), y2 - (fh / 2 + pad));
        rect[1] = new Point(x2 + (fw / 2 + pad), y2 - (fh / 2 + pad));
        rect[2] = new Point(x2 + (fw / 2 + pad), y2 + (fh / 2 + pad));
        rect[3] = new Point(x2 - (fw / 2 + pad), y2 + (fh / 2 + pad));

        return rect;
    }

    private Point[] createVertexRect(Vertex v)
    {
        if (getShapeType() == SHAPE_RECTANGLE) {
            return createTextRect(v.getCenter(), v.getLabel(), true);
        } else {
            Point[] rect = new Point[4];
            int x = gc.ix(v.getCenter().x());
            int y = gc.iy(v.getCenter().y());
            int r = gc.ix(v.getRadius()) - gc.ix(0);
            rect[0] = new Point(x - r, y - r);
            rect[1] = new Point(x + r, y - r);
            rect[2] = new Point(x + r, y + r);
            rect[3] = new Point(x - r, y + r);

            return rect;
        }
    }


    public boolean hasChanges()
    {
        return changes > 0;
    }

    public void setChanges(int value)
    {
        changes = value;
    }

    public void toggleSmoothLines()
    {
        smoothLines = !smoothLines;
        repaint();
    }


    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton) e.getSource();
            if (source == alignLeftButton) {
                setTextAlignment(StyleConstants.ALIGN_LEFT);
            } else if (source == alignCenterButton) {
                setTextAlignment(StyleConstants.ALIGN_CENTER);
            } else if (source == alignRightButton) {
                setTextAlignment(StyleConstants.ALIGN_RIGHT);
            }
        }

        private void setTextAlignment(int newAlignment)
        {
            String text = labelEditor.getText();
            StyledDocument document = new DefaultStyledDocument();
            Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
            StyleConstants.setAlignment(defaultStyle, newAlignment);
            labelEditor.setDocument(document);
            labelEditor.setText(text);
            textAlignment = newAlignment;
        }
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlKeyStatus = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)  {
        switch (getCurrentAction()) {
            case ACTION_EDIT_NODE_LABEL:
            case ACTION_EDIT_NEW_NODE_LABEL:
                resizeLabelEditor(vertexBeingEdited.getCenter(),
                                  labelEditor.getText());
                break;
            case ACTION_EDIT_EDGE_LABEL:
            case ACTION_EDIT_NEW_EDGE_LABEL:
                resizeLabelEditor(edgeBeingEdited.getLabelCenter(),
                                  labelEditor.getText());
                break;
        }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlKeyStatus = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }


    /* -------------------- Private methods. --------------------*/
    private void finishNewNodeLabelEditing()
    {
        String label = labelEditor.getText().trim();
        if (!label.isEmpty()) {
            if (graph.containsKey(label)) {
                JOptionPane.showMessageDialog(null,
                        "A node with the same label already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                vertexBeingEdited.setLabel(label);
                vertexBeingEdited.setLabelChanged(true);
                vertexBeingEdited.setLabelAlignment(textAlignment);
                graph.put(label, vertexBeingEdited);
                changes++;
            }
        }
        graph.remove("");

        labelEditor.setText("");
        this.remove(labelEditor);
    }

    private void finishNodeLabelEditing()
    {
        String oldLabel = vertexBeingEdited.getLabel();
        String newLabel = labelEditor.getText().trim();
        int oldAlignment = vertexBeingEdited.getLabelAlignment();
        if (oldAlignment != textAlignment) {
            vertexBeingEdited.setLabelAlignment(textAlignment);
        }

        if (!oldLabel.equals(newLabel) && !newLabel.isEmpty()) {
            Vertex vertex = graph.get(newLabel);
            if (vertex != null && !vertex.getLabel().equals(oldLabel)) {
                JOptionPane.showMessageDialog(null,
                        "A node with the same label already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                HashMap<String, Edge> neighbors =
                                            vertexBeingEdited.getNeighbors();
                for (Map.Entry<String, Edge> e : neighbors.entrySet()) {
                    e.getValue().setStart(newLabel);
                }

                for (Map.Entry<String, Vertex> entry : graph.entrySet()) {
                    Vertex v = entry.getValue();
                    if (!v.getLabel().equals(oldLabel)) {
                        Edge edge = v.getNeighbors().remove(oldLabel);
                        if (edge != null) {
                            edge.setEnd(newLabel);
                            v.getNeighbors().put(newLabel, edge);
                        }
                    }
                }

                graph.remove(oldLabel);
                vertexBeingEdited.setLabel(newLabel);
                vertexBeingEdited.setLabelChanged(true);
                graph.put(newLabel, vertexBeingEdited);
                changes++;
            }
        }
        labelEditor.setText("");
        this.remove(labelEditor);
    }

    private void finishEdgeLabelEditing()
    {
        String oldLabel = edgeBeingEdited.getLabel();
        String newLabel = labelEditor.getText();
        if (!oldLabel.equals(newLabel)) {
            edgeBeingEdited.setLabel(newLabel);
            changes++;
        }
        labelEditor.setText("");
        this.remove(labelEditor);
    }

    private void finishNewEdgeLabelEditing()
    {
        edgeBeingEdited.setLabel(labelEditor.getText());
        labelEditor.setText("");
        this.remove(labelEditor);
    }


    private void finishPendingActions()
    {
        switch (getCurrentAction()) {
            case ACTION_EDIT_NEW_NODE_LABEL:
                finishNewNodeLabelEditing();
                break;

            case ACTION_EDIT_NEW_EDGE_LABEL:
                finishNewEdgeLabelEditing();
                break;

            case ACTION_EDIT_NODE_LABEL:
                finishNodeLabelEditing();
                break;

            case ACTION_EDIT_EDGE_LABEL:
                finishEdgeLabelEditing();
                break;
        }

        pendingActions = false;
        repaint();
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    private void calculateFontSize(Graphics2D g2d)
    {
        int desiredHeight = gc.ix((int) Vertex.BASE_VERTEX_RADIUS) - gc.ix(0);
        if (currentFont == null) {
            // Search a font size(in points) such that its height in pixels
            // best approximates radius
            int bestSuited = 2;
            int low = 2, high = 512;
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

            currentFont = new Font(Font.MONOSPACED, Font.PLAIN, bestSuited);
        }
    }

    private void resizeLabelEditor(Point2D center, String text)
    {
        // prevent the TextPane to disappear in case of empty string
        text += " ";

        Point[] rect = createTextRect(center, text, false);
        labelEditor.setFont(currentFont);
        int a = (int) rect[0].getX();
        int b = (int) rect[0].getY();
        int w = (int) (rect[1].getX() - rect[0].getX());
        int h = (int) (rect[3].getY() - rect[0].getY());
        labelEditor.setBounds(a, b, w, h);
    }
}

