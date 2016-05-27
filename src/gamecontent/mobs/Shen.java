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
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.interactive.collision.CircleInteractiveCollision;
import game.gameobject.stats.MobStats;
import game.gameobject.temporalmodifiers.SpeedChanger;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import gamecontent.environment.Corpse;
import gamecontent.environment.MoneyBag;
import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class Shen extends Mob {

    private final static byte ATTACK_NORMAL = 0, ATTACK_CRITICAL = 1;
    private int seconds = 0, max = 5;
    private Delay attack_delay = Delay.createInMilliseconds(1250);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = Delay.createInMilliseconds(1250);            //TODO - te wartości losowe i zależne od poziomu trudności
    private ActionState idle, run_away, hide, attack, wander, follow, bounce;
    private SpeedChanger bouncer;
    private boolean attacking = true, unfold, fold;
    private RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());

    {
        idle = new ActionState() {
            @Override
            public void update() {
//                System.out.println("IDLE");
                if (rest.isOver()) {
                    brake(2);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    calculateDestinationsForEscape();
                    GameObject closerEnemy = getCloserEnemy();
                    if (closerEnemy != null) {
                        state = hide;
                        destination.set(-1, -1);
                        fold = true;
                    } else if (destination.getX() > 0) {
                        state = run_away;
                        destination.set(-1, -1);
                    } else {
                        calculateDestinationsForCloseFriends();
                        if (leader) {
                            state = wander;
                            maxSpeed = 0.8;
                            destination.set(getX(), getY());
                            seconds = 0;
                        } else if (secondaryDestination.getX() > 0) {
                            state = follow;
                            pathData.setAvoidMobile(false);
                            setPathStrategy(PathFindingModule.GET_TO, collision.getWidth() * 2);
                        } else {
                            state = wander;
                            maxSpeed = 1;
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
//                    System.out.println(secondaryDestination);
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForEscape();
                goTo(destination.getX() > 0 ? destination : secondaryDestination);
                GameObject closerEnemy = getCloserEnemy();
                if (closerEnemy != null) {
                    state = hide;
                    destination.set(-1, -1);
                    secondaryDestination.set(-1, -1);
                    fold = true;
                } else if (closeEnemies.isEmpty() || (destination.getX() < 0 && Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination.getX(),
                        secondaryDestination.getY()) < 4 * hearRange2 / 9)) {
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
                if (!fold) {
                    stats.setProtectionState(true);
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    Entity closerEnemy = getCloserEnemy();
                    if (!unfold && animation.getDirectionalFrameIndex() == 7) {
                        state = idle;
                        stats.setProtectionState(false);
                    } else if (closerEnemy == null) {
                        unfold = true;
                    } else if (rest.isOver() && target == null && (stats.getHealth() < stats.getMaxHealth() || isAnyFriendHurt())
                            && isInHalfHearingRange(closerEnemy)) {
                        state = attack;
                        target = closerEnemy;
                        setPathStrategy(PathFindingModule.GET_TO, 0);
                        attack_delay.start();
                    }
                }
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
//                System.out.println("ATTACK");
                if (rest.isOver()) {
                    if (!attacking && attack_delay.isOver()) {
                        attack_delay.start();
                        attacking = true;
                        stats.setProtectionState(true);
                    }
                    if (animation.getDirectionalFrameIndex() == 12) {
                        if (xSpeed == 0 && ySpeed == 0) {
                            if (stats.getHealth() < stats.getMaxHealth() / 2) {
                                maxSpeed = 11;
                            } else {
                                maxSpeed = 8;
                            }
                            stats.setUnhurtableState(true);
                            charge();
                        } else {
                            if (stats.getHealth() < stats.getMaxHealth() / 2) {
                                getAttackActivator(ATTACK_CRITICAL).setActivated(true);
                            } else {
                                getAttackActivator(ATTACK_NORMAL).setActivated(true);
                            }
                        }
                        if (attack_delay.isOver()) {
                            rest.start();
                            stats.setProtectionState(false);
                            attacking = false;
                            stats.setUnhurtableState(false);
                            brake(2);
                        }
                        if (!attacking && !isInRange(target) || target.getMap() != map || !target.getCollision().isHitable()) {
                            state = idle;
                            target = null;
                            stats.setUnhurtableState(false);
                            stats.setProtectionState(false);
                            unfold = true;
                            maxSpeed = 1;
                            brake(2);
                            setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
                        }
                    }
                } else {
                    brake(2);
                }
            }
        };
        bounce = new ActionState() {
            @Override
            public void update() {
//                System.out.println("BOUNCE");
                if (xSpeed != 0 || ySpeed != 0) {
                    bouncer.setFrames(30);
                    bouncer.setSpeed((int) (-xSpeed / 2), (int) (-ySpeed / 2));
                    bouncer.setType(SpeedChanger.DECREASING);
                    bouncer.start();
                    stats.setUnhurtableState(true);
                    addChanger(bouncer);
                    setUpForce((Math.abs(xSpeed) + Math.abs(ySpeed)) / 2);
                    xSpeed = 0;
                    ySpeed = 0;
                } else if (bouncer.isOver() && animation.getDirectionalFrameIndex() != 7) {
                    target = null;
                    unfold = true;
                    maxSpeed = 1;
                    stats.setUnhurtableState(false);
                    stats.setProtectionState(false);
                    setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
                } else if (!stats.isProtectionState() && animation.getDirectionalFrameIndex() == 7) {
                    state = idle;
                    rest.start();
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
                GameObject closerEnemy = getCloserEnemy();
                if (closerEnemy != null || isDistance2OutOfRange(distance)) {
                    state = idle;
                    pathData.setAvoidMobile(true);
                    secondaryDestination.set(-1, -1);
                    setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
                }
            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
//                System.out.println("WANDER");
                if (rest.isOver()) {
                    if (seconds == 0 || destination.getX() < 0 || Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <=
                            sightRange2 / 16) {
                        destination.set(getRandomPointInDistance((int) (sightRange * 1.5), spawnPosition.getX(), spawnPosition.getY()));
//                        System.out.println(destination);
                    }
                    seconds++;
                    if (seconds > max) {
                        seconds = 0;
                        max = random.randomInRange(5, 10);
                    }
                    rest.start();
                }
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                if (!closeEnemies.isEmpty() || (!leader && !closeFriends.isEmpty())) {
                    state = idle;
                    destination.set(-1, -1);
                    maxSpeed = 1;
                    rest.terminate();
                }
                if (leader && !closeFriends.isEmpty()) {
                    int x = 0, y = 0;
                    for (Mob mob : closeFriends) {
                        x += mob.getX();
                        y += mob.getY();
                    }
                    if (Methods.pointDistanceSimple2(getX(), getY(), x / closeFriends.size(), y / closeFriends.size()) < sightRange2 / 4) {
                        goTo(destination);
                    }
                } else {
                    goTo(destination);
                }
            }
        };
    }

    public Shen() {
    }


    public Shen(int x, int y, Place place) {
        super(x, y, 1, 512, "Shen", place, "shen", true, place.getNextMobID());
        setUp();
    }

    private void setUp() {
        //RandomGenerator r = RandomGenerator.createNonMasking();
        //skinColor = Color.getHSBColor(r.nextFloat(), 1, 1);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
        setUpDirectionalAnimation(0, 17);
        setDirection8way(random.randomInRange(0, 7));
        collision.setMobile(true);
        setHasStaticShadow(true);
        stats = new MobStats(this);
        stats.setStartHealth(1);
        stats.setStrength(10);
        stats.setDefence(3);
        stats.setWeight(70);
        stats.setSideDefenceModifier(10);
        stats.setBackDefenceModifier(4);
        stats.setProtection(10);
        stats.setProtectionSideModifier(4);
        stats.setProtectionBackModifier(1);
        rest.terminate();
        attack_delay.terminate();
        letGoDelay.terminate();
        state = idle;
        bouncer = new SpeedChanger();
        spawnPosition.set(getX(), getY());
        neutral.add(Plurret.class.getName());
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new CircleInteractiveCollision(0, 64, -24, 32), Interactive.STRENGTH_HURT,
                ATTACK_NORMAL, 0.5f, 2.5f));
        addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new CircleInteractiveCollision(0, 64, -24, 32), Interactive.STRENGTH_HURT,
                ATTACK_CRITICAL, 2f, 2.5f));
        addPushInteraction();
    }

    @Override
    public void initialize(int x, int y, Place place) {
        super.initialize(x, y, 1, 512, "Shen", place, "shen", true, place.getNextMobID());
        setUp();
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
            for (Mob mob : closeFriends) {
                if (mob.isLeader()) {
                    xSpeed += mob.getXSpeed() / 2;
                    ySpeed += mob.getYSpeed() / 2;
                }
            }
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

    private Entity getCloserEnemy() {
        for (Entity entity : closeEnemies) {
            if (isInHalfHearingRange(entity)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void update() {
        appearance.updateFrame();
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

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 6);
        brake(2);
    }

    private void runWhenHurt() {
        if (closeEnemies.isEmpty()) {
            state = run_away;
            destination.set(-1, -1);
            secondaryDestination.set(-1, -1);
            maxSpeed = 1;
            stats.setProtectionState(false);
            setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
            destination.set(getX() + (int) Methods.xRadius(knockBack.getAttackerDirection(), sightRange),
                    getY() - (int) Methods.yRadius(knockBack.getAttackerDirection(), sightRange));
        }
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
            if (target == null) {
                animation.setFPS((int) (getSpeed() * 10));
                animation.animateIntervalInDirection(getDirection8Way(), 0, 5);
            } else {
                animation.setFPS((int) (getSpeed() * 2));
                animation.animateIntervalInDirection(getDirection8Way(), 12, 14);
            }
        } else {
            if (fold) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 7, 12);
                animation.setStopAtEnd(true);
                if (animation.getDirectionalFrameIndex() == 12) {
                    fold = false;
                }
            } else if (unfold) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 12, 7);
                animation.setStopAtEnd(true);
                if (animation.getDirectionalFrameIndex() == 7) {
                    unfold = false;
                }
            } else if (stats.isProtectionState()) {
                animation.animateSingleInDirection(getDirection8Way(), 12);
            } else {
                animation.animateSingleInDirection(getDirection8Way(), 0);
            }
        }
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            if (Main.SHOW_INTERACTIVE_COLLISION) {
                interactiveObjects.stream().forEach((interactive) -> {
                    interactive.render();
                });
            }
            appearance.render();
//            renderPathPoints(t);
        }
    }

    @Override
    public void reactToAttack(byte attackType, GameObject attacked, int hurt) {
        super.reactToAttack(attackType, attacked, hurt);
        state = bounce;
    }

    @Override
    public Corpse deathReaction(InteractiveResponse response) {
        animation.setFPS(4);
        animation.animateIntervalInDirectionOnce(getDirection8Way(), 15, 16);
        Corpse corpse = super.deathReaction(response);
        if (corpse != null) {
            corpse.addItem(new MoneyBag(0, 0, place));
        }
        return corpse;
    }

}
