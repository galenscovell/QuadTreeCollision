package ui;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Mainframe implements Runnable {
    private int width, height;
    private JFrame frame;

    public Mainframe(int x, int y) {
        this.width = x;
        this.height = y;
    }

    public void run() {
        this.frame = new JFrame("Pathfinder");
        frame.setPreferredSize(new Dimension(width, height));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents(frame);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createComponents(Container container) {
        // SimulationPanel panel = new SimulationPanel(width, height);
        // container.add(panel);
        // panel.start();
    }
}
