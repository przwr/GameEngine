/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Executive;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyNPC extends Mob {

    private Animation animation;
    private boolean spinning;

    public MyNPC(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        if (appearance != null) {
            appearance = animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 0);
        }
    }

    @Override
    public void update() {
        if (getTarget() != null && ((MyPlayer) getTarget()).isInGame()) {
            MyPlayer mpPrey = (MyPlayer) getTarget();
            if (spinning) {
                setDirection8way(getDirection8Way()+ 1);
            } else {
                setDirection8way((int) Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
            }
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (mpPrey.getController().getAction(MyController.ACTION).isKeyClicked() 
                    && d <= Place.tileSize * 1.5 
                    && !mpPrey.getTextController().isStarted()) {
                mpPrey.getTextController().lockEntity(mpPrey);
                mpPrey.getTextController().startFromFile("drzewo");
                Executive e = () -> {
                    spinning = !spinning;
                };
                mpPrey.getTextController().addExternalEvent(e, "0", false);
                mpPrey.getTextController().addExternalEvent(e, "1", false);
                mpPrey.getTextController().addExternalEvent(e, "2", false);
                mpPrey.getTextController().addExternalEvent(e, "3", false);
            }
            if (d > range * 1.5 || getTarget().getMap() != map) {
                target = null;
            }
        } else {
            lookForPlayers(place.players);
        }
        animation.animateSingle(getDirection8Way());
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            animation.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
