/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.environment;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.MobStats;
import game.place.Place;

/**
 * @author Wojtek
 */
public class Rock extends Mob {

    public Rock(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "rock", place, "rock", true, mobID);
        setCollision(Rectangle.create(appearance.getActualWidth(), appearance.getActualWidth() / 2, OpticProperties.NO_SHADOW, this));
        setDepth(-collision.getHeight() / 2);
        setHasStaticShadow(true);
        stats = new MobStats(this);
        stats.setStartHealth(1);
        stats.setDefence(20);
        setCanBeCovered(false);
        setResistance(50);
        addPushInteraction();
        setTargetable(false);
        setCanInteract(true);
    }

    @Override
    public void update() {
        if (target != null && ((Player) getTarget()).isInGame()) {
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                target = null;
            }
        } else {
            lookForPlayers(place.players);
        }
        updateChangers();
        updateWithGravity();
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.getTextController().lockEntity(player);
            player.getTextController().startFromText(new String[]{
                "To jest kamień$FL",
                "Jego położenie jest doprawdy specyficzne."
            });
        }
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            appearance.render();
        }
    }
}
