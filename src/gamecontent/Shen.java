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
import engine.*;
import game.gameobject.*;
import game.place.Place;
import navmeshpathfinding.PathFindingModule;
import net.jodk.lang.FastMath;
import org.newdawn.slick.Color;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Shen extends Mob {

    private final Animation animation;
    int seconds = 0, max = 4;
    private Color skinColor;
    private Delay attack_delay = new Delay(1000);           //TODO - te wartości losowe i zależne od poziomu trudności
    private Delay rest = new Delay(1000);            //TODO - te wartości losowe i zależne od poziomu trudności
    private ActionState idle, run_away, hide, attack, wander, follow;

    {
        idle = new ActionState() {
            @Override
            public void update() {
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                brake(2);

                if (rest.isOver()) {
                    calculateDestinationsForEscape();
                    GameObject closerEnemy = getEnemyCloser();
                    if (closerEnemy != null) {
                        state = hide;
                        destination.set(-1, -1);
                        stats.setProtectionState(true);
                    } else if (destination.getX() > 0) {
                        state = run_away;
                        destination.set(-1, -1);
                        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
                    } else {
                        calculateDestinationsForCloseFriends();
                        if (alpha) {
                            System.out.println("Wander");
                            state = wander;
                            setPathStrategy(PathFindingModule.GET_CLOSE, 250);
                        } else if (secondaryDestination.getX() > 0) {
                            System.out.println("Follow");
                            state = follow;
                            setPathStrategy(PathFindingModule.GET_TO, 0);
                        }
                    }
                }
            }
        };
        run_away = new ActionState() {
            @Override
            public void update() {
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForEscape();
                goTo(destination);
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
                        setMaxSpeed(4);
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
        follow = new ActionState() {
            @Override
            public void update() {
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                calculateDestinationsForCloseFriends();
                xSpeed = ySpeed = 0;
                goTo(secondaryDestination);
                repulsion();
                if (xSpeed < 1 && xSpeed >= 0.5) {
                    xSpeed = 1;
                }
                if (ySpeed < 1 && ySpeed >= 0.5) {
                    ySpeed = 1;
                }

                calculateDestinationsForEscape();
                if (destination.getX() > 0) {
                    state = idle;
                    destination.set(-1, -1);
                    secondaryDestination.set(-1, -1);
                }
                if (Methods.pointDistance(getX(), getY(), secondaryDestination.getX(), secondaryDestination.getY()) > range * 1.5) {
                    state = idle;
                    secondaryDestination.set(-1, -1);
                }

            }
        };
        wander = new ActionState() {
            @Override
            public void update() {
                if (rest.isOver()) {
                    RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());
                    if (seconds == 0) {
                        int sign = Methods.roundDouble(random.next(10) > 512 ? range : -range);
                        int shift = random.next(7) * sign;
                        destination.setX(getX() + shift);
                        sign = Methods.roundDouble(random.next(10) > 512 ? range : -range);
                        shift = random.next(7) * sign;
                        destination.setY(getY() + shift);
                        if (destination.getX() < collision.getHeight()) {
                            destination.setX(collision.getHeight());
                        }
                        if (destination.getY() < collision.getWidth()) {
                            destination.setY(collision.getWidth());
                        }
                    }
                    seconds++;
                    if (seconds > max) {
                        seconds = 0;
                        max = random.next(4);
                    }
                    rest.start();
                }
                goTo(destination);
                lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
                GameObject closerEnemy = getEnemyCloser();
                if (closerEnemy != null) {
                    state = idle;
                    destination.set(-1, -1);
                }
//                calculateDestinationsForCloseFriends();
//                if (secondaryDestination.getX() > 0 && Methods.pointDistance(getX(), getY(), secondaryDestination.getX(), secondaryDestination.getY()) > collision.getWidth() * 18) {
//                    state = idle;
//                    destination.set(-1, -1);
//                    secondaryDestination.set(-1, -1);
//                }
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

    private void repulsion() {
        for (Mob mob : closeFriends) {
            int distance = Methods.pointDistance(getX(), getY(), mob.getX(), mob.getY());
            if (distance < collision.getWidth() * 2f) {
                float scale = 1 - (distance / (collision.getWidth() * 2f));
                int x = getX() - mob.getX();
                int y = getY() - mob.getY();
                float ratio = Math.abs(y / (float) x);
                x = (int) (Math.signum(x) * range) + getX();
                y = (int) (Math.signum(y) * (ratio * Math.abs(x))) + getY();
                double angle = Methods.pointAngleClockwise(getX(), getY(), x, y);
                xSpeed += scale * Methods.xRadius(angle, getMaxSpeed());
                ySpeed += scale * Methods.yRadius(angle, getMaxSpeed());
            }
        }
    }

    @Override
    public void update() {
        state.update();
        normalizeSpeed();
        updateAnimation();
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
    }

    protected void normalizeSpeed() {
        double directionalSpeed = FastMath.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
        if (directionalSpeed > getMaxSpeed()) {
            directionalSpeed = getMaxSpeed();
            double normalized = FastMath.sqrt((directionalSpeed * directionalSpeed) / 2);
            xSpeed = Math.signum(xSpeed) * normalized;
            ySpeed = Math.signum(ySpeed) * normalized;
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

    private void updateAnimation() {
        animation.setStopAtEnd(false);
        if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
            direction = (int) Methods.pointAngleCounterClockwise(0, 0, xSpeed, ySpeed);
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
                animation.setStopAtEnd(true);
                animation.animateIntervalInDirection(direction / 45, 7, 12);
//                collision.setWidthAndHeight(32, 23);
            } else {
                animation.animateSingleInDirection(direction / 45, 0);
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
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            if (map != null) {
                Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
            }
            glPopMatrix();

//            renderPathPoints(xEffect, yEffect);


        }
    }

    private void renderPathPoints(int xEffect, int yEffect) {
        PointContainer path = pathData.getPath();
        int current = pathData.getCurrentPointIndex();
        Drawer.setColor(new Color(0.5f, 0.1f, 0.1f));

        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        if (path != null) {
            for (int i = current; i < path.size(); i++) {
                Drawer.drawRectangle(path.get(i).getX(), path.get(i).getY(), 10, 10);
            }

        }
        if (destination.getX() > 0) {
            Drawer.drawRectangle(destination.getX(), destination.getY(), 10, 10);
        }
        Drawer.refreshColor();
        glPopMatrix();
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public String getName() {
        return super.getName() + " " + alpha;
    }
}
