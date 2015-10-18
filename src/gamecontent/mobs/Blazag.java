/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.entities.ActionState;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.CurveInteractiveCollision;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.LineInteractiveCollision;
import game.gameobject.interactive.UpdateBasedActivator;
import game.gameobject.stats.MobStats;
import game.gameobject.temporalmodifiers.SpeedChanger;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import net.jodk.lang.FastMath;
import sprites.Animation;
import sprites.SpriteSheet;

import static game.logic.navmeshpathfinding.PathData.OBSTACLE_BETWEEN;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Blazag extends Mob {

    private final static byte ATTACK_SLASH = 0, ATTACK_JUMP = 1;
    private final Animation animation;
    private int seconds = 0, max = 5;
    private ActionState idle, attack, wander, jump, jumpAttack, protect;
    private Delay attackDelay = Delay.createDelayInMiliseconds(600);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createDelayInMiliseconds(1000);                  //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay jumpDelay = Delay.createDelayInMiliseconds(400);             //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay getHurtDelay = Delay.createDelayInMiliseconds(600);             //TODO - te wartości losowe i zależne od poziomu trudności
    private boolean attacking = true, chasing, jumpOver;
    private SpeedChanger jumper;

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (closeEnemies.isEmpty()) {
                        state = wander;
                        seconds = 0;
                    } else {
                        state = attack;
                        chasing = true;
                        setEnemyToAttack();
                    }
                } else {
                    brake(2);
                }
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
                if (animation.getDirectionalFrameIndex() < 26) {
                    int distance = Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY());
                    if (distance >= sightRange2 || target == null) {
                        target = null;
                        state = idle;
                        chasing = false;
                        brake(2);
                    } else {
                        if ((chasing && distance >= sightRange2 / 4) || getPathData().isTrue(OBSTACLE_BETWEEN)) {
                            chase();
                            attackDelay.start();
                        } else {
                            if (attackDelay.isOver()) {
//                                if (!closeFriends.isEmpty()) {
//                                    if (alpha) {
//                                        setOrders();
//                                    } else {
//                                        getOrders();
//                                    }
//                                } else {
                                loneAttack(distance);
//                                }
                            } else {
                                brake(2);
                            }
                            chasing = distance >= sightRange2 / 8;
                        }
                    }
                } else {
                    brake(2);
                }
            }
        };
        protect = new ActionState() {
            @Override
            public void update() {
                brake(2);
                setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
                animation.animateSingleInDirection(getDirection8Way(), 3);
                if (attackDelay.isOver()) {
                    jumpOver = false;
                    stats.setProtectionState(false);
                    state = attack;
                }
            }
        };
        jump = new ActionState() {
            @Override
            public void update() {
                if (jumper.isOver()) {
                    if (jumpDelay.isOver()) {
                        if (jumpOver) {
                            animation.animateSingleInDirection(getDirection8Way(), 22);
                            stats.setUnhurtableState(false);
                            jumpOver = false;
                            state = attack;
                        } else if (jumpDelay.isOver()) {
                            jumper.setFrames(30);
                            double angle = Methods.pointAngleClockwise(x, y, target.getX(), target.getY());
                            jumper.setSpeed(Methods.xRadius(angle, 3.5 * maxSpeed), Methods.yRadius(angle, 3.5 * maxSpeed));
                            jumper.setType(SpeedChanger.DECREASING);
                            jumper.start();
                            jumpOver = true;
                            stats.setUnhurtableState(true);
                            addChanger(jumper);
                            setJumpForce(maxSpeed);
                        }
                    }
                } else {
                    animation.animateSingleInDirection(getDirection8Way(), jumper.getPercentDone() > 0.5 ? 21 : 20);
                }
            }
        };
        jumpAttack = new ActionState() {
            @Override
            public void update() {
                if (jumper.isOver()) {
                    if (jumpDelay.isOver()) {
                        if (jumpOver) {
                            animation.animateSingleInDirection(getDirection8Way(), 25);
                            stats.setUnhurtableState(false);
                            jumpOver = false;
                            state = attack;
                            jumpDelay.start();
                        } else {
                            jumper.setFrames(30);
                            double angle = Methods.pointAngleClockwise(x, y, target.getX(), target.getY());
                            jumper.setSpeed(Methods.xRadius(angle, 4 * maxSpeed), Methods.yRadius(angle, 4 * maxSpeed));
                            jumper.setType(SpeedChanger.DECREASING);
                            jumper.start();
                            jumpOver = true;
                            stats.setUnhurtableState(true);
                            addChanger(jumper);
                            setJumpForce(maxSpeed);
                        }
                    }
                } else {
                    if (jumper.getPercentDone() > 0.5) {
                        getAttackActivator(ATTACK_JUMP).setActivated(true);
                        animation.animateSingleInDirection(getDirection8Way(), 24);
                    } else {
                        animation.animateSingleInDirection(getDirection8Way(), 23);
                    }
                }
            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
//                System.out.println("WANDER");
                if (rest.isOver()) {
                    RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
                    if (destination.getX() <= 0 || Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2 /
                            16) {
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
                    }
                    seconds++;
                    if (seconds > max) {
                        seconds = 0;
                        max = random.next(4);
                    }
                    rest.start();
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!closeEnemies.isEmpty()) {
                    state = idle;
                    destination.set(-1, -1);
                }
                goTo(destination);
            }
        };
    }

    public Blazag(int x, int y, Place place, short ID) {
        super(x, y, 5, 768, "Blazag", place, "blazag", true, ID);
        setHearRange(512);
        setCollision(Rectangle.create(54, 38, OpticProperties.NO_SHADOW, this));
        setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 44);
        appearance = animation;
        collision.setMobile(true);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(2);
        stats.setWeight(90);
        stats.setMaxHealth(150);
        stats.setHealth(150);
        stats.setSideDefenceModifier(1);
        stats.setBackDefenceModifier(0.5f);
        stats.setProtection(3);
        stats.setProtectionSideModifier(1);
        stats.setProtectionBackModifier(1);
        rest.start();
        jumpDelay.start();
        getHurtDelay.start();
        state = idle;
        jumper = new SpeedChanger();
        homePosition.set(getX(), getY());
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new CurveInteractiveCollision(48, 32, 0, 38, 180),
                Interactive.STRENGTH_HURT, ATTACK_SLASH, 0.5f));
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new LineInteractiveCollision(0, 128, 0, 24, 24),
                Interactive.STRENGTH_HURT, ATTACK_JUMP, 2f));
    }

    private void loneAttack(int distance) {
//        System.out.println("LONE ATTACK");
        int close = 1444 + (target.getCollision().getWidth() * target.getCollision().getWidth() + collision.getWidth() * collision.getWidth()) / 2;
        if (target != null && jumpDelay.isOver() && jumper.isOver()) {
            if (distance >= sightRange2 / 8) {
                brake(2);
                setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
                animation.animateSingleInDirection(getDirection8Way(), 19);
                jumpDelay.start();
                if (distance <= sightRange2 / 7) {
                    state = jumpAttack;
                } else {
                    state = jump;
                }
                attackDelay.start();
            } else if (distance <= close) {
                brake(2);
                setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
                double random = FastMath.random();
                double lifePercent = ((stats.getMaxHealth() - stats.getHealth()) / (double) stats.getMaxHealth()) / 4;
                if (random < 0.2 + lifePercent) {
                    stats.setProtectionState(true);
                    jumpOver = true;
                    state = protect;
                } else if (random > 0.6 + lifePercent / 2) {
                    getAttackActivator(ATTACK_SLASH).setActivated(true);
                    animation.animateIntervalInDirectionOnce(getDirection8Way(), 26, 34);
                } else {
                    getAttackActivator(ATTACK_SLASH).setActivated(true);
                    animation.animateIntervalInDirectionOnce(getDirection8Way(), 35, 43);
                }
                attacking = true;
                attackDelay.start();
            } else if (attackDelay.isOver() && distance >= close && animation.getDirectionalFrameIndex() < 19) {
                charge();
            } else {
                brake(2);
            }
        } else {
            brake(2);
        }
    }

    private void getOrders() {
        Blazag alpha = null;
        for (Mob mob : closeFriends) {
            if (mob.isAlpha()) {
                alpha = ((Blazag) mob);
            }
        }
        if (alpha != null) {
            Order order = alpha.getOrder();
            if (order != null) {

            }
        }
    }

    private void setOrders() {

    }

    private Order getOrder() {
        return new Order();
    }

    private void setEnemyToAttack() {
        int distance = Integer.MAX_VALUE;
        int currentDistance;
        for (GameObject object : closeEnemies) {
            currentDistance = Methods.pointDistanceSimple2(getX(), getY(), object.getX(), object.getY());
            if (currentDistance < distance) {
                target = object;
                distance = currentDistance;
            }
        }
    }

    @Override
    public void update() {
        if (isHurt()) {
            updateGettingHurt();
            getHurtDelay.start();
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
    public void getHurt(int knockbackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockbackPower, jumpPower, attacker);
    }

    /*
    <('-^<) PRZEMKO-SCIAGA (>^-')>
    animation.animateSingleInDirection(getDirection8Way(), 0); - siedzenie
    animation.animateSingleInDirection(getDirection8Way(), 1); - stanie
    animation.animateSingleInDirection(getDirection8Way(), 2); - zranienie
    animation.animateSingleInDirection(getDirection8Way(), 3); - blok
    
    ~30fps?
    animation.animateIntervalInDirection(getDirection8Way(), 4, 18); - bieg
    
    animation.animateSingleInDirection(getDirection8Way(), 19); - przygotowanie do skoku
    animation.animateSingleInDirection(getDirection8Way(), 20); - skok 1/2
    animation.animateSingleInDirection(getDirection8Way(), 21); - skok 2/2
    animation.animateSingleInDirection(getDirection8Way(), 22); - lądowanie
    
    animation.animateSingleInDirection(getDirection8Way(), 23); - skok z atakiem 1/2
    animation.animateSingleInDirection(getDirection8Way(), 24); - skok z atakiem 2/2
    animation.animateSingleInDirection(getDirection8Way(), 25); - atak po skoku
    
    animation.animateIntervalInDirectionOnce(getDirection8Way(), 26, 34); - ciachnięcie z prawej
    animation.animateIntervalInDirectionOnce(getDirection8Way(), 35, 43); - ciachnięcie z lewej
    */

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockback.getXSpeed(), knockback.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 2);
        brake(2);
    }

    private void updateAnimation() {
        int frame = animation.getDirectionalFrameIndex();
        if ((!jumpOver || !jumpDelay.isOver()) && (frame < 19 || frame == 34 || frame == 43 || frame == 22 || frame == 25)) {
            if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
                pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
                if (currentPastDirection > 1) {
                    currentPastDirection = 0;
                }
                if (pastDirections[0] == pastDirections[1]) {
                    setDirection(pastDirections[0] * 45);
                }
                animation.setFPS(30);
                animation.animateIntervalInDirection(getDirection8Way(), 4, 18);
            } else {
                if (attacking) {
                    attacking = false;
                } else {
                    animation.animateSingleInDirection(getDirection8Way(), 1);
                }
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
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            /*if (map != null) {
             Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
             }*/
            glPopMatrix();

//          renderPathPoints(xEffect, yEffect);
        }
    }

    private class Order {
        private final static byte ATTACK = 0, GO_TO = 1;
        private byte order;
        private Point point = new Point();
    }
}
