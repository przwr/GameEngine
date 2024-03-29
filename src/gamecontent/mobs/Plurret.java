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
import game.gameobject.entities.Player;
import game.gameobject.stats.MobStats;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;

import java.util.List;

/**
 * @author przemek
 */
public class Plurret extends Mob {

    private int seconds = 0, max = 5, highLevel = 350, lowLevel = 334;
    private Delay rest = Delay.createInSeconds(3);          //TODO - te wartości losowe i zależne od poziomu trudności
    private ActionState idle, run_away, wander;
    private RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
    private boolean rising = true;
    private int aboveAllHeight = 250;
    private Point lastPosition = new Point(0, 0);
    private Delay collided = Delay.createInMilliseconds(250);

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    brake(2);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    calculateDestinationsForEscape();
                    if (destination.getX() > 0) {
                        state = run_away;
                        destination.set(-1, -1);
                    } else {
                        state = wander;
                        destination.set(getX(), getY());
                        seconds = 0;
                    }
                } else {
                    if (floatHeight == 0)
                        brake(2);
                }
            }
        };
        run_away = new ActionState() {
            @Override
            public void update() {
                rise();
//                System.out.println("RUN_AWAY");
                if (destination.getX() > 0) {
                    secondaryDestination.set(destination.getX(), destination.getY());
//                    System.out.println(secondaryDestination);
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForEscape();
                if (collided.isOver() && floatHeight > aboveAllHeight && (lastPosition.getX() != getX() || lastPosition.getY() != getY())) {
                    chargeToPoint(destination.getX() > 0 ? destination : secondaryDestination);
                } else {
                    goTo(destination.getX() > 0 ? destination : secondaryDestination);
                }
                if (closeEnemies.isEmpty() || (destination.getX() < 0 && Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination.getX(),
                        secondaryDestination.getY()) < hearRange2 / 4)) {
                    state = idle;
                    secondaryDestination.set(-1, -1);
                    destination.set(-1, -1);
                }
            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
//                System.out.println("WANDER");
                if (rest.isOver()) {
                    if (seconds == 0 || destination.getX() < 0) {
                        int sign = random.next(1) == 1 ? 1 : -1;
                        int shift = (sightRange + random.next(7)) * sign;
                        destination.setX(spawnPosition.getX() + shift);
                        sign = random.next(1) == 1 ? 1 : -1;
                        shift = (sightRange + random.next(7)) * sign;
                        destination.setY(spawnPosition.getY() + shift);
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
                        max = 1 + random.next(1);
                    }
                    rest.start();
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!closeEnemies.isEmpty()) {
                    state = idle;
                    destination.set(-1, -1);
                    rest.terminate();
                }
                if (Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) > hearRange2 / 16) {
                    rise();
                    if (floatHeight > aboveAllHeight && (lastPosition.getX() != getX() || lastPosition.getY() != getY())) {
                        chargeToPoint(destination);
                    } else {
                        goTo(destination);
                    }
                } else if (Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) < hearRange2 / 49) {
                    brake(2);
                }
            }
        };
    }

    public Plurret() {
    }

    public Plurret(int x, int y, Place place) {
        super(x, y, 10, 512, "Plurret", place, "plurret", true, place.getNextMobID());
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
        setUpDirectionalAnimation(0, 18);
        collision.setMobile(true);
        setHasStaticShadow(true);
        stats = new MobStats(this);
        stats.setStrength(1);
        stats.setDefence(1);
        stats.setWeight(2);
        stats.setMaxHealth(5);
        stats.setHealth(5);
        rest.terminate();
        state = idle;
        spawnPosition.set(getX(), getY());
        secondaryDestination.set(-1, -1);
        destination.set(-1, -1);
        neutral.add(Shen.class.getName());
        setGravity(0.1);
        addPushInteraction();
    }

    @Override
    public void initialize(int x, int y, Place place) {
        super.initialize(x, y, 10, 1024, "Plurret", place, "plurret", true, place.getNextMobID());
        setUp();
    }


    private void rise() {
        if (rising) {
            setUpForce(2);
            if (getFloatHeight() >= highLevel) {
                rising = false;
                lowLevel = 334 + random.next(4);
            }
        } else {
            if (getFloatHeight() <= lowLevel) {
                rising = true;
                highLevel = 350 + random.next(4);
            }
        }
    }

    @Override
    protected void lookForCloseEntities(Player[] players, List<Mob> mobs) {
        closeEnemies.clear();
        closeFriends.clear();
        Player player;
        for (int i = 0; i < getPlace().playersCount; i++) {
            player = players[i];
            if (player.getMap() == map && player.getCollision().isHitable() && player.getFloatHeight() + player.getActualHeight() > floatHeight && (isHeard
                    (player) || isSeen(player))) {
                closeEnemies.add(player);
            }
        }
        for (Mob mob : mobs) {
            if (mob.getClass().getName() == this.getClass().getName()) {
                if (this != mob && mob.getMap() == map && isInRange(mob)) {
                    closeFriends.add(mob);
                }
            } else if (mob.getCollision().isHitable() && !isNeutral(mob) && mob.getMap() == map && mob.getFloatHeight() + mob.getActualHeight() > floatHeight &&
                    (isHeard(mob) || isSeen(mob))) {
                closeEnemies.add(mob);
            }
        }
        updateLeadership();
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
            if (isHurt()) {
                updateGettingHurt();
            } else {
                state.update();
                lastPosition.set(getX(), getY());
                updateAnimation();
            }
            updateChangers();
            updateWithGravity();
            moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
            brakeOthers();
        }
    }

    /*
     <('-^<) PRZEMKO-SCIAGA (>^-')>
     ~20fps?
     animation.animateIntervalInDirection(getDirection8Way(), 0, 10); - poza neutralna
     animation.animateSingleInDirection(getDirection8Way(), 11); - zranienie
    
     ~30fps?
     animation.animateIntervalInDirection(getDirection8Way(), 12, 15); - lot
    
     animation.animateSingleInDirection(getDirection8Way(), 16); - stanie
     animation.animateSingleInDirection(getDirection8Way(), 17); - zranienie w locie
     */
    @Override
    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockBackPower, jumpPower, attacker);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
        if (floatHeight > 0) {
            animation.animateSingleInDirection(getDirection8Way(), 17);
        } else {
            animation.animateSingleInDirection(getDirection8Way(), 11);
        }
        brake(2);
    }

    private void updateAnimation() {
        if (floatHeight > 0 || Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1 || rising) {
            pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
            if (currentPastDirection > 1) {
                currentPastDirection = 0;
            }
            if (pastDirections[0] == pastDirections[1]) {
                setDirection(pastDirections[0] * 45);
            }
            animation.setFPS((int) (getSpeed()) + 10);
            animation.animateIntervalInDirection(getDirection8Way(), 12, 15);
        } else {
            animation.setFPS(20);
            animation.animateIntervalInDirectionFluctuating(getDirection8Way(), 0, 10);
        }
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            animation.render();
            //Drawer.setColor(skinColor);
            appearance.render();
            Drawer.refreshColor();
//          renderPathPoints();
        }
    }
}
