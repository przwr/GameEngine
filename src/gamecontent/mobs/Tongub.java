/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.ActionState;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.LineInteractiveCollision;
import game.gameobject.interactive.UpdateBasedActivator;
import game.gameobject.stats.MobStats;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Tongub extends Mob {

    private final static byte ATTACK_NORMAL = 0;
    private final Animation animation;
    private int seconds = 0, max = 5;
    private ActionState idle, run_away, hide, attack, wander;
    private Delay attack_delay = Delay.createInMilliseconds(1000);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createInMilliseconds(600);            //TODO - te wartości losowe i zależne od poziomu trudności
    private boolean attacking, undig;

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    brake(2);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (closeFriends.size() > 1) {
                        GameObject closerEnemy = getCloserEnemy();
                        if (closerEnemy != null && isInHalfHearingRange(closerEnemy)) {
                            state = attack;
                            target = closerEnemy;
                            attack_delay.start();
                        }
                    } else {
                        calculateDestinationsForEscape();
                        GameObject closerEnemy = getCloserEnemy();
                        if (closerEnemy != null) {
                            state = hide;
                            destination.set(-1, -1);
                            stats.setProtectionState(true);
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
                calculateDestinationsForEscape();
                goTo(destination.getX() > 0 ? destination : secondaryDestination);
                GameObject closerEnemy = getCloserEnemy();
                if (closerEnemy != null) {
                    state = hide;
                    destination.set(-1, -1);
                    secondaryDestination.set(-1, -1);
                    stats.setProtectionState(true);
                } else if (destination.getX() < 0 && (secondaryDestination.getX() < 0 || Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination
                        .getX(), secondaryDestination.getY()) < 4 * hearRange2 / 9)) {
                    state = idle;
                    secondaryDestination.set(-1, -1);
                    destination.set(-1, -1);
                }
            }
        };
        hide = new ActionState() {
            @Override
            public void update() {
//                System.out.println("HIDE");
                brake(2);
                if (animation.getDirectionalFrameIndex() == 16) {
                    collision.setWidth(0);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    GameObject closerEnemy = getCloserEnemy();
                    if (closerEnemy == null) {
                        stats.setProtectionState(false);
                        undig = true;
                        collision.setWidth(48);
                    }
                } else if (!stats.isProtectionState() && animation.getDirectionalFrameIndex() == 22) {
                    state = idle;

                }
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
//                System.out.println("ATTACK");
                if (rest.isOver()) {
                    if (attack_delay.isOver()) {
                        if (attacking) {
                            rest.start();
                            attacking = false;
                            maxSpeed = 3;
                            brake(2);
                        } else {
                            maxSpeed = 5;
                            attack_delay.start();
                            attacking = true;
                            getAttackActivator(ATTACK_NORMAL).setActivated(true);
                        }
                    } else {
                        charge();
                        getAttackActivator(ATTACK_NORMAL).setActivated(true);
                    }
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (!attacking && (!isInRange(target) || target.getMap() != map || closeFriends.size() < 2)) {
                        state = idle;
                        target = null;
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
                    RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
                    if (Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2 / 16) {
                        int sign = random.next(1) == 1 ? 1 : -1;
                        int shift = (sightRange + random.next(9)) * sign;
                        destination.setX(homePosition.getX() + shift);
                        sign = random.next(1) == 1 ? 1 : -1;
                        shift = (sightRange + random.next(9)) * sign;
                        destination.setY(homePosition.getY() + shift);
                        if (destination.getX() < sightRange / 2) {
                            destination.setX(sightRange / 2);
                        }
                        if (destination.getX() > map.getWidth()) {
                            destination.setX(map.getWidth() - sightRange / 2);
                        }
                        if (destination.getY() < collision.getHeight()) {
                            destination.setY(sightRange / 2);
                        }
                        if (destination.getY() > map.getHeight()) {
                            destination.setY(map.getHeight() - sightRange / 2);
                        }
//                        System.out.println(destination);
                    }
                    seconds++;
                    if (seconds > max) {
                        seconds = 0;
                        max = random.next(4);
                    }
                    rest.start();
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!closeEnemies.isEmpty() || (!alpha && !closeFriends.isEmpty())) {
                    state = idle;
                    destination.set(-1, -1);
                }
                goTo(destination);
            }
        };
    }

    public Tongub(int x, int y, Place place, short ID) {
        super(x, y, 3, 768, "Tongub", place, "tongub", true, ID);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 23);
        setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
        appearance = animation;
        collision.setMobile(true);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(1);
        stats.setWeight(20);
        stats.setMaxHealth(100);
        stats.setHealth(100);
        rest.start();
        attack_delay.start();
        state = idle;
        homePosition.set(getX(), getY());
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new LineInteractiveCollision(0, 64, 0, 16, 16), Interactive
                .STRENGTH_HURT, ATTACK_NORMAL, 0.5f));
    }

    private GameObject getCloserEnemy() {
        for (GameObject object : closeEnemies) {
            if (isInHalfHearingRange(object)) {
                return object;
            }
        }
        return null;
    }

    @Override
    public void update() {
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
        if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
            pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
            if (currentPastDirection > 1) {
                currentPastDirection = 0;
            }
            if (pastDirections[0] == pastDirections[1]) {
                setDirection(pastDirections[0] * 45);
            }
            if (attacking) {
                animation.setFPS(30);
                animation.animateIntervalInDirection(getDirection8Way(), 7, 10);
            } else {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 2, 6);
            }
        } else {
            if (stats.isProtectionState()) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 11, 16);
                animation.setStopAtEnd(true);
            } else if (undig) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 17, 22);
                animation.setStopAtEnd(true);
                if (animation.getDirectionalFrameIndex() == 22) {
                    undig = false;
                }
            } else {
                animation.animateSingleInDirection(getDirection8Way(), 0);
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            //Drawer.setColor(skinColor);
            animation.updateFrame();
            Drawer.setColor(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) collision.getWidth() / 2), Methods.roundDouble((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -floatHeight, 0);
            appearance.render();
            Drawer.refreshColor();
//            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            /*if (map != null) {
             Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
             }*/
            glPopMatrix();

//          renderPathPoints(xEffect, yEffect);
        }
    }
}