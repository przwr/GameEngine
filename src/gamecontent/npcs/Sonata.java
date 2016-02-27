package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.lights.Light;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.items.Weapon;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import game.text.Writer;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import gamecontent.environment.MoneyBag;
import gamecontent.mobs.Plurret;
import java.util.ArrayList;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 01.02.16.
 */
public class Sonata extends Mob {

    private Animation animation;
    private String dialog = "0";
    private MoneyBag money;
    private Zuocieyka zuo;
    private int left;

    public Sonata(int x, int y, Place place, short mobID, Zuocieyka zuo, MoneyBag money) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        this.money = money;
        this.zuo = zuo;
        if (appearance != null) {
            appearance = animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 0);
        }
        lights = new ArrayList<>();
        lights.add(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        setEmits(true);
        emitter = true;
        addPushInteraction();
        setDirection8way(DOWN);
    }

    @Override
    public void update() {
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if (isPlayerTalkingToMe(player)) {
                    if (!dialog.equals("0") && !dialog.equals("3a2") && money.getMap() == null) {
                        if (zuo.getMap() == null) {
                            dialog = "3b";
                        } else {
                            dialog = "3a";
                        }
                    }                    
                    setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("sonata", dialog);

                    player.getTextController().addEventOnBranchStart(() -> {
                        dialog = "3";
                    }, "0");
                    player.getTextController().addEventOnBranchEnd(() -> {
                        dialog = "3a2";
                        zuo.delete();
                    }, "4");
                    player.getTextController().addEventOnBranchEnd(() -> {
                        player.getStats().setHealth(player.getStats().getMaxHealth());
                    }, "5");
                    player.getTextController().addExternalWriter(new Writer("but") {
                        @Override
                        public String write() {
                            return "[" + player.getController().actions[MyController.INPUT_ACTION_3].input.getLabel() + "]";
                        }
                    });
                }
                if (d > hearRange * 1.5 || getTarget().getMap() != map) {
                    setDirection8way(RIGHT);
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
            animation.updateFrame();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
