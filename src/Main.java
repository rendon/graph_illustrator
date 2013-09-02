import edu.inforscience.graphics.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.HashMap;

public class Main extends  JFrame {
    private Plane plane;
    private PrintStream log;
    private static final int MAX = 200;

    public Main()
    {
        super("Graph Illustrator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        log = System.out;
        plane = new Plane();
        add(plane, BorderLayout.CENTER);
    }

    public void run() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String line, u = null, v = null, label = null;
        int i = 0, nodeId = 0;
        HashMap<String, Integer> nodes = new HashMap<String, Integer>();

        plane.setShowAxis(false);
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            u = tokens[0];
            v = tokens[1];
            log.println("tokens.size = " + tokens.length);
            if (tokens.length == 3)
                label = tokens[2];
            else
                label = "";

            if (!nodes.containsKey((String)u)) {
                nodes.put(u, nodeId++);
                plane.addVertex(u);
            }

            if (!nodes.containsKey((String)v)) {
                nodes.put(v, nodeId++);
                plane.addVertex(v);
            }

            int uid = nodes.get(u);
            int vid = nodes.get(v);
            plane.addEdge(uid, vid, label);
        }

        plane.updateUI();
    }

    public static void main(String[] args) throws IOException
    {
        Main test = new Main();
        test.run();
    }
}
