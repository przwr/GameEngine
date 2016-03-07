/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.ActionState;
import game.gameobject.entities.Agro;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.interactive.collision.LineInteractiveCollision;
import game.gameobject.stats.MobStats;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import net.jodk.lang.FastMath;
import sprites.Animation;
import sprites.SpriteSheet;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Tongub extends Mob {

    private final static byte ATTACK_NORMAL = 0;
    private Animation animation;
    private int seconds = 0, max = 5;
    private ActionState idle, run_away, hide, attack, wander;
    private Delay attack_delay = Delay.createInMilliseconds(1500);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createInMilliseconds(250);            //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay peakTime = Delay.createInSeconds(2500);            //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay hideTime = Delay.createInSeconds(7500);            //TODO - te wartości losowe i zależne od poziomu trudności
    private boolean attacking, dig, undig, side, letGo;
    private RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    brake(2);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (closeEnemies.size() * 1.5 <= closeFriends.size()) {
                        GameObject closerEnemy = getCloserEnemy();
                        if (closerEnemy != null && isInHalfHearingRange(closerEnemy)) {
                            state = attack;
                            target = closerEnemy;
                            attack_delay.start();
                            letGoDelay.start();
                            return;
                        }
                    }
                    calculateDestinationsForEscape();
                    GameObject closerEnemy = getReallyCloseEnemy();
                    if (closerEnemy != null && peakTime.isOver()) {
                        hideTime.setFrameLengthInMilliseconds(7000 + random.next(10));
                        hideTime.start();
                        state = hide;
                        destination.set(-1, -1);
                        dig = true;
                    } else if (destination.getX() > 0) {
                        state = run_away;
                        destination.set(-1, -1);
                    } else {
                        state = wander;
                        destination.set(getX(), getY());
                        seconds = 0;
                    }
                }
            }

        };
        run_away = new ActionState() {
            @Override
            public void update() {
//                System.out.println(RUN_AWAY);
                if (destination.getX() > 0) {
                    secondaryDestination.set(destination.getX(), destination.getY());
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (closeEnemies.size() * 1.5 <= closeFriends.size()) {
                    state = idle;
                    secondaryDestination.set(-1, -1);
                    destination.set(-1, -1);
                } else {
                    calculateDestinationsForEscape();
                    GameObject closerEnemy = getReallyCloseEnemy();
                    if (closerEnemy != null && peakTime.isOver()) {
                        state = hide;
                        hideTime.setFrameLengthInMilliseconds(7000 + random.next(10));
                        hideTime.start();
                        destination.set(-1, -1);
                        secondaryDestination.set(-1, -1);
                        dig = true;
                    } else if (destination.getX() < 0 && (secondaryDestination.getX() < 0 || Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination
                            .getX(), secondaryDestination.getY()) < 4 * hearRange2 / 9)) {
                        state = idle;
                        secondaryDestination.set(-1, -1);
                        destination.set(-1, -1);
                    } else {
                        goTo(destination.getX() > 0 ? destination : secondaryDestination);
                    }
                }

            }
        };
        hide = new ActionState() {
            @Override
            public void update() {
//                System.out.println("HIDE");
                brake(2);
                if (!undig && !dig) {
                    stats.setProtectionState(true);
                    collision.setCollide(false);
                    collision.setHitable(false);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    GameObject closerEnemy = getReallyCloseEnemy();
                    if (!isCollided() && (closerEnemy == null || closeEnemies.size() * 1.5 <= closeFriends.size() || hideTime.isOver())) {
                        peakTime.setFrameLengthInMilliseconds(3000 + random.random(10));
                        peakTime.start();
                        stats.setProtectionState(false);
                        undig = true;
                        hideTime.terminate();
                        collision.setCollide(true);
                        collision.setHitable(true);
                    }
                } else if (!stats.isProtectionState() && animation.getDirectionalFrameIndex() == 23) {
                    state = idle;
                }
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
//                System.out.println("ATTACK");
                if (letGoDelay.isOver()) {
                    Agro agro = getAgresor(target);
                    if (agro == null || agro.getHurtedByOwner() <= 5) {
                        letGo();
                        return;
                    } else {
                        for (Agro ag : getAgro()) {
                            ag.clearHurtedByOwner();
                        }
                    }
                }
                if (rest.isOver()) {
                    if (attack_delay.isOver()) {
                        if (attacking || side) {
                            rest.start();
                            if (attacking && Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY()) < FastMath.pow2(target.getCollision()
                                    .getWidth() + collision.getWidth() + collision.getHeight())) {
                                getAttackActivator(ATTACK_NORMAL).setActivated(false);
                            }
                            attacking = false;
                            side = false;
                            brake(2);
                        } else {
                            if (closeFriends.isEmpty()) {
                                state = idle;
                                target = null;
                                maxSpeed = 3;
                                brake(2);
                            }
                            attack_delay.setFrameLengthInMilliseconds(1000 + random.next(9));
                            attack_delay.start();
                            attacking = true;
                            side = false;
                            if (Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY())
                                    <= Methods.pointDistanceSimple2(getX(), getY(), getX() + collision.getWidthHalf() + target.getCollision().getWidthHalf(),
                                    getY() + collision.getHeightHalf() + target.getCollision().getHeightHalf())) {
                                getAttackActivator(ATTACK_NORMAL).setActivated(true);
                            }
                        }
                    } else {
                        if (side) {
                            if (isObstacleBetween()) {
                                goTo(destination);
                            } else {
                                chargeToPoint(destination);
                            }
                        } else {
                            if (isObstacleBetween()) {
                                chase();
                            } else {
                                charge();
                            }
                            if (getInteractive(ATTACK_NORMAL).isActivated()) {
                                getAttackActivator(ATTACK_NORMAL).setActivated(false);
                                maxSpeed = 3;
                                side = true;
                                attacking = false;
                                closeRandomDestination(getX(), getY());
                                brake(2);
                            } else {
                                maxSpeed = 5;
                                if (Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY())
                                        <= Methods.pointDistanceSimple2(getX(), getY(), getX() + collision.getWidthHalf() + target.getCollision()
                                                .getWidthHalf(),
                                        getY() + collision.getHeightHalf() + target.getCollision().getHeightHalf())) {
                                    getAttackActivator(ATTACK_NORMAL).setActivated(true);
                                }
                            }
                        }
                    }
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (!attacking && (!target.getCollision().isHitable() || !isInHalfHearingRange(target) || target.getMap() != map)) {
                        state = idle;
                        target = null;
                        maxSpeed = 3;
                        brake(2);
                    }
                } else {
                    brake(2);
                }
            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
