/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.place.Light;
import game.place.Shadow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 *
 * @author przemek
 */
public class OpticProperties {

    public static final int FULL_SHADOW = 0, NO_SHADOW = 1, IN_SHADE_NO_SHADOW = 2, IN_SHADE = 3;
    private static final boolean[] LITABLE = {true, true, false, false};
    private static final boolean[] GIVE_SHADOW = {true, false, false, true};
    private final int type;
    private final int shadowHeight;
    private float shadowColor;
    private final ArrayList<Shadow> shadows = new ArrayList<>();

    public static OpticProperties create(int type, int shadowHeight) {
        return new OpticProperties(type, shadowHeight);
    }

    public static OpticProperties create(int type) {
        return new OpticProperties(type, 0);
    }

    private OpticProperties(int type, int shadowHeight) {
        this.type = type;
        this.shadowHeight = shadowHeight;
    }

    public void addShadow(Shadow shadow) {
        shadows.add(shadow);
    }

    public void clearShadows() {
        shadows.clear();
    }

    public void renderShadowBlack(Light emitter, Point[] points) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public void renderShadowLit(Light emitter, Point[] points) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(shadowColor, shadowColor, shadowColor);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public boolean isLitable() {
        return LITABLE[type];
    }

    public boolean isGiveShadow() {
        return GIVE_SHADOW[type];
    }

    public int getShadowHeight() {
        return shadowHeight;
    }

    public float getShadowColor() {
        return shadowColor;
    }

    public Collection<Shadow> getShadows() {
        return Collections.unmodifiableList(shadows);
    }

    public void setShadowColor(float shadowColor) {
        this.shadowColor = shadowColor;
    }
}
