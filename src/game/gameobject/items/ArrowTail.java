/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.place.Place;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class ArrowTail extends TailEffect {
    private final Arrow arrow;
    
    public ArrowTail(int length, float width, Arrow arrow) {
        super(length, width);
        this.arrow = arrow;
    }

    @Override
    public void innerRender() {
        Drawer.setColorStatic(color);
        glDisable(GL_TEXTURE_2D);
        if (length >= 3 && tail[2] != null) {
            glBegin(GL_TRIANGLES);
            glVertex2f(tail[0].x, tail[0].y - tail[0].height);
            glVertex2f(tail[1].getX(width, true), tail[1].getY(width, true));
            glVertex2f(tail[1].getX(width, false), tail[1].getY(width, false));
            glEnd();
            glBegin(GL_QUAD_STRIP);
            int i;
            for (i = 1; i < last; i++) {
                glVertex2f(tail[i].getX(calcWidth(i), true), tail[i].getY(calcWidth(i), true));
                glVertex2f(tail[i].getX(calcWidth(i), false), tail[i].getY(calcWidth(i), false));
            }
            glEnd();
            glBegin(GL_TRIANGLES);
            glVertex2f(tail[i - 1].getX(calcWidth(i - 1), true), tail[i - 1].getY(calcWidth(i - 1), true));
            glVertex2f(tail[i - 1].getX(calcWidth(i - 1), false), tail[i - 1].getY(calcWidth(i - 1), false));
            glVertex2f(tail[i].x, tail[i].y - tail[i].height);
            glEnd();
        } else {
            Joint tmp = new Joint((tail[0].x + tail[0].x) / 2,
                    (tail[0].y + tail[0].y) / 2,
                    (tail[0].height + tail[0].height) / 2, tail[0].direction);
            glBegin(GL_TRIANGLES);
            glVertex2f(tail[0].x, tail[0].y - tail[0].height);
            glVertex2f(tmp.getX(width, true), tmp.getY(width, true));
            glVertex2f(tmp.getX(width, false), tmp.getY(width, false));
            glEnd();
            glBegin(GL_TRIANGLES);
            glVertex2f(tmp.getX(calcWidth(1), true), tmp.getY(calcWidth(1), true));
            glVertex2f(tmp.getX(calcWidth(1), false), tmp.getY(calcWidth(1), false));
            glVertex2f(tail[1].x, tail[1].y - tail[1].height);
            glEnd();
        }
        glEnable(GL_TEXTURE_2D);
    }
}
