/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import collision.interactive.CircleInteractiveCollision;
import collision.interactive.InteractiveActivatorFrames;
import engine.Delay;
import engine.Drawer;
import engine.Methods;
import game.gameobject.*;
import game.place.Place;
import navmeshpathfinding.PathFindingModule;
import org.newdawn.slick.Color;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Shen extends Mob {

    private final Animation animation;
    private Color skinColor;
    private Delay attack_delay = new Delay(1000);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = new Delay(1000);            //TODO - te wartości losowe i zależne od poziomu trudności
    private ActionState idle, run_away, hide, attack;


    {
        idle = new ActionState() {
            @Override
            public void update() {
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForEscape();
                brake(2);

                if (rest.isOver()) {
                    GameObject closerEnemy = getEnemyCloser();
                    if (closerEnemy != null) {
                        state = hide;
                        destination.set(-1, -1);
                        stats.setProtectionState(true);
                    } else if (destination.getX() > 0) {
                        state = run_away;
                        setMaxSpeed(1);
                        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
                    }
                }
            }
        };
        run_away = new ActionState() {
            @Override
            public void update() {
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForEscape();
                goTo();

                GameObject closerEnemy = getEnemyCloser();
                if (closerEnemy != null) {
                    state = hide;
                    destination.set(-1, -1);
                    stats.setProtectionState(true);
                } else if (destination.getX() < 0) {
                    state = idle;
                }
            }
        };
        hide = new ActionState() {
            @Override
            public void update() {
                brake(2);
                if (appearance.getCurrentFrameIndex() % animation.getFramesPerDirection() == 12) {
                    lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                    GameObject closerEnemy = getEnemyCloser();

                    if (closerEnemy == null) {
                        state = idle;
                        stats.setProtectionState(false);
                    } else if (rest.isOver() && target == null && (Methods.pointDistance(closerEnemy.getX(), closerEnemy.getY(), getX(), getY()) < range && stats.getHealth() < stats.getMaxHealth())) {
                        state = attack;
                        target = closerEnemy;
                        setMaxSpeed(5);
                        setPathStrategy(PathFindingModule.GET_TO, 0);
                        attack_delay.start();
                    }
                }
            }
        };
        attack = new ActionState() {
            @Override
            public void update() {
                chase();

                if (attack_delay.isOver() || Methods.pointDistance(getX(), getY(), target.getX(), target.getY()) > range * 1.5 || getTarget().getMap() != map) {
                    state = idle;
                    target = null;
                    pathData.clearPath();
                    brake(2);
                    rest.start();
                    stats.setProtectionState(false);
                    setMaxSpeed(1);
                    setPathStrategy(PathFindingModule.GET_CLOSE, 250);
                }
            }
        };
    }

    public Shen(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Shen", place, "shen", true, ID);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 15);
        appearance = animation;
        //RandomGenerator r = RandomGenerator.create();
        //skinColor = Color.getHSBColor(r.nextFloat(), 1, 1);
        collision.setMobile(true);
        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
        stats = new MobStats(this);
        stats.setStrength(1);
        stats.setDefence(3);
        attack_delay.start();
        rest.start();
        state = idle;
        int[] frames = new int[8];
        for (int i = 0; i < 8; i++) {
            frames[i] = 13 + i * animation.getFramesPerDirection();
        }
        addInteractive(new Interactive(this, new InteractiveActivatorFrames(frames), new CircleInteractiveCollision(0, 64, 24, 28), Interactive.HURT, 0.1f));
    }

    @Override
    public void update() {
        state.update();
        updateAnimation();
    }

    private void updateAI() {
        lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
        GameObject closerEnemy = getEnemyCloser();
        if (!stats.isProtectionState() || closerEnemy == null) {
            calculateDestinationsForEscape();
        }
        if (closerEnemy != null && stats.isProtectionState() && rest.isOver() && target == null) {
            if (Methods.pointDistance(closerEnemy.getX(), closerEnemy.getY(), getX(), getY()) < range && stats.getHealth() < stats.getMaxHealth()) {
                target = closerEnemy;
                attack_delay.start();
            }
        }
        if (target != null && (!(target instanceof MyPlayer) || ((MyPlayer) target).isInGame())) {
            setChasingMode();
            if (Methods.pointDistance(getX(), getY(), target.getX(), target.getY()) > range * 1.5 || getTarget().getMap() != map) {
                target = null;
                pathData.clearPath();
                setNormalMode();
            } else {
                if (attack_delay.isOver()) {
                    target = null;
                    brake(2);
                    rest.start();
                    stats.setProtectionState(false);
                } else {
                    chase();
                }

            }
        } else {
            if (!rest.isOver()) {
                brake(2);
            } else {
                setNormalMode();
                if (destination.getX() > 0) {
                    stats.setProtectionState(false);
                    if (closerEnemy != null) {
                        destination.set(-1, -1);
                        stats.setProtectionState(true);
                    }
                    if (stats.isProtectionState()) {
                        brake(2);
                    } else {
                        goTo();
                    }
                } else {
                    brake(2);
                }
            }
        }
    }

    private GameObject getEnemyCloser() {
        for (GameObject object : closeEnemies) {
            if (Methods.pointDistance(object.getX(), object.getY(), getX(), getY()) < range / 3) {
                return object;
            }
        }
        return null;
    }

    private void setChasingMode() {
        stats.setProtectionState(true);
        setMaxSpeed(5);
        setPathStrategy(PathFindingModule.GET_TO, 0);
    }

    private void setNormalMode() {
        setMaxSpeed(1);
        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
    }

    private void setRestingMode() {
        setNormalMode();
        rest.start();
        stats.setProtectionState(false);
    }

    private void updateAnimation() {
        if (xSpeed != 0 || ySpeed != 0) {
            if (Math.abs(xSpeed) > 0.9 || Math.abs(ySpeed) > 0.9)
                direction = (int) Methods.pointAngleCounterClockwise(0, 0, (int) xSpeed, (int) ySpeed);
            if (target == null) {
                animation.setFPS(7);
                animation.animateIntervalInDirection(direction / 45, 0, 5);
//                collision.setWidthAndHeight(48, 34);

            } else {
                animation.setFPS(30);
                animation.animateIntervalInDirection(direction / 45, 12, 14);
//                collision.setWidthAndHeight(32, 23);
            }
        } else {
            if (stats.isProtectionState()) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(direction / 45, 7, 12);
                animation.setStopAtEnd(true);
//                collision.setWidthAndHeight(32, 23);
            } else {
                animation.animateSingleInDirection(direction / 45, 0);
            }
        }

        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
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
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
        }
        if (map != null) {
            Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                    map.getLightColor());
        }
        glPopMatrix();
    }
}
