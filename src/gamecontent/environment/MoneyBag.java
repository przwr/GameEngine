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
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.TextController;
import gamecontent.MyPlayer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MoneyBag extends Mob {

    public MoneyBag(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "moneyBag", place, "money", true, mobID);
        setCollision(Rectangle.create(appearance.getActualWidth() / 2, appearance.getActualWidth() / 2, OpticProperties.NO_SHADOW, this));
        solid = false;
        stats = new NPCStats(this);
        addPushInteraction();
        setTargetable(false);
    }

    @Override
    public void update() {
        if (target != null && ((Player) getTarget()).isInGame()) {
            MyPlayer player = (MyPlayer) target;
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (isPlayerTalkingToMe(player)) {
                TextController text = player.getTextController();
                text.lockEntity(player);
                text.startFromText(new String[] {
                    "Aria włożyła $cfFF0000$sakiewkę z pieniędzmi$CN do kieszeni."
                });
                text.addEventOnBranchEnd(this::delete, "0");
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
    public void render() {
        if (appearance != null) {
            glPushMatrix();
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            Drawer.setColorStatic(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) appearance.getActualWidth() / 4), Methods.roundDouble((float) appearance.getActualWidth() / 8), 15);
            Drawer.refreshColor();
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
