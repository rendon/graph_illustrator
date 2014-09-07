package edu.inforscience;
import edu.inforscience.graphics.*;
import edu.inforscience.graphics.Point2D;
import edu.inforscience.graph.*;
import edu.inforscience.util.Utils;
import edu.inforscience.util.MathUtils;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

@SuppressWarnings("serial")
public class Main extends JFrame {
    private final Plane plane;
    private final PrintStream log;
    private static final int MAX = 200;
    private final JButton pointerButton;
    private final JButton openButton;
    private final JButton reloadButton;
    private final JButton saveButton;
    private final JButton saveAsButton;
    private final JButton exportSvgButton;
    private final JButton newNodeButton;
    private final JButton newDirectedEdgeButton;
    private final JButton newUndirectedEdgeButton;
    private final JButton eraserButton;
    private final JButton deleteButton;
    private final JButton showGridButton;
    private final JButton smoothLinesButton;

    private final JButton shapeCircleButton;
    private final JButton shapeRectangleButton;
    private final JButton shapeNoneButton;
    private final JButton quitButton;

    private final JButton vertexFgColor;
    private final JButton vertexBgColorButton;
    private final JButton vertexBorderColorButton;

    private final JButton highlightButton;;

    private final JButton edgeFgColorButton;
    private final JButton edgeStrokeColorButton;

    private final JButton zoomInButton;
    private final JButton zoomOutButton;
    private final JButton zoomResetButton;
    private final JToolBar mainToolBar;

    private static final int READ_EDGE_INFO = 1;
    private static final int READ_VERTEX_INFO = 2;

    private String filePath;

