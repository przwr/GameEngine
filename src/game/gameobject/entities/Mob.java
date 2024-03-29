/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.entities;

import collision.Figure;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.stats.Stats;
import game.place.Place;
import gamecontent.SpawnPoint;
import gamecontent.environment.Corpse;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author przemek
 */
public abstract class Mob extends Entity {

    public short mobID;
    protected boolean leader;
    protected ActionState state;
    protected Delay letGoDelay = Delay.createInSeconds(30);
    protected int pastDirections[] = new int[2];
    protected int currentPastDirection;
    protected ArrayList<Mob> closeFriends = new ArrayList<>();
    protected ArrayList<Agro> agro = new ArrayList<>();
    protected ArrayList<String> neutral = new ArrayList<>();
    private SpawnPoint spawner;
    private boolean targetable = true;

    public Mob() {
    }

    protected Mob(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID) {
        initialize(x, y, speed, hearRange, name, place, spriteName, solid, mobID);
    }

    protected Mob(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID, boolean NPC) {
        initialize(x, y, speed, hearRange, name, place, spriteName, solid, mobID, NPC);
    }

    public abstract void initialize(int x, int y, Place place);

    public void initialize(int x, int y, double speed, int hearRange, String name, Place place, String spriteName, boolean solid, short mobID, boolean... npc) {
        this.place = place;
        this.setSolid(solid);
        this.hearRange = hearRange;
        this.hearRange2 = hearRange * hearRange;
        this.sightRange = hearRange;
        this.sightRange2 = sightRange * sightRange;
        this.sightAngle = 180;
        this.setMaxSpeed(speed);
        if (npc.length > 0) {
            this.appearance = place.getSprite(spriteName, "entities/npcs", false, true, false);
            setTargetable(false);
        } else {
            this.appearance = place.getSprite(spriteName, "entities/mobs", false, true);
        }
        initialize(name, x, y);
        this.mobID = mobID;
        spawner = null;
    }

    protected synchronized void lookForPlayers(Player[] players) {
        Entity object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && isInRange(object)) {
                target = object;
                break;
            }
        }
    }

    protected synchronized void lookForCloseEntities(Player[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        Player player;
        for (int i = 0; i < getPlace().playersCount; i++) {
            player = players[i];
            if (player.getMap() == map && player.getCollision().isHitable() && (isHeard(player) || isSeen(player))) {
                closeEnemies.add(player);
            }
        }
        for (Mob mob : mobs) {
            if (mob.getClass().getName().equals(this.getClass().getName())) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (mob.getCollision().isHitable() && !isNeutral(mob) && mob.getMap() == map && (isHeard(mob) || isSeen(mob))) {
                closeEnemies.add(mob);
            }
        }
        updateLeadership();
    }

    protected void updateLeadership() {
        if (closeFriends.isEmpty()) {
            leader = false;
        } else {
            leader = true;
            for (Mob mob : closeFriends) {
                if (mob.leader) {
                    leader = false;
                    break;
                }
            }
        }
    }

    public boolean isTargetable() {
        return targetable;
    }

    public void setTargetable(boolean targetable) {
        this.targetable = targetable;
    }

    protected boolean isNeutral(Mob mob) {
        if (!mob.targetable) {
            return true;
        }
        if (!isAgresor(mob)) {
            for (String className : neutral) {
                if (className.equals(mob.getClass().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected synchronized void lookForCloseEntitiesWhileSleep(Player[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        Player player;
        for (int i = 0; i < getPlace().playersCount; i++) {
            player = players[i];
            if (player.getMap() == map && (isHeardWhileSleep(player))) {
                closeEnemies.add(player);
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
        updateLeadership();
    }

    protected synchronized void chase() {
        if (target != null && pathStrategy != null) {
            pathStrategy.findPath(this, pathData, target.getX(), target.getY());
            if (pathData.getXSpeed() != 0 && pathData.getYSpeed() != 0) {
                if (this.getMaxSpeed() > 4) {
                    changeSpeed(pathData.getXSpeed(), pathData.getYSpeed());
                } else {
                    xSpeed = pathData.getXSpeed();
                    ySpeed = pathData.getYSpeed();
                }
            } else {
                charge();
            }
        }
    }

    public SpawnPoint getSpawner() {
        return spawner;
    }

    public void setSpawner(SpawnPoint spawn) {
        spawner = spawn;
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
            destination.set(getRandomPointInDistance(sightRange / 8, destination.getX(), destination.getY()));
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
                    if (!mob.isLeader()) {
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
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeLit(appearance, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartLit(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight, xStart, xEnd);
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
    public void reactToAttack(byte attackType, GameObject hurted, int hurt) {
        Agro agro = getAgresor(hurted);
        if (agro != null) {
            agro.addHurtedByOwner(hurt);
        } else {
            agro = new Agro(hurted);
            agro.addHurtedByOwner(hurt);
            getAgro().add(agro);
        }
        if (hurt > 2) {
            letGoDelay.start();
        }
    }

    public Corpse deathReaction(InteractiveResponse response) {
        return createCorpse(response);
    }

    public Corpse createCorpse(InteractiveResponse response) {
        if (animation != null) {
            double knockback = Stats.attackKnockbackPower(response.getKnockBack() * 1.5f, stats.getWeight());
            knockBack((int) knockback, knockback / 6, response.getAttacker());
            Corpse corpse = new Corpse(this, animation);
            map.addObject(corpse);
            return corpse;
        }
        return null;
    }

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public ArrayList<Agro> getAgro() {
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

    protected void renderPathPoints() {
        PointContainer path = pathData.getPath();
        int current = pathData.getCurrentPointIndex();
        Drawer.setColorStatic(Color.green);
        if (path != null) {
            for (int i = current; i < path.size(); i++) {
                Drawer.drawRectangle(path.get(i).getX() - 5, path.get(i).getY() - 5, 10, 10);
            }
        }
        if (destination.getX() > 0) {
            Drawer.drawRectangle(destination.getX() - 5, destination.getY() - 5, 10, 10);
        }
        Drawer.refreshColor();
    }
}
