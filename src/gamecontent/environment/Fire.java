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
        particleSource = new ParticleSource(2.5f, 5, 0.1f, 0.02f, 240, place.getSpriteSheet("particle", ""));
        setCollision(Rectangle.create(0, 0, OpticProperties.NO_SHADOW, this));
        toUpdate = true;
    }

    @Override
    public void update() {
        particleSource.updateParticles(0, 0, 120);
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
