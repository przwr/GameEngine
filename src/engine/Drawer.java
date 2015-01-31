/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import sprites.Animation;
import game.place.Place;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.Color;
import sprites.Sprite;

/**
 *
 * @author Wojtek
 */
public class Drawer {

    private static final int white = glGenTextures();
    private static Place place;

    public static void setColor(Color color) {
        glColor4f(color.r, color.g, color.b, color.a);
    }

    public static void refreshForRegularDrawing() {
        refreshColor();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void refreshColor() {
        glColor4f(place.red, place.green, place.blue, 1.0f);
    }

    public static void drawRectangleInShade(int xStart, int yStart, int width, int height, float color) {
        glColor3f(color, color, color);
        drawRectangle(xStart, yStart, width, height);
        glColor3f(1f, 1f, 1f);
    }

    public static void drawRectangleInBlack(int xStart, int yStart, int width, int height) {
        glColor3f(0f, 0f, 0f);
        drawRectangle(xStart, yStart, width, height);
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
    
    

    public static void drawCircle(int xStart, int yStart, int radius, int precision) {
        drawElipse(xStart, yStart, radius, radius, precision);
    }

    public static void drawElipse(int xStart, int yStart, int xRadius, int yRadius, int precision) {  //Zbyt mała precyzja tworzy figury foremne
        glTranslatef(xStart, yStart, 0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(0, 0);
        int step = 360 / precision;
        for (int i = 0; i <= 360; i += step) {
            glVertex2f((float) Methods.xRadius(i, xRadius), (float) Methods.yRadius(i, yRadius));
        }
        glVertex2f(xRadius, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }
    
    

    public static void drawShapeInShade(Sprite sprite, float color) {
        prepareDrawingShape(color);
        sprite.getTexture().bind();
        changeShapeToColor();
        sprite.renderNotBind();
        cleanAfterDrawingShape();
    }

    public static void drawShapePartInShade(Sprite sprite, float color, int xStart, int xEnd) {
        prepareDrawingShape(color);
        sprite.getTexture().bind();
        changeShapeToColor();
        sprite.renderPartNotBind(xStart, xEnd);
        cleanAfterDrawingShape();
    }
    
    

    public static void drawShapeInShade(Animation animation, float color) {
        prepareDrawingShape(color);
        animation.getSpriteSheet().getTexture().bind();
        changeShapeToColor();
        animation.renderNotBind();
        cleanAfterDrawingShape();
    }

    public static void drawShapePartInShade(Animation animation, float color, int xStart, int xEnd) {
        prepareDrawingShape(color);
        animation.getSpriteSheet().getTexture().bind();
        changeShapeToColor();
        animation.renderPartNotBind(xStart, xEnd);
        cleanAfterDrawingShape();
    }
    
    

    public static void drawShapeInBlack(Sprite sprite) {
        sprite.getTexture().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        sprite.renderNotBind();
        setBlendAttributesForShadows();
    }

    public static void drawShapePartInBlack(Sprite sprite, int xStart, int xEnd) {
        sprite.getTexture().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        sprite.renderPartNotBind(xStart, xEnd);
        setBlendAttributesForShadows();
    }
    
    

    public static void drawShapeInBlack(Animation animation) {
        animation.getSpriteSheet().getTexture().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        animation.renderNotBind();
        setBlendAttributesForShadows();
    }

    public static void drawShapePartInBlack(Animation animation, int xStart, int xEnd) {
        animation.getSpriteSheet().getTexture().bind();
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
        animation.renderPartNotBind(xStart, xEnd);
        setBlendAttributesForShadows();
    }
    
    
    
    

    private static void prepareDrawingShape(float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(color, color, color);
        glActiveTexture(white);
    }

    private static void cleanAfterDrawingShape() {
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

    public static void setPlace(Place place) {
        Drawer.place = place;
    }
}
