package edu.inforscience;
import edu.inforscience.graphics.*;
import edu.inforscience.graphics.Point2D;
import edu.inforscience.graphics.Edge;
import edu.inforscience.util.Utils;

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
    private final JButton newEdgeButton;
    private final JButton eraserButton;
    private final JButton deleteButton;
    private final JButton showGridButton;
    private final JButton smoothLinesButton;

    private final JButton shapeCircleButton;
    private final JButton shapeRectangleButton;
    private final JButton shapeNoneButton;
    private final JButton quitButton;

    private final JButton labelColorButton;
    private final JButton backgroundColorButton;
    private final JButton borderColorButton;

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

        newEdgeButton = new JButton(getImage("edge"));
        newEdgeButton.addActionListener(actionHandler);
        newEdgeButton.setToolTipText("New edge");

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

        labelColorButton = new JButton(getImage("labelColor"));
        labelColorButton.addActionListener(actionHandler);
        labelColorButton.setToolTipText("Set text color");

        backgroundColorButton = new JButton(getImage("backgroundColor"));
        backgroundColorButton.addActionListener(actionHandler);
        backgroundColorButton.setToolTipText("Set background color");

        borderColorButton = new JButton(getImage("borderColor"));
        borderColorButton.addActionListener(actionHandler);
        borderColorButton.setToolTipText("Set border color");

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
        mainToolBar.add(newEdgeButton);
        mainToolBar.add(eraserButton);
        mainToolBar.add(deleteButton);
        mainToolBar.addSeparator();
        mainToolBar.add(shapeCircleButton);
        mainToolBar.add(shapeRectangleButton);
        mainToolBar.add(shapeNoneButton);
        mainToolBar.add(showGridButton);
        mainToolBar.add(smoothLinesButton);
        mainToolBar.addSeparator();
        mainToolBar.add(labelColorButton);
        mainToolBar.add(backgroundColorButton);
        mainToolBar.add(borderColorButton);
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

    private void readLaxGraph() throws IOException
    {
        InputStream fis = new FileInputStream(filePath);
        InputStreamReader in = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(in);
        HashMap<Integer, Vertex> G = new HashMap<Integer, Vertex>();
        HashMap<String, Integer> keys = new HashMap<String, Integer>();
        Integer nextKey = 1;
        String line, u , v, label;

        int operation = 0;
        while ((line = reader.readLine()) != null) {
            if (line.equals("[EDGES]")) {
                operation = READ_EDGE_INFO;
                continue;
            } else if (line.equals("[VERTICES]")) {
                operation = READ_VERTEX_INFO;
                continue;
            }

            if (operation == READ_EDGE_INFO) {
                String[] tokens = line.split(",");
                u = tokens[0].trim();
                v = tokens[1].trim();
                if (tokens.length == 3) {
                    label = tokens[2];
                } else {
                    label = "";
                }

                u = u.replace("\\n", "\n");
                v = v.replace("\\n", "\n");

                Integer ku = null, kv = null;
                if (!keys.containsKey(u)) {
                    ku = nextKey++;
                    Vertex vu = new Vertex(u);
                    vu.setKey(ku);
                    keys.put(u, ku);
                    G.put(ku, vu);
                } else {
                    ku = keys.get(u);
                }

                if (!keys.containsKey(v)) {
                    kv = nextKey++;
                    Vertex vv = new Vertex(v);
                    vv.setKey(kv);
                    keys.put(v, kv);
                    G.put(kv, vv);
                } else {
                    kv = keys.get(v);
                }

                G.get(ku).addNeighbor(kv, label);
                //G.get(vid).addNeighbor(uid, label);
            } else if (operation ==  READ_VERTEX_INFO) {
                String[] tokens = line.split(":|,");

                u = tokens[0].trim();
                u = u.replace("\\n", "\n");
                //String[] coordinates = tokens[1].split(",");
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                int align = StyleConstants.ALIGN_LEFT;
                if (tokens.length == 4) {
                    if (tokens[3].equals("C"))
                        align = StyleConstants.ALIGN_CENTER;
                    else if (tokens[3].equals("R"))
                        align = StyleConstants.ALIGN_RIGHT;
                }

                if (keys.containsKey(u)) {
                    G.get(keys.get(u)).setCenter(new Point2D(x, y));
                    G.get(keys.get(u)).setLabelAlignment(align);
                } else{
                    Integer key = nextKey++;
                    Vertex vertex = new Vertex(u, new Point2D(x, y));
                    vertex.setKey(key);
                    vertex.setLabelAlignment(align);
                    keys.put(u, key);
                    G.put(key, vertex);
                }
            }
        }

        plane.setGraph(G, keys, nextKey);
        plane.resetZoom();
        fis.close();
    }

    private void readGraph() throws InvalidFormatException, IOException
    {
        HashMap<Integer, Vertex> graph = new HashMap<Integer, Vertex>();
        HashMap<String, Integer> labelKeys = new HashMap<String, Integer>();
        HashSet<Integer> keys = new HashSet<Integer>();
        HashSet<String> labels = new HashSet<String>();
        File input = new File(filePath);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(input);
        Iterator<JsonNode> V = root.path("Graph").path("Vertices").elements();
        Integer nextKey = 0;
        while (V.hasNext()) {
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

            Vertex vertex = new Vertex(label);
            vertex.setKey(key);
            labelKeys.put(label, key);
            nextKey = Math.max(nextKey, key);

            // Optional properties
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

            if (v.get("labelColor") != null) {
                String hex = v.get("labelColor").textValue();
                Color labelColor = Utils.decode(hex);
                vertex.setLabelColor(labelColor);
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

            graph.put(key, vertex);
            keys.add(key);
            labels.add(label);
        }

        Iterator<JsonNode> E = root.get("Graph").get("Edges").elements();
        while (E.hasNext()) {
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

            Edge edge = new Edge(start, end, "");

            if (e.get("label") != null) {
                String label = e.get("label").textValue();
                edge.setLabel(label);
            }

            if (e.get("labelColor") != null) {
                String hex = e.get("labelColor").textValue();
                Color labelColor = Utils.decode(hex);
                edge.setLabelColor(labelColor);
            }

            if (e.get("strokeColor") != null) {
                String hex = e.get("strokeColor").textValue();
                Color strokeColor = Utils.decode(hex);
                edge.setStrokeColor(strokeColor);
            }

            if (e.get("strokeSize") != null) {
                String str = e.get("strokeSize").toString();
                Float strokeSize = Float.parseFloat(str);
                edge.setStrokeSize(strokeSize);
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

            graph.get(start).getNeighbors().put(end, edge);
        }

        plane.finishPendingActions();
        plane.resetZoom();
        nextKey++;
        plane.setGraph(graph, labelKeys, nextKey);
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
            } else if (source == newEdgeButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
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
            } else if (source == labelColorButton ||
                       source == backgroundColorButton ||
                       source == borderColorButton) {
                Color color = JColorChooser.showDialog(
                            Main.this,
                            "Choose a color",
                            null
                        );

                if (source == labelColorButton) {
                    plane.setLabelColorToSelectedVertices(color);
                } else if (source == backgroundColorButton) {
                    plane.setBackgroundColorToSelectedVertices(color);
                } else if (source == borderColorButton) {
                    plane.setBorderColorToSelectedVertices(color);
                }

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
        FileFilter gi, lgi;
        gi = new FileNameExtensionFilter("Graph Illustrator", "gi");
        lgi = new FileNameExtensionFilter("Lax Graph Illustrator", "lgi");

        fc.addChoosableFileFilter(gi);
        fc.addChoosableFileFilter(lgi);
        fc.setFileFilter(gi);

        int code = fc.showOpenDialog(null);
        if (code == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String tempFilePath = filePath;
                filePath = file.getAbsolutePath();
                if (filePath.toLowerCase().endsWith(".gi")) {
                    readGraph();
                } else if (filePath.toLowerCase().endsWith(".lgi")) {
                    readLaxGraph();
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
        FileFilter gi, lgi;
        gi = new FileNameExtensionFilter("Graph Illustrator", "gi");
        lgi = new FileNameExtensionFilter("Lax Graph Illustrator", "lgi");
        fc.addChoosableFileFilter(gi);
        fc.addChoosableFileFilter(lgi);
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

            if (selectedFilter == lgi) {
                if (!fn.toLowerCase().endsWith(".lgi")) {
                    fn += ".lgi";
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
            } else if (filePath.toLowerCase().endsWith(".lgi")) {
                saveToLgi();
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
                    readLaxGraph();
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
        Map<Integer, Vertex> graph = plane.getGraph();
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
        for (Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex v = entry.getValue();
            String hexLabelColor = Utils.encode(v.getLabelColor());
            String hexBorderColor = Utils.encode(v.getBorderColor());
            String hexBackgroundColor = Utils.encode(v.getBackgroundColor());
            generator.writeStartObject();
            generator.writeNumberField("key", v.getKey());
            generator.writeStringField("label", v.getLabel());
            generator.writeNumberField("radius", v.getRadius());
            generator.writeStringField("labelColor", hexLabelColor);
            generator.writeStringField("backgroundColor", hexBackgroundColor);
            generator.writeStringField("borderColor", hexBorderColor);

            generator.writeFieldName("center");
            generator.writeStartObject();
            generator.writeNumberField("x", v.getCenter().x());
            generator.writeNumberField("y", v.getCenter().y());
            generator.writeEndObject();

            generator.writeEndObject();
        }
        generator.writeEndArray();

        generator.writeFieldName("Edges");
        generator.writeStartArray();
        for (Entry<Integer, Vertex> vertexEntry : graph.entrySet()) {
            Vertex v = vertexEntry.getValue();
            for (Entry<Integer, Edge>  edgeEntry: v.getNeighbors().entrySet()) {
                generator.writeStartObject();
                Edge e = edgeEntry.getValue();
                String hexLabelColor = Utils.encode(e.getLabelColor());
                String hexStrokeColor = Utils.encode(e.getStrokeColor());
                generator.writeNumberField("start", e.getStart());
                generator.writeNumberField("end", e.getEnd());
                generator.writeStringField("label", e.getLabel());
                generator.writeStringField("labelColor", hexLabelColor);
                generator.writeStringField("strokeColor", hexStrokeColor);
                generator.writeNumberField("strokeSize", e.getStrokeSize());

                generator.writeFieldName("center");
                generator.writeStartObject();
                generator.writeNumberField("x", e.getLabelCenter().x());
                generator.writeNumberField("y", e.getLabelCenter().y());
                generator.writeEndObject();

                generator.writeEndObject();
            }
        }

        generator.close();
    }

    private void saveToLgi() throws IOException
    {
        File file = new File(filePath);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fw);

        Map<Integer, Vertex> graph = plane.getGraph();
        writer.write("[EDGES]" + "\n");
        for (Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            for (Entry<Integer, Edge> e : u.getNeighbors().entrySet()) {
                Edge edge = e.getValue();
                Vertex v = graph.get(edge.getEnd());
                writer.write(u.getLabel().replace("\n", "\\n") + ","
                           + v.getLabel().replace("\n", "\\n"));

                if (!edge.getLabel().isEmpty())
                    writer.write("," + edge.getLabel().replace("\n", "\\n"));

                writer.write("\n");
            }
        }

        writer.write("[VERTICES]" + "\n");
        for (Entry<Integer, Vertex> entry : graph.entrySet()) {
            Vertex u = entry.getValue();
            double x = u.getCenter().x();
            double y = u.getCenter().y();
            int align = u.getLabelAlignment();
            String a = "L";
            if (align == StyleConstants.ALIGN_CENTER)
                a = "C";
            else if (align == StyleConstants.ALIGN_RIGHT)
                a = "R";
            writer.write(u.getLabel().replace("\n", "\\n")
                         + ":" + x + "," + y
                         + "," + a + "\n");
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
