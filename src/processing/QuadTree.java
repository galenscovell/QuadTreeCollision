package processing;

import java.awt.*;
import java.util.*;
import java.util.List;

public class QuadTree {
    private int maxObjects = 10;
    private int max_levels = 5;
    private int level;
    private List<Rectangle> objects;
    public Rectangle bounds;
    private QuadTree[] nodes;

    public QuadTree(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new ArrayList<Rectangle>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    public void insert(Rectangle rect) {
        if (nodes[0] != null) {
            int index = getIndex(rect);
            if (index != -1) {
                nodes[index].insert(rect);
                return;
            }
        }
        objects.add(rect);
        // If node exceeds capacity it will split and add all objects to their corresponding nodes
        if (objects.size() > maxObjects && level < max_levels) {
            if (nodes[0] == null) {
                split();
            }
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Rectangle> retrieve(List<Rectangle> returnObjects, Rectangle rect) {
        // Return all objects that could collide with given object
        int index = getIndex(rect);
        if  (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, rect);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public void drawNodes(Graphics2D gfx) {
        // Recursively draw each subnode border
        gfx.drawRect(bounds.getX() + 1, bounds.getY() + 1, bounds.getWidth() - 1, bounds.getHeight() - 1);
        for (int i = 0; i < 4; i++) {
            if (nodes[i] != null) {
                nodes[i].drawNodes(gfx);
            }
        }
    }

    private void split() {
        // Split node into 4 subnodes
        int subWidth = bounds.getWidth() / 2;
        int subHeight = bounds.getHeight() / 2;
        int x = bounds.getX();
        int y = bounds.getY();
        // topright
        nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        // topleft
        nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        // bottomleft
        nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        // bottomright
        nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Rectangle rect) {
        // Determine which node object belongs to, -1 means object doesn't fit within
        // child node and is part of parent node.
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        // Object completely fits within top quadrants
        boolean topQuadrant = (rect.getY() < horizontalMidpoint && rect.getY() + rect.getHeight() < horizontalMidpoint);
        // Object completely fits within bottom quadrants
        boolean bottomQuadrant = (rect.getY() > horizontalMidpoint);

        // Object completely fits within left quadrants
        if (rect.getX() < verticalMidpoint && rect.getX() + rect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object completely fits within right quadrants
        else if (rect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }
}
