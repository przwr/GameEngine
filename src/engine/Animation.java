/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.gameobject.GameObject;
import sprites.Sprite;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class Animation {

    private final GameObject owner;
    private final SpriteSheet sprite;
    private final Delay animDelay;
    private boolean flip;
    private int curFrame, start, end;

    public Animation(int start, int end, SpriteSheet spr, int delay, GameObject owner) {
        this.owner = owner;
        this.sprite = spr;
        this.start = start >= 0 ? start : 0;
        this.end = sprite.getLenght() - 1 <= end ? end : sprite.getLenght() - 1;
        animDelay = new Delay(delay);
        animDelay.restart();
        curFrame = start;
    }

    public Animation(SpriteSheet spr, int delay, GameObject owner) {
        this.owner = owner;
        this.sprite = spr;
        this.start = 0;
        this.end = sprite.getLenght() - 1;
        animDelay = new Delay(delay);
        animDelay.restart();
        curFrame = 0;
    }

    public void render(boolean anim) {
        sprite.bindCheck();
        sprite.render(curFrame);
        if (anim) {
            if (animDelay.isOver()) {
                curFrame++;
                animDelay.restart();
                if (curFrame > getEnd()) {
                    curFrame = getStart();
                }
            }
        }
    }

    public void renderNotBind(boolean anim) {
        sprite.render(curFrame);
        if (anim) {
            if (animDelay.isOver()) {
                curFrame++;
                animDelay.restart();
                if (curFrame > getEnd()) {
                    curFrame = getStart();
                }
            }
        }
    }

    
    
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public GameObject getOwner() {
        return owner;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
