/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

import engine.utilities.Drawer;
import game.gameobject.entities.Player;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class FrameBufferedSpriteSheet {

    private static final int fboSize = 512;
    private final FrameBufferObject[] frameBufferObjects;
    private final int xStart;
    private final int yStart;
    private final int xDelta;
    private final int yDelta;
    private final int xFrames;
    private final int yFrames;
    private final int frameWidth;
    private final int frameHeight;
    private final int framesCount;
    private final int framesPerSpriteSheet;
    private int currentSpriteSheet;
    private int currentSpriteSheetFrame;
    private boolean upToDate;

    public FrameBufferedSpriteSheet(int frameWidth, int frameHeight, int framesCount,
                                    int xStart, int yStart, int xDelta, int yDelta) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesCount = framesCount;
        this.xDelta = xDelta;
        this.yDelta = yDelta;
        this.xStart = -xStart;
        this.yStart = -yStart;
        xFrames = fboSize / frameWidth;
        yFrames = fboSize / frameHeight;
        framesPerSpriteSheet = xFrames * yFrames;

        int spriteSheetsCount = (int) (framesCount / ((float) framesPerSpriteSheet));
        if (spriteSheetsCount > spriteSheetsCount) {
            spriteSheetsCount++;
        }

        frameBufferObjects = new FrameBufferObject[spriteSheetsCount];
        for (int i = 0; i < frameBufferObjects.length; i++) {
            frameBufferObjects[i] = new RegularFrameBufferObject(fboSize, fboSize);
        }
    }

    public void updateFrame(int currentFrame) {
        currentSpriteSheet = currentFrame / framesPerSpriteSheet;
        currentSpriteSheetFrame = currentFrame % framesPerSpriteSheet;
    }

    public void updateTexture(Player owner) {
        if (!upToDate) {
            int frame = 0;
            for (FrameBufferObject frameBufferObject : frameBufferObjects) {
                prepareTextureUpdate(frameBufferObject);
                frames:
                for (int j = 0; j < yFrames; j++) {
                    for (int k = 0; k < xFrames; k++) {
                        //owner.renderClothedUpperBody(frame);
//                        owner.renderShadow(0,0, null);
                        glTranslatef(frameWidth, 0, 0);
                        frame++;
                        if (frame > framesCount) {
                            break frames;
                        }
                    }
                    glTranslatef(-fboSize, frameHeight, 0);
                }
                endTextureUpdate(frameBufferObject);
            }
            upToDate = true;
        }
    }

    private void prepareTextureUpdate(FrameBufferObject frameBufferObject) {
        glPushMatrix();
        frameBufferObject.activate();
        Drawer.clearScreen(0);
        glColor3f(1, 1, 1);
        glTranslatef(-xStart - xDelta, Display.getHeight() - fboSize - yStart - yDelta, 0);
    }

    private void endTextureUpdate(FrameBufferObject frameBufferObject) {
        glPopMatrix();
        frameBufferObject.deactivate();
    }

    public void renderWhole() {
        int xShift = (currentSpriteSheetFrame % xFrames) * frameWidth;
        int yShift = fboSize - frameHeight - ((currentSpriteSheetFrame / xFrames) * frameHeight);
        Drawer.translate(xStart, yStart);
        frameBufferObjects[currentSpriteSheet].render();
    }

    public void render() {
        int xShift = (currentSpriteSheetFrame % xFrames) * frameWidth;
        int yShift = fboSize - frameHeight - ((currentSpriteSheetFrame / xFrames) * frameHeight);
        Drawer.translate(xStart, yStart);
        frameBufferObjects[currentSpriteSheet].renderPiece(-xShift, -yShift, xShift, yShift, xShift + frameWidth, yShift + frameHeight);
    }

    public void renderPart(int partXStart, int partXEnd) {
        int xShift = (currentSpriteSheetFrame % xFrames) * frameWidth;
        int yShift = fboSize - frameHeight - ((currentSpriteSheetFrame / xFrames) * frameHeight);
        Drawer.translate(xStart, yStart);
        frameBufferObjects[currentSpriteSheet].renderPiece(-xShift, -yShift, xShift + partXStart, yShift, xShift + partXEnd, yShift + frameHeight);
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

}
