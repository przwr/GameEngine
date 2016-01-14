/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Executive;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.items.Weapon;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import sprites.Animation;
import sprites.SpriteSheet;

import static game.gameobject.items.Weapon.SWORD;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Melodia extends Mob {

    private Animation animation;
    private String dialog = "demonpc";
    private int talks;

    public Melodia(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        if (appearance != null) {
            appearance = animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 0);
        }
        addPushInteraction();
    }

    @Override
    public void update() {
        if (getTarget() != null && ((MyPlayer) getTarget()).isInGame()) {
            MyPlayer player = (MyPlayer) getTarget();
            setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (player.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                    && d <= Place.tileSize * 1.5 && !player.getTextController().isStarted()) {
                if (dialog == "demonpc2" && map.getSolidMobById(0) == null) {
                    dialog = "demonpc3";
                }
                player.getTextController().lockEntity(player);
                player.getTextController().startFromFile(dialog);
                Executive e = () -> {
                    if (player.getFirstWeapon() == null) {
                        Weapon sword = new Weapon("Sword", SWORD);
                        sword.setModifier(1.2f);
                        player.addWeapon(sword);
                    }
                };
                Executive e1 = () -> {
                    dialog = "demonpc2";
                };
                Executive e2 = () -> {
                    player.getStats().setHealth(player.getStats().getMaxHealth());
                    dialog = "demonpc4";
                };
                Executive e3 = () -> {
                    map.deleteBlock(5120, 3712);
                };
                if (dialog == "demonpc") {
                    player.getTextController().addExternalEventOnBranch(e, "0", true);
                    player.getTextController().addExternalEventOnBranch(e1, "1", false);
                    player.getTextController().addExternalEventOnBranch(e1, "2", false);
                } else if (dialog == "demonpc3") {
                    player.getTextController().addExternalEventOnBranch(e2, "0", false);
                }
                if (dialog == "demonpc4") {
                    player.getTextController().addExternalEventOnBranch(e3, "0", false);
                }

//                text.addExternalStatement(new Statement("spr") {
//
//                    @Override
//                    public int check() {
//                        if (talks < 3) {
//                            talks++;
//                            return talks - 1;
//                        } else if (talks < 8) {
//                            talks++;
//                            return 2;
//                        } else {
//                            return 3;
//                        }
//                    }
//                });
//                text.addExternalEvent(() -> {
//                    moveWithSliding(0, 80);
//                }, "e");


            }
            if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                target = null;
            }
        } else {
            lookForPlayers(place.players);
        }
        animation.animateSingle(getDirection8Way());
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
            animation.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
