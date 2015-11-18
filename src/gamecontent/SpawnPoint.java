package gamecontent;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.place.Place;
import game.place.map.Area;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 16.11.15.
 */
public class SpawnPoint extends GameObject {

    Delay delay = Delay.createInSeconds(60);
    Class<? extends Mob> mob;
    int maxMobs = 5;

    public SpawnPoint(int x, int y, int width, int height, Place place, String name, Class<? extends Mob> mob) {
        initialize(name, x, y);
        setCollision(Rectangle.create(width, height, OpticProperties.NO_SHADOW, this));
        solid = true;
        delay.start();
        this.mob = mob;
        appearance = place.getSprite("rabbit", "");
    }

    @Override
    public void update() {
        if (delay.isOver()) {
            delay.start();
            int mobs = 0;
            Area area = map.getArea(getX(), getY());
            for (Mob m : area.getNearSolidMobs()) {
                if (m.getClass().getName() == mob.getName()) {
                    mobs++;
                }
            }
            if (mobs < maxMobs) {
                if (area.getNearSolidMobs().stream().anyMatch((object) -> (object.getClass().getName() == mob.getName()
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
