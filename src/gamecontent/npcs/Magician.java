package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Executive;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 01.02.16.
 */
public class Magician extends Mob {

    private Animation animation;
    private boolean upperSide = true;

    public Magician(int x, int y, Place place, short mobID) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        if (appearance != null) {
            appearance = animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 0);
        }
        addPushInteraction();
        setDirection8way(RIGHT);
    }

    @Override
    public void update() {
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                if (player.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                        && d <= Place.tileSize * 1.5 && !player.getTextController().isStarted()) {
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("npcCzary");
                    player.getTextController().addExternalEvent(new Executive() {

                        @Override
                        public void execute() {
                            if (upperSide) {
                                setPosition(3850, 6610);
                                player.setPosition(3750, 6610);
                                player.getCamera().updateStatic();
                                upperSide = false;
                            } else {
                                setPosition(2700, 1836);
                                player.setPosition(2600, 1836);
                                player.getCamera().updateStatic();
                                upperSide = true;
                            }
                        }
                    }, "tele");
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
