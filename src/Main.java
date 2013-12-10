import edu.inforscience.graphics.*;
import edu.inforscience.graphics.Point2D;
import edu.inforscience.graphics.Edge;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class Main extends JFrame {
    private final Plane plane;
    private final PrintStream log;
    private static final int MAX = 200;
    private final JButton pointerButton;
    private final JButton openButton;
    private final JButton saveButton;
    private final JButton exportSvgButton;
    private final JButton newNodeButton;
    private final JButton newEdgeButton;
    private final JButton eraserButton;

    private final JButton shapeCircleButton;
    private final JButton shapeRectangleButton;
    private final JButton shapeNoneButton;
    private static final int READ_EDGE_INFO = 1;
    private static final int READ_VERTEX_INFO = 2;

    private String fileName;

    public Main()
    {
        super("Graph Illustrator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        log = System.out;
        plane = new Plane();
        JToolBar toolBar = new JToolBar();
        openButton = new JButton(getImage("open"));
        saveButton = new JButton(getImage("save"));
        exportSvgButton = new JButton(getImage("svg"));
        JButton quitButton = new JButton(getImage("quit"));
        newNodeButton = new JButton(getImage("node"));
        newEdgeButton = new JButton(getImage("edge"));
        pointerButton = new JButton(getImage("pointer"));
        eraserButton = new JButton(getImage("eraser"));

        shapeCircleButton = new JButton(getImage("circle"));
        shapeRectangleButton = new JButton(getImage("rectangle"));
        shapeNoneButton = new JButton(getImage("none"));


        ActionHandler actionHandler = new ActionHandler();
        openButton.addActionListener(actionHandler);
        saveButton.addActionListener(actionHandler);
        exportSvgButton.addActionListener(actionHandler);
        newNodeButton.addActionListener(actionHandler);
        newEdgeButton.addActionListener(actionHandler);
        pointerButton.addActionListener(actionHandler);
        eraserButton.addActionListener(actionHandler);
        shapeCircleButton.addActionListener(actionHandler);
        shapeRectangleButton.addActionListener(actionHandler);
        shapeNoneButton.addActionListener(actionHandler);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(exportSvgButton);
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
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    void run() throws Exception
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        readGraph(reader);
        plane.updateUI();
    }

    public static void main(String[] args) throws Exception
    {
        Main test = new Main();
        // try to read from STDOUT
        test.run();
    }

    private void readGraph(BufferedReader reader)
            throws Exception
    {
        HashMap<Integer, Vertex> G = new HashMap<Integer, Vertex>();
        String line, u , v, label;
        int nodeId = 0;
        HashMap<String, Integer> ids = new HashMap<String, Integer>();

        try {
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
                    String[] tokens = line.split(":");
                    u = tokens[0];
                    String[] coordinates = tokens[1].split(",");
                    double x = Double.parseDouble(coordinates[0]);
                    double y = Double.parseDouble(coordinates[1]);

                    if (ids.containsKey(u)) {
                        int id = ids.get(u);
                        G.get(id).setCenter(new Point2D(x, y));
                    } else{
                        Vertex vertex = new Vertex(nodeId, u, new Point2D(x,y));
                        ids.put(u, nodeId);
                        G.put(nodeId, vertex);
                        nodeId++;
                    }
                }
            }
        } catch (Exception ioe) {
            throw new Exception();
        }

        plane.setGraph(G);
        plane.setIds(ids);
        plane.setNodeId(nodeId);
    }

    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            if (source == openButton) {
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
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(null,
                                "There was  a problem while trying to read "
                                + "the file.\nCheck if your file contains a "
                                + "valid input.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (source == saveButton) {
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
            } else if (source == exportSvgButton) {
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
            }
        }
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
                writer.write(u.getLabel() + ","
                           + v.getLabel());

                if (!edge.getLabel().isEmpty())
                    writer.write("," + edge.getLabel());

                writer.write("\n");
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

    private void saveToSVG(File file) throws IOException
    {
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(plane.exportToSVG());
        bw.close();
    }


}
