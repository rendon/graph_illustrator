import edu.inforscience.graphics.*;
import edu.inforscience.graphics.Point2D;
import edu.inforscience.graphics.Edge;

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
import javax.swing.filechooser.FileFilter;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Main extends JFrame {
    private final Plane plane;
    private final PrintStream log;
    private static final int MAX = 200;
    private final JButton pointerButton;
    private final JButton openButton;
    private final JButton reloadButton;
    private final JButton saveButton;
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

    private static final int READ_EDGE_INFO = 1;
    private static final int READ_VERTEX_INFO = 2;

    private String fileName;

    public Main()
    {
        super("Graph Illustrator");
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        log = System.out;

        ActionHandler actionHandler = new ActionHandler();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(actionHandler);

        JToolBar toolBar = new JToolBar();
        plane = new Plane(toolBar);
        openButton = new JButton(getImage("open"));
        openButton.addActionListener(actionHandler);
        openButton.setToolTipText("Open file");

        reloadButton = new JButton(getImage("reload"));
        reloadButton.addActionListener(actionHandler);
        reloadButton.setToolTipText("Reload file");

        saveButton = new JButton(getImage("save"));
        saveButton.addActionListener(actionHandler);
        saveButton.setToolTipText("Save to file");

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

        showGridButton = new JButton(getImage("show_grid"));
        showGridButton.addActionListener(actionHandler);
        showGridButton.setToolTipText("Toggle grid");

        smoothLinesButton = new JButton(getImage("antialias"));
        smoothLinesButton.addActionListener(actionHandler);
        smoothLinesButton.setToolTipText("Toggle smooth lines");

        shapeCircleButton = new JButton(getImage("circle"));
        shapeCircleButton.addActionListener(actionHandler);
        shapeCircleButton.setToolTipText("Use circles for nodes");

        shapeRectangleButton = new JButton(getImage("rectangle"));
        shapeRectangleButton.addActionListener(actionHandler);
        shapeRectangleButton.setToolTipText("Use rectangles for nodes");

        shapeNoneButton = new JButton(getImage("none"));
        shapeNoneButton.addActionListener(actionHandler);
        shapeNoneButton.setToolTipText("Remove shape from nodes");

        labelColorButton = new JButton(getImage("labelColor"));
        labelColorButton.addActionListener(actionHandler);
        labelColorButton.setToolTipText("Set text color");

        backgroundColorButton = new JButton(getImage("backgroundColor"));
        backgroundColorButton.addActionListener(actionHandler);
        backgroundColorButton.setToolTipText("Set background color");

        borderColorButton = new JButton(getImage("borderColor"));
        borderColorButton.addActionListener(actionHandler);
        borderColorButton.setToolTipText("Set border color");

        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(reloadButton);
        toolBar.add(exportSvgButton);
        toolBar.addSeparator();
        toolBar.add(pointerButton);
        toolBar.add(newNodeButton);
        toolBar.add(newEdgeButton);
        toolBar.add(eraserButton);
        toolBar.add(deleteButton);
        toolBar.add(showGridButton);
        toolBar.add(smoothLinesButton);
        toolBar.addSeparator();
        toolBar.add(shapeCircleButton);
        toolBar.add(shapeRectangleButton);
        toolBar.add(shapeNoneButton);
        toolBar.addSeparator();
        toolBar.add(labelColorButton);
        toolBar.add(backgroundColorButton);
        toolBar.add(borderColorButton);
        toolBar.addSeparator();
        toolBar.add(quitButton);
        add(toolBar, BorderLayout.NORTH);
        add(plane, BorderLayout.CENTER);

        revalidate();
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    public static void main(String[] args) throws Exception
    {
        Main test = new Main();
    }

    private void readLightGraph() throws IOException, ParseException
    {
        InputStream fis = new FileInputStream(fileName);
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

        plane.finishPendingActions();
        plane.setGraph(G);
        plane.setChanges(0);
        plane.resetZoom();
        fis.close();
    }

    private void readGraph() throws Exception, IOException, ParseException
    {
        HashMap<Integer, Vertex> graph = new HashMap<Integer, Vertex>();
        HashSet<Integer> keys = new HashSet<Integer>();
        HashSet<String> labels = new HashSet<String>();
        FileReader reader = new FileReader(fileName);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(reader);
        JSONObject graphObject = (JSONObject) json.get("Graph");
        JSONArray vertices = (JSONArray) graphObject.get("Vertices");
        for (Object obj : vertices) {
            JSONObject v = (JSONObject) obj;
            Integer key = Integer.parseInt(v.get("key").toString());
            String label = (String) v.get("label");


            // Mandatory properties
            if (!v.containsKey("key") || !v.containsKey("label")) {
                throw new Exception(); // Pending: Define custom exception
            }

            if (keys.contains(key) || labels.contains(label)) {
                throw new Exception(); // Repeated node key or node label
            }

            Vertex vertex = new Vertex(label);
            vertex.setKey(key);

            // Optional properties
            Point2D center = null;
            if (v.containsKey("center")) {
                JSONObject point = (JSONObject) v.get("center");
                if (point.containsKey("x") && point.containsKey("y")) {
                    double x = Double.parseDouble(point.get("x").toString());
                    double y = Double.parseDouble(point.get("y").toString());
                    center = new Point2D(x, y);
                }
                vertex.setCenter(center);
            }

            if (v.containsKey("radius")) {
                Double radius = Double.parseDouble(v.get("radius").toString());
                System.out.println("Radius = " + radius);
                vertex.setRadius(radius);
            }

            String str;
            if (v.containsKey("labelColor")) {
                str = (String) v.get("labelColor");
                Color labelColor = Color.decode(str);
                vertex.setLabelColor(labelColor);
            }

            if (v.containsKey("backgroundColor")) {
                str = (String) v.get("backgroundColor");
                Color backgroundColor = Color.decode(str);
                vertex.setBackgroundColor(backgroundColor);
            }

            if (v.containsKey("borderColor")) {
                str = (String) v.get("borderColor");
                Color borderColor = Color.decode(str);
                vertex.setBorderColor(borderColor);
            }

            graph.put(key, vertex);
            keys.add(key);
            labels.add(label);
        }

        JSONArray edges = (JSONArray) graphObject.get("Edges");
        for (Object obj : edges) {
            JSONObject e = (JSONObject) obj;
            if (!e.containsKey("start") || !e.containsKey("end")) {
                throw new Exception(); // Invalid edge
            }

            Integer start = Integer.parseInt(e.get("start").toString());
            Integer end = Integer.parseInt(e.get("end").toString());

            if (!keys.contains(start) || !keys.contains(end)) {
                throw new Exception(); // Invalid edge
            }

            Edge edge = new Edge(start, end, "");

            if (e.containsKey("label")) {
                String label = (String) e.get("label");
                edge.setLabel(label);
            }

            String str;
            if (e.containsKey("labelColor")) {
                str = (String) e.get("labelColor");
                Color labelColor = Color.decode(str);
                edge.setLabelColor(labelColor);
            }

            if (e.containsKey("strokeColor")) {
                str = (String) e.get("strokeColor");
                Color strokeColor = Color.decode(str);
                edge.setStrokeColor(strokeColor);
            }

            if (e.containsKey("strokeSize")) {
                str = e.get("strokeSize").toString();
                Float strokeSize = Float.parseFloat(str);
                edge.setStrokeSize(strokeSize);
            }

            // Not supported yet.
            if (e.containsKey("center")) {
                JSONObject point = (JSONObject) e.get("center");
                if (point.containsKey("x") && point.containsKey("y")) {
                    double x = Double.parseDouble(point.get("x").toString());
                    double y = Double.parseDouble(point.get("y").toString());
                    Point2D center = new Point2D(x, y);
                    edge.setLabelCenter(center);
                }
            }

            graph.get(start).getNeighbors().put(end, edge);
        }

        plane.finishPendingActions();
        plane.resetZoom();
        plane.setGraph(graph);
        plane.setChanges(0);
    }

    private class ActionHandler implements ActionListener, WindowListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            if (source == openButton) {
                open();
            } else if (source == saveButton) {
                save();
            } else if (source == reloadButton) {
                reload();
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
           }

        }

        void open()
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
            lgi = new FileNameExtensionFilter("Light Graph Illustrator", "lgi");

            fc.addChoosableFileFilter(gi);
            fc.addChoosableFileFilter(lgi);
            fc.setFileFilter(gi);

            int code = fc.showOpenDialog(null);
            if (code == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    fileName = file.getAbsolutePath();
                    if (fileName.toLowerCase().endsWith(".gi")) {
                        readGraph();
                    } else {
                        readLightGraph();
                    }
                    plane.updateUI();
                } catch (ParseException pe) {
                    JOptionPane.showMessageDialog(
                                    null,
                                    "Error while parsing file.",
                                    "Error", JOptionPane.ERROR_MESSAGE
                                );
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(
                                    null,
                                    "Couldn't open file",
                                    "Error", JOptionPane.ERROR_MESSAGE
                                );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        void save()
        {
            if (fileName == null) {
                JFileChooser fc = new JFileChooser();
                javax.swing.filechooser.FileFilter f;
                f = new FileNameExtensionFilter("Graph Illustrator", "gi");
                fc.addChoosableFileFilter(f);
                fc.setFileFilter(f);

                int code = fc.showSaveDialog(null);
                if (code == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    fileName = file.getAbsolutePath();
                    if (!fileName.endsWith(".gi"))
                        fileName += ".gi";

                    file = new File(fileName);
                    try {
                        if (file.exists()) {
                            int op = JOptionPane.showConfirmDialog(null,
                                    "File already exists. Override?",
                                    "Override?",
                                    JOptionPane.YES_NO_OPTION);
                            if (op == JOptionPane.YES_OPTION)
                                saveToFile();

                        } else {
                            saveToFile();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            } else {
                try {
                    saveToFile();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        void reload()
        {
            if (fileName != null) {
                int op = JOptionPane.showConfirmDialog(
                                null,
                                "WARNING: All unsaved changes will be lost! " +
                                "Are you sure?",
                                "Confirmation",
                                JOptionPane.YES_NO_OPTION
                            );
                if (op != JOptionPane.YES_OPTION) {
                    return;
                }

                try {
                    if (fileName.toLowerCase().endsWith(".gi")) {
                        readGraph();
                    } else {
                        readLightGraph();
                    }
                    plane.updateUI();
                } catch (ParseException pe) {
                    JOptionPane.showMessageDialog(
                                    null,
                                    "Error while parsing file.",
                                    "Error", JOptionPane.ERROR_MESSAGE
                                );
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(
                                    null,
                                    "Couldn't open file",
                                    "Error", JOptionPane.ERROR_MESSAGE
                                );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        void exportToSvg()
        {
            JFileChooser fc = new JFileChooser();
            javax.swing.filechooser.FileFilter f;
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
                        if (op == JOptionPane.YES_OPTION)
                            saveToSVG(file);

                    } else {
                        saveToSVG(file);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
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

    private void saveToFile() throws IOException
    {
        File file = new File(fileName);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fw);

        HashMap<Integer, Vertex> graph = plane.getGraph();
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
        plane.setChanges(0);
    }

    private void saveToSVG(File file) throws IOException
    {
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(plane.exportToSVG());
        bw.close();
    }
}
