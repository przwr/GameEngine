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

    private final static byte ATTACK_NORMAL = 0, ATTACK_CRITICAL = 1;
    private final Animation animation;
    private int seconds = 0, max = 5;
    private ActionState idle, run_away, protect, attack, wander, follow, bounce;
    private Delay attack_delay = Delay.createDelayInMiliseconds(500);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createDelayInMiliseconds(500);                  //TODO - te wartości losowe i zależne od poziomu trudności
    private boolean attacking = true, chasing;
    private SpeedChanger bouncer;

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    brake(2);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
//                    calculateDestinationsForCloseFriends();
                    if (closeEnemies.isEmpty()) {
                        state = wander;
                        seconds = 0;
                    } else {
                        state = attack;
                        chasing = true;
                        setEnemyToAttack();
                    }
                }
            }
        };
        protect = new ActionState() {
            @Override
            public void update() {
//                System.out.println("PROTECT");
                brake(2);
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
//                System.out.println("ATTACK");
                int distance = Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY());
                if (chasing && distance >= sightRange2 / 16 || getPathData().isTrue(OBSTACLE_BETWEEN)) {
                    chase();
                    attack_delay.start();
                } else {
                    if (distance >= sightRange2) {
                        target = null;
                        state = idle;
                        chasing = false;
                    }
                    if (attack_delay.isOver()) {
                        attack_delay.start();

                    }
                    chasing = distance >= sightRange2 / 8;
                    brake(2);
                }
            }
        };
        follow = new ActionState() {
            @Override
            public void update() {
//                System.out.println("FOLLOW");
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForCloseFriends();
                xSpeed = ySpeed = 0;
                int distance = Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination.getX(), secondaryDestination.getY());
                if (distance > collision.getWidth() * collision.getWidth() * 4) {
                    goTo(secondaryDestination);
                }
                repulsion();
                if (xSpeed == 0 && ySpeed == 0) {
                    alignment();
                }
//                if (closerEnemy != null || isDistance2OutOfRange(distance)) {
//                    state = idle;
//                    pathData.setAvoidMobile(true);
//                    secondaryDestination.set(-1, -1);
//                    setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
//                }
            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
//                System.out.println("WANDER");
                if (rest.isOver()) {
                    RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
                    if (destination.getX() <= 0 || Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2 / 16) {
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
        state = idle;
        bouncer = new SpeedChanger();
        homePosition.set(getX(), getY());
    }

    private void repulsion() {
        for (Mob mob : closeFriends) {
            int distance = Methods.pointDistanceSimple2(getX(), getY(), mob.getX(), mob.getY());
            if (distance < collision.getWidth() * collision.getWidth() * 4f) {
                double scale = 1 - (FastMath.sqrt(distance) / (collision.getWidth() * 2f));
                int x = getX() - mob.getX();
                int y = getY() - mob.getY();
                float ratio = Math.abs(y / (float) x);
                x = (int) (Math.signum(x) * hearRange / 2);
                y = (int) (Math.signum(y) * (ratio * Math.abs(x)));
                x += getX();
                y += getY();
                double angle = Methods.pointAngleClockwise(getX(), getY(), x, y);
                xSpeed += scale * Methods.xRadius(angle, maxSpeed);
                ySpeed += scale * Methods.yRadius(angle, maxSpeed);
            }
        }
    }

    private void alignment() {
        if (!closeFriends.isEmpty()) {
            closeFriends.stream().filter(mob -> mob.isAlpha()).forEach(mob -> {
                xSpeed += mob.getXSpeed() / 2;
                ySpeed += mob.getYSpeed() / 2;
            });
        }
    }

    private boolean isAnyFriendHurt() {
        if (!closeFriends.isEmpty()) {
            for (Mob mob : closeFriends) {
                if (mob.getStats().getHealth() < mob.getStats().getMaxHealth()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void runWhenHurt() {
        if (closeEnemies.isEmpty()) {
            state = run_away;
            destination.set(-1, -1);
            secondaryDestination.set(-1, -1);
            maxSpeed = 1;
            stats.setProtectionState(false);
            setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
            destination.set(getX() + (int) Methods.xRadius(knockback.getAttackerDirection(), sightRange),
                    getY() - (int) Methods.yRadius(knockback.getAttackerDirection(), sightRange));
        }
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

    @Override
    public void getHurt(int knockbackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockbackPower, jumpPower, attacker);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockback.getXSpeed(),
                knockback.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 2);
        brake(2);
    }

    private void updateAnimation() {
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
//            if (target == null) {
//                animation.setFPS(7);
//                animation.animateIntervalInDirection(getDirection8Way(), 0, 5);
//            } else {
//
//                animation.animateIntervalInDirection(getDirection8Way(), 12, 14);
//            }
        } else {
            animation.animateSingleInDirection(getDirection8Way(), 1);
//            if (stats.isProtectionState()) {
//                animation.setFPS(15);
//                animation.animateIntervalInDirection(getDirection8Way(), 7, 12);
//                animation.setStopAtEnd(true);
//            } else if (unfold) {
//                animation.setFPS(15);
//                animation.animateIntervalInDirection(getDirection8Way(), 12, 7);
//                animation.setStopAtEnd(true);
//                if (appearance.getCurrentFrameIndex() % animation.getFramesPerDirection() == 7) {
//                    unfold = false;
//                }
//            } else {
//                animation.animateSingleInDirection(getDirection8Way(), 0);
//            }
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
}
