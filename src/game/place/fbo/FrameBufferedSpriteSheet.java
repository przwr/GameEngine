/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

import game.Settings;
import game.gameobject.Player;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author przemek
 */
public class FrameBufferedSpriteSheet {

    private static int fboSize = 512;
    private FrameBufferObject[] frameBufferObjects;
    private int xStart, yStart, xFrames, yFrames, frameWidth, frameHeight, framesCount, framesPerSpriteSheet, currentSpriteSheet, currentSpriteSheetFrame;
    private boolean upToDate;

    public FrameBufferedSpriteSheet(int frameWidth, int frameHeight, int framesCount, int xStart, int yStart) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.framesCount = framesCount;
        this.xStart = xStart;
        this.yStart = yStart;
        xFrames = fboSize / frameWidth;
        yFrames = fboSize / frameHeight;
        framesPerSpriteSheet = xFrames * yFrames;

        float spriteSheetsCount = framesCount / ((float) framesPerSpriteSheet);
        if (spriteSheetsCount > (int) spriteSheetsCount) {
            spriteSheetsCount++;
        }

        frameBufferObjects = new FrameBufferObject[(int) spriteSheetsCount];
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
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            int frame = 0;
            for (FrameBufferObject frameBufferObject : frameBufferObjects) {
                prepareTextureUpdate(frameBufferObject);
                frames:
                for (int j = 0; j < yFrames; j++) {
                    for (int k = 0; k < xFrames; k++) {
                        owner.renderClothed(frame);
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
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    private void prepareTextureUpdate(FrameBufferObject frameBufferObject) {
        glPushMatrix();
        frameBufferObject.activate();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glTranslatef(0, Settings.resolutionHeight - fboSize, 0);
    }

    private void endTextureUpdate(FrameBufferObject frameBufferObject) {
        glPopMatrix();
        frameBufferObject.deactivate();
    }

    public void render() {
        int xShift = (currentSpriteSheetFrame % xFrames) * frameWidth;
        int yShift = fboSize - frameHeight - ((currentSpriteSheetFrame / xFrames) * frameHeight);
        glTranslatef(xStart, yStart, 0);
        frameBufferObjects[currentSpriteSheet].renderPiece(-xShift, -yShift, xShift, yShift, xShift + frameWidth, yShift + frameHeight);
    }

    public void renderPart(int partXStart, int partXEnd) {
        int xShift = (currentSpriteSheetFrame % xFrames) * frameWidth;
        int yShift = fboSize - frameHeight - ((currentSpriteSheetFrame / xFrames) * frameHeight);
        glTranslatef(xStart, yStart, 0);
        frameBufferObjects[currentSpriteSheet].renderPiece(-xShift, -yShift, xShift + partXStart, yShift, xShift + partXEnd, yShift + frameHeight);
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

}
