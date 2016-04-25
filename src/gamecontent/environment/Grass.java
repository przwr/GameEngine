package gamecontent.environment;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.PointedValue;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.place.map.Area;
import game.place.map.Map;
import gamedesigner.ObjectPlayer;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 27.11.15.
 */
public class Grass extends GameObject {

    public static RandomGenerator random;
    private final Color color = new Color(0x28A705);
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
                int color = random.random(48);
                blades[idx] = new PointedValue(placeR + widthR + Math.round((xCentralized + c) * bladeSpacing) + bladeWidth / 2, -bladeHeight + heightR
                        + (yCentralized + i) * bladeWidth, color);
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

    @Override
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
                if (!(player instanceof ObjectPlayer) && player.getFloatHeight() < bladeHeight) {
                    if ((xCurrentDistance = Math.abs(getX() - player.getX())) < xRadius + player.getCollision().getWidthHalf()) {
                        if ((yCurrentDistance = Math.abs(getY() - player.getY())) < yRadius + player.getCollision().getHeightHalf()) {
                            if (xCurrentDistance + yCurrentDistance < player.getCollision().getWidthHalf()) {
                                ((Player) player).setShadowVisibility(false);
                            }
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
    public void render() {
        Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
        if (masking) {
            tempX = blades[(yBladesCount - 1) * 3 * xBladesCount + 2].getX();
            Drawer.setColorBlended(color);
            Drawer.drawRectangle(tempX, blades[1].getY(), blades[blades.length - 2].getX() - tempX, blades[blades.length - 1].getY() - blades[1].getY());
        }
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        for (int i = 0; i < blades.length; i += 3) {
            calculateFactor(blades[i].getX(), blades[i + 1].getY());
            if (factor == 0) {
                Drawer.streamVertexData.add(
                        blades[i + 2].getX(), blades[i + 2].getY(),
                        blades[i + 1].getX(), blades[i + 1].getY(),
                        blades[i].getX(), blades[i].getY());
                Drawer.streamColorData.add(color.r * Drawer.getCurrentColor().r, color.g * Drawer.getCurrentColor().g, color.b * Drawer.getCurrentColor().b,
                        color.r * Drawer.getCurrentColor().r, color.g * Drawer.getCurrentColor().g, color.b * Drawer.getCurrentColor().b, color.r * Drawer
                                .getCurrentColor().r, (color.g + ((float) blades[i].getValue() / 256)) * Drawer.getCurrentColor().g, color.b * Drawer
                                .getCurrentColor().b);
            } else {
                tempX = (blades[i].getX() + factor + (blades[i + 1].getX() + blades[i + 2].getX())) / 3;
                tempY = (blades[i].getY() + blades[i + 1].getY()) / 2;
                Drawer.streamColorData.add(color.r * Drawer.getCurrentColor().r, color.g * Drawer.getCurrentColor().g, color.b * Drawer.getCurrentColor().b,
                        color.r * Drawer.getCurrentColor().r, color.g * Drawer.getCurrentColor().g, color.b * Drawer.getCurrentColor().b,
                        color.r * Drawer.getCurrentColor().r, (color.g + ((float) blades[i].getValue() / 256)) * Drawer.getCurrentColor().g,
                        color.b * Drawer.getCurrentColor().b, color.r * Drawer.getCurrentColor().r, (color.g + ((float) blades[i].getValue() / 256)) * Drawer
                                .getCurrentColor().g, color.b * Drawer.getCurrentColor().b, color.r * Drawer.getCurrentColor().r, (color.g + ((float)
                                blades[i].getValue() / 256)) * Drawer.getCurrentColor().g, color.b * Drawer.getCurrentColor().b, color.r * Drawer
                                .getCurrentColor().r, (color.g + ((float) blades[i].getValue() / 256)) * Drawer.getCurrentColor().g, color.b * Drawer
                                .getCurrentColor().b);
                Drawer.streamVertexData.add(blades[i + 2].getX(),
                        blades[i + 2].getY(), blades[i + 1].getX(),
                        blades[i + 1].getY(), tempX, tempY);
                if (factor > 0) {
                    Drawer.streamVertexData.add(blades[i].getX() + factor, blades[i].getY(), tempX, tempY, blades[i + 2].getX(), blades[i + 2].getY());
                } else {
                    Drawer.streamVertexData.add(blades[i].getX() + factor, blades[i].getY(), blades[i + 1].getX(), blades[i + 1].getY(), tempX, tempY);
                }
            }
        }
        Drawer.regularShader.setUseTexture(false);
        Drawer.regularShader.setUseColor(true);
        Drawer.grassVBO.renderColoredTriangleStream(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray());
        Drawer.regularShader.setUseTexture(true);
        Drawer.regularShader.setUseColor(false);
        Drawer.refreshColor();
    }

    public void renderStill() {
        if (masking) {
            tempX = blades[(yBladesCount - 1) * 3 * xBladesCount + 2].getX();
            Drawer.setColorStatic(color);
            Drawer.drawRectangle(tempX, blades[1].getY(), blades[blades.length - 2].getX() - tempX, blades[blades.length - 1].getY() - blades[1].getY());
        }
        for (int i = 0; i < blades.length; i += 3) {
            float[] vertices = {
                    blades[i + 2].getX(), blades[i + 2].getY(),
                    blades[i + 1].getX(), blades[i + 1].getY(),
                    blades[i].getX(), blades[i].getY(),
            };
            float[] colors = {
                    color.r, color.g, color.b,
                    color.r, color.g, color.b,
                    color.r, (color.g + ((float) blades[i].getValue() / 256)), color.b
            };
            Drawer.regularShader.setUseTexture(false);
            Drawer.regularShader.setUseColor(true);
            Drawer.grassVBO.renderColoredTriangleStream(vertices, colors);
            Drawer.regularShader.setUseTexture(true);
            Drawer.regularShader.setUseColor(false);
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
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeLit(appearance, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartLit(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartBlack(appearance, collision.getDarkValue(), getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    public void reset() {
        factor = 0;
        distance = Integer.MAX_VALUE;
    }

    @Override
    public int getXSpriteBegin(boolean... forCover) {
        return getX() - xRadius;
    }

    @Override
    public int getYSpriteBegin(boolean... forCover) {
        return getY() - yRadius;
    }

    @Override
    public int getXSpriteEnd(boolean... forCover) {
        return getX() + xRadius;
    }

    @Override
    public int getYSpriteEnd(boolean... forCover) {
        return getY() + yRadius;
    }
}
