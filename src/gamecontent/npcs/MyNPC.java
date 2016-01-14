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
import engine.utilities.RandomGenerator;
import game.gameobject.entities.Mob;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.Statement;
import game.text.TextController;
import game.text.Writer;
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
    private int talks = -1;

    public MyNPC(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        this.appearance = place.getSprite("melodia", "entities/npcs");
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
            MyPlayer mpPrey = (MyPlayer) getTarget();
            if (spinning) {
                setDirection8way(getDirection8Way() + 1);
            } else {
                setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
            }
            int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
            if (mpPrey.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                    && d <= Place.tileSize * 1.5
                    && !mpPrey.getTextController().isStarted()) {
                TextController text = mpPrey.getTextController();
                text.lockEntity(mpPrey);
                text.startFromFile("drzewo");
                Executive e = () -> {
                    spinning = !spinning;
                };
                text.addExternalEventOnBranch(e, "0", false);
                text.addExternalEventOnBranch(e, "1", false);
                text.addExternalEventOnBranch(e, "2", false);
                text.addExternalEventOnBranch(e, "3", false);
                text.addExternalStatement(new Statement("spr") {

                    @Override
                    public int check() {
                        if (talks < 2) {
                            talks++;
                            return talks;
                        } else if (talks < 6) {
                            talks++;
                            return 2;
                        } else if (talks < 7) {
                            talks++;
                            return 3;
                        } else {
                            talks++;
                            return 4;
                        }
                    }
                });
                text.addExternalEvent(() -> {
                    moveWithSliding(0, 80);
                }, "e");
                text.addExternalWriter(new Writer("num") {

                    @Override
                    public String write() {
                        return "" + talks;
                    }
                });
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
