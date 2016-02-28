/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.utilities.Delay;
import engine.utilities.Methods;
import game.gameobject.entities.Player;

/**
 * @author przemek
 */
public class Animation implements Appearance {

    private final SpriteSheet spriteSheet;
    private final Delay delay;
    private int finalEnd;
    private int start, end, currentFrame, fps;
    private int framesPerDirection;
    private boolean animate = true, stopAtEnd = false, reversed = false, fluctuate = false, upToDate;

    private Animation(SpriteSheet sprite, int delayTime, int framesPerDirection) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        if (sprite == null) {
            this.finalEnd = this.end = framesPerDirection * 8 - 1;
        } else {
            this.finalEnd = this.end = spriteSheet.getSize() - 1;
        }
        delay = Delay.createInMilliseconds(delayTime);
        delay.start();
        this.framesPerDirection = framesPerDirection;
    }

    public static Animation createSimpleAnimation(SpriteSheet sprite, int delayTime) {
        return new Animation(sprite, delayTime, 0);
    }

    public static Animation createDirectionalAnimation(SpriteSheet sprite, int delayTime, int framesPerDirection) {
        return new Animation(sprite, delayTime, framesPerDirection);
    }

    protected void setCurrentFrame(int newFrame) {
        currentFrame = newFrame;
    }

    @Override
    public void updateTexture(Player owner) {
    }

    @Override
    public void updateFrame() {
        if (upToDate) {
            if (animate && delay.isOver()) {
                delay.start();
                if (!reversed) {    //NORMALNE
                    setCurrentFrame(currentFrame + 1);
                    if (currentFrame > end) {
                        if (stopAtEnd) {
                            animate = false;
                            setCurrentFrame(end);
                        } else {
                            if (fluctuate) {
                                setCurrentFrame(end);
                                reverseAnimation();
                            } else {
                                setCurrentFrame(start);
                            }
                        }
                    } else if (currentFrame < start) {
                        setCurrentFrame(start);
                    }
                } else {    //ODWROCONE
                    setCurrentFrame(currentFrame - 1);
                    if (currentFrame < end) {
                        if (stopAtEnd) {
                            animate = false;
                            setCurrentFrame(end);
                        } else {
                            if (fluctuate) {
                                setCurrentFrame(end);
                                reverseAnimation();
                            } else {
                                setCurrentFrame(start);
                            }
                        }
                    } else if (currentFrame > start) {
                        setCurrentFrame(start);
                    }
                }
            }
        } else {
            updateValues();
        }
    }

    public void animateSingle(int index) {
        animate = false;
        setCurrentFrame(start = end = Methods.interval(0, index, finalEnd));
    }

    public void animateSingleInDirection(int direction, int index) {
        animateSingle(direction * framesPerDirection + Methods.interval(0, index, framesPerDirection - 1));
    }

    public void animateWhole() {
        animateInterval(0, finalEnd);
    }

    private void animateInterval(int start, int end) {
        this.start = start;
        this.end = end;
        reversed = start > end;
        animate = true;
        stopAtEnd = false;
        fluctuate = false;
    }

    public void animateIntervalInDirection(int direction, int start, int end) {
        animateInterval(direction * framesPerDirection + Methods.interval(0, start, framesPerDirection - 1),
                direction * framesPerDirection + Methods.interval(0, end, framesPerDirection - 1));
    }

    public void changeDirection(int direction) {
        int directionalStart = direction * framesPerDirection;
        start = directionalStart + start % framesPerDirection;
        end = directionalStart + end % framesPerDirection;
        setCurrentFrame(directionalStart + currentFrame % framesPerDirection);
    }

    public void reverseAnimation() {
        int tmp = start;
        start = end;
        end = tmp;
        reversed = !reversed;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        if (this.reversed != reversed) {
            reverseAnimation();
        }
    }

    public void animateIntervalInDirectionOnce(int direction, int start, int end) {
        animateIntervalInDirection(direction, start, end);
        setCurrentFrame(this.start);
        stopAtEnd = true;
    }

    public void animateIntervalInDirectionFluctuating(int direction, int start, int end) {
        start = direction * framesPerDirection + Methods.interval(0, start, framesPerDirection - 1);
        end = direction * framesPerDirection + Methods.interval(0, end, framesPerDirection - 1);
        if ((this.start != start || this.end != end) && (this.start != end || this.end != start)) {
            animateInterval(start, end);
        }
        fluctuate = true;
    }

    public boolean isFluctuating() {
        return fluctuate;
    }

    public void setFluctuating(boolean fluctuate) {
        this.fluctuate = fluctuate;
    }

    @Override
    public boolean bindCheck() {
        return spriteSheet.bindCheck();
    }

    @Override
    public void render() {
        spriteSheet.renderPiece(currentFrame);
    }

    public void renderWhole() {
        spriteSheet.renderPiece(currentFrame);

    }

    @Override
    public void renderMirrored() {
        spriteSheet.renderPieceMirrored(currentFrame);
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        spriteSheet.renderPiecePart(currentFrame, partXStart, partXEnd);

    }

    @Override
    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public int getDirectionalFrameIndex() {
        return currentFrame % framesPerDirection;
    }

    public void setDelay(int length) {
        delay.setFrameLengthInMilliseconds(length);
    }

    public int getFPS() {
        return fps;
    }

    public void setFPS(int fps) {
        if (this.fps != fps) {
            delay.setFPS(fps);
        }
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

    public boolean isUpToDate() {
        return upToDate;
    }

    public void updateValues() {
        if (spriteSheet == null) {
            upToDate = true;
        } else if (spriteSheet.getTextureID() != 0) {
            this.finalEnd = this.end = spriteSheet.getSize() - 1;
            upToDate = true;
        }
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof Animation && spriteSheet == ((Animation) o).spriteSheet;
    }

    @Override
    public int hashCode() {
        return 13 * spriteSheet.hashCode();
    }
}
