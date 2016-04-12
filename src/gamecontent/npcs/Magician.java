package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import gamecontent.MyPlayer;
import sprites.Animation;
import sprites.SpriteSheet;

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
            appearance = animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 1);
        }
        addPushInteraction();
        setDirection8way(RIGHT);
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                if (isPlayerTalkingToMe(player)) {
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("npcCzary");
                    player.getTextController().addExternalEvent(() -> {
                        if (upperSide) {
                            setPosition(2567, 4346);
                            player.setPosition(2467, 4346);
                            player.getCamera().updateStatic();
                            player.setCurrentLocationAsSpawnPosition();
                            upperSide = false;
                        } else {
                            setPosition(2906, 763);
                            player.setPosition(2806, 763);
                            player.getCamera().updateStatic();
                            player.setCurrentLocationAsSpawnPosition();
                            upperSide = true;
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
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            appearance.renderStaticShadow(this, 0, 0);
            animation.render();
        }
    }
}
