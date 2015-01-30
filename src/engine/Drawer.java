/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.place.Place;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL13.GL_COMBINE;
import static org.lwjgl.opengl.GL13.GL_COMBINE_RGB;
import static org.lwjgl.opengl.GL13.GL_OPERAND0_RGB;
import static org.lwjgl.opengl.GL13.GL_OPERAND1_RGB;
import static org.lwjgl.opengl.GL13.GL_PREVIOUS;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_SRC0_RGB;
import static org.lwjgl.opengl.GL15.GL_SRC1_RGB;
import org.newdawn.slick.Color;
import sprites.Sprite;

/**
 *
 * @author Wojtek
 */
public class Drawer {

    private static final int white = glGenTextures();
    private static Place place;

    public static void drawRectangleInShade(int xStart, int yStart, int width, int height, float color) {
        glColor3f(color, color, color);
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, height);
        glVertex2f(width, height);
        glVertex2f(width, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
    }

    public static void drawRectangleInBlack(int xStart, int yStart, int width, int height) {
        glColor3f(0f, 0f, 0f);
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, height);
        glVertex2f(width, height);
        glVertex2f(width, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
    }
    
    public static void drawRectangle(int xStart, int yStart, int width, int height) {
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, height);
        glVertex2f(width, height);
        glVertex2f(width, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawCircle(int xStart, int yStart, int radius, int precision) {   //dla małych ilości kroków wychodzą figury foremne (trójkąt, czworokąt, itp.)
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLE_FAN);
        int step = 360 / precision;
        glVertex2f(0, 0);
        for (int i = 0; i <= 360; i += step) {
            glVertex2f((float) Methods.xRadius(i, radius), (float) Methods.yRadius(i, radius));
        }
        glVertex2f(radius, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawElipse(int xStart, int yStart, int xRadius, int yRadius, int precision) {   //dla małych ilości kroków wychodzą figury foremne (trójkąt, czworokąt, itp.)
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLE_FAN);
        int step = 360 / precision;
        glVertex2f(0, 0);
        for (int i = 0; i <= 360; i += step) {
            glVertex2f((float) Methods.xRadius(i, xRadius), (float) Methods.yRadius(i, yRadius));
        }
        glVertex2f(xRadius, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void setColor(float color) {
        glColor3f(color, color, color);
    }

    public static void setColorToWhite() {
        glColor3f(1f, 1f, 1f);
    }

    public static void setColor(Color c) {
        glColor4f(c.r, c.g, c.b, c.a);
    }

    public static void refreshForRegularDrawing() {
        refreshColor();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void refreshColor() {
        glColor4f(place.red, place.green, place.blue, 1.0f);
    }

    public static void drawShapeInShade(Sprite sprite, float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color, color, color);
        glActiveTexture(white);
        sprite.getTex().bind();
        changeShapeToColor();
        sprite.renderNotBind();
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        setBlendAttributesForShadows();
        glColor3f(1, 1, 1);

    }

    public static void drawShapeInShade(Sprite sprite, float color, int xStart, int xEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color, color, color);
        glActiveTexture(white);
        sprite.getTex().bind();
        changeShapeToColor();
        sprite.renderNotBind(xStart, xEnd);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        setBlendAttributesForShadows();
        glColor3f(1, 1, 1);
    }

    public static void drawShapeInShade(Animation animation, float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color, color, color);
        glActiveTexture(white);
        animation.getSprite().getTex().bind();
        changeShapeToColor();
        animation.renderNotBind();
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        setBlendAttributesForShadows();
        glColor3f(1, 1, 1);
    }

    public static void drawShapeInShade(Animation animation, float color, int xStart, int xEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color, color, color);
        glActiveTexture(white);
        animation.getSprite().getTex().bind();
        changeShapeToColor();
        animation.renderNotBind(xStart, xEnd);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        setBlendAttributesForShadows();
        glColor3f(1, 1, 1);
    }

    private static void changeShapeToColor() {
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SRC0_RGB, GL_PREVIOUS);
        glTexEnvi(GL_TEXTURE_ENV, GL_SRC1_RGB, GL_TEXTURE);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
    }

    private static void setBlendAttributesForShadows() {
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeInBlack(Sprite sprite) {
        sprite.getTex().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        sprite.renderNotBind();
        setBlendAttributesForShadows();
    }

    public static void drawShapeInBlack(Sprite sprite, int xStart, int xEnd) {
        sprite.getTex().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        sprite.renderNotBind(xStart, xEnd);
        setBlendAttributesForShadows();
    }

    public static void drawShapeInBlack(Animation anim) {
        anim.getSprite().getTex().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        anim.renderNotBind();
        setBlendAttributesForShadows();
    }

    public static void drawShapeInBlack(Animation anim, int xStart, int xEnd) {
        anim.getSprite().getTex().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        anim.renderNotBind(xStart, xEnd);
        setBlendAttributesForShadows();
    }

    private Drawer() {
    }

    public static void setPlace(Place place) {
        Drawer.place = place;
    }
}
