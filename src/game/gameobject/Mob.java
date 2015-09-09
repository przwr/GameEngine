/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import engine.Drawer;
import engine.Methods;
import game.place.Place;
import net.packets.Update;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public abstract class Mob extends Entity {

    public final short mobID;
    protected ActionState state;

    protected Mob(int x, int y, double speed, int range, String name, Place place, String spriteName, boolean solid, short mobID) {
        this.place = place;
        this.solid = solid;
        this.range = range;
        this.setMaxSpeed(speed);
        this.appearance = place.getSprite(spriteName, "");
        initialize(name, x, y);
        this.mobID = mobID;
    }

    public abstract void update();

    protected synchronized void lookForPlayers(GameObject[] players) {
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && Methods.pointDistance(object.getX(), object.getY(), getX(), getY()) < range) {
                target = object;
                break;
            }
        }
    }

    protected synchronized void lookForCloseEntities(GameObject[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        GameObject object;
        int x = getX(), y = getY();
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && Methods.pointDistance(object.getX(), object.getY(), x, y) < range) {
                closeEnemies.add(object);
            }
        }
        for (GameObject mob : mobs) {
            if (mob.getMap() == map && Methods.pointDistance(mob.getX(), mob.getY(), x, y) < range) {
                if (mob.getClass().getName() == this.getClass().getName()) {
                    closeFriends.add(mob);
                } else {
                    closeEnemies.add(mob);
                }
            }
        }
    }

    protected synchronized void chase() {
        if (target != null && pathStrategy != null) {
            pathStrategy.findPath(this, pathData, target.getX(), target.getY());
            if (this.getMaxSpeed() > 4) {
                changeSpeed(pathData.getXSpeed(), pathData.getYSpeed());
            } else {
                xSpeed = pathData.getXSpeed();
                ySpeed = pathData.getYSpeed();
            }
        }


    }

    protected synchronized void goTo() {
        if (destination.getX() > 0) {
            pathStrategy.findPath(this, pathData, destination.getX(), destination.getY());
            if (this.getMaxSpeed() > 4) {
                changeSpeed(pathData.getXSpeed(), pathData.getYSpeed());
            } else {
                xSpeed = pathData.getXSpeed();
                ySpeed = pathData.getYSpeed();
            }
        }
    }

    protected synchronized void calculateDestinationsForEscape() {
        if (closeEnemies.isEmpty()) {
            destination.set(-1, -1);
        } else {
            int x = 0, y = 0;
            for (GameObject object : closeEnemies) {
                x += object.getX();
                y += object.getY();
            }
            x = getX() - (x / closeEnemies.size());
            y = getY() - (y / closeEnemies.size());
            float ratio = Math.abs(y / (float) x);
            x = (int) (Math.signum(x) * range) + getX();
            y = (int) (Math.signum(y) * (ratio * Math.abs(x))) + getY();
            if (x <= 0) {
                x = 1;
            }
            if (y <= 0) {
                y = 1;
            }
            destination.set(x, y);
        }
    }

    @Override

    protected boolean isCollided(int xMagnitude, int yMagnitude) {
        return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map)
                || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return collision.firstPlayerCollide(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        setPosition(x + xPosition, y + yPosition);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
        }
        if (map != null)
            Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                    map.getLightColor());
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }
}
