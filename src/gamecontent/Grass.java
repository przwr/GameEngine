package gamecontent;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.place.Place;
import game.place.map.Area;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 27.11.15.
 */
public class Grass extends GameObject {


    private static RandomGenerator random = RandomGenerator.create();
    int radius;
    int distance = Integer.MAX_VALUE, factor, xBladesCount, yBladesCount, bladeWidth, bladeSpacing, bladeHeight, bladeHeightHalf, tempX, tempY, currentDistance,
            currentDistance2;
    float objectRadius, objectRadiusSqrt2;
    Point object = new Point();
    Point[] blades;

    public Grass(int x, int y, String name) {
        initialize(name, x, y);
        toUpdate = true;
        xBladesCount = 10;
        yBladesCount = 2;
        bladeWidth = 6;
        bladeSpacing = Math.round(bladeWidth * 0.75f);
        bladeHeight = 32;
        bladeHeightHalf = bladeHeight / 2;
        radius = xBladesCount * bladeWidth / 2;
        blades = new Point[3 * xBladesCount * yBladesCount];
        int xCentralized = -xBladesCount / 2;
        int yCentralized = -yBladesCount / 2;
        int yWidth = Math.round(bladeWidth);
        for (int i = 0; i < yBladesCount; i++) {
            for (int j = 0; j < 3 * xBladesCount; j += 3) {
                int c = j / 3;
                int idx = (i * xBladesCount * 3) + j;
                int placeR = (-2 + random.next(2));
                int widthR = (-4 + random.next(3));
                int heightR = (-8 + random.next(4));
                blades[idx] = new Point(placeR + widthR + (xCentralized + c) * bladeSpacing + bladeWidth / 2, -bladeHeight + heightR +
                        (yCentralized + i) * yWidth);
                blades[idx + 1] = new Point(placeR + (xCentralized + c + 1) * bladeSpacing, (yCentralized + i) * yWidth);
                blades[idx + 2] = new Point(placeR + (xCentralized + c) * bladeSpacing, (yCentralized + i) * yWidth);
            }
        }
    }


    @Override
    public void update() {
        distance = Integer.MAX_VALUE;
        factor = depth = 0;
        Area area = map.getArea(getX(), getY());
        for (int i = 0; i < this.map.place.getPlayersCount(); i++) {
            GameObject player = map.place.players[i];
            currentDistance = player.getCollision().getWidth() / 3 + radius;
            currentDistance2 = currentDistance * currentDistance;
            if ((currentDistance = Methods.pointDistanceSimple2(getX(), getY(), player.getX(), player.getY())) < currentDistance2) {
                if (currentDistance < distance) {
                    distance = currentDistance;
                    objectRadius = player.getCollision().getWidth() / 3;
                    objectRadiusSqrt2 = Methods.roundDouble(objectRadius * Methods.ONE_BY_SQRT_ROOT_OF_2);
                    object.set(player.getX(), player.getY());
                }
            }
        }
        for (Mob mob : area.getNearSolidMobs()) {
            currentDistance = mob.getCollision().getWidth() / 2 + radius;
            currentDistance2 = currentDistance * currentDistance;
            if ((currentDistance = Methods.pointDistanceSimple2(getX(), getY(), mob.getX(), mob.getY())) < currentDistance2) {
                if (currentDistance < distance) {
                    distance = currentDistance;
                    objectRadius = mob.getCollision().getWidth() / 2;
                    objectRadiusSqrt2 = Methods.roundDouble(objectRadius * Methods.ONE_BY_SQRT_ROOT_OF_2);
                    object.set(mob.getX(), mob.getY());
                }
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(getX(), (int) (getY() - floatHeight), 0);
        Drawer.setColor(new Color(0, 0.8f * Place.getDayCycle().getShade().g, 0));
        for (int i = 0; i < blades.length; i += 3) {
            calculateFactor(blades[i].getX(), blades[i + 1].getY());
            tempY = (blades[i].getY() + blades[i + 1].getY()) / 2;
            tempX = (blades[i].getX() + factor + (blades[i + 1].getX() + blades[i + 2].getX())) / 3;
            Drawer.drawTriangle(tempX, tempY, blades[i + 1].getX(), blades[i + 1].getY(), blades[i + 2].getX(), blades[i + 2].getY());
            if (factor > 0) {
                Drawer.drawTriangle(blades[i].getX() + factor, blades[i].getY(),
                        tempX, tempY, blades[i + 2].getX(), blades[i + 2].getY());
            } else {
                Drawer.drawTriangle(blades[i].getX() + factor, blades[i].getY(),
                        blades[i + 1].getX(), blades[i + 1].getY(), tempX, tempY);
            }
        }
        tempX = blades[(yBladesCount - 1) * 3 * xBladesCount + 2].getX();
        Drawer.drawRectangle(tempX, blades[blades.length - 1].getY(), blades[blades.length - 2].getX() - tempX, bladeWidth / 2);
        Drawer.refreshColor();
        glPopMatrix();
    }

    private void calculateFactor(int x, int y) {
        if (distance < Integer.MAX_VALUE) {
            factor = getX() + x - object.getX() + (getY() + y - object.getY()) / 3;
            if (factor == 0) {
                factor = bladeHeightHalf;
            } else if (factor <= bladeHeightHalf) {
                factor = bladeHeightHalf / factor;
            } else {
                factor = 0;
            }
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

}
