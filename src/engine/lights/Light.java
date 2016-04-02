/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GameObject;
import game.place.cameras.Camera;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.SpriteSheet;
import sprites.fbo.FrameBufferObject;
import sprites.fbo.MultiSampleFrameBufferObject;
import sprites.fbo.RegularFrameBufferObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Light {

    private static final int LEFT_TOP_PART = 0;
    private static final int RIGHT_TOP_PART = 1;
    private static final int LEFT_BOTTOM_PART = 2;
    private static final int RIGHT_BOTTOM_PART = 3;
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

    private Light(Sprite sprite, Color color, int width, int height, GameObject owner, boolean giveShadows) {
        this.color = color;
        this.owner = owner;
        this.sprite = sprite;
        this.sprite.setUnload(false);
        this.width = width;
        this.height = adjustHeightForWindow(height);
        this.giveShadows = giveShadows;
        setFrameBuffer();
        setShift();
    }

    private Light(SpriteSheet spriteSheet, Color color, int width, int height, GameObject owner, int piece) {
        this.color = color;
        this.owner = owner;
        this.spriteSheet = spriteSheet;
        this.spriteSheet.setUnload(false);
        this.width = Methods.roundDouble(width / (1.75f - Settings.nativeScale));
        this.height = Methods.roundDouble(height / (1.75f - Settings.nativeScale));
        this.piece = piece;
        this.giveShadows = false;
        this.widthWholeLight = this.width * 2;
        this.heightWholeLight = this.height * 2;
        setShift();
    }

    private static int adjustHeightForWindow(int height) {
        if (height > Display.getHeight()) {
            return Display.getHeight();
        }
        return height;
    }

    public static Light create(Sprite sprite, Color color, int width, int height, GameObject owner) {
        return new Light(sprite, color, width, height, owner, true);
    }

    public static Light createNoShadows(Sprite sprite, Color color, int width, int height, GameObject owner) {
        return new Light(sprite, color, width, height, owner, false);
    }

    public static Light createNoShadows(SpriteSheet spriteSheet, Color color, int width, int height, GameObject owner, int piece) {
        return new Light(spriteSheet, color, width, height, owner, piece);
    }

    private void setFrameBuffer() {
        if (!Settings.shadowOff && giveShadows) {
            frameBufferObject = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(width, height)
                    : new RegularFrameBufferObject(width, height);
        }
    }

    public void render(int x, int y, Camera camera) {
        glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        glTranslatef(x, y, 0);
        glScaled(camera.getScale(), camera.getScale(), 1);
        glTranslatef(owner.getX() - xCenterShift, owner.getY() - yCenterShift, 0);
        if (spriteSheet != null) {
            spriteSheet.renderPiece(piece);
        } else {
            sprite.render();
        }
        glTranslatef(-owner.getX() + xCenterShift, -owner.getY() + yCenterShift, 0);
        glScaled(1 / camera.getScale(), 1 / camera.getScale(), 1);
        glTranslatef(-x, -y, 0);
    }

    public void render(int height) {
        glColor3f(color.r, color.g, color.b);
        glTranslatef(0, height - this.height, 0);
        if (spriteSheet != null) {
            spriteSheet.renderPiece(piece);
        } else {
            sprite.render();
        }
        glTranslatef(0, -height + this.height, 0);
    }

    private void setShift() {
        if (giveShadows) {
            xCenterShift = width / 2;
            yCenterShift = height / 2;
        } else {
            switch (piece) {
                case LEFT_TOP_PART:
                    xCenterShift = width;
                    yCenterShift = height;
                    break;
                case RIGHT_TOP_PART:
                    xCenterShift = 0;
                    yCenterShift = height;
                    break;
                case LEFT_BOTTOM_PART:
                    xCenterShift = width;
                    yCenterShift = 0;
                    break;
                case RIGHT_BOTTOM_PART:
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

    public void setColor(Color color) {
        this.color = color;
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

    public int getXRightEdge() {
        if (giveShadows) {
            return xCenterShift;
        } else if (xCenterShift > 0) {
            return 0;
        } else {
            return width;
        }
    }

    public int getYTopEdge() {
        if (giveShadows) {
            return yCenterShift;
        } else if (yCenterShift > 0) {
            return height;
        } else {
            return 0;
        }
    }

    public int getXLeftEdge() {
        if (giveShadows) {
            return xCenterShift;
        } else {
            return width;
        }
    }

    public int getYBottomEdge() {
        if (giveShadows) {
            return yCenterShift;
        } else if (yCenterShift > 0) {
            return 0;
        } else {
            return height;
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

    public void setPiece(int piece) {
        this.piece = piece;
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
