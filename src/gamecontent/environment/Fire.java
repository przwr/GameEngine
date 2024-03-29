/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.lights.Light;
import engine.particles.ParticleSource;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import org.newdawn.slick.Color;

import java.util.ArrayList;

/**
 * @author Wojtek
 */
public class Fire extends GameObject {

    Point updatePosition = new Point(0, 0);
    int direction = 0;
    private ParticleSource particleSource;
    private boolean shouldUpdate;

    public Fire(int x, int y, Place place) {
        initialize("Fire", x, y);
        this.place = place;
        particleSource = new ParticleSource(2.5f, 5, 0.001f, 0.8f, 30, place.getSpriteSheet("particle", ""));
        particleSource.setXSpread(24);
        particleSource.setYSpread(8);
        particleSource.setPPF(16);
        particleSource.setSpeed(0.8f);
        particleSource.setGravity(0f);
        particleSource.setDrag(0.01f);
        particleSource.setLifeLength(25);
        particleSource.setFrames(6);
        particleSource.setXDirectionFactor(-1f);
        particleSource.setYDirectionBalance(-0.5f);
        particleSource.updateParticles(0, 0, 0);
        setCollision(Rectangle.create(0, 0, OpticProperties.NO_SHADOW, this));
        setToUpdate(true);
        setEmitter(true);
        if (lights == null) {
            lights = new ArrayList<>(1);
        }
        if (lights.isEmpty()) {
            addLight(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(1f, 0.9f, 0.8f), 768, 768, this));
        }
    }

    @Override
    public void update() {
        shouldUpdate = true;
    }

    private void updateFire() {
        if (shouldUpdate) {
            particleSource.updateParticles(updatePosition.getX(), updatePosition.getY(), 0);
            updatePosition.set((int) Methods.xRadius(direction, 4), (int) Methods.yRadius(direction, 4));
            direction += 18 + ParticleSource.random.randomInRange(0, 4);
            if (!isEmits() && Main.backgroundLoader.allLoaded()) {
                setEmits(true);
            }
            if (lights.get(0).getSizeChange() <= 0.9f) {
                lights.get(0).setSizeChange(lights.get(0).getSizeChange() + ParticleSource.random.randomInRange(10, 10) / 1000f);
            } else if (lights.get(0).getSizeChange() >= 1f) {
                lights.get(0).setSizeChange(lights.get(0).getSizeChange() - ParticleSource.random.randomInRange(10, 10) / 1000f);
            } else {
                lights.get(0).setSizeChange(lights.get(0).getSizeChange() + ParticleSource.random.randomInRange(-10, 10) / 1000f);
            }
        }
    }

    @Override
    public void render() {
        updateFire();
        particleSource.render(getX(), (int) (getY() - floatHeight));
    }

    @Override
    public void renderShadowLit(Figure figure) {

    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {

    }

    @Override
    public void renderShadow(Figure figure) {

    }

    @Override
    public void renderShadow(int xStart, int xEnd) {

    }
}
