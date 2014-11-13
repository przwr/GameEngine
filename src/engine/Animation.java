/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.gameobject.GameObject;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Animation {

    private final GameObject owner;
    private final Frame[] frames;
    private final Sprite sprite;
    private final Delay animDelay;
    private boolean flip;
    private int curFrame;

    public Animation(int num, Sprite spr, int delay, GameObject owner) {
        this.owner = owner;
        frames = new Frame[num];
        this.sprite = spr;
        for (float i = 0; i < num; i++) {
            frames[(int) i] = new Frame(i / (float) num, (i + 1) / (float) num, 0, 1);
        }
        animDelay = new Delay(delay);
        animDelay.restart();
    }

    public void render(boolean anim) {
        sprite.bindCheck();
        if (anim) {
            if (animDelay.isOver()) {
                frames[curFrame++].render(sprite, flip);
                animDelay.restart();
                if (curFrame == frames.length) {
                    curFrame = 0;
                }
            } else {
                frames[curFrame].render(sprite, flip);
            }
        } else {
            frames[0].render(sprite, flip);
        }
    }

    public void renderNotBind(boolean anim) {
        if (anim) {
            if (animDelay.isOver()) {
                frames[curFrame++].render(sprite, flip);
                animDelay.restart();
                if (curFrame == frames.length) {
                    curFrame = 0;
                }
            } else {
                frames[curFrame].render(sprite, flip);
            }
        } else {
            frames[0].render(sprite, flip);
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
}
