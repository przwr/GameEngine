/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.effects.TextController;
import gamecontent.MyPlayer;

/**
 * @author Wojtek
 */
public class MyNPC extends Mob {

    private boolean spinning;

    public MyNPC(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        this.appearance = place.getSprite("melodia", "entities/npcs");
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        setHasStaticShadow(true);
        if (appearance != null) {
            setUpDirectionalAnimation(0, 18);
        }
        addPushInteraction();
        setCanInteract(true);
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (getTarget() != null && ((MyPlayer) getTarget()).isInGame()) {
                if (spinning) {
                    setDirection8way(getDirection8Way() + 1);
                } else {
                    setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                }
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                    target = null;
                }
            } else {
                lookForPlayers(place.players);
            }
            animation.animateSingle(getDirection8Way());
        }
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            TextController text = player.getTextController();
            text.lockEntity(player);
            text.startFromFile("inwokacja");
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
            animation.render();
        }
    }
}
