/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.place.map.Area;
import net.packets.Update;
import sprites.Animation;

/**
 *
 * @author Wojtek
 */
public class Corpse extends Entity {

    private String ownerName;
    
    public Corpse(Entity owner, Animation animation, int index) {
        this(owner, animation);
        animation.animateSingle(index);
    }

    //TUTAJ TRZEBA USTALIĆ ANIMACJĘ PRZED (LUB PO) JEJ PRZEKAZANIEM
    public Corpse(Entity owner, Animation animation) {
        initialize(owner.getName() + "'s DEAD corpse!", owner.getX(), owner.getY());
        ownerName = owner.getName();
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
        setCanInteract(true);
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
    public void interact(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.getTextController().lockEntity(player);
            player.getTextController().startFromText(new String[]{
                "Proszę państwa, oto " + ownerName + ".$FL",
                ownerName + " jest bardzo martwy dziś.$FL",
                "Chętnie państwu łapę poda.$FL",
                "Nie chce podać?$VE0.1$....$VE0.7$",
                "A to szkoda."
            });
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
