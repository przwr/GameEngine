/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.particles.ParticleSource;
import game.gameobject.GameObject;
import game.place.Place;

/**
 * @author Wojtek
 */
public class Fire extends GameObject {

    private ParticleSource particleSource;

    public Fire(int x, int y, Place place) {
        initialize("Fire", x, y);
        this.place = place;
        particleSource = new ParticleSource(2.5f, 5, 0.001f, 0.8f, 30, place.getSpriteSheet("particle", ""));
        particleSource.setXSpread(16);
        setCollision(Rectangle.create(0, 0, OpticProperties.NO_SHADOW, this));
        setToUpdate(true);
    }

    @Override
    public void update() {
        particleSource.setXSpread(18);
        particleSource.setYSpread(12);
        particleSource.setPPF(16);
        particleSource.setSpeed(1f);
        particleSource.setGravity(0f);
        particleSource.setDrag(0.01f);
        particleSource.setLifeLength(45);
        particleSource.setFrames(7);
        particleSource.updateParticles(0, 0, 0);
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
