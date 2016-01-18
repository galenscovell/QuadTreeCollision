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

    public synchronized void start() {
        this.thread = new Thread(this, "Pathfinder");
        running = true;
        thread.start(); // call run()
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

    private void update() {
        handleCollisions();
        quadTree.clear();
        for (Rectangle rect : allObjects) {
            checkBounds(rect, quadTree.bounds);
            rect.move();
            quadTree.insert(rect);
        }
        if (addingObject) {
            createObject();
            addingObject = false;
        }
        if (removingObject) {
            removeObject();
            removingObject = false;
        }
    }

    private void createObject() {
        Rectangle newObject = new Rectangle(
            randomGenerator.nextInt(Constants.SCREEN_X - Constants.OBJECT_SIZE - 8),
            randomGenerator.nextInt(Constants.SCREEN_X - Constants.OBJECT_SIZE - 24),
            Constants.OBJECT_SIZE,
            Constants.OBJECT_SIZE
        );
        // Set random velocity
        int velocityX = 0;
        int velocityY = 0;
        while (velocityX == 0 && velocityY == 0) {
            velocityX = randomGenerator.nextInt(3) - 1;
            velocityY = randomGenerator.nextInt(3) - 1;
        }
        newObject.changeVelocity(velocityX, velocityY);
        allObjects.add(newObject);
    }

    private void removeObject() {
        if (allObjects.size() > 0) {
            allObjects.remove(allObjects.size() - 1);
        }
    }

    private void handleCollisions() {
        List<Rectangle> returnObjects = new ArrayList<Rectangle>();
        for (Rectangle a : allObjects) {
            returnObjects.clear();
            quadTree.retrieve(returnObjects, a);

            for (Rectangle b : returnObjects) {
                if (a != null && b != null && a != b) {
                    if (intersection(a, b)) {
                        if (a.x > b.x) {
                            a.changeVelocity(2, 0);
                            b.changeVelocity(-2, 0);
                        }
                        if (a.y > b.y) {
                            a.changeVelocity(0, 2);
                            b.changeVelocity(0, -2);
                        }
                        if (a.x + a.width < b.width) {
                            a.changeVelocity(-2, 0);
                            b.changeVelocity(2, 0);
                        }
                        if (a.y + a.height < b.height) {
                            a.changeVelocity(0, -2);
                            b.changeVelocity(0, 2);
                        }
                    }
                }
            }
        }
    }

    private boolean intersection(Rectangle a, Rectangle b) {
      return (Math.abs(a.x - b.x) * 2 <= (a.width + b.width)) &&
             (Math.abs(a.y - b.y) * 2 <= (a.height + b.height));
    }

    private void checkBounds(Rectangle a, Rectangle b) {
        // Left wall collision
        if (a.x < b.x) {
            a.changeVelocity(2, 0);
        }
        // Top wall collision
        if (a.y < b.y) {
            a.changeVelocity(0, 2);
        }
        // Right wall collision
        if (a.x + a.width > b.width) {
            a.changeVelocity(-2, 0);
        }
        // Bottom wall collision
        if (a.y + a.height > b.height) {
            a.changeVelocity(0, -2);
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
}
