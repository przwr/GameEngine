/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Executive;
import engine.Methods;
import game.gameobject.Mob;
import game.place.Place;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyNPC extends Mob {

    private SpriteSheet spritesheet;
    private boolean spinning;

    public MyNPC(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        if (sprite != null) {
            spritesheet = (SpriteSheet) sprite;
        }
    }

    @Override
    public void update() {
        if (getTarget() != null && ((MyPlayer) getTarget()).isInGame()) {
            MyPlayer mpPrey = (MyPlayer) getTarget();
            direction = spinning ? direction + 1 : Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (direction > 7) {
                direction = 0;
            }
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (mpPrey.getController().isKeyClicked(MyController.JUMP) && d <= Place.tileSize * 1.5 && !mpPrey.getTextController().isStarted()) {
                mpPrey.getTextController().lockEntity(mpPrey);
                mpPrey.getTextController().startFromFile("drzewo");
                Executive e = new Executive() {

                    @Override
                    public void execute() {
                        spinning = !spinning;
                    }
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
            look(place.players);
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            spritesheet.renderPiece(direction);
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }

}
