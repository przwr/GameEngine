package gamecontent.npcs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.lights.Light;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.stats.NPCStats;
import game.place.Place;
import gamecontent.MyPlayer;
import gamecontent.environment.MoneyBag;
import org.newdawn.slick.Color;
import sprites.Animation;
import sprites.SpriteSheet;

import java.util.ArrayList;

/**
 * Created by przemek on 01.02.16.
 */
public class Zuocieyka extends Mob {

    private final MoneyBag money;
    private Animation animation;
    private String dialog = "0";

    public Zuocieyka(int x, int y, Place place, short mobID, MoneyBag money) {
        super(x, y, 3, 400, "NPC", place, "melodia", true, mobID, true);
        setCollision(Rectangle.create(Place.tileSize / 3, Place.tileSize / 3, OpticProperties.NO_SHADOW, this));
        stats = new NPCStats(this);
        if (appearance != null) {
            appearance = animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 1);
        }
        lights = new ArrayList<>();
        lights.add(Light.create(place.getSpriteInSize("light", "", 768, 768), new Color(0.85f, 0.85f, 0.85f), 768, 768, this));
        setEmits(true);
        emitter = true;
        setDirection8way(DOWN);
        this.money = money;
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (target != null && ((Player) getTarget()).isInGame()) {
                MyPlayer player = (MyPlayer) target;
                int d = Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY());
                if (isPlayerTalkingToMe(player)) {
                    if (money.getMap() == null) {
                        dialog = "a";
                    }
                    setDirection8way(Methods.pointAngle8Directions(getX(), getY(), getTarget().getX(), getTarget().getY()));
                    player.getTextController().lockEntity(player);
                    player.getTextController().startFromFile("zuo", dialog);
                    player.getTextController().addEventOnBranchEnd(() -> {
                        delete();
                    }, "1");
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
