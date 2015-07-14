/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Delay;
import engine.Methods;
import game.gameobject.Player;
import game.place.fbo.FrameBufferedSpriteSheet;

/**
 *
 * @author przemek
 */
public class Animation implements Appearance {

    private SpriteSheet spriteSheet;
    private FrameBufferedSpriteSheet fboSpriteSheet;

    private final Delay delay;
    private int start, end, currentFrame;
    private boolean animate = true;

    public Animation(SpriteSheet sprite, int delayTime) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getSize() - 1;
        delay = new Delay(delayTime);
        delay.start();
        fboSpriteSheet = new FrameBufferedSpriteSheet(64, 128, 152, sprite.getXStart(), sprite.getYStart());
    }

    private void setCurrentFrame(int newFrame) {
        currentFrame = newFrame;
        if (fboSpriteSheet != null) {
            fboSpriteSheet.updateFrame(currentFrame);
        }
    }

    public void updateTexture(Player owner) {
        fboSpriteSheet.updateTexture(owner);
    }

    public void updateFrame() {
        if (animate && delay.isOver()) {
            delay.start();
            setCurrentFrame(currentFrame + 1);
            if (currentFrame > end) {
                setCurrentFrame(start);
            }
        }
    }

    public void animateSingle(int index) {
        animate = false;
        setCurrentFrame(Methods.interval(0, index, spriteSheet.getSize() - 1));
    }

    public void animateWhole() {
        start = 0;
        end = spriteSheet.getSize() - 1;
        if (currentFrame < start || currentFrame > end) {
            setCurrentFrame(0);
            animate = true;
        }
    }

    public void animateInterval(int start, int end) {
        this.start = start;
        this.end = end;
        if (currentFrame < start || currentFrame > end) {
            setCurrentFrame(start);
            animate = true;
        }
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

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        spriteSheet.renderPiecePartMirrored(currentFrame, partXStart, partXEnd);
    }

    public boolean isUpToDate() {
        return fboSpriteSheet.isUpToDate();
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public void setUpToDate(boolean upToDate) {
        fboSpriteSheet.setUpToDate(upToDate);
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

}