//                System.out.println("WANDER");
                if (rest.isOver()) {
                    if (Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2 / 16) {
                        closeRandomDestination(spawnPosition.getX(), spawnPosition.getY());
                        destination.set(getRandomPointInDistance((int) (sightRange * 1.5), spawnPosition.getX(), spawnPosition.getY()));
//                        System.out.println(destination);
                        letGo = false;
                    }
                    seconds++;
                    if (seconds > max) {
                        seconds = 0;
                        max = random.next(4);
                    }
                    rest.start();
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!letGo && !closeEnemies.isEmpty()) {
                    state = idle;
                    destination.set(-1, -1);
                    rest.terminate();
                }
                goTo(destination);
            }
        };
    }


    public Tongub() {
    }

    public Tongub(int x, int y, Place place, short ID) {
        super(x, y, 3, 768, "Tongub", place, "tongub", true, ID);
        setUp();
    }

    private boolean isCollided() {
        collision.setCollide(true);
        boolean collided = isCollided(0, 0);
        collision.setCollide(false);
        return collided;
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        super.initialize(x, y, 3, 768, "Tongub", place, "tongub", true, ID);
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 24);
        setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
        appearance = animation;
        collision.setMobile(true);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(1);
        stats.setWeight(20);
        stats.setMaxHealth(100);
        stats.setHealth(100);
        rest.terminate();
        attack_delay.terminate();
        letGoDelay.terminate();
        peakTime.terminate();
        state = idle;
        spawnPosition.set(getX(), getY());
        neutral.add(Plurret.class.getName());
        neutral.add(Shen.class.getName());
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new LineInteractiveCollision(0, 32, 0, 16, 16), Interactive
                .STRENGTH_HURT, ATTACK_NORMAL, 0.5f, 1f));
        addPushInteraction();
    }

    private boolean isObstacleBetween() {
        return getPathData().isAnyObstacleBetween(this, target.getX(), target.getY(), closeEnemies);
    }

    @Override
    protected void lookForCloseEntities(GameObject[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && object.getCollision().isHitable() && isInHalfHearingRange(object)) {
                closeEnemies.add(object);
            }
        }
        for (Mob mob : mobs) {
            if (mob.getClass().getName().equals(this.getClass().getName())) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (mob.getCollision().isHitable() && !isNeutral(mob) && mob.getMap() == map && isInHalfHearingRange(mob)) {
                closeEnemies.add(mob);
            }
        }
        updateAlpha();
    }


    private void closeRandomDestination(int xD, int yD) {
        destination.set(getRandomPointInDistance((int) (sightRange * 0.375), xD, yD));
    }

    private GameObject getCloserEnemy() {
        for (GameObject object : closeEnemies) {
            if (isInHalfHearingRange(object)) {
                return object;
            }
        }
        return null;
    }

    private GameObject getReallyCloseEnemy() {
        for (GameObject object : closeEnemies) {
            if (Methods.pointDistanceSimple2(object.getX(), object.getY(), getX(), getY()) < (hearRange2 / 16)) {
                return object;
            }
        }
        return null;
    }


    private void letGo() {
        state = wander;
        letGo = true;
        seconds = 0;
        max = 15;
        maxSpeed = 3;
        destination.set(spawnPosition);
        brake(2);
    }

    @Override
    public void update() {
        if (animation.isUpToDate()) {
            if (isHurt()) {
                updateGettingHurt();
                runWhenHurt();
            } else {
                state.update();
                normalizeSpeed();
                updateAnimation();
            }
            updateChangers();
            updateWithGravity();
            moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
            brakeOthers();
        }
    }

    @Override
    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockBackPower, jumpPower, attacker);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 1);
        brake(2);
    }

    private void runWhenHurt() {
        if (closeEnemies.isEmpty()) {
            state = run_away;
            destination.set(-1, -1);
            secondaryDestination.set(-1, -1);
            stats.setProtectionState(false);
            setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
            destination.set(getX() + (int) Methods.xRadius(knockBack.getAttackerDirection(), sightRange),
                    getY() - (int) Methods.yRadius(knockBack.getAttackerDirection(), sightRange));
        }
    }


    /*
     <('-^<) PRZEMKO-SCIAGA (>^-')>
     animation.animateSingleInDirection(getDirection8Way(), 0); - poza neutralna
     animation.animateSingleInDirection(getDirection8Way(), 1); - zranienie

     ~30fps?
     animation.animateIntervalInDirection(getDirection8Way(), 2, 6); - chód

     animation.animateIntervalInDirection(getDirection8Way(), 7, 10); - atak

     animation.animateIntervalInDirection(getDirection8Way(), 11, 16); - wkopanie
     animation.animateIntervalInDirection(getDirection8Way(), 17, 22); - wykopanie
     */
    private void updateAnimation() {
        if (dig || stats.isProtectionState()) {
            animation.setFPS(15);
            animation.animateIntervalInDirection(getDirection8Way(), 11, 17);
            animation.setStopAtEnd(true);
            if (animation.getDirectionalFrameIndex() == 17) {
                dig = false;
            }
        } else if (undig) {
            animation.setFPS(15);
            animation.animateIntervalInDirection(getDirection8Way(), 18, 23);
            animation.setStopAtEnd(true);
            if (animation.getDirectionalFrameIndex() == 23) {
                undig = false;
            }
        } else if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
            pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
            if (currentPastDirection > 1) {
                currentPastDirection = 0;
            }
            if (pastDirections[0] == pastDirections[1]) {
                setDirection(pastDirections[0] * 45);
            }
            if (getInteractive(ATTACK_NORMAL).isActive()) {
                animation.setFPS((int) (getSpeed() * 5));
                animation.animateIntervalInDirection(getDirection8Way(), 7, 10);
            } else {
                animation.setFPS((int) (getSpeed() * 5));
                animation.animateIntervalInDirection(getDirection8Way(), 2, 6);
            }
        } else {
            animation.animateSingleInDirection(getDirection8Way(), 0);
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef((int) (getX() * Place.getCurrentScale() + xEffect), (int) (getY() * Place.getCurrentScale() + yEffect), 0);
            Drawer.setColorStatic(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
                    * Place.getCurrentScale() / 2f), 24);
            glTranslatef(0, -(int) (floatHeight * Place.getCurrentScale()), 0);
            Drawer.refreshColor();
//			Drawer.renderStringCentered(name, 0, -(((appearance.getActualHeight()) * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
            glPopMatrix();

            if (Main.SHOW_INTERACTIVE_COLLISION) {
                interactiveObjects.stream().forEach((interactive) -> {
                    interactive.render(xEffect, yEffect);
                });
            }

            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), (int) (getY() - floatHeight), 0);
            appearance.render();
            appearance.updateFrame();
            Drawer.refreshColor();
            glPopMatrix();
//            renderPathPoints(xEffect, yEffect);
        }
    }
}
