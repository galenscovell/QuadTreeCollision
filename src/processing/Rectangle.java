package processing;

import util.Constants;

import java.awt.*;
import java.util.Random;

public class Rectangle {
    private int x, y;
    private int direction;
    private int width, height;
    private int frame;
    private int xSpeed, ySpeed;
    private Color color;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        Random random = new Random();
        this.direction = random.nextInt(8);
        this.frame = 0;
        this.xSpeed = Constants.MOVESPEED;
        this.ySpeed = Constants.MOVESPEED;
        this.color = Constants.EXPLORE_COLOR_0;
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

    public void setDirection(int dir) {
        this.direction = dir;
        this.color = Constants.OBJECT_COLOR;
        frame = 4;
    }

    public void setXSpeed(int speed) {
        this.xSpeed = speed;
    }

    public void setYSpeed(int speed) {
        this.ySpeed = speed;
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public void move() {
        if (direction == Constants.DOWNLEFT) {
            x -= xSpeed;
            y += ySpeed;
        }
        if (direction == Constants.DOWN) {
            y += ySpeed;
        }
        if (direction == Constants.DOWNRIGHT) {
            x += xSpeed;
            y += ySpeed;
        }
        if (direction == Constants.RIGHT) {
            x += xSpeed;
        }
        if (direction == Constants.UPLEFT) {
            x -= xSpeed;
            y -= ySpeed;
        }
        if (direction == Constants.UP) {
            y -= ySpeed;
        }
        if (direction == Constants.UPRIGHT) {
            x += xSpeed;
            y -= ySpeed;
        }
        if (direction == Constants.LEFT) {
            x -= xSpeed;
        }
    }

    public void draw(Graphics2D gfx) {
        if (frame > 0) {
            frame--;
            if (frame == 0) {
                color = Constants.EXPLORE_COLOR_0;
            }
        }
        gfx.setColor(color);
        gfx.fillRect(x, y, width, height);
    }
}
