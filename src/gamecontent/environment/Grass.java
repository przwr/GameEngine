package gamecontent.environment;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.PointedValue;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.place.Place;
import game.place.map.Area;
import game.place.map.Map;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 27.11.15.
 */
public class Grass extends GameObject {

    public static RandomGenerator random;
    int distance = Integer.MAX_VALUE, factor, xBladesCount, yBladesCount, bladeWidth, bladeSpacing, bladeHeight, bladeHeightHalf, tempX, tempY,
            xCurrentDistance, yCurrentDistance, xRadius, yRadius;
    PointedValue object = new PointedValue();
    PointedValue[] blades;
    boolean masking;

    {
        random = random == null ? RandomGenerator.create() : random;
    }

    private Grass(int x, int y, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, boolean masking) {
        initialize("Grass", x, y);
        this.xBladesCount = xBladesCount;
        this.yBladesCount = yBladesCount;
        this.bladeWidth = bladeWidth;
        this.bladeHeight = bladeHeight;
        this.masking = masking;
        bladeSpacing = Math.round(bladeWidth * 0.75f);
        bladeHeightHalf = bladeHeight / 2;
        xRadius = xBladesCount * bladeWidth / 2;
        yRadius = yBladesCount * bladeWidth / 2;
        blades = new PointedValue[3 * xBladesCount * yBladesCount];
        float xCentralized = -xBladesCount / 2f;
        int yCentralized = -yBladesCount / 2;
        for (int i = 0; i < yBladesCount; i++) {
            for (int j = 0; j < 3 * xBladesCount; j += 3) {
                int c = j / 3;
                int idx = (i * xBladesCount * 3) + j;
                int placeR = (-2 + random.next(2));
                int widthR = (-4 + random.next(3));
                int heightR = (-8 + random.next(4));
                int color = 170 + random.next(5);
                blades[idx] = new PointedValue(placeR + widthR + Math.round((xCentralized + c) * bladeSpacing) + bladeWidth / 2, -bladeHeight + heightR +
                        (yCentralized + i) * bladeWidth, color);
                blades[idx + 1] = new PointedValue(placeR + Math.round((xCentralized + c + 1) * bladeSpacing), (yCentralized + i) * bladeWidth, 0);
                blades[idx + 2] = new PointedValue(placeR + Math.round((xCentralized + c) * bladeSpacing), (yCentralized + i) * bladeWidth, 0);
            }
        }
    }

    public static Grass createNonMasking(int x, int y, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new Grass(x, y, xBladesCount, yBladesCount, bladeWidth, bladeHeight, false);
    }

    public static Grass create(int x, int y, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new Grass(x, y, xBladesCount, yBladesCount, bladeWidth, bladeHeight, true);
    }

    public void update() {
        update(map);
    }

    public void update(Map map) {
        distance = Integer.MAX_VALUE;
        factor = 0;
        if (map != null) {
            Area area = map.getArea(getX(), getY());
            for (int i = 0; i < map.place.getPlayersCount(); i++) {
                GameObject player = map.place.players[i];
                if (player.getFloatHeight() < bladeHeight) {
                    if ((xCurrentDistance = Math.abs(getX() - player.getX())) < xRadius + player.getCollision().getWidthHalf()) {
                        if ((yCurrentDistance = Math.abs(getY() - player.getY())) < yRadius + player.getCollision().getHeightHalf()) {
                            if (xCurrentDistance + yCurrentDistance < distance) {
                                distance = xCurrentDistance + yCurrentDistance;
                                object.set(player.getX(), player.getY());
                            }
                        }
                    }
                }
            }
            for (Mob mob : area.getNearSolidMobs()) {
                if (mob.getFloatHeight() < bladeHeight) {
                    if ((xCurrentDistance = Math.abs(getX() - mob.getX())) < xRadius + mob.getCollision().getWidthHalf()) {
                        if ((yCurrentDistance = Math.abs(getY() - mob.getY())) < yRadius + mob.getCollision().getHeightHalf()) {
                            if (xCurrentDistance + yCurrentDistance < distance) {
                                distance = xCurrentDistance + yCurrentDistance;
                                object.set(mob.getX(), mob.getY());
                            }
                        }
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
        if (masking) {
            tempX = blades[(yBladesCount - 1) * 3 * xBladesCount + 2].getX();
            Drawer.setColor(new Color(0, 0.7f * Place.getLightColor().g, 0));
            Drawer.setCentralPoint();
            Drawer.drawRectangle(tempX, blades[1].getY(), blades[blades.length - 2].getX() - tempX, blades[blades.length - 1].getY() - blades[1].getY());
            Drawer.returnToCentralPoint();
        }
        for (int i = 0; i < blades.length; i += 3) {
            calculateFactor(blades[i].getX(), blades[i + 1].getY());
            Drawer.setColor(new Color(0, (int) (blades[i].getValue() * Place.getLightColor().g), 0));
            if (factor == 0) {
                Drawer.drawTriangle(blades[i].getX(), blades[i].getY(), blades[i + 1].getX(), blades[i + 1].getY(), blades[i + 2].getX(), blades[i + 2].getY());
            } else {
                tempX = (blades[i].getX() + factor + (blades[i + 1].getX() + blades[i + 2].getX())) / 3;
                tempY = (blades[i].getY() + blades[i + 1].getY()) / 2;
                Drawer.drawTriangle(tempX, tempY, blades[i + 1].getX(), blades[i + 1].getY(), blades[i + 2].getX(), blades[i + 2].getY());
                if (factor > 0) {
                    Drawer.drawTriangle(blades[i].getX() + factor, blades[i].getY(), tempX, tempY, blades[i + 2].getX(), blades[i + 2].getY());
                } else {
                    Drawer.drawTriangle(blades[i].getX() + factor, blades[i].getY(), blades[i + 1].getX(), blades[i + 1].getY(), tempX, tempY);
                }
            }
        }
        Drawer.refreshColor();
        glPopMatrix();
    }

    public void renderStill() {
        if (masking) {
            tempX = blades[(yBladesCount - 1) * 3 * xBladesCount + 2].getX();
            Drawer.setColor(new Color(0, 0.7f, 0));
            Drawer.setCentralPoint();
            Drawer.drawRectangle(tempX, blades[1].getY(), blades[blades.length - 2].getX() - tempX, blades[blades.length - 1].getY() - blades[1].getY());
            Drawer.returnToCentralPoint();
        }
        for (int i = 0; i < blades.length; i += 3) {
            tempX = (blades[i].getX() + (blades[i + 1].getX() + blades[i + 2].getX())) / 3;
            tempY = (blades[i].getY() + blades[i + 1].getY()) / 2;
            Drawer.setColor(new Color(0, blades[i].getValue(), 0));
            Drawer.drawTriangle(blades[i].getX(), blades[i].getY(), blades[i + 1].getX(), blades[i + 1].getY(), blades[i + 2].getX(), blades[i + 2].getY());
        }
        Drawer.refreshColor();
    }


    private void calculateFactor(int x, int y) {
        if (distance < Integer.MAX_VALUE) {
            factor = 2 * (getX() + x - object.getX()) / 3 + (getY() + y - object.getY()) / 3;
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

    public void reset() {
        factor = 0;
        distance = Integer.MAX_VALUE;
    }

    public int getXSpriteBegin() {
        return getX() - xRadius;
    }

    public int getYSpriteBegin() {
        return getY() - yRadius;
    }

    public int getXSpriteEnd() {
        return getX() + xRadius;
    }

    public int getYSpriteEnd() {
        return getY() + yRadius;
    }
}
