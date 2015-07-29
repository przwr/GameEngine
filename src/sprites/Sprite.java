/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Drawer;
import java.util.Objects;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author przemek
 */
public class Sprite implements Appearance {

    protected int texture;

    protected SpriteBase spriteBase;
    protected byte[] data;
    protected float widthWhole, heightWhole;
    protected int width;
    protected int height;
    protected int xStart;
    protected int yStart;

    protected int actualWidth;
    protected int actualHeight;
    protected int xOffset;
    protected int yOffset;
    protected String key;

    protected double begin, ending;

    public static Sprite create(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new Sprite(texture, width, height, xStart, yStart, spriteBase);
    }

    protected Sprite(int texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        this.texture = texture;
        this.spriteBase = spriteBase;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    protected Sprite(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        this.widthWhole = texture.getImageWidth();
        this.heightWhole = texture.getImageHeight();
        this.texture = texture.getTextureID();
        this.data = texture.getTextureData();
        this.spriteBase = spriteBase;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    @Override
    public void bindCheck() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }
    }

    protected void moveToStart() {
        if (xStart != 0 && yStart != 0) {
            Drawer.translate(xStart, yStart);
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
    public void renderPartMirrored(int partXstart, int partXend) {
        bindCheck();
        moveToStart();
        glBegin(GL_QUADS);
        glTexCoord2d(((double) partXend / (double) width), 0);
        glVertex2f(partXstart, 0);
        glTexCoord2d(((double) partXstart / (double) width), 0);
        glVertex2f(partXend, 0);
        glTexCoord2d(((double) partXstart / (double) width), 1);
        glVertex2f(partXend, height);
        glTexCoord2d(((double) partXend / (double) width), 1);
        glVertex2f(partXstart, height);
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

    public void renderSpritePiecePart(float xBegin, float xEnd, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) {
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

    public void renderSpritePiecePartMirrored(float xBegin, float xEnd, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) { //NOT TESTED!
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXStart() {
        return xStart;
    }

    public int getYStart() {
        return yStart;
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

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public SpriteBase getSpriteBase() {
        return spriteBase;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public float getWidthWhole() {
        return widthWhole;
    }

    public float getHeightWhole() {
        return heightWhole;
    }

    public byte[] getPixelData() {
        return data;
    }
}
