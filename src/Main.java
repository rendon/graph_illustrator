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
import java.util.HashMap;
import java.util.Map.Entry;

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

    private final JButton shapeCircleButton;
    private final JButton shapeRectangleButton;
    private final JButton shapeNoneButton;
    private final JButton quitButton;

    private static final int READ_EDGE_INFO = 1;
    private static final int READ_VERTEX_INFO = 2;

    private String fileName;

    public Main()
    {
        super("Graph Illustrator");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        log = System.out;

        ActionHandler actionHandler = new ActionHandler();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(actionHandler);

        plane = new Plane();
        JToolBar toolBar = new JToolBar();
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

        shapeCircleButton = new JButton(getImage("circle"));
        shapeCircleButton.addActionListener(actionHandler);
        shapeCircleButton.setToolTipText("Use circles for nodes");

        shapeRectangleButton = new JButton(getImage("rectangle"));
        shapeRectangleButton.addActionListener(actionHandler);
        shapeRectangleButton.setToolTipText("Use rectangles for nodes");

        shapeNoneButton = new JButton(getImage("none"));
        shapeNoneButton.addActionListener(actionHandler);
        shapeNoneButton.setToolTipText("Remove shape from nodes");


        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(reloadButton);
        toolBar.add(exportSvgButton);
        toolBar.addSeparator();
        toolBar.add(pointerButton);
        toolBar.add(newNodeButton);
        toolBar.add(newEdgeButton);
        toolBar.add(eraserButton);
        toolBar.addSeparator();
        toolBar.add(shapeCircleButton);
        toolBar.add(shapeRectangleButton);
        toolBar.add(shapeNoneButton);
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

    void run()
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            readGraph(reader);
            plane.updateUI();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,
                    "There was  a problem while trying to read " +
                    "the file.\nCheck if your file contains a "  +
                    "valid input.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) throws Exception
    {
        Main test = new Main();
        // try to read from STDOUT
        test.run();
    }

    private void readGraph(BufferedReader reader) throws IOException
    {
        HashMap<Integer, Vertex> G = new HashMap<Integer, Vertex>();
        String line, u , v, label;
        int nodeId = 0;
        HashMap<String, Integer> ids = new HashMap<String, Integer>();

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
                u = tokens[0];
                v = tokens[1];
                if (tokens.length == 3)
                    label = tokens[2];
                else
                    label = "";

                u = u.replace("\\n", "\n");
                v = v.replace("\\n", "\n");
                Point2D pos = null;
                if (tokens.length == 5) {
                    double x = Double.parseDouble(tokens[4]);
                    double y = Double.parseDouble(tokens[5]);
                    pos = new Point2D(x, y);
                }

                int uid;
                if (ids.containsKey(u)) {
                    uid = ids.get(u);
                } else {
                    uid = nodeId++;
                    ids.put(u, uid);
                    Vertex vu = new Vertex(uid, u, pos);
                    G.put(uid, vu);
                }

                int vid;
                if (ids.containsKey(v)) {
                    vid = ids.get(v);
                } else {
                    vid = nodeId++;
                    ids.put(v, vid);
                    Vertex vv = new Vertex(vid, v, pos);
                    G.put(vid, vv);
                }

                G.get(uid).addNeighbor(vid, label);
                //G.get(vid).addNeighbor(uid, label);
            } else if (operation ==  READ_VERTEX_INFO) {
                String[] tokens = line.split(":|,");

                u = tokens[0];
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

                if (ids.containsKey(u)) {
                    int id = ids.get(u);
                    G.get(id).setCenter(new Point2D(x, y));
                    G.get(id).setLabelAlignment(align);
                } else{
                    Vertex vertex = new Vertex(nodeId, u, new Point2D(x,y));
                    vertex.setLabelAlignment(align);
                    ids.put(u, nodeId);
                    G.put(nodeId, vertex);
                    nodeId++;
                }
            }
        }

        plane.setGraph(G);
        plane.setIds(ids);
        plane.setNodeId(nodeId);
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
                plane.setCurrentOperation(Plane.DRAW_NEW_VERTEX);
            } else if (source == newEdgeButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setCurrentOperation(Plane.DRAW_NEW_EDGE);
            } else if (source == pointerButton) {
                plane.setCurrentOperation(Plane.DEFAULT_OPERATION);
                Cursor c  = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                plane.setCursor(c);
            } else if (source == eraserButton) {
                Cursor c  = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                plane.setCursor(c);
                plane.setCurrentOperation(Plane.ERASE_OBJECT);
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
            }
        }

        void open()
        {
            JFileChooser fc = new JFileChooser();
            javax.swing.filechooser.FileFilter f;
            f = new FileNameExtensionFilter("Graph Illustrator", "gi");
            fc.addChoosableFileFilter(f);
            fc.setFileFilter(f);

            int code = fc.showOpenDialog(null);
            if (code == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    InputStream fis = new FileInputStream(file);
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader reader = new BufferedReader(in);
                    readGraph(reader);
                    fis.close();
                    fileName = file.getAbsolutePath();
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null,
                            "There was  a problem while trying to read "
                            + "the file.\nCheck if your file contains a "
                            + "valid input.",
                            "Error", JOptionPane.ERROR_MESSAGE);
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
                try {
                    InputStream fis = new FileInputStream(new File(fileName));
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader reader = new BufferedReader(in);
                    readGraph(reader);
                    fis.close();
                    plane.updateUI();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
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
                            JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    save();
                }
            }
            System.exit(0);
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
