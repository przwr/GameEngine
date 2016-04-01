/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.items.Weapon;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.Writer;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import gamecontent.environment.Rock;
import gamecontent.mobs.Shen;
import sprites.Animation;
import sprites.SpriteSheet;

import static game.gameobject.items.Weapon.SWORD;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Melodia extends Mob {

    private final Shen shen;
    private final Rock rock;
    private Animation animation;
    private String dialog = "0";

    public Melodia(int x, int y, Place place, short mobID, Shen shen, Rock rock) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        if (appearance != null) {
            appearance = animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 0);
        }
        addPushInteraction();
        setDirection8way(DOWN);
        this.shen = shen;
        this.rock = rock;
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if ("2".equals(dialog) && shen.getMap() == null) {
                    dialog = "3";
                }
                if (isPlayerTalkingToMe(player)) {
                    setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("npcdemo", dialog);
                    player.getTextController().addEventOnBranchEnd(() -> {
                        if (player.getFirstWeapon() == null) {
                            Weapon sword = new Weapon("Sword", SWORD);
                            sword.setModifier(1.2f);
                            player.addWeapon(sword);
//                        map.deleteBlock(4096, 6592); // otworzenie 2 przejścia
                        }
                        dialog = "2";
                    }, "11", "12");
                    player.getTextController().addEventOnBranchEnd(() -> {
                        player.getStats().setHealth(player.getStats().getMaxHealth());
                    }, "3");
                    player.getTextController().addEventOnBranchEnd(() -> {
                        player.getSpawnPosition().set(rock.getX(), rock.getY());
                        rock.delete();
                        dialog = "2a";
                    }, "3", "24");
                    player.getTextController().addExternalWriter(new Writer("wpn") {
                        @Override
                        public String write() {
                            return "[" + player.getController().actions[MyController.INPUT_CHANGE_WEAPON].input.getLabel() + "]";
                        }
                    });
                    player.getTextController().addExternalWriter(new Writer("atk1") {
                        @Override
                        public String write() {
                            return "[" + player.getController().actions[MyController.INPUT_ATTACK].input.getLabel() + "]";
                        }
                    });
                    player.getTextController().addExternalWriter(new Writer("atk2") {
                        @Override
                        public String write() {
                            return "[" + player.getController().actions[MyController.INPUT_SECOND_ATTACK].input.getLabel() + "]";
                        }
                    });
                    player.getTextController().addExternalWriter(new Writer("blck") {
                        @Override
                        public String write() {
                            return "[" + player.getController().actions[MyController.INPUT_BLOCK].input.getLabel() + "]";
                        }
                    });
                }
                if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                    setDirection8way(DOWN);
                    target = null;
                }
            } else {
                lookForPlayers(place.players);
            }
            animation.animateSingle(getDirection8Way());
        }
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
