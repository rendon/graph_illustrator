import edu.inforscience.graphics.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;

public class Main extends JFrame {
    private Plane plane;
    private PrintStream log;
    private static final int MAX = 200;
    private JButton pointerButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton exportSvgButton;
    private JButton drawOnlyTextButton;
    private JButton newNodeButton;
    private JButton newEdgeButton;
    private static final int READ_EDGE_INFO = 1;
    private static final int READ_VERTEX_INFO = 2;

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
        drawOnlyTextButton = new JButton(getImage("onlytext"));
        JButton quitButton = new JButton(getImage("quit"));
        newNodeButton = new JButton(getImage("node"));
        newEdgeButton = new JButton(getImage("edge"));
        pointerButton = new JButton(getImage("pointer"));

        ActionHandler actionHandler = new ActionHandler();
        drawOnlyTextButton.addActionListener(actionHandler);
        openButton.addActionListener(actionHandler);
        saveButton.addActionListener(actionHandler);
        exportSvgButton.addActionListener(actionHandler);
        newNodeButton.addActionListener(actionHandler);
        newEdgeButton.addActionListener(actionHandler);
        pointerButton.addActionListener(actionHandler);

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
        toolBar.add(drawOnlyTextButton);
        toolBar.add(quitButton);
        add(toolBar, BorderLayout.NORTH);
        add(plane, BorderLayout.CENTER);
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    public void run() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        readGraph(reader);
        plane.updateUI();
    }

    public static void main(String[] args) throws IOException
    {
        Main test = new Main();
        // try to read from STDOUT
        test.run();
    }

    private void readGraph(BufferedReader reader)
            throws IOException
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
                log.println("tokens.size = " + tokens.length);
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
                    Vertex vertex = new Vertex(nodeId, u, new Point2D(x, y));
                    ids.put(u, nodeId);
                    G.put(nodeId, vertex);
                    nodeId++;
                }
            }
        }

        plane.setGraph(G);
        plane.setIds(ids);
        plane.setNodeId(nodeId);
    }

    class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == openButton) {
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
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            } else if (e.getSource() == saveButton) {
                JFileChooser fc = new JFileChooser();
                javax.swing.filechooser.FileFilter f;
                f = new FileNameExtensionFilter("Graph Illustrator", "gi");
                fc.addChoosableFileFilter(f);
                fc.setFileFilter(f);

                int code = fc.showSaveDialog(null);
                if (code == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String path = file.getAbsolutePath();
                    if (!path.endsWith(".gi"))
                        path += ".gi";

                    file = new File(path);
                    try {
                        if (file.exists()) {
                            int op = JOptionPane.showConfirmDialog(null,
                                    "File already exists. Override?",
                                    "Override?",
                                    JOptionPane.YES_NO_OPTION);
                            if (op == JOptionPane.YES_OPTION)
                                plane.saveToFile(file);

                        } else {
                            plane.saveToFile(file);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

            } else if (e.getSource() == exportSvgButton) {
                JFileChooser fc = new JFileChooser();
                int code = fc.showSaveDialog(null);
                if (code == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        if (file.exists()) {
                            int op = JOptionPane.showConfirmDialog(null,
                                        "File already exists. Override?",
                                        "Override?",
                                        JOptionPane.YES_NO_OPTION);
                            if (op == JOptionPane.YES_OPTION)
                                plane.exportToSvg(file);

                        } else {
                            plane.exportToSvg(file);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

            } else if (e.getSource() == newNodeButton) {
                plane.setCurrentOperation(Plane.DRAW_NEW_VERTEX);
            } else if (e.getSource() == drawOnlyTextButton) {
                plane.toggleDrawOnlyText();
            } else if (e.getSource() == newEdgeButton) {
                plane.setCurrentOperation(Plane.DRAW_NEW_EDGE);
            } else if (e.getSource() == pointerButton) {
                plane.setCurrentOperation(Plane.DEFAULT_OPERATION);
            }
        }
    }
}
