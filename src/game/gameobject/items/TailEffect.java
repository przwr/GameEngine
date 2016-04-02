/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.place.Place;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public abstract class TailEffect {

    final Joint[] tail;
    final int length;
    final float width;
    final Color color;
    int last;

    public TailEffect(int length, float width) {
        this.tail = new Joint[length];
        this.length = Math.max(length, 2);
        this.width = width / 2;
        last = -1;
        color = new Color(1, 1, 1, 0.3f);
    }

    public void updatePoint(int x, int y, int height, int direction) {
        for (int i = length - 1; i >= 0; i--) {
            if (tail[i] != null && i < length - 1) {
                tail[i + 1] = tail[i];
            }
        }
        tail[0] = new Joint(x, y, height, direction);
        if (last < length - 1) {
            last++;
        }
    }

    public void updateStatic() {
        if (tail[0] != null) {
            for (int i = length - 1; i >= 0; i--) {
                if (tail[i] != null) {
                    tail[i] = null;
                    last--;
                    break;
                }
            }
        }
    }

    public int getWidth() {
        if (isActive()) {
            return Math.abs(tail[0].x - tail[last].x);
        }
        return 0;
    }

    public int getHeight() {
        if (isActive()) {
            return Math.abs(tail[0].y - tail[last].y);
        }
        return 0;
    }

    public double getDirection(int start, int end) {
        if (tail[start] == null) {
            start = 1;
        }
        if (tail[end] == null) {
            end = 0;
        }
        return Methods.pointAngleCounterClockwise(tail[start].x, tail[start].y - tail[start].height, tail[end].x, tail[end].y - tail[end].height);
    }

    public double getDirection() {
        return Methods.pointAngleCounterClockwise(tail[1].x, tail[1].y - tail[1].height, tail[0].x, tail[0].y - tail[0].height);
    }

    public boolean isActive() {
        return tail[0] != null && tail[1] != null;
    }

    public float calcWidth(int i) {
        return width - ((i - 1) * width / (last + 1));
    }

    public abstract void innerRender();

    public void render() {
        if (isActive()) {
            glPushMatrix();
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            innerRender();
            Drawer.refreshColor();
            glPopMatrix();
        }
    }

    class Joint {

        int x, y, height, direction;

        public Joint(int x, int y, int height, int direction) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.direction = direction;
        }

        public float getX(float width, boolean side) {
            return (float) (x + Methods.xRadius(direction + 90, width) * (side ? 1 : -1));
        }

        public float getY(float width, boolean side) {
            return (float) (y - Methods.yRadius(direction + 90, width) * (side ? 1 : -1) - height);
        }
    }
}