    public Main()
    {
        super("Graph Illustrator : Untitled");
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        log = System.out;

        ActionHandler actionHandler = new ActionHandler();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(actionHandler);

        plane = new Plane(this);
        openButton = new JButton(getImage("open"));
        openButton.addActionListener(actionHandler);
        openButton.setToolTipText("Open file");

        reloadButton = new JButton(getImage("reload"));
        reloadButton.addActionListener(actionHandler);
        reloadButton.setToolTipText("Reload file");

        saveButton = new JButton(getImage("save"));
        saveButton.addActionListener(actionHandler);
        saveButton.setToolTipText("Save");

        saveAsButton = new JButton(getImage("saveAs"));
        saveAsButton.addActionListener(actionHandler);
        saveAsButton.setToolTipText("Save as");

        exportSvgButton = new JButton(getImage("svg"));
        exportSvgButton.addActionListener(actionHandler);
        exportSvgButton.setToolTipText("Export to SVG");

        quitButton = new JButton(getImage("quit"));
        quitButton.addActionListener(actionHandler);
        quitButton.setToolTipText("Quit");

        newNodeButton = new JButton(getImage("node"));
        newNodeButton.addActionListener(actionHandler);
        newNodeButton.setToolTipText("New node");

        newDirectedEdgeButton = new JButton(getImage("directedEdge"));
        newDirectedEdgeButton.addActionListener(actionHandler);
        newDirectedEdgeButton.setToolTipText("New directed edge");

        newUndirectedEdgeButton = new JButton(getImage("undirectedEdge"));
        newUndirectedEdgeButton.addActionListener(actionHandler);
        newUndirectedEdgeButton.setToolTipText("New undirected edge");

        pointerButton = new JButton(getImage("pointer"));
        pointerButton.addActionListener(actionHandler);
        pointerButton.setToolTipText("Pointer mode");

        eraserButton = new JButton(getImage("eraser"));
        eraserButton.addActionListener(actionHandler);
        eraserButton.setToolTipText("Eraser");

        deleteButton = new JButton(getImage("delete"));
        deleteButton.addActionListener(actionHandler);
        deleteButton.setToolTipText("Delete selected nodes");

        shapeCircleButton = new JButton(getImage("circle"));
        shapeCircleButton.addActionListener(actionHandler);
        shapeCircleButton.setToolTipText("Use circles for nodes");

        shapeRectangleButton = new JButton(getImage("rectangle"));
        shapeRectangleButton.addActionListener(actionHandler);
        shapeRectangleButton.setToolTipText("Use rectangles for nodes");

        shapeNoneButton = new JButton(getImage("none"));
        shapeNoneButton.addActionListener(actionHandler);
        shapeNoneButton.setToolTipText("Remove shape from nodes");

        showGridButton = new JButton(getImage("showGrid"));
        showGridButton.addActionListener(actionHandler);
        showGridButton.setToolTipText("Toggle grid");

        smoothLinesButton = new JButton(getImage("antialias"));
        smoothLinesButton.addActionListener(actionHandler);
        smoothLinesButton.setToolTipText("Toggle smooth lines");

        vertexFgColor = new JButton(getImage("vertexForegroundColor"));
        vertexFgColor.addActionListener(actionHandler);
        vertexFgColor.setToolTipText("Set vertex label color");

        vertexBgColorButton = new JButton(getImage("vertexBackgroundColor"));
        vertexBgColorButton.addActionListener(actionHandler);
        vertexBgColorButton.setToolTipText("Set vertex's background color");

        vertexBorderColorButton = new JButton(getImage("vertexBorderColor"));
        vertexBorderColorButton.addActionListener(actionHandler);
        vertexBorderColorButton.setToolTipText("Set vertex's border color");

        edgeFgColorButton = new JButton(getImage("edgeForegroundColor"));
        edgeFgColorButton.addActionListener(actionHandler);
        edgeFgColorButton.setToolTipText("Set edge's label color");

        edgeStrokeColorButton = new JButton(getImage("edgeStrokeColor"));
        edgeStrokeColorButton.addActionListener(actionHandler);
        edgeStrokeColorButton.setToolTipText("Set edge's stroke color");


        highlightButton = new JButton(getImage("highlight"));
        highlightButton.addActionListener(actionHandler);
        highlightButton.setToolTipText("Highlight edge");

        zoomInButton = new JButton(getImage("zoomIn"));
        zoomInButton.addActionListener(actionHandler);
        zoomInButton.setToolTipText("Zoom in");

        zoomOutButton = new JButton(getImage("zoomOut"));
        zoomOutButton.addActionListener(actionHandler);
        zoomOutButton.setToolTipText("Zoom out");

        zoomResetButton = new JButton(getImage("zoomReset"));
        zoomResetButton.addActionListener(actionHandler);
        zoomResetButton.setToolTipText("Zoom reset");

        mainToolBar = new JToolBar();
        mainToolBar.add(openButton);
        mainToolBar.add(saveButton);
        mainToolBar.add(saveAsButton);
        mainToolBar.add(reloadButton);
        mainToolBar.add(exportSvgButton);
        mainToolBar.addSeparator();
        mainToolBar.add(pointerButton);
        mainToolBar.add(newNodeButton);
        mainToolBar.add(newDirectedEdgeButton);
        mainToolBar.add(newUndirectedEdgeButton);
        mainToolBar.add(eraserButton);
        mainToolBar.add(deleteButton);
        mainToolBar.addSeparator();
        mainToolBar.add(shapeCircleButton);
        mainToolBar.add(shapeRectangleButton);
        mainToolBar.add(shapeNoneButton);
        mainToolBar.add(showGridButton);
        mainToolBar.add(smoothLinesButton);
        mainToolBar.addSeparator();
        mainToolBar.add(vertexFgColor);
        mainToolBar.add(vertexBgColorButton);
        mainToolBar.add(vertexBorderColorButton);
        mainToolBar.add(highlightButton);
        mainToolBar.add(edgeFgColorButton);
        mainToolBar.add(edgeStrokeColorButton);
        mainToolBar.addSeparator();
        mainToolBar.add(zoomInButton);
        mainToolBar.add(zoomOutButton);
        mainToolBar.add(zoomResetButton);
        mainToolBar.addSeparator();
        mainToolBar.add(quitButton);
        add(mainToolBar, BorderLayout.NORTH);
        add(plane, BorderLayout.CENTER);

        revalidate();
        plane.requestFocus();
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    public static void main(String[] args) throws Exception
    {
        Main test = new Main();
    }

    private void readSimpleGraph() throws IOException
    {
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        Graph graph = new Graph();
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.trim().split("\\s");
            if (tokens.length < 2) {
                continue;
            }

            String u = tokens[0];
            String v = tokens[1];
            String label = (tokens.length == 3) ? tokens[2] : "";
            Integer ku = null, kv = null;
            try {
                if (!graph.containsVertexWithLabel(u)) {
                    ku = graph.addVertex(new Vertex(u));
                } else {
                    ku = graph.getVertexWithLabel(u).getKey();
                }

                if (!graph.containsVertexWithLabel(v)) {
                    kv = graph.addVertex(new Vertex(v));
                } else {
                    kv = graph.getVertexWithLabel(v).getKey();
                }
            } catch (InvalidOperationException ioe) {

            }

            // Edges are directed
            Vertex vertexU = graph.getVertex(ku);
            vertexU.addNeighbor(kv, label);
        }

        reader.close();

        plane.finishPendingActions();
        plane.setChanges(0);
        plane.setGraph(graph);
    }

