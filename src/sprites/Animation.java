/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Delay;
import engine.Methods;

/**
 *
 * @author przemek
 */
public class Animation implements Appearance {

    private final SpriteSheet spriteSheet;
    private final Delay delay;
    private int start, end;
    private boolean animate = true;
    private int currentFrame;

    public Animation(SpriteSheet sprite, int delayTime) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getSize() - 1;
        delay = new Delay(delayTime);
        delay.start();
    }

    @Override
    public void bindCheckByID() {
        spriteSheet.bindCheckByID();
    }

    @Override
    public void bindCheckByTexture() {
        spriteSheet.bindCheckByTexture();
    }

    @Override
    public void render() {
        spriteSheet.renderPiece(currentFrame);
        changeFrameIfNeeded();
    }

    @Override
    public void renderMirrored() {
        spriteSheet.renderPieceMirrored(currentFrame);
        changeFrameIfNeeded();
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        spriteSheet.renderPiecePart(currentFrame, partXStart, partXEnd);
        changeFrameIfNeeded();
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        spriteSheet.renderPiecePartMirrored(currentFrame, partXStart, partXEnd);
        changeFrameIfNeeded();
    }

    private void changeFrameIfNeeded() {
        if (animate && delay.isOver()) {
            currentFrame++;
            delay.start();
            if (currentFrame > end) {
                currentFrame = start;
            }
        }
    }

    public void animateSingle(int index) {
        animate = false;
        currentFrame = Methods.interval(0, index, spriteSheet.getSize() - 1);
    }

    public void animateWhole() {
        start = 0;
        end = spriteSheet.getSize() - 1;
        if (currentFrame < start || currentFrame > end) {
            currentFrame = 0;
            animate = true;
        }
    }

    public void animateInterval(int iSt, int iEn) {
        start = iSt;
        end = iEn;
        if (currentFrame < start || currentFrame > end) {
            currentFrame = iSt;
            animate = true;
        }
    }

    public int getCurrentFrameIndex() {
        changeFrameIfNeeded();
        return currentFrame;
    }
    
    public void setDelay(int d) {
        delay.setFrameLength(d);
    }

    public void setFPS(int d) {
        delay.setFPS(d);
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
}
