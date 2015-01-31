/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author przemek
 */
public class Sprite {

    protected Texture texture;

    protected SpriteBase spriteBase;
    protected int width;
    protected int height;
    protected int xStart;
    protected int yStart;
    protected String key;
    protected int id;

    public static Sprite create(String textureKey, int width, int height, SpriteBase spriteBase) {
        return new Sprite(textureKey, width, height, 0, 0, spriteBase);
    }

    private Sprite(String textureKey, int width, int height, int xStart, int yStart, SpriteBase base) {
        if (textureKey != null) {
            this.texture = loadTexture(textureKey);
            this.key = textureKey;
            this.spriteBase = base;
            this.xStart = -xStart;
            this.yStart = -yStart;
            this.width = width;
            this.height = height;
        }
    }

    private static Texture loadTexture(String textureKey) {
        try {
            return TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("/res/" + textureKey + ".png"), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Sprite create(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new Sprite(texture, width, height, xStart, yStart, spriteBase);
    }

    protected Sprite(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        this.texture = texture;
        this.spriteBase = spriteBase;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    protected void bindCheck() {
        if (spriteBase == null) {
            texture.bind();
        } else if (spriteBase.getLastTex() != id) {
            texture.bind();
            spriteBase.setLastTex(id);
        }
    }

    public void render() {
        bindCheck();
        renderNotBind();
    }

    public void renderNotBind() {
        glTranslatef(xStart, yStart, 0);
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

    public void renderPart(int xStart, int xEnd) {
        bindCheck();
        renderPartNotBind(xStart, xEnd);
    }

    public void renderPartNotBind(int xStart, int xEnd) {
        glTranslatef(this.xStart, yStart, 0);
        glBegin(GL_QUADS);
        glTexCoord2d(((double) xStart / (double) width), 0);
        glVertex2f(xStart, 0);
        glTexCoord2d(((double) xStart / (double) width), 1);
        glVertex2f(xStart, height);
        glTexCoord2d(((double) xEnd / (double) width), 1);
        glVertex2f(xEnd, height);
        glTexCoord2d(((double) xEnd / (double) width), 0);
        glVertex2f(xEnd, 0);
        glEnd();
    }

    public void renderMirrored() {
        bindCheck();
        renderMirroredNotBind();
    }

    public void renderMirroredNotBind() {
        glTranslatef(xStart, yStart, 0);
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

    public void renderPartMirrored(int xStart, int xEnd) {
        bindCheck();
        renderPartMirroredNotBind(xStart, xEnd);
    }

    public void renderPartMirroredNotBind(int xStart, int xEnd) {
        glTranslatef(xStart, yStart, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(1, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(width, 0);
        glTexCoord2f(0, 1);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(0, height);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, 0);

        glTexCoord2d(((double) xStart / (double) width), 0);
        glVertex2f(xStart, 0);
        glTexCoord2d(((double) xStart / (double) width), 1);
        glVertex2f(xStart, height);
        glTexCoord2d(((double) xEnd / (double) width), 1);
        glVertex2f(xEnd, height);
        glTexCoord2d(((double) xEnd / (double) width), 0);
        glVertex2f(xEnd, 0);

        glEnd();
    }

    public void renderRotated(double angle) { // DO WE NEED THIS??
        glRotatef((float) angle, 0, 0, 1);
        render();
    }

    public void renderPieceNotBind(float xBegin, float xEnd, float yBegin, float yEnd) {
        glTranslatef(xStart, yStart, 0);
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

    public void renderPiecePartNotBind(float xBegin, float xEnd, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) {
        double begin = xBegin + ((double) partXStart) / (double) width / xTiles;
        double ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
        glTranslatef(xStart, yStart, 0);
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

    public void renderPieceMirroredNotBind(float xBegin, float xEnd, float yBegin, float yEnd) {
        glTranslatef(xStart, yStart, 0);
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

    public void renderPieceRotatedNotBind(double angle, float xBegin, float xEnd, float yBegin, float yEnd) {
        glRotatef((float) angle, 0, 0, 1);
        renderPieceNotBind(xBegin, xEnd, yBegin, yEnd);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSx() {
        return xStart;
    }

    public int getSy() {
        return yStart;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void setXstart(int xStart) {
        this.xStart = -xStart;
    }

    public void setYstart(int yStart) {
        this.yStart = -yStart;
    }

}