    private void readGraph() throws InvalidFormatException, IOException
    {
        Graph graph = new Graph();
        HashMap<String, Integer> labelKeys = new HashMap<String, Integer>();
        HashSet<Integer> keys = new HashSet<Integer>();
        HashSet<String> labels = new HashSet<String>();

        // Reassign keys to preserve the key space and avoid overflow.
        HashMap<Integer, Integer> newKeys = new HashMap<Integer, Integer>();
        Integer nextKey = 1;

        File input = new File(filePath);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(input);
        if (root.path("Graph") == null) {
            throw new InvalidFormatException("Invalid input file", null, null);
        }

        Iterator<JsonNode> V = null;
        if (root.path("Graph").get("Vertices") != null) {
            V = root.path("Graph").get("Vertices").elements();
        } else {
            throw new InvalidFormatException("Invalid input file", null, null);
        }

        while (V != null && V.hasNext()) {
            JsonNode v = V.next();
            // Mandatory properties
            if (v.get("key") == null || v.get("label") == null) {
                throw new InvalidFormatException(
                            "Vertex's key and label are not optional.",
                            null, null, null
                        );
            }

            Integer key = v.path("key").asInt();
            String label = v.path("label").textValue();

            if (keys.contains(key) || labels.contains(label)) {
                // Repeated node key or node label
                throw new InvalidFormatException(
                            "Repeated vertex key and/or label.",
                            null, null, null
                        );
            }


            // Reassign keys to preserve the key space and avoid overflow.
            newKeys.put(key, nextKey);
            Vertex vertex = new Vertex(label);
            vertex.setKey(nextKey);
            labelKeys.put(label, nextKey);
            nextKey++;

            // Optional properties
            
            if (v.get("labelAlignment") != null) {
                String alignment = v.get("labelAlignment").textValue();
                int la = StyleConstants.ALIGN_LEFT;
                if ("right".equals(alignment.toLowerCase())) {
                    la = StyleConstants.ALIGN_RIGHT;
                } else if ("center".equals(alignment.toLowerCase())) {
                    la = StyleConstants.ALIGN_CENTER;
                }

                vertex.setLabelAlignment(la);
            }

            if (v.get("center") != null) {
                JsonNode point = v.get("center");
                if (point.get("x") != null && point.get("y") != null) {
                    double x = point.get("x").asDouble();
                    double y = point.get("y").asDouble();
                    vertex.setCenter(new Point2D(x, y));
                }
            }

            if (v.get("radius") != null) {
                Double radius = v.get("radius").asDouble();
                vertex.setRadius(radius);
            }

            if (v.get("foregroundColor") != null) {
                String hex = v.get("foregroundColor").textValue();
                Color foregroundColor = Utils.decode(hex);
                vertex.setForegroundColor(foregroundColor);
            }

            if (v.get("backgroundColor") != null) {
                String hex = v.get("backgroundColor").textValue();
                Color backgroundColor = Utils.decode(hex);
                vertex.setBackgroundColor(backgroundColor);
            }

            if (v.get("borderColor") != null) {
                String hex = v.get("borderColor").textValue();
                Color borderColor = Utils.decode(hex);
                vertex.setBorderColor(borderColor);
            }

            try {
                graph.addVertex(newKeys.get(key), vertex);
            } catch (InvalidOperationException ioe) {

            }

            keys.add(key);
            labels.add(label);
        }

        Iterator<JsonNode> E = null;
        if (root.path("Graph").get("Edges") != null) {
            E = root.path("Graph").get("Edges").elements();
        }

        while (E != null && E.hasNext()) {
            JsonNode e = E.next();
            if (e.get("start") == null || e.get("end") == null) {
                throw new InvalidFormatException(
                            "Edge's start and end are not optional.",
                            null, null, null
                        );
            }

            Integer start = e.get("start").asInt();
            Integer end = e.get("end").asInt();

            if (!keys.contains(start) || !keys.contains(end)) {
                throw new InvalidFormatException(
                            "Edge's start and end must exists in the graph.",
                            null, null, null
                        );
            }

            start = newKeys.get(start);
            end = newKeys.get(end);
            Edge edge = new Edge(start, end, "");

            if (e.get("label") != null) {
                String label = e.get("label").textValue();
                edge.setLabel(label);
            }

            if (e.get("foregroundColor") != null) {
                String hex = e.get("foregroundColor").textValue();
                Color foregroundColor = Utils.decode(hex);
                edge.setForegroundColor(foregroundColor);
            }

            if (e.get("strokeColor") != null) {
                String hex = e.get("strokeColor").textValue();
                Color strokeColor = Utils.decode(hex);
                edge.setStrokeColor(strokeColor);
            }

            if (e.get("directed") != null) {
                edge.setIsDirected(e.get("directed").asBoolean());
            }

            if (e.get("backEdge") != null) {
                edge.setIsBackEdge(e.get("backEdge").asBoolean());
            }

            if (e.get("highlighted") != null) {
                edge.setHighlighted(e.get("highlighted").asBoolean());
            }

            // Not supported yet.
            if (e.get("center") != null) {
                JsonNode point = e.get("center");
                if (point.get("x") != null && point.get("y") != null) {
                    double x = point.get("x").asDouble();
                    double y = point.get("y").asDouble();
                    edge.setLabelCenter(new Point2D(x, y));
                }
            }

            graph.getVertex(start).addNeighbor(edge);
        }

        plane.finishPendingActions();
        plane.resetZoom();
        graph.setNextKey(nextKey);
        plane.setGraph(graph);
    }

