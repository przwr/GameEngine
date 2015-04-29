/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author przemek
 */
public class Sprite implements Appearance {

    protected Texture texture;

    protected SpriteBase spriteBase;
    protected int width;
    protected int height;
    protected int xStart;
    protected int yStart;
    
    protected int actualWidth;
    protected int actualHeight;
    protected int xOffset;
    protected int yOffset;
    protected String key;

    private double begin, ending;

    public static Sprite create(String textureKey, int width, int height, SpriteBase base) {
        return new Sprite(textureKey, width, height, 0, 0, base);
    }

    protected Sprite(String textureKey, int width, int height, int xStart, int yStart, SpriteBase base) {
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

    public static Texture loadTexture(String textureKey) {
        try {
            return TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("/res/textures/" + textureKey + ".png"), GL_LINEAR);
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

    @Override
    public void bindCheckByID() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture.getTextureID()) {
            glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
        }
    }

    @Override
    public void bindCheckByTexture() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != texture.getTextureID()) {
            texture.bind();
        }
    }

    @Override
    public void render() {
        bindCheckByID();
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

    @Override
    public void renderMirrored() {
        bindCheckByID();
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

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        bindCheckByID();
        glTranslatef(xStart, yStart, 0);
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
        bindCheckByID();
        glTranslatef(xStart, yStart, 0);
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
        bindCheckByID();
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

    public void renderSpritePieceMirrored(float xBegin, float xEnd, float yBegin, float yEnd) {
        bindCheckByID();
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

    public void renderSpritePiecePart(float xBegin, float xEnd, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) {
        bindCheckByID();
        begin = xBegin + ((double) partXStart) / (double) width / xTiles;
        ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
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

    public void renderSpritePiecePartMirrored(float xBegin, float xEnd, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) { //NOT TESTED!
        bindCheckByID();
        begin = xBegin + ((double) partXStart) / (double) width / xTiles;
        ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
        glTranslatef(xStart, yStart, 0);
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

    public Texture getTexture() {
        return texture;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Sprite) {
            Sprite s = (Sprite) object;
            return s.texture.getTextureID() == texture.getTextureID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.texture.getTextureID());
        return hash;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }
}
