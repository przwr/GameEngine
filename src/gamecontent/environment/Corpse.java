/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import collision.Figure;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.items.Item;
import game.place.map.Area;
import java.util.ArrayList;
import net.packets.Update;
import sprites.Animation;

/**
 *
 * @author Wojtek
 */
public class Corpse extends Entity {

    public Corpse(Entity owner, Animation animation, int index) {
        this(owner, animation);
        animation.animateSingle(index);
    }

    //TUTAJ TRZEBA USTALIĆ ANIMACJĘ PRZED (LUB PO) JEJ PRZEKAZANIEM
    public Corpse(Entity owner, Animation animation) {
        initialize(owner.getName() + "'s DEAD corpse!", owner.getX(), owner.getY());
        setCanCover(false);
        this.items = owner.getItems();
        this.collision = owner.getCollision();
        collision.setOwner(this);
        this.appearance = animation;
        xEnvironmentalSpeed = owner.getXEnvironmentalSpeed();
        yEnvironmentalSpeed = owner.getYEnvironmentalSpeed();
        xSpeed = owner.getXSpeed();
        ySpeed = owner.getYSpeed();
        floatHeight = owner.getFloatHeight();
        upForce = owner.getUpForce();
        changers = owner.getChangers();
        setSolid(true);
    }

    @Override
    public void update() {
        appearance.updateFrame();
        if (isSolid()) {
            updateWithGravity();
            updateChangers();
            moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
            brakeOthers();
            brake(2);
            if (Math.abs(xEnvironmentalSpeed + xSpeed + yEnvironmentalSpeed + ySpeed) < 0.001) {
                Area tmp = map.getArea(area);
                tmp.deleteObject(this);
                setSolid(false);
                tmp.addObject(this);
            }
        }
    }

    @Override
    public void updateOnline() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateRest(Update update) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map)
                || collision.isCollidePlayer((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), getPlace());
    }

    @Override
    public Player getCollided(double xMagnitude, double yMagnitude) {
        return collision.firstPlayerCollide((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), getPlace());
    }

}
