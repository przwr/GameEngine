/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import engine.Delay;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.Settings;
import game.place.Place;
import net.packets.Update;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;
    protected Point[] path, oldPath;
    protected int currentPoint, oldPoint;
    public short mobID;
    public Delay delay = new Delay(250);

    {
        delay.start();
    }

    public abstract void update();

    public Mob(int x, int y, double speed, int range, String name, Place place, String spriteName, boolean solid) {
        this.place = place;
        this.solid = solid;
        this.range = range;
        this.setMaxSpeed(speed);
        this.sprite = place.getSprite(spriteName);
        initialize(name, x, y);
    }

    public synchronized void look(GameObject[] players) {
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && Methods.pointDistance(object.getX(), object.getY(), getX(), getY()) < range) {
                prey = object;
                break;
            }
        }
    }

    public synchronized void chase(GameObject prey) {
        int scope = collision.getWidth() + collision.getHeight();
        if (prey != null) {
            double angle;
            if (path != null && !delay.isOver()) {
                angle = Methods.pointAngle360(getX(), getY(), path[currentPoint].getX(), path[currentPoint].getY());
                //System.out.println("Follow path! " + currentPoint + "/" + (path.length - 1));
                if (Methods.pointDistance(getX(), getY(), path[currentPoint].getX(), path[currentPoint].getY()) < maxSpeed*2) {
                    //System.out.println("Get to point! " + currentPoint + "/" + (path.length - 1));
                    if (currentPoint < path.length - 1) {
                        currentPoint++;
                    } else {
                        path = null;
                    }
                }
            } else {
                delay.start();
                if (Methods.pointDistance(getX(), getY(), prey.getX(), prey.getY()) > scope) {
                    if (path == null || (Methods.pointDistance(path[path.length - 1].getX(), path[path.length - 1].getY(), prey.getX(), prey.getY()) > scope)) {
                        //System.out.println("Looking for a path! ");
                        setPath(map.findPath(getX(), getY(), prey.getX(), prey.getY(), collision));
                    }
                    if (path != null) {
                        oldPath = this.path;
                        oldPoint = this.currentPoint;
                        //System.out.println("Follow old path! ");
                        angle = Methods.pointAngle360(getX(), getY(), oldPath[oldPoint].getX(), oldPath[oldPoint].getY());
                    } else {
                        //System.out.println("Follow pray! ");
                        angle = Methods.pointAngle360(getX(), getY(), prey.getX(), prey.getY());
                    }
                } else {
                    //System.out.println("Follow pray! ");
                    angle = Methods.pointAngle360(getX(), getY(), prey.getX(), prey.getY());
                }
            }
            xSpeed = Methods.xRadius(angle, maxSpeed);
            ySpeed = Methods.yRadius(angle, maxSpeed);
        }
    }

    public synchronized void setPath(Point[] path) {
        if (path != null) {
            currentPoint = 0;
            this.path = path;
        }
    }

    @Override
    protected boolean isColided(int xMagnitude, int yMagnitude) {
        return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map) || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return collision.firstPlayerCollide(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        setPosition(x + xPosition, y + yPosition);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(getX(), getY(), 0);
            sprite.render();

            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2),
                    place.standardFont,
                    map.getLightColor());
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInShade(sprite, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInBlack(sprite);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInShade(sprite, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInBlack(sprite, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }
}
