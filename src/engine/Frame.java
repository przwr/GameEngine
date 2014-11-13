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
public class Frame {

    private final float begX;
    private final float endX;
    private final float begY;
    private final float endY;

    Frame(float begX, float endX, float begY, float endY) {
        this.begX = begX;
        this.endX = endX;
        this.begY = begY;
        this.endY = endY;
    }

    public void render(Sprite spr, boolean flip) {
        spr.renderPartMirrored(flip, begX, endX, begY, endY);
    }

    public void renderNotBind(Sprite spr, boolean flip) {
        spr.renderPartMirroredNotBind(flip, begX, endX, begY, endY);
    }
}
