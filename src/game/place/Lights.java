/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import sprites.SpriteSheet;

/**
 *
 * @author Domi
 */
public class Lights {

    //TO DO light created from smaller lights - up to 1024 x 1024 / 768 x 768
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

    public Collection<Light> getLights() {
        return Collections.unmodifiableCollection(lights);
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }
}
