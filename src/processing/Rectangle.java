package processing;

import util.Constants;

import java.awt.*;

public class Rectangle {
    private int x, y;
    private int left, top, right, bottom;
    private int width, height;
    private int direction, frame;
    private Color color;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.frame = 0;
        this.color = Constants.OBJECT_COLOR;
    }

    public void setDirection(int dir) {
        this.direction = dir;
        this.color = Constants.COLLISION_COLOR_0;
        this.frame = 4;
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

    public int getDirection() {
        return direction;
    }


    public void move() {
        switch (direction) {
            case Constants.DOWNLEFT:
                x -= Constants.MOVESPEED;
                y += Constants.MOVESPEED;
                break;
            case Constants.DOWN:
                y += Constants.MOVESPEED;
                break;
            case Constants.DOWNRIGHT:
                x += Constants.MOVESPEED;
                y += Constants.MOVESPEED;
                break;
            case Constants.RIGHT:
                x += Constants.MOVESPEED;
                break;
            case Constants.UPRIGHT:
                x += Constants.MOVESPEED;
                y -= Constants.MOVESPEED;
                break;
            case Constants.UP:
                y -= Constants.MOVESPEED;
                break;
            case Constants.UPLEFT:
                x -= Constants.MOVESPEED;
                y -= Constants.MOVESPEED;
                break;
            case Constants.LEFT:
                x -= Constants.MOVESPEED;
                break;
        }
    }

    public void draw(Graphics2D gfx) {
        if (frame > 0) {
            frame--;
            if (frame == 0) {
                color = Constants.OBJECT_COLOR;
            }
        }
        gfx.setColor(color);
        gfx.fillRoundRect(x, y, width, height, 5, 5);
    }
}
