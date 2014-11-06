/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.Methods;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class Drawer {

    public static void drawRectangle(int xs, int ys, int w, int h) {
        glTranslatef(xs, ys, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, 20);
        glVertex2f(20, 20);
        glVertex2f(20, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawCircle(int xs, int ys, int r, int steps) {   //dla małych ilości kroków wychodzą figury foremne (trójkąt, czworokąt, itp.)
        glTranslatef(xs, ys, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLE_FAN);
        int step = 360 / steps;
        glVertex2f(0, 0);        
        for (int i = 0; i <= 360; i += step) {
            glVertex2f((float)Methods.xRadius(i, r), (float)Methods.yRadius(i, r));
        }
        glVertex2f(r, 0);        
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }
    
    public static void setColor(Color c) {
        glColor4f(c.r, c.g, c.b, c.a);
    }
    
    public static void refreshColor() {
        glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
    }
}
