/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Main;
import engine.utilities.ErrorHandler;
import game.gameobject.entities.Player;
import org.newdawn.slick.opengl.Texture;
import sprites.vbo.VertexBufferObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Sprite implements Appearance {

    protected final int xStart;
    protected final int yStart;
    public boolean AA = false;
    public String path;
    protected VertexBufferObject vbo;
    float widthWhole;
    float heightWhole;
    int width;
    int height;
    int actualWidth;
    int actualHeight;
    int xOffset;
    int yOffset;
    private int textureID;
    private Texture texture;
    private String key;
    private double begin;
    private double ending;
    private long lastUsed;

    Sprite(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        lastUsed = System.currentTimeMillis();
        this.widthWhole = 2048;
        this.heightWhole = 2048;
        this.path = path;
        this.xStart = -xStart;
        this.yStart = -yStart;
        this.width = width;
        this.height = height;
    }

    public static Sprite create(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new Sprite(path, folder, width, height, xStart, yStart, spriteBase);
    }

    public void initializeBuffers() {
    }

    @Override
    public boolean bindCheck() {
        if (lastUsed != 0) {
            lastUsed = System.currentTimeMillis();
        }
        int tex = textureID;
        if (tex == 0) {
            glBindTexture(GL_TEXTURE_2D, 0);
            Main.backgroundLoader.requestSprite(this);
        } else {
            if (vbo == null) {
                initializeBuffers();
            }
            if (glGetInteger(GL_TEXTURE_BINDING_2D) != tex) {
                glBindTexture(GL_TEXTURE_2D, tex);
            }
        }
        return tex != 0;
    }

    @Override
    public void render() {
        if (bindCheck()) {
            glBegin(GL_TRIANGLES);
            glTexCoord2f(0, 0);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(0, 1);
            glVertex2f(getXStart(), getYStart() + height);
            glTexCoord2f(1, 1);
            glVertex2f(getXStart() + width, getYStart() + height);

            glTexCoord2f(1, 1);
            glVertex2f(getXStart() + width, getYStart() + height);
            glTexCoord2f(1, 0);
            glVertex2f(getXStart() + width, getYStart());
            glTexCoord2f(0, 0);
            glVertex2f(getXStart(), getYStart());
            glEnd();
        }
    }

    public void renderRotate(float angle) {
        if (bindCheck()) {
            glTranslatef(width / 2, height / 2, 0);
            glRotatef(angle, 0f, 0f, 1f);
            glTranslatef(-width / 2, -height / 2, 0);
            glBegin(GL_TRIANGLES);
            glTexCoord2f(0, 0);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(0, 1);
            glVertex2f(getXStart(), getYStart() + height);
            glTexCoord2f(1, 1);
            glVertex2f(getXStart() + width, getYStart() + height);

            glTexCoord2f(1, 1);
            glVertex2f(getXStart() + width, getYStart() + height);
            glTexCoord2f(1, 0);
            glVertex2f(getXStart() + width, getYStart());
            glTexCoord2f(0, 0);
            glVertex2f(getXStart(), getYStart());
            glEnd();
        }
    }


    @Override
    public void renderPart(int partXStart, int partXEnd) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            glBegin(GL_TRIANGLES);
            glTexCoord2d(((double) partXStart / (double) width), 0);
            glVertex2f(getXStart() + partXStart, getYStart());
            glTexCoord2d(((double) partXStart / (double) width), 1);
            glVertex2f(getXStart() + partXStart, getYStart() + height);
            glTexCoord2d(((double) partXEnd / (double) width), 1);
            glVertex2f(getXStart() + partXEnd, getYStart() + height);

            glTexCoord2d(((double) partXEnd / (double) width), 1);
            glVertex2f(getXStart() + partXEnd, getYStart() + height);
            glTexCoord2d(((double) partXEnd / (double) width), 0);
            glVertex2f(getXStart() + partXEnd, getYStart());
            glTexCoord2d(((double) partXStart / (double) width), 0);
            glVertex2f(getXStart() + partXStart, getYStart());
            glEnd();
        }
    }

    public void renderSpritePiece(float xBegin, float xEnd, float yBegin, float yEnd) {
        if (bindCheck()) {
            glBegin(GL_TRIANGLES);
            glTexCoord2f(xBegin, yBegin);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(xBegin, yEnd);
            glVertex2f(getXStart(), getYStart() + height);
            glTexCoord2f(xEnd, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);

            glTexCoord2f(xBegin, yBegin);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(xEnd, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);
            glTexCoord2f(xEnd, yBegin);
            glVertex2f(getXStart() + width, getYStart());
            glEnd();
        }
    }

    public void renderSpritePieceResized(float xBegin, float xEnd, float yBegin, float yEnd, float width, float height) {
        if (bindCheck()) {
            glBegin(GL_TRIANGLES);
            glTexCoord2f(xBegin, yBegin);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(xBegin, yEnd);
            glVertex2f(getXStart(), getYStart() + height);
            glTexCoord2f(xEnd, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);

            glTexCoord2f(xEnd, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);
            glTexCoord2f(xEnd, yBegin);
            glVertex2f(getXStart() + width, getYStart());
            glTexCoord2f(xBegin, yBegin);
            glVertex2f(getXStart(), getYStart());
            glEnd();
        }
    }


    public void renderSpritePieceMirrored(float xBegin, float xEnd, float yBegin, float yEnd) {
        if (bindCheck()) {
            glBegin(GL_TRIANGLES);
            glTexCoord2f(xEnd, yBegin);
            glVertex2f(getXStart(), getYStart());
            glTexCoord2f(xBegin, yBegin);
            glVertex2f(getXStart() + width, getYStart());
            glTexCoord2f(xBegin, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);

            glTexCoord2f(xBegin, yEnd);
            glVertex2f(getXStart() + width, getYStart() + height);
            glTexCoord2f(xEnd, yEnd);
            glVertex2f(getXStart(), getYStart() + height);
            glTexCoord2f(xEnd, yBegin);
            glVertex2f(getXStart(), getYStart());
            glEnd();
        }
    }

    public void renderSpritePiecePart(float xBegin, float yBegin, float yEnd, int partXStart, int partXEnd, float xTiles) {
        if (bindCheck()) {
            begin = xBegin + ((double) partXStart) / (double) width / xTiles;
            ending = xBegin + ((double) partXEnd) / (double) width / xTiles;
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            glBegin(GL_TRIANGLES);
            glTexCoord2d(begin, yBegin);
            glVertex2f(getXStart() + partXStart, getYStart());
            glTexCoord2d(begin, yEnd);
            glVertex2f(getXStart() + partXStart, getYStart() + height);
            glTexCoord2d(ending, yEnd);
            glVertex2f(getXStart() + partXEnd, getYStart() + height);

            glTexCoord2d(ending, yEnd);
            glVertex2f(getXStart() + partXEnd, getYStart() + height);
            glTexCoord2d(ending, yBegin);
            glVertex2f(getXStart() + partXEnd, getYStart());
            glTexCoord2d(begin, yBegin);
            glVertex2f(getXStart() + partXStart, getYStart());
            glEnd();
        }
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

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.heightWhole = texture.getImageHeight();
        this.widthWhole = texture.getImageWidth();
        this.textureID = texture.getTextureID();
        initializeBuffers();
    }

    public synchronized void releaseTexture() {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) == textureID) {
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        if (textureID != 0) {
            glDeleteTextures(textureID);
            textureID = 0;
        }
        if (texture != null) {
            texture.release();
            texture = null;
        }
        if (textureID != 0) {
            textureID = 0;
        }
        if (vbo != null) {
            vbo.clear();
            vbo = null;
        }
        //System.out.println("Unloaded: " + path);
    }

    public String getPath() {
        return path;
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

    public long getLastUsed() {
        return lastUsed;
    }

    @Override
    protected void finalize() {
        if (textureID != 0 || texture != null) {
            releaseTexture();
        }
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public void setUnload(boolean unload) {
        if (unload) {
            lastUsed = System.currentTimeMillis();
        } else {
            lastUsed = 0;
        }
    }
}
