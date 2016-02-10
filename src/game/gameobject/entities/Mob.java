/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.entities;

import collision.Figure;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.place.Place;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import gamecontent.SpawnPoint;
import net.jodk.lang.FastMath;
import net.packets.Update;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public abstract class Mob extends Entity {

    public short mobID;
    protected ActionState state;
    protected BlueArray<Mob> closeFriends = new BlueArray<>();
    protected boolean alpha;
    protected int pastDirections[] = new int[2];
    protected int currentPastDirection;
    protected BlueArray<Agro> agro = new BlueArray<>();
    protected ArrayList<String> neutral = new ArrayList<>();
    protected Delay letGoDelay = Delay.createInSeconds(30);
    private SpawnPoint spawner;

    public Mob() {
    }

    protected Mob(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID) {
        initialize(x, y, speed, hearRange, name, place, spriteName, solid, mobID);
    }

    protected Mob(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID, boolean NPC) {
        initialize(x, y, speed, hearRange, name, place, spriteName, solid, mobID, NPC);
    }

    public abstract void initialize(int x, int y, Place place, short ID);

    public void initialize(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID, boolean... npc) {
        this.place = place;
        this.solid = solid;
        this.hearRange = hearRange;
        this.hearRange2 = hearRange * hearRange;
        this.sightRange = hearRange;
        this.sightRange2 = sightRange * sightRange;
        this.sightAngle = 180;
        this.setMaxSpeed(speed);
        if (npc.length > 0) {
            this.appearance = place.getSprite(spriteName, "entities/npcs");
        } else {
            this.appearance = place.getSprite(spriteName, "entities/mobs");
        }
        initialize(name, x, y);
        this.mobID = mobID;
        spawner = null;
    }

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
            if (mob.getClass().getName().equals(this.getClass().getName())) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (!isNeutral(mob) && mob.getMap() == map && (isHeard(mob) || isSeen(mob))) {
                closeEnemies.add(mob);
            }
        }
        updateAlpha();
    }

    protected void updateAlpha() {
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

    protected boolean isNeutral(Mob mob) {
        if (isAgresor(mob)) {
            return false;
        }
        for (String className : neutral) {
            if (className.equals(mob.getClass().getName())) {
                return true;
            }
        }
        return false;
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
            if (mob.getClass().getName().equals(this.getClass().getName())) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (!isNeutral(mob) && mob.getMap() == map && (isHeardWhileSleep(mob))) {
                closeEnemies.add(mob);
            }
        }
        updateAlpha();
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

    public void setSpawner(SpawnPoint spawn) {
        spawner = spawn;
    }

    public SpawnPoint getSpawner() {
        return spawner;
    }

    protected synchronized void charge() {
        if (target != null) {
            double angle = Methods.pointAngleClockwise(x, y, target.getX(), target.getY());
            changeSpeed(Methods.xRadius(angle, maxSpeed), Methods.yRadius(angle, maxSpeed));
        }
    }

    protected synchronized void chargeToPoint(Point destination) {
        double angle = Methods.pointAngleClockwise(x, y, destination.getX(), destination.getY());
        changeSpeed(Methods.xRadius(angle, maxSpeed), Methods.yRadius(angle, maxSpeed));
    }

    protected synchronized void goTo(Point destination) {
        goTo(destination.getX(), destination.getY());
    }

    protected synchronized void goTo(int xD, int yD) {
        if (xD > 0) {
            pathStrategy.findPath(this, pathData, xD, yD);
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
        return collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map)
                || collision.isCollidePlayer((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), getPlace());
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
        if (map != null) {
            Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                    map.getLightColor());
        }
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

    public void updateAgro(Agro newAgro, int diffrence) {
        ArrayList<Agro> toRemove = new ArrayList<>();
        for (Agro a : agro) {
            if (a != newAgro) {
                a.addHurtsOwner(diffrence);
                if (a.hurtsOwner <= 0 && a.hurtedByOwner <= 0) {
                    toRemove.add(a);
                }
            }
        }
        for (Agro a : toRemove) {
            agro.remove(a);
        }
    }

    @Override
    public void updateCausedDamage(GameObject hurted, int hurt) {
        Agro agro = getAgresor(hurted);
        if (agro != null) {
            agro.addHurtedByOwner(hurt);
        } else {
            agro = new Agro(hurted);
            agro.addHurtedByOwner(hurt);
            getAgro().add(agro);
        }
        if (hurt > 5) {
            letGoDelay.start();
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

    public BlueArray<Agro> getAgro() {
        return agro;
    }

    public boolean isAgresor(GameObject attacker) {
        for (Agro a : agro) {
            if (a.agresor == attacker) {
                return true;
            }
        }
        return false;
    }

    public Agro getAgresor(GameObject attacker) {
        for (Agro a : agro) {
            if (a.agresor == attacker) {
                return a;
            }
        }
        return null;
    }

    public boolean isPlayerTalkingToMe(MyPlayer player) {
        return player.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                && !player.getTextController().isStarted()
                && Methods.pointDistanceSimple(getX(), getY(),
                        player.getX(), player.getY()) <= Place.tileSize * 1.5
                && player.getDirection8Way() == Methods.pointAngle8Directions(player.getX(), player.getY(), x, y);
    }
}
