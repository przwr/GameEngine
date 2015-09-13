/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import sprites.SpriteSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Domi
 */
class Lights {

    private final ArrayList<Light> lights;
    private final SpriteSheet spriteSheet;

    public Lights(int lightNumber, SpriteSheet spriteSheet) {
        this.lights = new ArrayList<>(lightNumber);
        this.spriteSheet = spriteSheet;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void renderPiece(int part) {
        spriteSheet.renderPiece(part);
    }

    public List<Light> getLights() {
        return lights;
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }
}
