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
import game.Settings;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.items.Item;
import game.gameobject.items.Weapon;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.effects.Writer;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import gamecontent.environment.Rock;
import gamecontent.mobs.Shen;
import sounds.Sound3D;

import static game.gameobject.items.Weapon.SWORD;

/**
 * @author Wojtek
 */
public class Melodia extends Mob {

    private final Shen shen;
    private final Rock rock;
    private String dialog = "0";
    private Sound3D piano;

    public Melodia(int x, int y, Place place, Shen shen, Rock rock) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, place.getNextMobID(), true);
        initializeSounds();
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        setHasStaticShadow(true);
        if (appearance != null) {
            setUpDirectionalAnimation(0, 1);
        }
        addPushInteraction();
        setDirection8way(DOWN);
        this.shen = shen;
        this.rock = rock;
        setCanInteract(true);
    }

    public final void initializeSounds() {
        if (piano == null) {
            piano = Settings.sounds.get3DBGSound("melody.ogg", this);
            piano.setSoundRanges(0.7f);
        }
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if ("2".equals(dialog) && shen.getMap() == null) {
                    dialog = "3";
                }
                if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                    setDirection8way(DOWN);
                    target = null;
                }
            } else {
                lookForPlayers(place.players);
            }
            animation.animateSingle(getDirection8Way());
            piano.play();
        }
    }

    @Override
    public void interact(Entity entity) {
        if (entity instanceof MyPlayer) {
            MyPlayer player = (MyPlayer) entity;
            setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
            player.getTextController().lockEntity(player);
            player.getTextController().startFromFile("npcdemo", dialog);
            player.getTextController().addEventOnBranchEnd(() -> {
                if (player.getFirstWeapon() == Item.EMPTY) {
                    Weapon sword = new Weapon(0, 0, "Sword", place, 2, null, SWORD);
                    sword.setModifier(1.2f);
                    player.addWeapon(sword);
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
    }

    @Override
    public void initialize(int x, int y, Place place) {
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
