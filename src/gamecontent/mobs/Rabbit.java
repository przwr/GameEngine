/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.stats.MobStats;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import gamecontent.MyPlayer;

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
//        addInteractive(new Interactive(this, Interactive.ALWAYS, new CircleInteractiveCollision(32), Interactive.HURT));
    }

    @Override
    public void update() {
        if (target != null && (!(target instanceof MyPlayer) || ((MyPlayer) target).isInGame())) {
            if (Methods.pointDistance(getX(), getY(), target.getX(), target.getY()) > hearRange * 1.5 || getTarget().getMap() != map) {
                target = null;
                pathData.clearPath();
            } else {
                chase();
            }
        } else {
            lookForPlayers(place.players);
            brake(2);
        }
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
    }
}
