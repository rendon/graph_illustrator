import edu.inforscience.graphics.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JFrame {
    private Plane plane;
    private PrintStream log;
    private static final int MAX = 200;
    private JButton openButton;
    private JButton saveButton;
    private JButton newNodeButton;
    private JButton newEdgeButton;

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
        JButton quitButton = new JButton(getImage("quit"));
        newNodeButton = new JButton(getImage("node"));
        newEdgeButton = new JButton(getImage("edge"));
        ActionHandler actionHandler = new ActionHandler();
        openButton.addActionListener(actionHandler);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(newNodeButton);
        toolBar.add(newEdgeButton);
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

        HashMap<Integer, Vertex> G = readGraph(reader);
        plane.setGraph(G);
        plane.updateUI();
    }

    public static void main(String[] args) throws IOException
    {
        Main test = new Main();
        // try to read from STDOUT
        test.run();
    }

    private HashMap<Integer, Vertex> readGraph(BufferedReader reader)
            throws IOException
    {
        HashMap<Integer, Vertex> G = new HashMap<Integer, Vertex>();
        String line, u , v, label;
        int nodeId = 0;
        HashMap<String, Integer> ids = new HashMap<String, Integer>();

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            u = tokens[0];
            v = tokens[1];
            log.println("tokens.size = " + tokens.length);
            if (tokens.length == 3)
                label = tokens[2];
            else
                label = "";

            int uid;
            if (ids.containsKey(u)) {
                uid = ids.get(u);
            } else {
                uid = nodeId++;
                ids.put(u, uid);
                Vertex vu = new Vertex(uid, u);
                G.put(uid, vu);
            }

            int vid;
            if (ids.containsKey(v)) {
                vid = ids.get(v);
            } else {
                vid = nodeId++;
                ids.put(v, vid);
                Vertex vv = new Vertex(vid, v);
                G.put(vid, vv);
            }

            G.get(uid).addNeighbor(vid, label);
            //G.get(vid).addNeighbor(uid, label);
        }

        return G;
    }

    class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == openButton) {
                JFileChooser fc = new JFileChooser();
                int code = fc.showOpenDialog(null);
                if (code == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        InputStream fis = new FileInputStream(file);
                        InputStreamReader in = new InputStreamReader(fis);
                        BufferedReader reader = new BufferedReader(in);
                        HashMap<Integer, Vertex> G = readGraph(reader);
                        plane.setGraph(G);
                        fis.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

            }
        }
    }
}
