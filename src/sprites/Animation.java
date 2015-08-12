/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Delay;
import engine.Methods;
import engine.Point;
import game.gameobject.Player;
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
    private boolean animate = true,
            stopAtEnd = false;

    public Animation(SpriteSheet sprite, int delayTime) {
        this(sprite, delayTime, 0);
    }

    public Animation(SpriteSheet sprite, int delayTime, int framesPerDirection) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getSize() - 1;
        delay = new Delay(delayTime);
        delay.start();
        this.framesPerDirection = framesPerDirection;
    }
    
    public Animation(SpriteSheet sprite, int delayTime, int framesPerDirection, 
            Point dimensions, Point centralPoint) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getSize() - 1;
        delay = new Delay(delayTime);
        delay.start();
        this.framesPerDirection = framesPerDirection;
        fboSpriteSheet = new FrameBufferedSpriteSheet(dimensions.getX(), dimensions.getY(), 
                framesPerDirection * 8, -centralPoint.getX(), -centralPoint.getY());
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
        /*System.out.println(animate
         + " d: " + delay.isOver()
         + " cf: " + currentFrame
         + " s: " + stopAtEnd
         + " st.en: " + start + "." + end);*/
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

    private void animateSingle(int index) {
        animate = false;
        setCurrentFrame(Methods.interval(0, index, spriteSheet.getSize() - 1));
    }

    public void animateSingleInDirection(int direction, int index) {
        animateSingle(direction * framesPerDirection + index);
    }

    public void animateWhole() {
        animateInterval(0, spriteSheet.getSize() - 1);
    }

    private void animateInterval(int start, int end) {
        this.start = start;
        this.end = end;
        animate = true;
    }

    public void animateIntervalInDirection(int direction, int start, int end) {
        animateInterval(direction * framesPerDirection + start,
                direction * framesPerDirection + end);
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
        delay.setFrameLength(length);
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
        spriteSheet.getWidth();
        return 0;
    }

    @Override
    public int getHeight() {
        spriteSheet.getHeight();
        return 0;
    }

    @Override
    public int getXStart() {
        spriteSheet.getXStart();
        return 0;
    }

    @Override
    public int getYStart() {
        spriteSheet.getYStart();
        return 0;
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
