/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Delay;

/**
 *
 * @author przemek
 */
public class Animation { // ANIMATION extends Sprite!

    private final SpriteSheet spriteSheet;
    private final Delay delay;
    private int currentFrame, start, end;
    private boolean animate = true;

    public Animation(SpriteSheet sprite, int delayTime) {
        this.spriteSheet = sprite;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getLenght() - 1;
        delay = new Delay(delayTime);
        delay.start();
    }

    public void render() {
        spriteSheet.bindCheck();
        renderNotBind();
    }

    public void renderNotBind() {
        spriteSheet.render(currentFrame);
        changeFrameIfNeeded();
    }

    public void renderPart(int xStart, int xEnd) {
        spriteSheet.bindCheck();
        renderPartNotBind(xStart, xEnd);
    }

    public void renderPartNotBind(int xStart, int xEnd) {
        spriteSheet.renderSpriteSheetPart(currentFrame, xStart, xEnd);
        changeFrameIfNeeded();
    }

    public void renderMirrored() {
        spriteSheet.bindCheck();
        renderMirroredNotBind();
    }

    public void renderMirroredNotBind() {
        spriteSheet.renderSpriteSheetMirrored(currentFrame);
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

    public Sprite getSpriteSheet() {
        return spriteSheet;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
}
