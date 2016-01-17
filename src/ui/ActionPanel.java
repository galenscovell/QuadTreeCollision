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
        this.quadTree = new QuadTree(0, new Rectangle(0, 0, x - 8, y - 24));

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
        handleCollisions();
        quadTree.clear();
        for (Rectangle object : allObjects) {
            checkBounds(object, quadTree.bounds);
            object.move();
            quadTree.insert(object);
        }
    }

    private void handleCollisions() {
        List<Rectangle> returnObjects = new ArrayList<Rectangle>();
        for (Rectangle object : allObjects) {
            returnObjects.clear();
            quadTree.retrieve(returnObjects, object);

            for (Rectangle returnObject : returnObjects) {
                if (object == null || returnObject == null || object == returnObject) {
                    continue;
                }
                checkCollision(object, returnObject);
            }
        }
    }

    private void checkCollision(Rectangle a, Rectangle b) {
        if (a == null || a == b) {
            return;
        } else {
            int aDir = a.getDirection();
            int bDir = b.getDirection();

            if (pointInsideRect(a.getX(), a.getY(), b) ||
                pointInsideRect(a.getX(), a.getY() + a.getHeight(), b) ||
                pointInsideRect(a.getX() + a.getWidth(), a.getY(), b) ||
                pointInsideRect(a.getX() + a.getWidth(), a.getY() + a.getHeight(), b)) {

                a.setDirection(bDir);
                b.setDirection(aDir);
            }
        }
    }

    private boolean pointInsideRect(int x, int y, Rectangle rect) {
        if (x > rect.getX() && x < rect.getX() + rect.getWidth() &&
            y > rect.getY() && y < rect.getY() + rect.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    private void checkBounds(Rectangle a, Rectangle b) {
        if (a.getX() < b.getX()) {
            switch (a.getDirection()) {
                case Constants.DOWNLEFT:
                    a.setDirection(Constants.DOWNRIGHT);
                    break;
                case Constants.LEFT:
                    a.setDirection(Constants.RIGHT);
                    break;
                case Constants.UPLEFT:
                    a.setDirection(Constants.UPRIGHT);
                    break;
            }
        }
        if (a.getY() < b.getY()) {
            switch (a.getDirection()) {
                case Constants.UPLEFT:
                    a.setDirection(Constants.DOWNLEFT);
                    break;
                case Constants.UP:
                    a.setDirection(Constants.DOWN);
                    break;
                case Constants.UPRIGHT:
                    a.setDirection(Constants.DOWNRIGHT);
                    break;
            }
        }
        if (a.getX() + a.getWidth() > b.getWidth()) {
            switch (a.getDirection()) {
                case Constants.DOWNRIGHT:
                    a.setDirection(Constants.DOWNLEFT);
                    break;
                case Constants.RIGHT:
                    a.setDirection(Constants.LEFT);
                    break;
                case Constants.UPRIGHT:
                    a.setDirection(Constants.UPLEFT);
                    break;
            }
        }
        if (a.getY() + a.getHeight() > b.getHeight()) {
            switch (a.getDirection()) {
                case Constants.DOWNLEFT:
                    a.setDirection(Constants.UPLEFT);
                    break;
                case Constants.DOWN:
                    a.setDirection(Constants.UP);
                    break;
                case Constants.DOWNRIGHT:
                    a.setDirection(Constants.UPRIGHT);
                    break;
            }
        }
    }
}
