/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import game.gameobject.entities.Player;
import org.newdawn.slick.opengl.Texture;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Sprite implements Appearance {

    final int xStart;
    final int yStart;
    private final int texture;
    private final SpriteBase spriteBase;
    float widthWhole;
    float heightWhole;
    int width;
    int height;
    int actualWidth;
    int actualHeight;
    int xOffset;
    int yOffset;
    private String key;
    private String folder;

    private double begin;
    private double ending;

    Sprite(int texture, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        this.texture = texture;
        this.spriteBase = spriteBase;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    Sprite(Texture texture, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        this.widthWhole = texture.getImageWidth();
        this.heightWhole = texture.getImageHeight();
        this.texture = texture.getTextureID();
        this.spriteBase = spriteBase;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    public static Sprite create(Texture texture, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new Sprite(texture, folder, width, height, xStart, yStart, spriteBase);
    }

    @Override
    public void bindCheck() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }


    @Override
    public void render() {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, 0);
        glEnd();
    }

    @Override
    public void renderMirrored() {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2f(1, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(width, 0);
        glTexCoord2f(0, 1);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(0, height);
        glEnd();
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2d(((double) partXStart / (double) width), 0);
        glVertex2f(partXStart, 0);
        glTexCoord2d(((double) partXStart / (double) width), 1);
        glVertex2f(partXStart, height);
        glTexCoord2d(((double) partXEnd / (double) width), 1);
        glVertex2f(partXEnd, height);
        glTexCoord2d(((double) partXEnd / (double) width), 0);
        glVertex2f(partXEnd, 0);
        glEnd();
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2d(((double) partXEnd / (double) width), 0);
        glVertex2f(partXStart, 0);
        glTexCoord2d(((double) partXStart / (double) width), 0);
        glVertex2f(partXEnd, 0);
        glTexCoord2d(((double) partXStart / (double) width), 1);
        glVertex2f(partXEnd, height);
        glTexCoord2d(((double) partXEnd / (double) width), 1);
        glVertex2f(partXStart, height);
        glEnd();
    }


    public void renderSpritePiece(float xBegin, float xEnd, float yBegin, float yEnd) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2f(xBegin, yBegin);
        glVertex2f(0, 0);
        glTexCoord2f(xBegin, yEnd);
        glVertex2f(0, height);
        glTexCoord2f(xEnd, yEnd);
        glVertex2f(width, height);
        glTexCoord2f(xEnd, yBegin);
        glVertex2f(width, 0);
        glEnd();
    }

    public void renderSpritePieceResized(float xBegin, float xEnd, float yBegin, float yEnd, float width, float height) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2f(xBegin, yBegin);
        glVertex2f(0, 0);
        glTexCoord2f(xBegin, yEnd);
        glVertex2f(0, height);
        glTexCoord2f(xEnd, yEnd);
        glVertex2f(width, height);
        glTexCoord2f(xEnd, yBegin);
        glVertex2f(width, 0);
        glEnd();
    }

    public void renderSpritePieceHere(float xBegin, float xEnd, float yBegin, float yEnd) {
        bindCheck();
        glBegin(GL_QUADS);
        glTexCoord2f(xBegin, yBegin);
        glVertex2f(0, 0);
        glTexCoord2f(xBegin, yEnd);
        glVertex2f(0, height);
        glTexCoord2f(xEnd, yEnd);
        glVertex2f(width, height);
        glTexCoord2f(xEnd, yBegin);
        glVertex2f(width, 0);
        glEnd();
    }

    public void renderSpritePieceMirrored(float xBegin, float xEnd, float yBegin, float yEnd) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2f(xEnd, yBegin);
        glVertex2f(0, 0);
        glTexCoord2f(xBegin, yBegin);
        glVertex2f(width, 0);
        glTexCoord2f(xBegin, yEnd);
        glVertex2f(width, height);
        glTexCoord2f(xEnd, yEnd);
        glVertex2f(0, height);
        glEnd();
    }

    public void renderSpritePiecePart(float xBegin, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) {
        bindCheck();
        begin = xBegin + ((double) partXStart) / (double) width / xTiles;
        ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2d(begin, yBegin);
        glVertex2f(partXStart, 0);
        glTexCoord2d(begin, yEnd);
        glVertex2f(partXStart, height);
        glTexCoord2d(ending, yEnd);
        glVertex2f(partXEnd, height);
        glTexCoord2d(ending, yBegin);
        glVertex2f(partXEnd, 0);
        glEnd();
    }

    public void renderSpritePiecePartMirrored(float xBegin, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) { //NOT TESTED!
        bindCheck();
        begin = xBegin + ((double) partXStart) / (double) width / xTiles;
        ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2d(ending, yBegin);
        glVertex2f(partXStart, 0);
        glTexCoord2d(begin, yBegin);
        glVertex2f(partXEnd, 0);
        glTexCoord2d(begin, yEnd);
        glVertex2f(partXEnd, height);
        glTexCoord2d(ending, yEnd);
        glVertex2f(partXStart, height);
        glEnd();
    }

    void moveToStart() {
        if (xStart != 0 && yStart != 0) {
            Drawer.translate(xStart, yStart);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Sprite) {
            Sprite s = (Sprite) object;
            return s.texture == texture;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(texture);
        return hash;
    }

    public float getWidthWhole() {
        return widthWhole;
    }

    public float getHeightWhole() {
        return heightWhole;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTexture() {
        return texture;
    }

    public SpriteBase getSpriteBase() {
        return spriteBase;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    @Override
    public int getXStart() {
        return xStart;
    }

    @Override
    public int getYStart() {
        return yStart;
    }

    @Override
    public int getActualWidth() {
        return actualWidth;
    }

    @Override
    public int getActualHeight() {
        return actualHeight;
    }

    @Override
    public int getXOffset() {
        return xOffset;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }

    @Override
    public void updateTexture(Player owner) {
        ErrorHandler.warring("Incorrect method use", this);
    }

    @Override
    public void updateFrame() {
        ErrorHandler.warring("Incorrect method use", this);
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }
}
