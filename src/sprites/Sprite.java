/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

    protected SpriteBase base;
    protected int width;
    protected int height;
    protected int sx;
    protected int sy;
    protected String key;
    protected int id;

    public Sprite(String textureKey, int width, int height, SpriteBase base) {
        this(textureKey, width, height, 0, 0, base);
    }

    public Sprite(Texture texture, int width, int height) {
        this(texture, width, height, 0, 0);
    }

    public Sprite(String textureKey, SpriteBase base) {
        if (textureKey != null) {
            this.texture = loadTexture(textureKey);
        }
        this.key = textureKey;
        this.base = base;
        this.sx = 0;
        this.sy = 0;
        this.width = texture.getTextureWidth();
        this.height = texture.getTextureHeight();
    }

    public Sprite(String textureKey, int width, int height, int sx, int sy, SpriteBase base) {
        if (textureKey != null) {
            this.texture = loadTexture(textureKey);
        }
        this.key = textureKey;
        this.base = base;
        this.sx = -sx;
        this.sy = -sy;
        this.width = width;
        this.height = height;
    }

    public Sprite(Texture texture, int width, int height, int sx, int sy) {
        this.texture = texture;
        this.sx = -sx;
        this.sy = -sy;
        this.width = width;
        this.height = height;
    }

    public static Texture loadTexture(String key) {
        try {
            return TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("/res/" + key + ".png"), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BufferedImage getImg() {
        try {
            return ImageIO.read(Sprite.class.getResourceAsStream("/res/" + key + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void bindCheck() {
        if (base == null) {
            texture.bind();
        } else if (base.getLastTex() != id) {
            texture.bind();
            base.setLastTex(id);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public void render() {
        bindCheck();
        glTranslatef(sx, sy, 0);
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

    public void renderNotBind() {
        glTranslatef(sx, sy, 0);
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

    public void renderFull() {
        bindCheck();
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

    public void renderFlipped() {
        bindCheck();
        glTranslatef(sx, sy, 0);
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

    public void renderMirrored(boolean flip) {
        if (flip) {
            renderFlipped();
        } else {
            render();
        }
    }

    public void renderRotated(double angle) {
        glRotatef((float) angle, 0, 0, 1);
        render();
    }

    public void renderTexPart(float bx, float ex, float by, float ey) {
        bindCheck();
        glBegin(GL_QUADS);
        glTexCoord2f(bx, by);
        glVertex2f(0, 0);
        glTexCoord2f(bx, ey);
        glVertex2f(0, height);
        glTexCoord2f(ex, ey);
        glVertex2f(width, height);
        glTexCoord2f(ex, by);
        glVertex2f(width, 0);
        glEnd();
    }

    public void renderPartMirrored(boolean flip, float bx, float ex, float by, float ey) {
        bindCheck();
        glTranslatef(sx, sy, 0);
        glBegin(GL_QUADS);
        if (flip) {
            glTexCoord2f(ex, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, by);
            glVertex2f(width, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(width, height);
            glTexCoord2f(ex, ey);
            glVertex2f(0, height);
        } else {
            glTexCoord2f(bx, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(0, height);
            glTexCoord2f(ex, ey);
            glVertex2f(width, height);
            glTexCoord2f(ex, by);
            glVertex2f(width, 0);
        }
        glEnd();
    }
    
        public void renderPartMirroredNotBind(boolean flip, float bx, float ex, float by, float ey) {
        bindCheck();
        glTranslatef(sx, sy, 0);
        glBegin(GL_QUADS);
        if (flip) {
            glTexCoord2f(ex, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, by);
            glVertex2f(width, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(width, height);
            glTexCoord2f(ex, ey);
            glVertex2f(0, height);
        } else {
            glTexCoord2f(bx, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(0, height);
            glTexCoord2f(ex, ey);
            glVertex2f(width, height);
            glTexCoord2f(ex, by);
            glVertex2f(width, 0);
        }
        glEnd();
    }

    public void renderPartRotated(double angle, float bx, float ex, float by, float ey) {
        glRotatef((float) angle, 0, 0, 1);
        renderTexPart(bx, ex, by, ey);
    }

    public void renderFaded(Color c) {
        System.err.println(c.r + " " + c.g + " " + c.b);
        glColor4f(c.r, c.g, c.b, c.a);
        render();
        glColor4f(0.5f, 0.5f, 0.5f, 1f);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public int getSx() {
        return width;
    }

    public int getSy() {
        return height;
    }

    public void setSx(int sx) {
        this.sx = -sx;
    }

    public void setSy(int sy) {
        this.sy = -sy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Texture getTex() {
        return texture;
    }
}
