package ui;

import processing.*;
import processing.Rectangle;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ActionPanel extends JPanel implements Runnable {
    private Thread thread;
    private QuadTree quadTree;
    private List<Rectangle> allObjects;

    private final int framerate = 60;
    private boolean running;

    public ActionPanel(int x, int y) {
        setPreferredSize(new Dimension(x, y));
        this.setFocusable(true);
        this.allObjects = new ArrayList<Rectangle>();
        this.quadTree = new QuadTree(0, new Rectangle(0, 0, x, y));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                allObjects.add(new Rectangle(0, 0, Constants.OBJECT_SIZE, Constants.OBJECT_SIZE));
            }
        });
    }

    public void run() {
        long start, end, sleepTime;

        while (running) {
            start = System.currentTimeMillis();
            update();
            repaint();
            end = System.currentTimeMillis();
            // Sleep to match FPS limit
            sleepTime = (1000 / framerate) - (end - start);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    }

    public synchronized void start() {
        this.thread = new Thread(this, "Pathfinder");
        running = true;
        thread.start(); // call run()
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            thread.interrupt();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gfx = (Graphics2D) g;
        gfx.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        // Clear screen
        gfx.setColor(Constants.BACKGROUND_COLOR);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        // Render next frame
        gfx.setColor(Constants.BORDER_COLOR);
        gfx.setStroke(new BasicStroke(1));
        quadTree.drawNodes(gfx);
        for (Rectangle object : allObjects) {
            object.draw(gfx);
        }
    }

    private void update() {
        quadTree.clear();
        for (Rectangle object : allObjects) {
            object.move();
            quadTree.insert(object);
        }
    }

    private void handleCollisions() {
        List<Rectangle> returnObjects = new ArrayList<Rectangle>();
        for (Rectangle object : allObjects) {
            returnObjects.clear();
            quadTree.check(returnObjects, object);
        }
        for (Rectangle object : returnObjects) {
            // Run collision detection
            return;
        }
    }
}
