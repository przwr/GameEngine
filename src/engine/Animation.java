/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import sprites.Sprite;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class Animation {

    private final SpriteSheet spriteSheet;
    private final Delay delay;
    private int currentFrame, start, end;
    private boolean animate = true;

    public Animation(SpriteSheet spr, int delayTime) {
        this.spriteSheet = spr;
        this.start = currentFrame = 0;
        this.end = spriteSheet.getLenght() - 1;
        delay = new Delay(delayTime);
        delay.restart();
    }

    public void render() {
        spriteSheet.bindCheck();
        spriteSheet.render(currentFrame);
        if (animate && delay.isOver()) {
            delay.restart();
            currentFrame++;
            if (currentFrame > end) {
                currentFrame = start;
            }
        }
    }

    public void renderNotBind() {
        spriteSheet.render(currentFrame);
        if (animate && delay.isOver()) {
            delay.restart();
            currentFrame++;
            if (currentFrame > end) {
                currentFrame = start;
            }
        }
    }

    public void renderNotBind(int xStart, int xEnd) {
        spriteSheet.render(currentFrame, xStart, xEnd);
        if (animate && delay.isOver()) {
            currentFrame++;
            delay.restart();
            if (currentFrame > end) {
                currentFrame = start;
            }
        }
    }

    public Sprite getSprite() {
        return spriteSheet;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
}
