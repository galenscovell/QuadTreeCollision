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
    private final Random randomGenerator;
    private boolean addingObject, removingObject;

    private final int framerate = 60;
    private boolean running;

    public ActionPanel(int x, int y) {
        setPreferredSize(new Dimension(x, y));
        this.setFocusable(true);

        this.randomGenerator = new Random();
        this.allObjects = new ArrayList<Rectangle>();
        this.quadTree = new QuadTree(0, new Rectangle(0, 0, x - 8, y - 24));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    removingObject = false;
                    addingObject = true;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    addingObject = false;
                    removingObject = true;
                }
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
        for (Rectangle rect : allObjects) {
            checkBounds(rect, quadTree.bounds);
            rect.move();
            quadTree.insert(rect);
        }
        if (addingObject) {
            Rectangle newObject = new Rectangle(
                    randomGenerator.nextInt(Constants.SCREEN_X - Constants.OBJECT_SIZE),
                    randomGenerator.nextInt(Constants.SCREEN_X - Constants.OBJECT_SIZE),
                    Constants.OBJECT_SIZE,
                    Constants.OBJECT_SIZE
            );
            newObject.setDirection(randomGenerator.nextInt(8));
            allObjects.add(newObject);
            addingObject = false;
        }
        if (removingObject) {
            if (allObjects.size() > 0) {
                allObjects.remove(allObjects.size() - 1);
            }
            removingObject = false;
        }
    }

    private void handleCollisions() {
        List<Rectangle> returnObjects = new ArrayList<Rectangle>();
        for (Rectangle a : allObjects) {
            returnObjects.clear();
            quadTree.retrieve(returnObjects, a);

            for (Rectangle b : returnObjects) {
                if (a != null && b != null && a != b) {
                    int aDir = a.getDirection();
                    int bDir = b.getDirection();

                    if (intersection(a, b)) {
                        a.setDirection(bDir);
                        b.setDirection(aDir);
                    }
                }
            }
        }
    }

    private boolean intersection(Rectangle a, Rectangle b) {
//        return !(a.getY() + a.getHeight() <= b.getY() ||
//                a.getY() >= b.getY() + b.getHeight() ||
//                a.getX() + a.getWidth() <= b.getX() ||
//                a.getX() >= b.getX() + b.getWidth());
        return (Math.abs(a.getX() - b.getX()) * 2 <= (a.getWidth() + b.getWidth())) &&
                (Math.abs(a.getY() - b.getY()) * 2 <= (a.getHeight() + b.getHeight()));
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
