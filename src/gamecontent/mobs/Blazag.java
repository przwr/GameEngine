/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.entities.ActionState;
import game.gameobject.entities.Agro;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.interactive.collision.CurveInteractiveCollision;
import game.gameobject.interactive.collision.LineInteractiveCollision;
import game.gameobject.stats.MobStats;
import game.gameobject.temporalmodifiers.SpeedChanger;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import sprites.Animation;
import sprites.SpriteSheet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static game.logic.navmeshpathfinding.PathData.OBSTACLE_BETWEEN;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Blazag extends Mob {

    private final static byte ATTACK_SLASH = 0, ATTACK_JUMP = 1;
    private final static Comparator<Mob> comparator = (Mob firstObject, Mob secondObject)
            -> ((Blazag) firstObject).targetDistance - ((Blazag) secondObject).targetDistance;
    private Animation animation;
    private int seconds = 0, max = 5, targetDistance, attackDelayTime = 150, attackCout = 0, maxAttackCount = 6;
    private float SLEEP_END = 17.5f, SLEEP_START = 7.5f;
    private float current_sleep_end, current_sleep_start;
    private ActionState idle, attack, wander, jump, jumpAttack, protect, sleep, run_to;
    private Delay attackDelay = Delay.createInMilliseconds(700);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay readyToAttackDelay = Delay.createInMilliseconds(attackDelayTime);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createInSeconds(2);                  //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay jumpRestDelay = Delay.createInSeconds(4);             //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay jumpDelay = Delay.createInMilliseconds(150);             //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay burstDelay = Delay.createInMilliseconds(500);             //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay changeDelay = Delay.createInMilliseconds(750);              //TODO - te wartości losowe i zależne od poziomu trudności
    private boolean attacking = true, chasing, jumpOver, awake = true, can_attack, bursting = false;
    private SpeedChanger jumper;
    private RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
    private Order order = new Order();

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (closeEnemies.isEmpty()) {
                        short time = place.getTimeInMinutes();
                        if (time >= current_sleep_start && time <= current_sleep_end) {
                            state = sleep;
                            if (alpha) {
                                alpha = false;
                            }
                        } else {
                            state = wander;
                            seconds = 0;
                        }
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
        sleep = new ActionState() {
            @Override
            public void update() {
//                System.out.println("SLEEP");
                if (awake) {
                    if (Methods.pointDistanceSimple2(getX(), getY(), homePosition.getX(), homePosition.getY()) > sightRange2 / 4) {
                        goTo(homePosition);
                    } else {
                        brake(2);
                        float rand = random.next(6) / 64f;
                        current_sleep_end = (SLEEP_END + rand) * 60;
                        rand = random.next(6) / 64f;
                        current_sleep_start = (SLEEP_START + rand) * 60;
                        awake = false;
                        order.order = 0;
                    }
                } else {
                    lookForCloseEntitiesWhileSleep(place.players, map.getArea(area).getNearSolidMobs());
                    short time = place.getTimeInMinutes();
                    getOrders();
                    if (!closeEnemies.isEmpty() || time <= current_sleep_start || time >= current_sleep_end || (!closeFriends.isEmpty() && order.order >= 0)) {
                        state = idle;
                        awake = true;
                    }
                    brake(2);
                }
            }
        };
        run_to = new ActionState() {
            @Override
            public void update() {
//                System.out.println("RUN_TO");
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!closeEnemies.isEmpty() || Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) < hearRange2 / 9) {
                    state = idle;
                    destination.set(-1, -1);
                }
                goTo(destination);
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
                maxSpeed = 5;
                if (animation.getDirectionalFrameIndex() < 26) {
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    if (!closeFriends.isEmpty()) {
                        if (!getPathData().isTrue(OBSTACLE_BETWEEN)) {
                            if (alpha) {
                                setOrders();
                            } else {
                                getOrders();
                            }
                        } else {
                            chase();
                        }
                    } else {
                        setEnemyToAttack();
                        int distance = target != null ? Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY()) : sightRange2;
                        if (distance >= sightRange2 || target == null) {
                            target = null;
                            state = idle;
                            chasing = false;
                            maxSpeed = 5;
                            brake(2);
                        } else {
                            if ((chasing && distance >= sightRange2 / 4) || getPathData().isTrue(OBSTACLE_BETWEEN)) {
                                chase();
                                attackDelay.start();
                            } else {
                                if (attackDelay.isOver()) {
                                    loneAttack(distance);
                                } else {
                                    brake(2);
                                }
                                chasing = distance >= sightRange2 / 8;
                            }
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
                            readyToAttackDelay.start();
                        } else if (readyToAttackDelay.isOver()) {
                            jumper.setFrames(30);
                            double angle = Methods.pointAngleClockwise(x, y, target.getX(), target.getY());
                            jumper.setSpeed(Methods.xRadius(angle, 4.5 * maxSpeed), Methods.yRadius(angle, 4.5 * maxSpeed));
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
                    if (destination.getX() <= 0 || Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2
                            / 16) {
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
                } else {
                    short time = place.getTimeInMinutes();
                    if (time >= current_sleep_start && time <= current_sleep_end) {
                        state = idle;
                        destination.set(-1, -1);
                    }
                }
                goTo(destination);
            }
        };
    }

    public Blazag() {
    }

    public Blazag(int x, int y, Place place, short ID) {
        super(x, y, 5, 1024, "Blazag", place, "blazag", true, ID);
        setUp();
    }

    private void setUp() {
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
        jumpRestDelay.start();
        attackDelay.start();
        changeDelay.start();
        readyToAttackDelay.start();
        burstDelay.start();
        state = idle;
        jumper = new SpeedChanger();
        homePosition.set(getX(), getY());
        float rand = random.next(6) / 64f;
        current_sleep_end = (SLEEP_END + rand) * 60;
        rand = random.next(6) / 64f;
        neutral.add(Shen.class.getName());
        neutral.add(Plurret.class.getName());
        current_sleep_start = (SLEEP_START + rand) * 60;
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new CurveInteractiveCollision(48, 32, 0, 38, 180),
                Interactive.STRENGTH_HURT, ATTACK_SLASH, 1.5f));
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new LineInteractiveCollision(0, 128, 0, 24, 24),
                Interactive.STRENGTH_HURT, ATTACK_JUMP, 4f));
        addPushInteraction();
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        super.initialize(x, y, 4.5, 1024, "Blazag", place, "blazag", true, ID);
        setUp();
    }

    private void loneAttack(int distance) {
        if (target != null && jumpDelay.isOver() && jumper.isOver()) {
            if (attackCout > maxAttackCount) {
                rest.start();
                attackCout = 0;
            }
            if (!rest.isOver()) {
                maxSpeed = 3;
                if (getPathData().isObstacleBetween(this, target.getX(), target.getY())) {
                    chase();
                } else {
                    charge();
                }
                return;
            } else {
                if (distance >= sightRange2 / 9) {
                    if (jumpRestDelay.isOver()) {
                        brake(2);
                        setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
                        animation.animateSingleInDirection(getDirection8Way(), 19);
                        if (distance <= sightRange2 / 16) {
                            state = jumpAttack;
                            jumpDelay.start();
                            attackCout += 2;
                        } else {
                            state = jump;
                            jumpDelay.start();
                        }
                        attackDelay.start();
                        jumpRestDelay.start();
                        return;
                    } else if (animation.getDirectionalFrameIndex() < 19) {
                        if (getPathData().isObstacleBetween(this, target.getX(), target.getY())) {
                            chase();
                        } else {
                            charge();
                        }
                        return;
                    }
                } else if (attackDelay.isOver()) {
                    if (getInteractive(ATTACK_SLASH).wouldCollide(target)) {
                        brake(2);
                        setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
                        if (!can_attack) {
                            can_attack = true;
                            if (stats.getHealth() != stats.getMaxHealth()) {
                                readyToAttackDelay.setFrameLengthInMilliseconds(Math.round(attackDelayTime * (stats.getHealth() / (float) stats.getMaxHealth
                                        ())));
                            }
                            readyToAttackDelay.start();
                        } else if (readyToAttackDelay.isOver()) {
                            can_attack = false;
                            float rand = random.next(10) / 1024f;
                            double lifePercent = (((stats.getMaxHealth() - stats.getHealth()) / (double) stats.getMaxHealth()) / 4) - 0.1;
                            if (rand < lifePercent) {
                                stats.setProtectionState(true);
                                jumpOver = true;
                                state = protect;
                            } else {
                                attackCout++;
                                animation.setFPS(30);
                                getAttackActivator(ATTACK_SLASH).setActivated(true);
                                if (rand > 0.5 + lifePercent / 2) {
                                    animation.animateIntervalInDirectionOnce(getDirection8Way(), 26, 34);
                                } else {
                                    animation.animateIntervalInDirectionOnce(getDirection8Way(), 35, 43);
                                }
                            }
                            attacking = true;
                            attackDelay.start();
                            return;
                        }
                    } else {
                        can_attack = false;
                        if (attackDelay.isOver() && animation.getDirectionalFrameIndex() < 19) {
                            if (getPathData().isObstacleBetween(this, target.getX(), target.getY())) {
                                chase();
                            } else {
                                if (distance <= sightRange2 / 25) {
                                    if (burstDelay.isOver()) {
                                        burstDelay.start();
                                        if (!bursting) {
                                            bursting = true;
                                        } else {
                                            bursting = false;
                                            if (distance <= sightRange2 / 36) {
                                                float rand = random.next(10) / 1024f;
                                                if (rand > 0.5) {
                                                    attackDelay.start();
                                                    animation.setFPS(30);
                                                    getAttackActivator(ATTACK_SLASH).setActivated(true);
                                                    attacking = true;
                                                    attackCout++;
                                                    if (rand > 0.75) {
                                                        animation.animateIntervalInDirectionOnce(getDirection8Way(), 26, 34);
                                                    } else {
                                                        animation.animateIntervalInDirectionOnce(getDirection8Way(), 35, 43);
                                                    }
                                                }
                                            }
                                        }
                                    } else if (bursting) {
                                        maxSpeed = 7;
                                    }
                                }
                                charge();
                            }
                            return;
                        }
                    }
                }
            }
        }
        brake(2);
    }

    private void getOrders() {
//        System.out.println("GET_ORDERS");
        boolean listenToOrders = true;
        for (GameObject enemy : closeEnemies) {
            if (isAgresor(enemy)) {
                if (getInteractive(ATTACK_SLASH).wouldCollide(enemy)) {
                    target = enemy;
                    listenToOrders = false;
                    break;
                }
            }
        }
        if (listenToOrders && order.order >= 0) {
            switch (order.order) {
                case Order.ATTACK:
                    target = order.target;
                    if (target != null) {
                        loneAttack(Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY()));
                    }
                    break;
                case Order.GO_TO:
                    target = order.target;
                    walkAround(order.type);
                    break;
            }
        } else {
            loneAttack(Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY()));
        }
    }

    private void setOrders() {
//        System.out.println("SET_ORDERS");
        findTargetForGroup();
        setAttackingAndFollowing();
    }

    private void findTargetForGroup() {
        int xCenter = getX(), yCenter = getY();
        int distance = Integer.MAX_VALUE;
        int agro = 0;
        int currentAgro;
        int currentDistance;
        boolean agresor = false;
        target = null;
        Set<GameObject> targets = new HashSet<>();
        for (Mob mob : closeFriends) {
            targets.addAll((mob.getCloseEnemies()));
            xCenter += mob.getX();
            yCenter += mob.getY();
        }
        yCenter /= (closeFriends.size() + 1);
        xCenter /= (closeFriends.size() + 1);
        for (GameObject object : targets) {
            if (agresor) {
                currentDistance = Methods.pointDistanceSimple2(xCenter, yCenter, object.getX(), object.getY());
                currentAgro = 0;
                Agro a = getAgresor(object);
                if (a != null) {
                    currentAgro = a.getValue();
                }
                for (Mob mob : closeFriends) {
                    a = mob.getAgresor(object);
                    if (a != null) {
                        currentAgro += a.getValue();
                    }
                }
                if (currentAgro > 0 && currentDistance < sightRange2) {
                    if (currentAgro > agro) {
                        agro = currentAgro;
                        target = object;
                    }
                }
            } else {
                currentDistance = Methods.pointDistanceSimple2(xCenter, yCenter, object.getX(), object.getY());
                currentAgro = 0;
                Agro a = getAgresor(object);
                if (a != null) {
                    currentAgro = a.getValue();
                }
                for (Mob mob : closeFriends) {
                    a = mob.getAgresor(object);
                    if (a != null) {
                        currentAgro += a.getValue();
                    }
                }
                if (currentAgro > 0 && currentDistance < sightRange2) {
                    agresor = true;
                    agro = currentAgro;
                    target = object;
                } else if (currentDistance < distance && currentDistance < sightRange2) {
                    target = object;
                    distance = currentDistance;
                }
            }
        }
    }

    @Override
    protected void updateAlpha() {
        if (closeFriends.isEmpty()) {
            alpha = false;
        } else {
            if (awake) {
                alpha = true;
                for (Mob mob : closeFriends) {
                    if (mob.isAlpha()) {
                        alpha = false;
                        break;
                    }
                }
            }
        }
    }

    private void setAttackingAndFollowing() {
        Blazag friend;
        if (target != null) {
            int attacks = 0;
            for (GameObject object : closeEnemies) {
                if (object instanceof Player) {
                    attacks += 2;
                }
            }
            if (attacks == 0) {
                attacks = 2;
            }
            byte goes = (byte) closeFriends.size();
            for (Mob mob : closeFriends) {
                friend = (Blazag) mob;
                friend.targetDistance = Methods.pointDistanceSimple2(friend.getX(), friend.getY(), target.getX(), target.getY());
            }
            closeFriends.sort(comparator);
            for (Mob mob : closeFriends) {
                friend = (Blazag) mob;
                if (friend != null && friend.awake) {
                    friend.order.target = target;
                    if (attacks > 0) {
                        friend.order.order = Order.ATTACK;
                        attacks--;
                    } else {
                        friend.order.order = Order.GO_TO;
                        friend.order.type = goes;
                        goes--;
                    }
                }
            }
            if (attacks > 0) {
                loneAttack(Methods.pointDistanceSimple2(getX(), getY(), target.getX(), target.getY()));
            } else {
                walkAround(goes);
            }
        }
    }

    private void walkAround(byte way) {
        if (changeDelay.isOver()) {
            shiftDestination(way, destination);
            changeDelay.start();
        }
        int distance = Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY());
        if ((chasing && distance >= sightRange2 / 8) || getPathData().isTrue(OBSTACLE_BETWEEN)) {
            goTo(destination);
        } else {
            setDirection((int) Methods.pointAngleCounterClockwise(x, y, target.getX(), target.getY()));
            brake(2);
            chasing = distance >= sightRange2 / 16;
        }
    }

    private void shiftDestination(int way, Point destination) {
        switch (way % 4) {
            case 0:
                destination.set(target.getX() + sightRange / 2 + getPlusMinusRandom(8), target.getY() + getPlusMinusRandom(8));
                break;
            case 1:
                destination.set(target.getX() - sightRange / 2 + getPlusMinusRandom(8), target.getY() + getPlusMinusRandom(8));
                break;
            case 2:
                destination.set(target.getX() + getPlusMinusRandom(8), target.getY() + sightRange / 2 + getPlusMinusRandom(8));
                break;
            case 3:
                destination.set(target.getX() + getPlusMinusRandom(8), target.getY() - sightRange / 2 + getPlusMinusRandom(8));
                break;
        }
    }

    private int getPlusMinusRandom(int bits) {
        return (random.next(1) > 0 ? 1 : 0) * random.next(bits);
    }

    private void setEnemyToAttack() {
        int distance = Integer.MAX_VALUE;
        int agro = 0;
        int currentDistance;
        int currentAgro;
        boolean agresor = false;
        for (GameObject object : closeEnemies) {
            if (agresor) {
                Agro a = getAgresor(object);
                if (a != null) {
                    currentAgro = a.getValue();
                    if (currentAgro > agro) {
                        agro = currentAgro;
                        target = object;
                    }
                }
            } else {
                currentDistance = Methods.pointDistanceSimple2(getX(), getY(), object.getX(), object.getY());
                Agro a = getAgresor(object);
                if (a != null) {
                    currentAgro = a.getValue();
                    agresor = true;
                    agro = currentAgro;
                    target = object;
                } else if (currentDistance < distance) {
                    target = object;
                    distance = currentDistance;
                }
            }
        }
    }

    @Override
    public void update() {
        if (isHurt()) {
            updateGettingHurt();
            runTo();
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

    private void runTo() {
        if (closeEnemies.isEmpty()) {
            awake = true;
            state = run_to;
            stats.setProtectionState(false);
            destination.set(getX() - (int) Methods.xRadius(knockBack.getAttackerDirection(), sightRange),
                    getY() + (int) Methods.yRadius(knockBack.getAttackerDirection(), sightRange));
        }
    }

    @Override
    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        if (stats.getHealth() * 2 > stats.getMaxHealth()) {
            super.getHurt(knockBackPower, jumpPower, attacker);
        }
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 2);
        brake(2);
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
                animation.setFPS((int) (getSpeed() * 10));
                animation.animateIntervalInDirection(getDirection8Way(), 4, 18);
            } else {
                if (attacking) {
                    attacking = false;
                } else if (awake) {
                    animation.animateSingleInDirection(getDirection8Way(), 1);
                } else {
                    animation.animateSingleInDirection(getDirection8Way(), 0);
                }
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef((int) (getX() * Place.getCurrentScale() + xEffect), (int) (getY() * Place.getCurrentScale() + yEffect), 0);
            Drawer.setColor(JUMP_SHADOW_COLOR);
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
//          renderPathPoints(xEffect, yEffect);
        }
    }

    private class Order {

        private final static byte ATTACK = 0, GO_TO = 1;
        private byte order;
        private byte type = -1;
        private GameObject target;
    }
}
