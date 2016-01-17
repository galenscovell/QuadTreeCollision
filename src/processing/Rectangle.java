package processing;

import util.Constants;

import java.awt.*;

public class Rectangle {
    private int x, y;
    private int xVelocity, yVelocity;
    private int width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xVelocity = 1;
        this.yVelocity = 1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public void draw(Graphics2D gfx) {
        gfx.setColor(Constants.OBJECT_COLOR);
        gfx.fillRect(x, y, width, height);
    }
}
