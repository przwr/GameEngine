package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.place.Place;
import game.place.cameras.Camera;
import game.place.map.Area;
import game.place.map.Map;
import sprites.Appearance;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 16.11.15.
 */
public class SpawnPoint extends GameObject {

    Delay delay;
    Class<? extends Mob> mob;
    int maxMobs, width, height;


    private SpawnPoint(int x, int y, int width, int height, String name, Class<? extends Mob> mob, int seconds, int maxMobs, Appearance appearance) {
        initialize(name, x, y);
        if (appearance != null) {
            setCollision(Rectangle.create(width, height, OpticProperties.TRANSPARENT, this));
            solid = true;
        }
        this.width = width;
        this.height = height;
        this.mob = mob;
        this.appearance = appearance;
        this.maxMobs = maxMobs;
        this.toUpdate = true;
        delay = Delay.createInSeconds(seconds);
        delay.start();
    }

    public static SpawnPoint createVisible(int x, int y, int width, int height, String name, Class<? extends Mob> mob, int seconds, int maxMobs, Appearance
            appearance) {
        return new SpawnPoint(x, y, width, height, name, mob, seconds, maxMobs, appearance);
    }

    public static SpawnPoint createInVisible(int x, int y, int width, int height, String name, Class<? extends Mob> mob, int seconds, int maxMobs) {
        return new SpawnPoint(x, y, width, height, name, mob, seconds, maxMobs, null);
    }

    @Override
    public void update() {
        if (delay.isOver()) {
            delay.start();
            if (appearance != null || !canBeSeen()) {
                int mobs = 0;
                Area area = map.getArea(getX(), getY());
                for (Mob m : area.getNearSolidMobs()) {
                    if (m.getClass().getName() == mob.getName()) {
                        mobs++;
                    }
                }
                if (mobs < maxMobs) {
                    if (collision != null && area.getNearSolidMobs().stream().anyMatch((object) -> (object.getClass().getName() == mob.getName()
                            && collision.checkCollision(getX(), getY(), object)))) {
                        return;
                    }
                    try {
                        Mob m = mob.newInstance();
                        m.initialize(getX(), getY(), map.place, map.getNextMobID());
                        map.addObject(m);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean canBeSeen() {
        Camera cam;
        Map map;
        for (int player = 0; player < this.map.place.getPlayersCount(); player++) {
            map = this.map.place.players[player].getMap();
            if (map == this.map) {
                cam = (((Player) this.map.place.players[player]).getCamera());
                if (cam.getYStart() <= y + height - Place.tileSize + floatHeight
                        && cam.getYEnd() >= y - height - Place.tileSize
                        && cam.getXStart() <= x - width - Place.tileSize
                        && cam.getXEnd() >= x + width + Place.tileSize) {
                    return true;
                }
            }
        }
        return false;
    }


    public Class getType() {
        return mob;
    }


    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), (int) (getY() - floatHeight), 0);
            appearance.render();
            Drawer.refreshColor();
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

}
