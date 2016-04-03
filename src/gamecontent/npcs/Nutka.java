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
public class Nutka extends Mob {

    private Animation animation;
    private String dialog = "0";

    public Nutka(int x, int y, Place place, short mobID) {
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
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if (isPlayerTalkingToMe(player)) {
                    setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("npc2demo", dialog);
                    player.getTextController().addEventOnBranchEnd(() -> {
                        dialog = "1";
                    }, "0");
                    player.getTextController().addEventOnBranchEnd(() -> {
                        player.getStats().setHealth(player.getStats().getMaxHealth());
                    }, "12");
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
            animation.render();
        }
    }
}
