/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import engine.Methods;
import game.place.fbo.RegularFrameBufferObject;
import game.place.fbo.MultisampleFrameBufferObject;
import game.place.fbo.FrameBufferObject;
import game.Settings;
import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class Light {

    private final GameObject owner;
    private final boolean giveShadows;
    private final int width, height;
    private Color color;

    private SpriteSheet spriteSheet;
    private int xCenterShift, yCenterShift;
    private int piece;
    private int widthWholeLight, heightWholeLight;

    private Sprite sprite;
    private FrameBufferObject frameBufferObject;

    public static Light create(Sprite sprite, Color color, int width, int height, GameObject owner) {
        return new Light(sprite, color, width, height, owner);
    }

    private Light(Sprite sprite, Color color, int width, int height, GameObject owner) {
        this.color = color;
        this.owner = owner;
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        this.giveShadows = true;
        setFrameBuffer();
        setShift();
    }

    private void setFrameBuffer() {
        if (!Settings.shadowOff) {
            frameBufferObject = (Settings.samplesCount > 0) ? new MultisampleFrameBufferObject(width, height)
                    : new RegularFrameBufferObject(width, height);
        }
    }

    public static Light create(SpriteSheet spriteSheet, Color color, int width, int height, GameObject owner, int piece) {
        return new Light(spriteSheet, color, width, height, owner, piece);
    }

    private Light(SpriteSheet spriteSheet, Color color, int width, int height, GameObject owner, int piece) {
        this.color = color;
        this.owner = owner;
        this.spriteSheet = spriteSheet;
        this.width = width;
        this.height = height;
        this.piece = piece;
        this.giveShadows = false;
        this.widthWholeLight = width * 2;
        this.heightWholeLight = height * 2;
        setShift();
    }

    private void setShift() {
        if (giveShadows) {
            xCenterShift = width / 2;
            yCenterShift = height / 2;
        } else {
            switch (piece) {
                case 0:
                    xCenterShift = Methods.roundDouble(width / (1.75f - Settings.nativeScale));
                    yCenterShift = Methods.roundDouble(height / (1.75f - Settings.nativeScale));
                    break;
                case 1:
                    xCenterShift = 0;
                    yCenterShift = Methods.roundDouble(height / (1.75f - Settings.nativeScale));
                    break;
                case 2:
                    xCenterShift = Methods.roundDouble(width / (1.75f - Settings.nativeScale));
                    yCenterShift = 0;
                    break;
                case 3:
                    xCenterShift = 0;
                    yCenterShift = 0;
                    break;
            }
        }
    }

    public void setSize(int width, int height) {
        spriteSheet.setWidth(width);
        spriteSheet.setHeight(height);
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void render(int x, int y) {
        if (spriteSheet != null) {
            glColor3f(color.getRed(), color.getGreen(), color.getBlue());
            glPushMatrix();
            glTranslatef(x, y, 0);
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(owner.getX() - xCenterShift, owner.getY() - yCenterShift, 0);
            spriteSheet.renderPiece(piece);
            glPopMatrix();
        } else {
            glColor3f(color.getRed(), color.getGreen(), color.getBlue());
            glPushMatrix();
            glTranslatef(x, y, 0);
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(owner.getX(), owner.getY(), 0);
            sprite.render();
            glPopMatrix();
        }
    }

    public void render(int height) {
        if (spriteSheet != null) {
            glColor3f(color.r, color.g, color.b);
            glPushMatrix();
            glTranslatef(0, height, 0);
            spriteSheet.renderPiece(piece);
            glPopMatrix();
        } else {
            glColor3f(color.r, color.g, color.b);
            glPushMatrix();
            glTranslatef(0, height, 0);
            sprite.render();
            glPopMatrix();
        }
    }

    public boolean isEmits() {
        return owner.isEmits();
    }

    public boolean isGiveShadows() {
        return giveShadows;
    }

    public int getX() {
        return owner.getX();
    }

    public int getY() {
        return owner.getY();
    }

    public int getXCenterShift() {
        return xCenterShift;
    }

    public int getYCenterShift() {
        return yCenterShift;
    }

    public int getXEdge() {
        if (giveShadows) {
            return xCenterShift;
        } else {
            return 2 * width - xCenterShift;
        }
    }

    public int getYEdge() {
        if (giveShadows) {
            return yCenterShift;
        } else {
            return 2 * height - yCenterShift;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOwnerCollisionWidth() {
        return owner.getCollisionWidth();
    }

    public int getOwnerCollisionHeight() {
        return owner.getCollisionHeight();
    }

    public int getPiece() {
        return piece;
    }

    public Figure getOwnerCollision() {
        return owner.getCollision();
    }

    public FrameBufferObject getFrameBufferObject() {
        return frameBufferObject;
    }

    public GameObject getOwner() {
        return owner;
    }

    public int getWidthWholeLight() {
        if (giveShadows) {
            return width;
        } else {
            return widthWholeLight;
        }
    }

    public int getHeightWholeLight() {
        if (giveShadows) {
            return height;
        } else {
            return heightWholeLight;
        }

    }
}
