/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.Settings;
import game.gameobject.Entity;
import game.gameobject.Mob;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class MyNPC extends Mob {

    private SpriteSheet spritesheet;

    public MyNPC(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        if (sprite != null) {
            spritesheet = (SpriteSheet) sprite;
        }
    }

    @Override
    public void update() {
        if (prey != null && ((MyPlayer) prey).isInGame()) {
            MyPlayer mpPrey = (MyPlayer) prey;
            direction = Methods.pointAngle8Directions(getX(), getY(), prey.getX(), prey.getY());
            int d = Methods.pointDistance(getX(), getY(), prey.getX(), prey.getY());
            if (mpPrey.getController().isKeyClicked(MyController.JUMP) && d <= Place.tileSize * 1.5 && !mpPrey.getTextController().isStarted()) {
                mpPrey.getTextController().lockEntity(mpPrey);
                mpPrey.getTextController().startFromFile("drzewo");
            }
            if (d > range * 1.5 || prey.getMap() != map) {
                prey = null;
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
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(getX(), getY(), 0);
            spritesheet.renderPiece(direction);

            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            glPopMatrix();
        }
    }

}
