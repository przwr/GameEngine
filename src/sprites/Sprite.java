/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import org.newdawn.slick.opengl.Texture;
import sprites.vbo.VertexBufferObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Sprite implements Appearance {

    private static Point ZERO = new Point(0, 0);
    protected final int xStart;
    protected final int yStart;
    public boolean AA = false, canBeMirrored = false;
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


    protected void initializeBuffers() {
        float[] vertices = {
                xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart + height,
                xStart + width, yStart
        };
        float[] textureCoordinates = {
                0, 0,                           //Całość
                0, 1f,
                1f, 1f,
                1f, 0
        };
        int[] indices = {0, 1, 3, 2};
        vbo = VertexBufferObject.create(vertices, textureCoordinates, indices);
    }

    @Override
    public void renderStaticShadow(GameObject object) {
        float scale = (float) Methods.ONE_BY_SQRT_ROOT_OF_2;
        float changeX = (float) (object.getFloatHeight());
        Drawer.regularShader.scaleNoReset(1f, scale);
        Drawer.regularShader.translateNoReset(changeX, 0);
        Drawer.regularShader.rotateNoReset(90);
        render();
        Drawer.regularShader.rotateNoReset(-90);
        Drawer.regularShader.translateNoReset(-changeX, 0);
        Drawer.regularShader.scaleNoReset(1f, 1f / scale);
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
            vbo.renderTextured(0, 4);
        }
    }

    @Override
    public void renderShadow(float color) {
        if (bindCheck()) {
            Drawer.shadowShader.loadColorModifier(color);
            vbo.renderTextured(0, 4);
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
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.regularShader.resetTransformationMatrix();
            vbo.renderTextured(0, 4);
        }
    }


    @Override
    public void renderShadowPart(int partXStart, int partXEnd, float color) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            Drawer.shadowShader.loadColorModifier(color);
            vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width, (partXEnd - width) / (float) width);
            Drawer.shadowShader.loadSizeModifier(vectorModifier);
            vbo.renderTextured(0, 4);
            Drawer.shadowShader.loadSizeModifier(ZERO_VECTOR);
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
    public void updateTexture(GameObject owner) {
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
