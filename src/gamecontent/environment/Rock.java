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
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.MobStats;
import game.place.Place;
import gamecontent.MyPlayer;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Rock extends Mob {

    public Rock(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "rock", place, "rock", true, mobID);
        setCollision(Rectangle.create(appearance.getActualWidth(), appearance.getActualWidth() / 2, OpticProperties.NO_SHADOW, this));
        stats = new MobStats(this);
        stats.setStartHealth(1000);
        stats.setDefence(15);
        setResistance(50);
        addPushInteraction();
        setTargetable(false);
    }

    @Override
    public void update() {
        if (target != null && ((Player) getTarget()).isInGame()) {
            MyPlayer player = (MyPlayer) target;
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (isPlayerTalkingToMe(player)) {
                player.getTextController().lockEntity(player);
                player.getTextController().startFromText(new String[] {
                    "To jest kamień$FL",
                    "Jego położenie jest doprawdy specyficzne."
                });
            }
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
    public void initialize(int x, int y, Place place, short ID) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            Drawer.setColorStatic(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) collision.getWidthHalf()), Methods.roundDouble((float) collision.getHeightHalf()), 15);
            Drawer.refreshColor();
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
