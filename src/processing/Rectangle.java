package processing;

import util.Constants;

import java.awt.*;

public class Rectangle {
    public int x, y, width, height, frame;
    public int velocityX, velocityY;
    private Color color;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.frame = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        this.color = Constants.OBJECT_COLOR;
    }

    public void changeVelocity(int dx, int dy) {
        dx *=  Constants.MOVESPEED;
        dy *=  Constants.MOVESPEED;
        if (Math.abs(velocityX + dx) <= Constants.MOVESPEED) {
            velocityX += dx;
        }
        if (Math.abs(velocityY + dy) <= Constants.MOVESPEED) {
            velocityY += dy;
        }
        this.color = Constants.COLLISION_COLOR;
        this.frame = 8;
    }

    public void move() {
        x += velocityX;
        y += velocityY;
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
