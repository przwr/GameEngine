package gamecontent;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.PointedValue;
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
    int distance = Integer.MAX_VALUE, factor, xBladesCount, yBladesCount, bladeWidth, bladeSpacing, bladeHeight, bladeHeightHalf, tempX, tempY,
            xCurrentDistance, yCurrentDistance, currentDistance2, xRadius, yRadius;
    PointedValue object = new PointedValue();
    PointedValue[] blades;

    public Grass(int x, int y, String name) {
        initialize(name, x, y);
        toUpdate = true;
        xBladesCount = 8;
        yBladesCount = 2;
        bladeWidth = 8;
        bladeHeight = 32;
        bladeSpacing = Math.round(bladeWidth * 0.75f);
        bladeHeightHalf = bladeHeight / 2;
        xRadius = xBladesCount * bladeWidth / 2;
        yRadius = yBladesCount * bladeWidth / 2;
        blades = new PointedValue[3 * xBladesCount * yBladesCount];
        int xCentralized = -xBladesCount / 2;
        int yCentralized = -yBladesCount / 2;
        for (int i = 0; i < yBladesCount; i++) {
            for (int j = 0; j < 3 * xBladesCount; j += 3) {
                int c = j / 3;
                int idx = (i * xBladesCount * 3) + j;
                int placeR = (-2 + random.next(2));
                int widthR = (-4 + random.next(3));
                int heightR = (-8 + random.next(4));
                int color = 170 + random.next(5);
                blades[idx] = new PointedValue(placeR + widthR + (xCentralized + c) * bladeSpacing + bladeWidth / 2, -bladeHeight + heightR +
                        (yCentralized + i) * bladeWidth, color);
                blades[idx + 1] = new PointedValue(placeR + (xCentralized + c + 1) * bladeSpacing, (yCentralized + i) * bladeWidth, 0);
                blades[idx + 2] = new PointedValue(placeR + (xCentralized + c) * bladeSpacing, (yCentralized + i) * bladeWidth, 0);
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
            if ((xCurrentDistance = Math.abs(getX() - player.getX())) < xRadius + player.getCollision().getWidth() / 3) {
                if ((yCurrentDistance = Math.abs(getY() - player.getY())) < yRadius + player.getCollision().getHeight() / 3) {
                    if (xCurrentDistance + yCurrentDistance < distance) {
                        distance = xCurrentDistance + yCurrentDistance;
                        object.set(player.getX(), player.getY());
                    }
                }
            }
        }
        for (Mob mob : area.getNearSolidMobs()) {
            if ((xCurrentDistance = Math.abs(getX() - mob.getX())) < xRadius + mob.getCollision().getWidth() / 3) {
                if ((yCurrentDistance = Math.abs(getY() - mob.getY())) < yRadius + mob.getCollision().getHeight() / 3) {
                    if (xCurrentDistance + yCurrentDistance < distance) {
                        distance = xCurrentDistance + yCurrentDistance;
                        object.set(mob.getX(), mob.getY());
                    }
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
        Drawer.setColor(new Color(0, 0.7f * Place.getDayCycle().getShade().g, 0));
        Drawer.setCentralPoint();
        Drawer.drawRectangle(tempX, blades[1].getY(), blades[blades.length - 2].getX() - tempX, blades[blades.length - 1].getY() - blades[1].getY());
        Drawer.returnToCentralPoint();
        for (int i = 0; i < blades.length; i += 3) {
            calculateFactor(blades[i].getX(), blades[i + 1].getY());
            tempY = (blades[i].getY() + blades[i + 1].getY()) / 2;
            tempX = (blades[i].getX() + factor + (blades[i + 1].getX() + blades[i + 2].getX())) / 3;
            Drawer.setColor(new Color(0, (int) (blades[i].getValue() * Place.getDayCycle().getShade().g), 0));
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