    private class ActionHandler implements ActionListener, WindowListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            plane.finishPendingActions();
            if (source == openButton) {
                if (open()) {
                    plane.setChanges(0);
                    File file = new File(filePath);
                    Main.this.setTitle("Graph Illustrator : " + file.getName());
                }
            } else if (source == saveButton) {
                if (save()) {
                    plane.setChanges(0);
                    File file = new File(filePath);
                    Main.this.setTitle("Graph Illustrator : " + file.getName());
                }
            } else if (source == saveAsButton) {
                if (saveAs()) {
                    plane.setChanges(0);
                    File file = new File(filePath);
                    Main.this.setTitle("Graph Illustrator : " + file.getName());
                }
            } else if (source == reloadButton) {
                if (reload()) {
                    plane.setChanges(0);
                }
            } else if (source == exportSvgButton) {
                exportToSvg();
            } else if (source == newNodeButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setCurrentAction(Plane.ACTION_CREATE_NEW_VERTEX);
            } else if (source == newDirectedEdgeButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setEdgeType(Edge.EDGE_TYPE_DIRECTED);
                plane.setCurrentAction(Plane.ACTION_DRAW_NEW_EDGE);
            } else if (source == newUndirectedEdgeButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setEdgeType(Edge.EDGE_TYPE_UNDIRECTED);
                plane.setCurrentAction(Plane.ACTION_DRAW_NEW_EDGE);
            } else if (source == pointerButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                plane.setCursor(c);
                plane.setCurrentAction(Plane.ACTION_DEFAULT);
            } else if (source == eraserButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setCurrentAction(Plane.ACTION_ERASE_OBJECT);
            } else if (source == deleteButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                plane.setCursor(c);
                plane.setCurrentAction(Plane.ACTION_DEFAULT);
                plane.deleteSelectedVertices();
            } else if (source == showGridButton) {
                plane.toggleShowGrid();
            } else if (source == smoothLinesButton) {
                plane.toggleSmoothLines();
            } else if (source == shapeCircleButton) {
                plane.setShapeType(Plane.SHAPE_CIRCLE);
                plane.repaint();
            } else if (source == shapeRectangleButton) {
                plane.setShapeType(Plane.SHAPE_RECTANGLE);
                plane.repaint();
            } else if (source == shapeNoneButton) {
                plane.setShapeType(Plane.SHAPE_NONE);
                plane.repaint();
            } else if (source == quitButton) {
                windowClosing(null);
            } else if (source == vertexFgColor ||
                       source == vertexBgColorButton ||
                       source == vertexBorderColorButton) {
                Color color = JColorChooser.showDialog(
                            Main.this,
                            "Choose a color",
                            null
                        );

                if (color != null) {
                    if (source == vertexFgColor) {
                        plane.setVertexForegroundColor(color);
                    } else if (source == vertexBgColorButton) {
                        plane.setVertexBackgroundColor(color);
                    } else if (source == vertexBorderColorButton) {
                        plane.setVertexBorderColor(color);
                    }
                }
            } else if (source == edgeFgColorButton ||
                       source == edgeStrokeColorButton) {
                Color color = JColorChooser.showDialog(
                            Main.this,
                            "Choose a color",
                            null
                        );

                if (color != null) {
                    if (source == edgeFgColorButton) {
                        plane.setEdgeForegroundColor(color);
                    } else if (source == edgeStrokeColorButton) {
                        plane.setEdgeStrokeColor(color);
                    }
                }
            } else if (source == highlightButton) {
                plane.toggleEdgeHighlight();
            } else if (source == zoomInButton) {
               plane.zoomIn();
            } else if (source == zoomOutButton) {
               plane.zoomOut();
            } else if (source == zoomResetButton) {
               plane.resetZoom();
            }

