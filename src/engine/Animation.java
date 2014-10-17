/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Animation {

    private final Frame[] frames;
    private final Sprite spr;    
    private final Delay animDelay;
    private int flip;
    private int curFrame;

    public Animation(int num, Sprite spr, int delay) {
        frames = new Frame[num];
        this.spr = spr;
        for (float i = 0; i < num; i++) {
            frames[(int) i] = new Frame(i / (float) num, (i + 1) / (float) num, 0, 1);
        }
        animDelay = new Delay(delay);
        animDelay.restart();
    }

    public void render(boolean anim) {
        spr.bind();
        if (anim) {
            if (animDelay.isOver()) {
                frames[curFrame++].render(spr, flip);
                animDelay.restart();
                if (curFrame == frames.length) {
                    curFrame = 0;
                }
            } else {
                frames[curFrame].render(spr, flip);
            }
        } else {
            frames[0].render(spr, flip);
        }
    }

    public void setFlip(int flip) {
        this.flip = flip;
    }
}
