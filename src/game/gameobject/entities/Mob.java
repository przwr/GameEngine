/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.entities;

import collision.Figure;
import engine.utilities.BlueArray;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import net.jodk.lang.FastMath;
import net.packets.Update;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public abstract class Mob extends Entity {

    public final short mobID;
    protected ActionState state;
    protected BlueArray<Mob> closeFriends = new BlueArray<>();
    protected boolean alpha;
    protected int pastDirections[] = new int[2];
    protected int currentPastDirection;

    protected Mob(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID) {
        this.place = place;
        this.solid = solid;
        this.hearRange = hearRange;
        this.hearRange2 = hearRange * hearRange;
        this.sightRange = hearRange;
        this.sightRange2 = sightRange * sightRange;
        this.sightAngle = 180;
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
            if (object.getMap() == map && isInRange(object)) {
                target = object;
                break;
            }
        }
    }

    protected synchronized void lookForCloseEntities(GameObject[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && (isHeard(object) || isSeen(object))) {
                closeEnemies.add(object);
            }
        }
        for (Mob mob : mobs) {
            if (mob.getClass().getName() == this.getClass().getName()) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (mob.getMap() == map && (isHeard(mob) || isSeen(mob))) {
                closeEnemies.add(mob);
            }
        }
        if (closeFriends.isEmpty()) {
            alpha = false;
        } else {
            alpha = true;
            for (Mob mob : closeFriends) {
                if (mob.alpha) {
                    alpha = false;
                    break;
                }
            }
        }
    }

    protected synchronized void lookForCloseEntitiesWhileSleep(GameObject[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && (isHeardWhileSleep(object))) {
                closeEnemies.add(object);
            }
        }
        for (Mob mob : mobs) {
            if (mob.getClass().getName() == this.getClass().getName()) {
                if (this != mob && mob.getMap() == map && isHeardWhileSleep(mob)) {
                    closeFriends.add(mob);
                }
            } else if (mob.getMap() == map && (isHeardWhileSleep(mob))) {
                closeEnemies.add(mob);
            }
        }
        if (closeFriends.isEmpty()) {
            alpha = false;
        } else {
            alpha = true;
            for (Mob mob : closeFriends) {
                if (mob.alpha) {
                    alpha = false;
                    break;
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

    protected synchronized void charge() {
        if (target != null) {
            double angle = Methods.pointAngleClockwise(x, y, target.getX(), target.getY());
            changeSpeed(Methods.xRadius(angle, maxSpeed), Methods.yRadius(angle, maxSpeed));
        }
    }

    protected synchronized void goTo(Point destination) {
        if (destination.getX() > 0) {
            pathStrategy.findPath(this, pathData, destination.getX(), destination.getY());
            if (this.getMaxSpeed() > 4) {
                changeSpeed(pathData.getXSpeed(), pathData.getYSpeed());
            } else {
                xSpeed = pathData.getXSpeed();
                ySpeed = pathData.getYSpeed();
            }
        } else {
            brake(2);
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
            x /= closeEnemies.size();
            y /= closeEnemies.size();
            calculateDestinationForEscapeFromPoint(x, y);
        }
    }

    public void calculateDestinationForEscapeFromPoint(int x, int y) {
        x = getX() - x;
        y = getY() - y;
        double ratio = Math.abs(y / (double) x);
        x = (int) (Math.signum(x) * (sightRange / ratio));
        y = (int) (Math.signum(y) * (ratio * Math.abs(x)));
        x += getX();
        y += getY();
        if (x < sightRange / 2) {
            x = sightRange / 2;
        }
        if (x > map.getWidth()) {
            x = map.getWidth() - sightRange / 2;
        }
        if (y < sightRange / 2) {
            y = sightRange / 2;
        }
        if (y > map.getHeight()) {
            y = map.getHeight() - sightRange / 2;
        }
        destination.set(x, y);
    }

    protected synchronized void calculateDestinationsForCloseFriends() {
        if (!closeFriends.isEmpty()) {
            if (closeFriends.size() == 1) {
                secondaryDestination.set(closeFriends.get(0).getX(), closeFriends.get(0).getY());
            } else {
                int x = 0, y = 0;
                Mob leader = this;
                for (Mob mob : closeFriends) {
                    if (!mob.isAlpha()) {
                        x += mob.getX();
                        y += mob.getY();
                    } else {
                        leader = mob;
                    }
                }
                x += (closeFriends.size() - 1) * leader.getX();
                y += (closeFriends.size() - 1) * leader.getY();
                x = (x / (2 * (closeFriends.size() - 1)));
                y = (y / (2 * (closeFriends.size() - 1)));
                if (x < sightRange / 2) {
                    x = sightRange / 2;
                }
                if (x > map.getWidth()) {
                    x = map.getWidth() - sightRange / 2;
                }
                if (y < sightRange / 2) {
                    y = sightRange / 2;
                }
                if (y > map.getHeight()) {
                    y = map.getHeight() - sightRange / 2;
                }
                secondaryDestination.set(x, y);
            }
        }
    }

    protected void normalizeSpeed() {
        if (xSpeed != 0 && ySpeed != 0) {
            double maxSpeed2 = maxSpeed * maxSpeed;
            if (xSpeed * xSpeed + ySpeed * ySpeed > maxSpeed2 - 0.01) {
                double ratio = Math.abs(ySpeed / xSpeed);
                double normalizedX = FastMath.sqrt(maxSpeed2 / (ratio * ratio + 1));
                xSpeed = Math.signum(xSpeed) * normalizedX;
                ySpeed = Math.signum(ySpeed) * normalizedX * ratio;
            }
        }
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map) ||
                collision.isCollidePlayer((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), getPlace());
    }

    @Override
    public Player getCollided(double xMagnitude, double yMagnitude) {
        return collision.firstPlayerCollide((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), getPlace());
    }


    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY() - (int) floatHeight, 0);
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

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }
}
