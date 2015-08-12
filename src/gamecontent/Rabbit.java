/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Methods;
import game.gameobject.Mob;
import game.gameobject.MobStats;
import game.place.Place;
import navmeshpathfinding.PathFindingModule;

/**
 * @author przemek
 */
public class Rabbit extends Mob {

    public Rabbit(int x, int y, int width, int height, double speed, int range, String name, Place place, boolean solid, short ID) {
        super(x, y, speed, range, name, place, "rabbit", solid, ID);
        setCollision(Rectangle.create(width, height, OpticProperties.NO_SHADOW, this));
        collision.setMobile(true);
        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
        stats = new MobStats(this);
    }

    @Override
    public void update() {
        if (target != null && (!(target instanceof MyPlayer) || ((MyPlayer) target).isInGame())) {
            if (Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY()) > range * 1.5 || getTarget().getMap() != map) {
                target = null;
                pathData.clearPath();
            } else {
                chase(target);
            }
        } else {
            look(place.players);
            brake(2);
        }
        moveWithSliding((xEnvironmentalSpeed + xSpeed), (yEnvironmentalSpeed + ySpeed));
        brakeOthers();
    }
}
