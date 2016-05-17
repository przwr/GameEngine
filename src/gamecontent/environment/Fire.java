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

    public Fire(int x, int y, Place place) {
        initialize("Fire", x, y);
        this.place = place;
        particleSource = new ParticleSource(2.5f, 5, 0.001f, 0.8f, 30, place.getSpriteSheet("particle", ""));
        particleSource.setXSpread(24);
        particleSource.setYSpread(4);
        particleSource.setPPF(16);
        particleSource.setSpeed(0.75f);
        particleSource.setGravity(0f);
        particleSource.setDrag(0.01f);
        particleSource.setLifeLength(25);
        particleSource.setFrames(7);
        particleSource.updateParticles(0, 0, 0);
        particleSource.setXDirectionFactor(-1f);
        particleSource.setYDirectionBalance(-1f);
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
        particleSource.setXSpread(24);
        particleSource.setYSpread(8);
        particleSource.setPPF(16);
        particleSource.setSpeed(0.8f);
        particleSource.setGravity(0f);
        particleSource.setDrag(0.01f);
        particleSource.setLifeLength(25);
        particleSource.setFrames(7);
        particleSource.setXDirectionFactor(-1f);
        particleSource.setYDirectionBalance(-0.5f);

        particleSource.updateParticles(updatePosition.getX(), updatePosition.getY(), 0);
        updatePosition.set((int) Methods.xRadius(direction, 4), (int) Methods.yRadius(direction, 4));
        direction += 18 + ParticleSource.random.randomInRange(0, 4);
        lights.get(0).setXEffect(-updatePosition.getX());
        lights.get(0).setYEffect(-updatePosition.getY());
        if (!isEmits() && Main.backgroundLoader.allLoaded()) {
            setEmits(true);
        }
    }


    @Override
    public void render() {
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
