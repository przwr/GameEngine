/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.utilities.Delay;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.entities.Player;
import game.place.fbo.FrameBufferedSpriteSheet;

/**
 * @author przemek
 */
public class Animation implements Appearance {

    private final SpriteSheet spriteSheet;
    private final Delay delay;
    private FrameBufferedSpriteSheet fboSpriteSheet;
    private int start, end, currentFrame;
    private int framesPerDirection;
    private boolean animate = true, stopAtEnd = false;

    private Animation(SpriteSheet sprite, int delayTime, int framesPerDirection) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getSize() - 1;
        delay = Delay.createDelayInMiliseconds(delayTime);
        delay.start();
        this.framesPerDirection = framesPerDirection;
    }

    public static Animation createSimpleAnimation(SpriteSheet sprite, int delayTime) {
        return new Animation(sprite, delayTime, 0);
    }

    public static Animation createDirectionalAnimation(SpriteSheet sprite, int delayTime, int framesPerDirection) {
        return new Animation(sprite, delayTime, framesPerDirection);
    }

    public static Animation createFBOAnimation(SpriteSheet sprite, int delayTime, int framesPerDirection, Point dimensions, Point centralPoint, Point delta) {
        Animation tmp = new Animation(sprite, delayTime, framesPerDirection);
        //tmp.fboSpriteSheet = new FrameBufferedSpriteSheet(dimensions.getX(), dimensions.getY(),
        //        framesPerDirection * 8, centralPoint.getX(), centralPoint.getY(), delta.getX(), delta.getY());
        return tmp;
    }

    private void setCurrentFrame(int newFrame) {
        currentFrame = newFrame;
        if (fboSpriteSheet != null) {
            fboSpriteSheet.updateFrame(currentFrame);
        }
    }

    @Override
    public void updateTexture(Player owner) {
        if (fboSpriteSheet != null) {
            fboSpriteSheet.updateTexture(owner);
        }
    }

    @Override
    public void updateFrame() {
        if (animate && delay.isOver()) {
            delay.start();
            setCurrentFrame(currentFrame + 1);
            if (currentFrame > end) {
                if (stopAtEnd) {
                    animate = false;
                    setCurrentFrame(end);
                } else {
                    setCurrentFrame(start);
                }
            } else if (currentFrame < start) {
                setCurrentFrame(start);
            }
        }
    }

    public void animateSingle(int index) {
        animate = false;
        setCurrentFrame(Methods.interval(0, index, spriteSheet.getSize() - 1));
    }

    public void animateSingleInDirection(int direction, int index) {
        animateSingle(direction * framesPerDirection + Methods.interval(0, index, framesPerDirection - 1));
    }

    public void animateWhole() {
        animateInterval(0, spriteSheet.getSize() - 1);
    }

    private void animateInterval(int start, int end) {
        this.start = start;
        this.end = end;
        animate = true;
        stopAtEnd = false;
    }

    public void animateIntervalInDirection(int direction, int start, int end) {
        animateInterval(direction * framesPerDirection + Methods.interval(0, start, framesPerDirection - 1),
                direction * framesPerDirection + Methods.interval(0, end, framesPerDirection - 1));
    }

    public void animateIntervalInDirectionOnce(int direction, int start, int end) {
        animateIntervalInDirection(direction, start, end);
        setCurrentFrame(this.start);
        stopAtEnd = true;
    }

    @Override
    public void bindCheck() {
        if (fboSpriteSheet == null) {
            spriteSheet.bindCheck();
        }
    }

    @Override
    public void render() {
        if (fboSpriteSheet != null) {
            fboSpriteSheet.render();
        } else {
            spriteSheet.renderPiece(currentFrame);
        }
    }

    public void renderWhole() {
        if (fboSpriteSheet != null) {
            fboSpriteSheet.renderWhole();
        } else {
            spriteSheet.renderPiece(currentFrame);
        }
    }

    @Override
    public void renderMirrored() {
        spriteSheet.renderPieceMirrored(currentFrame);
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        if (fboSpriteSheet != null) {
            fboSpriteSheet.renderPart(partXStart, partXEnd);
        } else {
            spriteSheet.renderPiecePart(currentFrame, partXStart, partXEnd);
        }
    }

    public boolean isUpToDate() {
        return fboSpriteSheet.isUpToDate();
    }

    public void setUpToDate(boolean upToDate) {
        fboSpriteSheet.setUpToDate(upToDate);
    }

    @Override
    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public void setDelay(int length) {
        delay.setFrameLengthInMiliseconds(length);
    }

    public void setFPS(int fps) {
        delay.setFPS(fps);
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isAnimating() {
        return animate;
    }

    public void setStopAtEnd(boolean stopAtEnd) {
        this.stopAtEnd = stopAtEnd;
    }

    public boolean isStoppingAtEnd() {
        return stopAtEnd;
    }

    public int getFramesPerDirection() {
        return framesPerDirection;
    }

    public void setFramesPerDirection(int frames) {
        framesPerDirection = frames;
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        spriteSheet.renderPiecePartMirrored(currentFrame, partXStart, partXEnd);
    }

    @Override
    public int getWidth() {
        return spriteSheet.getWidth();
    }

    @Override
    public int getHeight() {
        return spriteSheet.getHeight();
    }

    @Override
    public int getXStart() {
        return spriteSheet.getXStart();
    }

    @Override
    public int getYStart() {
        return spriteSheet.getYStart();
    }

    @Override
    public int getActualWidth() {
        return spriteSheet.getActualWidth();
    }

    @Override
    public int getActualHeight() {
        return spriteSheet.getActualHeight();
    }

    @Override
    public int getXOffset() {
        return spriteSheet.getXOffset();
    }

    @Override
    public int getYOffset() {
        return spriteSheet.getYOffset();
    }
}