           plane.requestFocus();
        }

        
        @Override
        public void windowClosing(WindowEvent e)
        {
            if (plane.hasChanges()) { 
                int op = JOptionPane
                    .showConfirmDialog(null, "Save changes?", "Save?",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    save();
                } 

                if (op != JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }

        @Override
        public void windowDeactivated(WindowEvent e) { }

        @Override
        public void windowActivated(WindowEvent e) { }

        @Override
        public void windowDeiconified(WindowEvent e) { }

        @Override
        public void windowIconified(WindowEvent e) { }

        @Override
        public void windowClosed(WindowEvent e) { }

        @Override
        public void windowOpened(WindowEvent e) { }
    }

    private boolean open()
    {
        if (plane.hasChanges()) {
            int op = JOptionPane.showConfirmDialog(
                            null,
                            "Do you want to save changes " + 
                            "before opening a new file?", "Save?",
                            JOptionPane.YES_NO_OPTION
                        );
            if (op == JOptionPane.YES_OPTION) {
                save();
            }
        }

        JFileChooser fc = new JFileChooser();
        FileFilter gi, sgi;
        gi = new FileNameExtensionFilter("Graph Illustrator", "gi");
        sgi = new FileNameExtensionFilter("Simple Graph Illustrator", "sgi");

        fc.addChoosableFileFilter(gi);
        fc.addChoosableFileFilter(sgi);
        fc.setFileFilter(gi);

        int code = fc.showOpenDialog(null);
        if (code == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String tempFilePath = filePath;
                filePath = file.getAbsolutePath();
                if (filePath.toLowerCase().endsWith(".gi")) {
                    readGraph();
                } else if (filePath.toLowerCase().endsWith(".sgi")) {
                    readSimpleGraph();
                } else {
                    JOptionPane.showMessageDialog(
                                    null, "Uknown file type", "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
                    filePath = tempFilePath;
                    return false;
                }

                plane.updateUI();
                return true;
            } catch (InvalidFormatException ife) {
                JOptionPane.showMessageDialog(
                                null,
                                "Parse error: " + ife.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE
                            );
            } catch (IOException ioe) {
                //ioe.printStackTrace();
                JOptionPane.showMessageDialog(
                                null, "Couldn't open file", "Error",
                                JOptionPane.ERROR_MESSAGE
                            );
            }
        }

        return false;
    }

    private String chooseSaveFile()
    {
        JFileChooser fc = new JFileChooser();
        FileFilter gi, sgi;
        gi = new FileNameExtensionFilter("Graph Illustrator", "gi");
        sgi = new FileNameExtensionFilter("Simple Graph Illustrator", "sgi");
        fc.addChoosableFileFilter(gi);
        fc.addChoosableFileFilter(sgi);
        fc.setFileFilter(gi);

        int code = fc.showSaveDialog(null);
        String fn = null;
        if (code == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fn = file.getAbsolutePath();

            FileFilter selectedFilter = fc.getFileFilter();

            if (selectedFilter == gi) {
                if (!fn.toLowerCase().endsWith(".gi")) {
                    fn += ".gi";
                }
            }

            if (selectedFilter == sgi) {
                if (!fn.toLowerCase().endsWith(".sgi")) {
                    fn += ".sgi";
                }
            }

            file = new File(fn);
            if (file.exists()) {
                int op = JOptionPane.showConfirmDialog(
                            null,
                            "File already exists. Override?",
                            "Override?",
                            JOptionPane.YES_NO_OPTION
                        );

                if (op != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
        }

        return fn;
    }

    private boolean save()
    {
        if (filePath == null) {
            filePath = chooseSaveFile();
            if (filePath == null) {
                return false;
            }
        }

        try {
            if (filePath.toLowerCase().endsWith(".gi")) {
                saveToGi();
            } else if (filePath.toLowerCase().endsWith(".sgi")) {
                saveToSgi();
            }
            return true;
        } catch (JsonGenerationException ge) {
            JOptionPane.showMessageDialog(
                            null,
                            "Generation error: " + ge.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE
                        );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return false;
    }

    private boolean saveAs()
    {
        if (filePath == null) {
            return save();
        } else {
            String newFileName = chooseSaveFile();
            if (newFileName == null) {
                return false;
            } else {
                filePath = newFileName;
                return save();
            }
        }
    }

    private boolean reload()
    {
        if (filePath != null) {
            int op = JOptionPane.showConfirmDialog(
                            null,
                            "WARNING: All unsaved changes will be lost! " +
                            "Are you sure?",
                            "Confirmation",
                            JOptionPane.YES_NO_OPTION
                        );
            if (op != JOptionPane.YES_OPTION) {
                return false;
            }

            try {
                if (filePath.toLowerCase().endsWith(".gi")) {
                    readGraph();
                } else {
                    readSimpleGraph();
                }
                plane.updateUI();
                return true;
            } catch (InvalidFormatException ife) {
                JOptionPane.showMessageDialog(
                                null,
                                "Parse error: " + ife.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE
                            );
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(
                                null,
                                "Couldn't open file",
                                "Error", JOptionPane.ERROR_MESSAGE
                            );
            }
        }

        return false;
    }

    private boolean exportToSvg()
    {
        JFileChooser fc = new JFileChooser();
        FileFilter f;
        f = new FileNameExtensionFilter("Scalable Vector Graphics",
                "svg");
        fc.addChoosableFileFilter(f);
        fc.setFileFilter(f);

        int code = fc.showSaveDialog(null);
        if (code == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".svg"))
                path += ".svg";

            file = new File(path);
            try {
                if (file.exists()) {
                    int op = JOptionPane.showConfirmDialog(null,
                            "File already exists. Override?",
                            "Override?",
                            JOptionPane.YES_NO_OPTION);
                    if (op != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
                saveToSvg(file);
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return false;
    }

    private void saveToGi() throws JsonGenerationException, IOException
    {
        Graph graph = plane.getGraph();
        JsonFactory factory = new JsonFactory();
        File output = new File(filePath);
        JsonGenerator generator = factory.createGenerator(
                    output,
                    JsonEncoding.UTF8
                );
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
        generator.writeStartObject();
        generator.writeFieldName("Graph");
        generator.writeStartObject();
        generator.writeFieldName("Vertices");
        generator.writeStartArray();
        for (Vertex v : graph.vertices()) {
            String hexLabelColor = Utils.encode(v.getForegroundColor());
            String hexBorderColor = Utils.encode(v.getBorderColor());
            String hexBackgroundColor = Utils.encode(v.getBackgroundColor());
            generator.writeStartObject();
            generator.writeNumberField("key", v.getKey());
            generator.writeStringField("label", v.getLabel());

            String alignment = "left";
            int la = v.getLabelAlignment();
            if (la == StyleConstants.ALIGN_CENTER) {
                alignment = "center";
            } else if (la == StyleConstants.ALIGN_RIGHT) {
                alignment = "right";
            }
            generator.writeStringField("labelAlignment", alignment);

            double r = MathUtils.round(v.getRadius(), 6);
            generator.writeNumberField("radius", r);

            generator.writeStringField("foregroundColor", hexLabelColor);
            generator.writeStringField("backgroundColor", hexBackgroundColor);
            generator.writeStringField("borderColor", hexBorderColor);

            double x = MathUtils.round(v.getCenter().x(), 6);
            double y = MathUtils.round(v.getCenter().y(), 6);
            generator.writeFieldName("center");
            generator.writeStartObject();
            generator.writeNumberField("x", x);
            generator.writeNumberField("y", y);
            generator.writeEndObject();

            generator.writeEndObject();
        }
        generator.writeEndArray();

        generator.writeFieldName("Edges");
        generator.writeStartArray();
        for (Edge e : graph.edges()) {
            generator.writeStartObject();
            String hexLabelColor = Utils.encode(e.getForegroundColor());
            String hexStrokeColor = Utils.encode(e.getStrokeColor());
            generator.writeNumberField("start", e.getStart());
            generator.writeNumberField("end", e.getEnd());
            if (!e.isBackEdge()) {
                generator.writeStringField("label", e.getLabel());
                generator.writeStringField("foregroundColor", hexLabelColor);
                generator.writeStringField("strokeColor", hexStrokeColor);
                generator.writeBooleanField("highlighted", e.isHighlighted());

                double x = MathUtils.round(e.getLabelCenter().x(), 6);
                double y = MathUtils.round(e.getLabelCenter().y(), 6);
                generator.writeFieldName("center");
                generator.writeStartObject();
                generator.writeNumberField("x", x);
                generator.writeNumberField("y", y);
                generator.writeEndObject();
            }

            generator.writeBooleanField("directed", e.isDirected());
            generator.writeBooleanField("backEdge", e.isBackEdge());

            generator.writeEndObject();
        }

        generator.close();
    }

    private void saveToSgi() throws IOException
    {
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        Graph graph = plane.getGraph();
        for (Edge e: graph.edges()) {
            Vertex u = graph.getVertex(e.getStart());
            Vertex v = graph.getVertex(e.getEnd());
            writer.write(u.getLabel() + " " + v.getLabel() + " ");
            writer.write(e.getLabel() + "\n");
        }

        writer.close();
    }

    private void saveToSvg(File file) throws IOException
    {
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(plane.exportToSvg());
        bw.close();
    }

    public JToolBar getToolBar()
    {
        return mainToolBar;
    }

    public void doSave()
    {
        saveButton.doClick();
    }

    public void doOpen()
    {
        openButton.doClick();
    }
}
