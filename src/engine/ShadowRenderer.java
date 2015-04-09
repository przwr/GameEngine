/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import collision.Block;
import collision.Figure;
import static collision.OpticProperties.INITIAL_SHADOWS_COUNT;
import static collision.OpticProperties.TRANSPARENT;
import collision.RoundRectangle;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import game.gameobject.GameObject;
import game.place.ForegroundTile;
import game.place.Light;
import game.place.Map;
import game.place.Place;
import game.place.Shadow;
import static game.place.Shadow.*;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class ShadowRenderer {

    private static final int displayWidth = Display.getWidth(), displayHeight = Display.getHeight(), shadowLength = 32768;
    private static final shadeRenderer[] singleShadeRenderers = new shadeRenderer[6];
    private static final ArrayList<Figure> shades = new ArrayList<>(512);
    private static final Point center = new Point(), corner = new Point();
    private static final Point[] shadowPoints = new Point[4];
    private static final Polygon polygon = new Polygon();
    private static boolean checked;
    private static int firstShadowPoint, secondShadowPoint, shX, shY, XL1, XL2, XR1, XR2, lightHeightHalf, lightWidthHalf, shadowsDarkenCount = 0, shadowsBrightenCount = 0;
    private static double angle, temp, al, bl, ar, br, as, bs, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Figure tempShade;
    private static Shadow tempShadow;
    private static Shadow[] shadowsDarken = new Shadow[INITIAL_SHADOWS_COUNT], shadowsBrighten = new Shadow[INITIAL_SHADOWS_COUNT];

    private static Point tempPoint;

    public static final boolean DEBUG = false;

    static {
        shadowPoints[0] = new Point();
        shadowPoints[1] = new Point();
        shadowPoints[2] = new Point();
        shadowPoints[3] = new Point();
        for (int i = 0; i < INITIAL_SHADOWS_COUNT; i++) {
            shadowsDarken[i] = new Shadow(0);
            shadowsBrighten[i] = new Shadow(0);
        }
        singleShadeRenderers[DARK] = (Light emitter, Figure shade, Point point) -> {
            shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade);
        };
        singleShadeRenderers[BRIGHT] = (Light emitter, Figure shade, Point point) -> {
            shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade);
        };
        singleShadeRenderers[BRIGHTEN] = (Light emitter, Figure shade, Point point) -> {
            if (!shade.isBottomRounded()) {
                drawShadeLit(emitter, shade, point);
            } else {
                shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, point.getX(), point.getY());
            }
        };
        singleShadeRenderers[DARKEN] = (Light emitter, Figure shade, Point point) -> {
            if (!shade.isBottomRounded()) {
                drawShade(emitter, shade, point);
            } else {
                shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, point.getX(), point.getY());
            }
        };
        singleShadeRenderers[BRIGHTEN_OBJECT] = (Light emitter, Figure shade, Point point) -> {
            shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, point.getX(), point.getY());
        };
        singleShadeRenderers[DARKEN_OBJECT] = (Light emitter, Figure shade, Point point) -> {
            shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, point.getX(), point.getY());
        };
    }

    public static void clearScreen(float color) {
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_ONE, GL_ZERO);
        glColor3f(color, color, color);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, displayHeight);
        glVertex2f(displayWidth, displayHeight);
        glVertex2f(displayWidth, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void prerenderLight(Map map, Light light) {
        lightHeightHalf = light.getHeight() / 2;
        lightWidthHalf = light.getWidth() / 2;
        findShades(light, map);
        light.getFrameBufferObject().activate();
        clearScreen(1);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shades.stream().forEach((shaded) -> {
            if (shaded != light.getOwnerCollision()) {
                if (shaded.isGiveShadow()) {
                    calculateShadow(light, shaded);
                    if (shaded.isConcave()) {
                        drawShadowFromConcave(light, (RoundRectangle) shaded);
                    } else {
                        drawShadow(light);
                    }
                    calculateWalls(shaded, light);
                }
                calculateShadowShade(shaded, light);
                if (shaded instanceof RoundRectangle) {
                    calculateAndDrawSelfShadow(light, (RoundRectangle) shaded);
                }
            } else {
                shaded.addShadow(BRIGHT);
            }
        }
        );
        for (Figure shaded : shades) {
            solveShadows(shaded);
            for (int i = 0; i < shaded.getShadowCount(); i++) {
                singleShadeRenderers[shaded.getShadow(i).type].render(light, shaded, shaded.getShadow(i).point);
            }
            shaded.clearShadows();
        }
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        light.render(displayHeight - light.getHeight());
        light.getFrameBufferObject().deactivate();
    }

    private static void findShades(Light light, Map map) {
        shades.clear();
        for (Block block : map.getBlocks()) {
            tempShade = block.getCollision();
            if (tempShade != null && tempShade.getType() != TRANSPARENT) {
                if (tempShade.getY() - FastMath.abs(tempShade.getShadowHeight()) - Place.tileSize <= light.getY() + lightHeightHalf && tempShade.getYEnd() >= light.getY() - lightHeightHalf
                        && tempShade.getX() <= light.getX() + lightWidthHalf && tempShade.getXEnd() >= light.getX() - lightWidthHalf) {
                    tempShade.setLightDistance(FastMath.abs(tempShade.getXCentral() - light.getX()));
                    shades.add(tempShade);
                }
                for (Figure top : block.getTop()) {
                    if (top != null && !top.isLittable()
                            && top.getY() - 2 * FastMath.abs(tempShade.getShadowHeight()) - tempShade.getHeight() <= light.getY() + lightHeightHalf
                            && top.getX() <= light.getX() + lightWidthHalf && top.getXEnd() >= light.getX() - lightWidthHalf) {
                        top.setLightDistance(FastMath.abs(top.getXCentral() - light.getX()));
                        shades.add(top);
                    }
                }
            }
        }
        for (GameObject object : map.getForegroundTiles()) {
            if (!((ForegroundTile) object).isInBlock()) {
                tempShade = object.getCollision();
                if (tempShade != null && !tempShade.isLittable()
                        && tempShade.getY() <= light.getY() + lightHeightHalf && tempShade.getYEnd() >= light.getY() - lightHeightHalf
                        && tempShade.getX() <= light.getX() + lightWidthHalf && tempShade.getXEnd() >= light.getX() - lightWidthHalf) {
                    tempShade.setLightDistance(FastMath.abs(tempShade.getXCentral() - light.getX()));
                    shades.add(tempShade);
                }
            }
        }
        for (GameObject object : map.getDepthObjects()) {
            tempShade = object.getCollision();
            if (tempShade != null && tempShade.isLittable()
                    && tempShade.getY() - tempShade.getHeight() / 2 <= light.getY() + lightHeightHalf && tempShade.getY() + tempShade.getHeight() / 2 >= light.getY() - lightHeightHalf
                    && tempShade.getX() <= light.getX() + lightWidthHalf && tempShade.getXEnd() >= light.getX() - lightWidthHalf) {
                shades.add(tempShade);
            }
        }
        Collections.sort(shades);
    }

    private static void calculateShadowShade(Figure shaded, Light light) {
        if (shaded.isLittable()) {
            if (shaded instanceof RoundRectangle) {
                calculateRoundShade((RoundRectangle) shaded, light);
            } else {
                calculateRegularShade(shaded, light);
            }
        } else {
            shaded.addShadow(DARK);
        }
    }

    private static void calculateRoundShade(RoundRectangle shaded, Light light) {
        if (isRoundInLightSetColor(shaded, light)) {
            shaded.getOwner().renderShadowLit((light.getXCenterShift()) - (light.getX()),
                    (light.getYCenterShift()) - (light.getY()) + displayHeight - light.getHeight(), shaded);
            shaded.addShadow(BRIGHT);
        } else {
            shaded.addShadow(DARK);
        }
    }

    private static boolean isRoundInLightSetColor(RoundRectangle shaded, Light emitter) {
        if (shaded.isBottomRounded()) {
            if (shaded.isTriangular()) {
                if (shaded.isLeftBottomRound()) {
                    double angle = 45 - Methods.pointAngle(emitter.getX(), emitter.getY(), shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
                    if (angle > 0.5 && angle < 179.5) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    double angle = -135 + Methods.pointAngle360(emitter.getX(), emitter.getY(), shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
                    if (angle > 0.5 && angle < 179.5) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                if (shaded.isLeftBottomRound()) {
                    double angle = 45 - Methods.pointAngle(emitter.getX(), emitter.getY(), shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
                    if (angle > 0 && angle <= 222) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    double angle = -135 + Methods.pointAngle360(emitter.getX(), emitter.getY(), shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
                    if (angle > 0 && angle <= 222) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            if (emitter.getY() >= shaded.getYEnd()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static void calculateRegularShade(Figure shaded, Light emitter) {
        if (emitter.getY() > shaded.getYEnd()) {
            shaded.getOwner().renderShadowLit(emitter.getXCenterShift() - emitter.getX(),
                    (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shaded);
            shaded.addShadow(BRIGHT);
        } else {
            shaded.addShadow(DARK);
        }
    }

    private static void solveShadows(Figure shaded) {
        tempShadow = null;
        shadowsDarkenCount = 0;
        shadowsBrightenCount = 0;
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            switch (shaded.getShadow(i).type) {
                case DARK:
                    shaded.clearShadows();
                    shaded.addShadow(DARK);
                    return;
                case BRIGHT:
                    tempShadow = shaded.getShadow(i);
                    break;
                case DARKEN:
                    if (shadowsDarkenCount == shadowsDarken.length) {
                        resizeShadowsDarken();
                    }
                    shadowsDarken[shadowsDarkenCount++].setDarken(shaded.getShadow(i).point.getX(), shaded.getShadow(i).point.getY());
                    break;
                case DARKEN_OBJECT:
                    if (shadowsDarkenCount == shadowsDarken.length) {
                        resizeShadowsDarken();
                    }
                    shadowsDarken[shadowsDarkenCount++].setDarkenObject(shaded.getShadow(i).point.getX(), shaded.getShadow(i).point.getY());
                    break;
                case BRIGHTEN:
                    if (shadowsBrightenCount == shadowsBrighten.length) {
                        resizeShadowsBrighten();
                    }
                    shadowsBrighten[shadowsBrightenCount++].setBrighten(shaded.getShadow(i).point.getX(), shaded.getShadow(i).point.getY(), shaded.getShadow(i).caster);
                    break;
                case BRIGHTEN_OBJECT:
                    if (shadowsBrightenCount == shadowsBrighten.length) {
                        resizeShadowsBrighten();
                    }
                    shadowsBrighten[shadowsBrightenCount++].setBrightenObject(shaded.getShadow(i).point.getX(), shaded.getShadow(i).point.getY());
                    break;
            }
        }
        shaded.clearShadows();
        if (tempShadow != null && shadowsBrightenCount == 0) {
            shaded.addShadow(BRIGHT);
        }
        if (shaded instanceof RoundRectangle || (!shaded.isGiveShadow() && shaded.isLittable())) {
            if (shadowsBrightenCount != 0) {
                int minValue = Integer.MAX_VALUE, maxValue = -1;
                Shadow minShadow = null, maxShadow = null;
                if (shadowsBrightenCount > 1) {
                    for (int i = 0; i < shadowsBrightenCount; i++) {
                        tempShadow = shadowsBrighten[i];
                        if (tempShadow.point.getX() < tempShadow.point.getY()) {
                            if (tempShadow.point.getX() > maxValue) {
                                maxValue = tempShadow.point.getX();
                                maxShadow = tempShadow;
                            }
                        } else if (tempShadow.point.getX() > tempShadow.point.getY()) {
                            if (minValue > tempShadow.point.getX()) {
                                minValue = tempShadow.point.getX();
                                minShadow = tempShadow;
                            }
                        } else if (tempShadow.point.getX() == Place.tileSize) {
                            maxValue = Integer.MAX_VALUE;
                            maxShadow = null;
                        } else {
                            minValue = -1;
                            minShadow = null;
                        }
                    }
                    if (minShadow != null) {
                        shaded.addShadow(minShadow.type, minShadow.point.getX(), minShadow.point.getY());
                    }
                    if (maxShadow != null) {
                        shaded.addShadow(maxShadow.type, maxShadow.point.getX(), maxShadow.point.getY());
                    }
                } else {
                    shaded.addShadow(shadowsBrighten[0].type, shadowsBrighten[0].point.getX(), shadowsBrighten[0].point.getY());
                }
            }
            for (int i = 0; i < shadowsDarkenCount; i++) {
                shaded.addShadow(shadowsDarken[i].type, shadowsDarken[i].point.getX(), shadowsDarken[i].point.getY());
            }
        } else {
            for (int i = 0; i < shadowsBrightenCount; i++) {
                shaded.addShadow(shadowsBrighten[i].type, shadowsBrighten[i].point.getX(), shadowsBrighten[i].point.getY());
            }
            for (int i = 0; i < shadowsDarkenCount; i++) {
                shaded.addShadow(shadowsDarken[i].type, shadowsDarken[i].point.getX(), shadowsDarken[i].point.getY());
            }
        }
    }

    private static void resizeShadowsDarken() {
        Shadow[] tempShadows = new Shadow[2 * shadowsDarken.length];
        System.arraycopy(shadowsDarken, 0, tempShadows, 0, shadowsDarken.length);
        shadowsDarken = tempShadows;
        for (int i = shadowsDarkenCount; i < shadowsDarken.length; i++) {
            shadowsDarken[i] = new Shadow(0);
        }
    }

    private static void resizeShadowsBrighten() {
        Shadow[] tempShadows = new Shadow[2 * shadowsBrighten.length];
        System.arraycopy(shadowsBrighten, 0, tempShadows, 0, shadowsBrighten.length);
        shadowsBrighten = tempShadows;
        for (int i = shadowsBrightenCount; i < shadowsBrighten.length; i++) {
            shadowsBrighten[i] = new Shadow(0);
        }
    }

    private static void calculateShadow(Light source, Figure thisShade) {
        findPoints(source, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
        setPolygonShape();
    }

    private static void setPolygonShape() {
        polygon.reset();
        polygon.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
        polygon.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
        polygon.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
        polygon.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
    }

    private static void findPoints(Light source, Figure thisShade) {
        center.set(source.getX(), source.getY());
        angle = 0;
        for (int i = 0; i < thisShade.getPoints().size(); i++) {
            for (int j = i + 1; j < thisShade.getPoints().size(); j++) {
                temp = Methods.threePointAngle(thisShade.getPoint(i).getX(), thisShade.getPoint(i).getY(), thisShade.getPoint(j).getX(), thisShade.getPoint(j).getY(), center.getX(), center.getY());
                if (temp > angle) {
                    angle = temp;
                    firstShadowPoint = i;
                    secondShadowPoint = j;
                }
            }
        }
        shadowPoints[0] = thisShade.getPoint(firstShadowPoint);
        shadowPoints[1] = thisShade.getPoint(secondShadowPoint);
        switchSidesIfNeeded(thisShade);
    }

    private static void switchSidesIfNeeded(Figure shade) {
        if (center.getY() > shade.getY()) {
            if (shadowPoints[0].getY() < center.getY() && shadowPoints[1].getY() < center.getY()) {
                checkBothUp();
            } else if (shadowPoints[0].getY() < center.getY()) {
                checkOneDown();
            }
        }
    }

    private static void checkBothUp() {
        if (shadowPoints[0].getX() == shadowPoints[1].getX()) {
            if (center.getX() > shadowPoints[0].getX() && center.getX() > shadowPoints[1].getX()) {
                if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                    switchSides();
                }
            } else if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
                switchSides();
            }
        } else if (center.getX() > shadowPoints[0].getX() && center.getX() > shadowPoints[1].getX()) {
            if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                switchSides();
            }
        } else if (center.getX() < shadowPoints[0].getX() && center.getX() < shadowPoints[1].getX()) {
            if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
                switchSides();
            }
        } else if (shadowPoints[0].getX() > shadowPoints[1].getX()) {
            switchSides();
        }
    }

    private static void checkOneDown() {
        if (center.getX() > shadowPoints[0].getX()) {
            if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                switchSides();
            }
        } else if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
            switchSides();
        }
    }

    private static void switchSides() {
        tempPoint = shadowPoints[0];
        shadowPoints[0] = shadowPoints[1];
        shadowPoints[1] = tempPoint;
    }

    private static void findLeftSideOfShadow() {
        if (shadowPoints[0].getX() == center.getX()) {
            shadowPoints[2].set(shadowPoints[0].getX(), shadowPoints[0].getY() + (shadowPoints[0].getY() > center.getY() ? shadowLength : -shadowLength));
        } else if (shadowPoints[0].getY() == center.getY()) {
            shadowPoints[2].set(shadowPoints[0].getX() + (shadowPoints[0].getX() > center.getX() ? shadowLength : -shadowLength), shadowPoints[0].getY());
        } else {
            al = ((double) (center.getY() - shadowPoints[0].getY())) / (double) (center.getX() - shadowPoints[0].getX());
            bl = shadowPoints[0].getY() - al * shadowPoints[0].getX();
            if (al > 0) {
                shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? shadowLength : -shadowLength);
                shY = (int) (al * shX + bl);
            } else if (al < 0) {
                shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? -shadowLength : shadowLength);
                shY = (int) (al * shX + bl);
            } else {
                shX = shadowPoints[0].getX();
                shY = shadowPoints[0].getY() + (shadowPoints[0].getY() > center.getY() ? shadowLength : -shadowLength);
            }
            shadowPoints[2].set(shX, shY);
        }
    }

    private static void findRightSideOfShadow() {
        if (shadowPoints[1].getX() == center.getX()) {
            shadowPoints[3].set(shadowPoints[1].getX(), shadowPoints[1].getY() + (shadowPoints[1].getY() > center.getY() ? shadowLength : -shadowLength));
        } else if (shadowPoints[1].getY() == center.getY()) {
            shadowPoints[3].set(shadowPoints[1].getX() + (shadowPoints[1].getX() > center.getX() ? shadowLength : -shadowLength), shadowPoints[1].getY());
        } else {
            ar = ((double) (center.getY() - shadowPoints[1].getY())) / (double) (center.getX() - shadowPoints[1].getX());
            br = shadowPoints[1].getY() - ar * shadowPoints[1].getX();
            if (ar > 0) {
                shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? shadowLength : -shadowLength);
                shY = (int) (ar * shX + br);
            } else if (ar < 0) {
                shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? -shadowLength : shadowLength);
                shY = (int) (ar * shX + br);
            } else {
                shX = shadowPoints[1].getX();
                shY = shadowPoints[1].getY() + (shadowPoints[1].getY() > center.getY() ? shadowLength : -shadowLength);
            }
            shadowPoints[3].set(shX, shY);
        }
    }

    private static void calculateAndDrawSelfShadow(Light emitter, RoundRectangle shaded) {
        // Tylko jeśli nie jest całe zaciemnione przez inny obiekt?
        if (!shaded.isTriangular()) {
            if (shaded.isConcave()) {
                if (shaded.isLeftBottomRound()) {
                    if ((emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() > shaded.getYEnd())) {
                        as = (emitter.getY() - shaded.getYEnd()) / (double) (emitter.getX() - shaded.getX() - Place.tileSize);
                        bs = shaded.getYEnd() - as * (shaded.getX() + Place.tileSize);
                        int yc = shaded.getYEnd() - Place.tileSize;
                        int xc = Methods.roundDouble((yc - bs) / as);
                        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
                            drawLeftConcaveBottom(emitter, shaded, xc, yc);
                        }
                        as *= as;
                        xc = Methods.roundDouble((Place.tileSize * (as - 1)) / (1 + as));
                        if (xc <= 0) {
                            shaded.addShadow(DARK);
                        } else {
                            shaded.addShadow(DARKEN, xc, Place.tileSize);
                        }
                        if (DEBUG) {
                            System.out.println("SelftShadow Left Bottom");
                        }
                    } else if ((emitter.getX() < shaded.getX() && emitter.getY() < shaded.getYEnd() - Place.tileSize)) {
                        as = (emitter.getY() - shaded.getYEnd() + Place.tileSize) / (double) (emitter.getX() - shaded.getX());
                        bs = shaded.getYEnd() - Place.tileSize - as * shaded.getX();
                        int xc = shaded.getX() + Place.tileSize;
                        int yc = Methods.roundDouble(as * xc + bs);
                        if (yc >= shaded.getYEnd() - Place.tileSize && xc <= shaded.getYEnd()) {
                            drawLeftConcaveTop(emitter, shaded, xc, yc);
                        }
                        yc = shaded.getYEnd() - yc;
                        xc = Methods.roundDouble(FastMath.sqrt(Place.tileSquared - yc * yc));
                        if (xc >= Place.tileSize || yc < 0) {
                            shaded.addShadow(DARK);
                        } else {
                            shaded.addShadow(DARKEN, 0, xc);
                        }
                        if (DEBUG) {
                            System.out.println("SelftShadow Left Top");
                        }
                    } else if (emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() <= shaded.getYEnd()) {
                        shaded.addShadow(DARK);
                    }
                } else if (shaded.isRightBottomRound()) {
                    if ((emitter.getX() < shaded.getX() && emitter.getY() > shaded.getYEnd())) {
                        as = (emitter.getY() - shaded.getYEnd()) / (double) (emitter.getX() - shaded.getX());
                        bs = shaded.getYEnd() - as * shaded.getX();
                        int yc = shaded.getYEnd() - Place.tileSize;
                        int xc = Methods.roundDouble((yc - bs) / as);
                        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
                            drawRightConcaveBottom(emitter, shaded, xc, yc);
                        }
                        xc = Methods.roundDouble((2 * Place.tileSize) / (1 + as * as));
                        if (xc >= Place.tileSize) {
                            shaded.addShadow(DARK);
                        } else {
                            shaded.addShadow(DARKEN, 0, xc);
                        }
                        if (DEBUG) {
                            System.out.println("SelftShadow Right Bottom");
                        }
                    } else if (emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() < shaded.getYEnd() - Place.tileSize) {
                        as = (emitter.getY() - shaded.getYEnd() + Place.tileSize) / (double) (emitter.getX() - shaded.getX() - Place.tileSize);
                        bs = shaded.getYEnd() - Place.tileSize - as * (shaded.getX() + Place.tileSize);
                        int xc = shaded.getX();
                        int yc = Methods.roundDouble(as * xc + bs);
                        if (yc >= shaded.getYEnd() - Place.tileSize) {
                            drawRightConcaveTop(emitter, shaded, xc, yc);
                        }
                        yc = shaded.getYEnd() - yc;
                        xc = Methods.roundDouble(Place.tileSize - FastMath.sqrt(Place.tileSquared - yc * yc));
                        if (xc <= 0 || yc < 0) {
                            shaded.addShadow(DARK);
                        } else {
                            shaded.addShadow(DARKEN, xc, Place.tileSize);
                        }
                        if (DEBUG) {
                            System.out.println("SelftShadow Right Top");
                        }
                    } else if (emitter.getX() < shaded.getX() && emitter.getY() <= shaded.getYEnd()) {
                        shaded.addShadow(DARK);
                    }
                }
            } else {
                int range = 50;
                if (shaded.isLeftBottomRound()) {
                    double angle = 45 - Methods.pointAngle(emitter.getX(), emitter.getY(), shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
                    if (angle > 0) {
                        if (angle < range) {
                            int shift = Methods.roundDouble((angle / range) * Place.tileSize);
                            shaded.addShadow(DARKEN, shift, Place.tileSize, null
                            );
                        } else {
                            range = 68;
                            if (angle > 222 - range && angle <= 222) {
                                int shift = Methods.roundDouble(((angle - 222 + range) / (range)) * Place.tileSize);
                                shaded.addShadow(DARKEN, 0, shift, null
                                );
                            }
                        }
                    }
                } else if (shaded.isRightBottomRound()) {
                    double angle = -135 + Methods.pointAngle360(emitter.getX(), emitter.getY(), shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(), shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
                    if (angle > 0) {
                        if (angle < range) {
                            int shift = Methods.roundDouble(((range - angle) / range) * Place.tileSize);
                            shaded.addShadow(DARKEN, 0, shift, null
                            );
                        } else {
                            range = 68;
                            if (angle > (222 - range) && angle <= 222) {
                                int shift = Methods.roundDouble(((222 - angle) / (range)) * Place.tileSize);
                                shaded.addShadow(DARKEN, shift, Place.tileSize, null
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    private static void drawLeftConcaveBottom(Light emitter, RoundRectangle shaded, int x, int y) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawLeftConcaveTop(Light emitter, RoundRectangle shaded, int x, int y) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawRightConcaveBottom(Light emitter, RoundRectangle shaded, int x, int y) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawRightConcaveTop(Light emitter, RoundRectangle shaded, int x, int y) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        glVertex2f(x, y);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void calculateWalls(Figure current, Light source) {
        for (Figure other : shades) {
            checked = false;
            if (other != current && other != source.getOwnerCollision()) {
                if (other.isGiveShadow()) {
                    if (other instanceof RoundRectangle && other.isBottomRounded()) {
                        if (other.getYEnd() < source.getY()) {
                            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                                XOL = shadowPoints[0].getX();
                            } else {
                                XOL = ((other.getYEnd() - bl) / al);
                                YOL = (al * other.getX() + bl);
                                YOL2 = (al * other.getXEnd() + bl);
                            }
                            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                                XOR = shadowPoints[1].getX();
                            } else {
                                XOR = ((other.getYEnd() - br) / ar);
                                YOR = (ar * other.getX() + br);
                                YOR2 = (ar * other.getXEnd() + br);
                            }
                            if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()))) {
                                calculateLeftRoundWall((RoundRectangle) other, current, source);
                            }
                            if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()))) {
                                calculateRightRoundWall((RoundRectangle) other, current, source);
                            }
                        } else {
                            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                                XOL = shadowPoints[0].getX();
                            } else {
                                XOL = ((other.getYEnd() - bl) / al);
                                YOL = (al * other.getX() + bl);
                                YOL2 = (al * other.getXEnd() + bl);
                            }
                            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                                XOR = shadowPoints[1].getX();
                            } else {
                                XOR = ((other.getYEnd() - br) / ar);
                                YOR = (ar * other.getX() + br);
                                YOR2 = (ar * other.getXEnd() + br);
                            }
//                            System.out.println("XOL " + XOL + " YOL " + YOL + " YOL2 " + YOL2);
                            if ((shadowPoints[0].getY() < shadowPoints[2].getY() && shadowPoints[0].getY() < other.getYEnd()) && ((shadowPoints[0].getX() != shadowPoints[2].getX()
                                    && ((XOL >= other.getX() && XOL <= other.getXEnd()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))))) {
                                calculateLeftRoundWallFromTop((RoundRectangle) other, current, source);
                            }
                            if ((shadowPoints[1].getY() < shadowPoints[3].getY() && shadowPoints[1].getY() < other.getYEnd()) && ((shadowPoints[1].getX() != shadowPoints[3].getX()
                                    && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))))) {
//                                System.out.println("XOR " + XOR + " YOR " + YOR + " YOR2 " + YOR2);
                                calculateRightRoundWallFromTop((RoundRectangle) other, current, source);
                            }
                        }
                        findRoundDarkness((RoundRectangle) other, current);
                    } else if (current.getY() < source.getY() && current.getYEnd() != other.getYEnd()) {
                        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                            XOL = shadowPoints[0].getX();
                        } else {
                            XOL = ((other.getYEnd() - bl) / al);
                            YOL = (al * other.getX() + bl);
                            YOL2 = (al * other.getXEnd() + bl);
                        }
                        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                            XOR = shadowPoints[1].getX();
                        } else {
                            XOR = ((other.getYEnd() - br) / ar);
                            YOR = (ar * other.getX() + br);
                            YOR2 = (ar * other.getXEnd() + br);
                        }
                        if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()))) {
                            calculateLeftWall(other, current, source);
                        }
                        if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()))) {
                            calculateRightWall(other, current, source);
                        }
                        findDarkness(other, current, source);
                    }
                } else if (other.isLittable()) {
                    if (current.getY() < source.getY()) {
                        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                            XOL = shadowPoints[0].getX();
                        } else {
                            XOL = ((other.getYOwnerEnd() - bl) / al);
                            YOL = (al * other.getXOwnerBegin() + bl);
                            YOL2 = (al * other.getXOwnerEnd() + bl);
                        }
                        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                            XOR = shadowPoints[1].getX();
                        } else {
                            XOR = ((other.getYOwnerEnd() - br) / ar);
                            YOR = (ar * other.getXOwnerBegin() + br);
                            YOR2 = (ar * other.getXOwnerEnd() + br);
                        }
                        if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getXOwnerBegin() && XOL <= other.getXOwnerEnd()) || (YOL > other.getYOwnerBegin() && YOL < other.getYOwnerEnd()) || (YOL2 > other.getYOwnerBegin() && YOL2 < other.getYOwnerEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getXOwnerBegin() && XOL <= other.getXOwnerEnd()))) {
                            calculateLeftObject(other, current, source);
                        }
                        if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()) || (YOR > other.getYOwnerBegin() && YOR < other.getYOwnerEnd()) || (YOR2 > other.getYOwnerBegin() && YOR2 < other.getYOwnerEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()))) {
                            calculateRightObject(other, current, source);
                        }
                    }
                    findObjectDarkness(other, current, source);
                }
            }
        }
    }

    private static void calculateLeftWall(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadowPoints[0].getY() > other.getYEnd() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getXEnd();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
                XL2 = XL1 <= source.getX() ? other.getX() : other.getXEnd();
            }
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if ((XL1 < source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX() && source.getY() < current.getYEnd() && shadowPoints[0].getX() < shadowPoints[2].getX()) || (XL1 == other.getXEnd() && XL1 == current.getX()))) { //dodaj światło
                    other.addShadow(BRIGHTEN, XL1, XL2);
                    XL2 = source.getX() < XL1 ? other.getX() : other.getXEnd();
                    if (shadowPoints[0].getX() != shadowPoints[2].getX()) {
                        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                            XR1 = shadowPoints[1].getX();
                        } else {
                            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
                        }
                        if (XR1 < XL2 && XR1 > other.getX() && shadowPoints[3].getY() < current.getYEnd()) {
                            XL2 = XR1;
                        }
                        other.addShadow(DARKEN, XL1, XL2);
                    }
                    if (DEBUG) {
                        System.out.println("Left Light");
                    }
                } else { //dodaj cień
                    if (current.getX() == XL1) {
                        XL2 = other.getXEnd();
                    }
                    if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                        XR1 = shadowPoints[1].getX();
                    } else {
                        XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
                    }
                    if (XR1 < XL2 && XR1 > other.getX() && shadowPoints[3].getY() < current.getYEnd()) {
                        XL2 = XR1;
                    }
                    other.addShadow(DARKEN, XL1, XL2);
                    if (DEBUG) {
                        System.out.println("Left Shade " + al + " XL1 " + XL1 + " figure.X " + current.getX());
                    }
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
                YOL = al * other.getX() + bl;
                YOL2 = al * other.getXEnd() + bl;
                if (((source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd()) || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd()))) {
                    if (XL1 < current.getX()) {
                        other.addShadow(BRIGHT);
                        if (DEBUG) {
                            System.out.println("Left Lightness - first");
                        }
                    } else {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Left Darkness - first");
                        }
                    }
                }
            }
        }
    }

    private static void calculateRightWall(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadowPoints[1].getY() > other.getYEnd() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
                XR2 = XR1 < source.getX() ? other.getX() : other.getXEnd();
            }
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if ((XR1 > source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX() && source.getY() < current.getYEnd() && shadowPoints[1].getX() > shadowPoints[3].getX()) || (XR1 == other.getX() && XR1 == current.getXEnd()))) { // dodaj światło
                    other.addShadow(BRIGHTEN, XR1, XR2);
                    XR2 = XR1 >= source.getX() ? other.getX() : other.getXEnd();
                    if (shadowPoints[1].getX() != shadowPoints[3].getX()) {
                        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                            XL1 = shadowPoints[0].getX();
                        } else {
                            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
                        }
                        if (XL1 > XR2 && XL1 < other.getXEnd() && shadowPoints[2].getY() < current.getYEnd()) {
                            XR2 = XL1;
                        }
                        other.addShadow(DARKEN, XR1, XR2);
                    }
                    if (DEBUG) {
                        System.out.println("Right Light XR1 " + XR1);
                    }
                } else { //dodaj cień
                    if (XR1 == current.getXEnd()) {
                        XR2 = other.getX();
                    }
                    if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                        XL1 = shadowPoints[0].getX();
                    } else {
                        XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
                    }
                    if (XL1 > XR2 && XL1 < other.getXEnd() && shadowPoints[2].getY() < current.getYEnd()) {
                        XR2 = XL1;
                    }
                    other.addShadow(DARKEN, XR1, XR2);
                    if (DEBUG) {
                        System.out.println("Right Shade " + ar + " XR1 " + XR1 + " figure.X " + current.getX());
                    }
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
                YOR = ar * other.getX() + br;
                YOR2 = ar * other.getXEnd() + br;
                if (((source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd()) || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd()))) {
                    if (XR1 > current.getXEnd()) { // check this shitty condition
                        other.addShadow(BRIGHT);
                        if (DEBUG) {
                            System.out.println("Right Lightness - first");
                        }
                    } else {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Right Darkness - first");
                        }
                    }
                }
            }
        }
    }

    private static void calculateLeftRoundWall(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd()) || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))))) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getXEnd();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii      
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XL1 > other.getX() && XL1 < other.getXEnd())) {
                    Point cross = getXIntersetction(al, bl, shadowPoints[0].getX(), shadowPoints[0].getY(), shadowPoints[2].getX(), shadowPoints[2].getY(), other);
                    if (cross != null) {
                        if (other.getYEnd() <= current.getYEnd() && cross.getY() >= other.getYEnd() - Place.tileSize && cross.getY() <= other.getYEnd()) {
                            XL1 = cross.getX();
                        } else {
                            findRoundDarkness(other, current);
                            return;
                        }
                    } else {
                        findRoundDarkness(other, current);
                        return;
                    }
                }
                XL2 = al > 0 ? other.getX() : other.getXEnd();
            }
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if (XL1 < source.getX() || (XL1 == other.getXEnd() && XL1 == current.getX()) || (XL1 == other.getX() && XL1 == current.getXEnd())) { //dodaj światło
                    if (other.getYEnd() == current.getYEnd()) {
                        tempShadow = null;
                        for (int i = 0; i < other.getShadowCount(); i++) {
                            if (other.getShadow(i).type == BRIGHTEN && other.getShadow(i).caster.getYEnd() == other.getYEnd()) {
                                if (FastMath.abs(current.getX() - other.getX()) > FastMath.abs(other.getShadow(i).caster.getX() - other.getX())) {
                                    return;
                                } else {
                                    tempShadow = other.getShadow(i);
                                }
                            }
                        }
                        if (tempShadow != null) {
                            other.removeShadow(tempShadow);
                        }
                    }
                    other.addShadow(BRIGHTEN, XL1 - other.getX(), XL2 - other.getX(), current);
                    if (XL1 != other.getXEnd()) {
                        XL2 = al <= 0 ? other.getX() : other.getXEnd();
                        other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX(), current);
                    }
                    checked = true;
                    if (DEBUG) {
                        System.out.println("Left Round Light XL1 " + (XL1 - other.getX()) + " XL2 " + (XL2 - other.getX()));
                    }
                } else if (shadowPoints[0].getY() > other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX()) || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX());
                    checked = true;
                    if (DEBUG) {
                        System.out.println("Left Round Shade XL1 " + (XL1 - other.getX()) + " XL2 " + (XL2 - other.getX()));
                    }
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
                YOL = al * other.getX() + bl;
                YOL2 = al * other.getXEnd() + bl;
                if (((source.getX() != current.getXEnd() && source.getX() != current.getX() && other.getYEnd() != current.getYEnd()) || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd()) || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd()))) {
                    if (XL1 < current.getX()) {
                        other.addShadow(BRIGHT);
                        if (DEBUG) {
                            System.out.println("Left Round Lightness - second");
                        }
                    } else if ((source.getX() >= current.getX() && current.getX() >= other.getXEnd()) || (source.getX() <= current.getXEnd() && current.getXEnd() <= other.getX())) {
                        other.addShadow(DARK);
                        checked = true;
                        if (DEBUG) {
                            System.out.println("Left Round Darkness - second");
                        }
                    }
                }
            }
        }
    }

    private static void calculateRightRoundWall(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd()) || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))))) {
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XR1 > other.getX() && XR1 < other.getXEnd())) {
                    Point cross = getXIntersetction(ar, br, shadowPoints[1].getX(), shadowPoints[1].getY(), shadowPoints[3].getX(), shadowPoints[3].getY(), other);
                    if (cross != null) {
                        if (other.getYEnd() <= current.getYEnd() && cross.getY() >= other.getYEnd() - Place.tileSize && cross.getY() <= other.getYEnd()) {
                            XR1 = cross.getX();
                        } else {
                            findRoundDarkness(other, current);
                            return;
                        }
                    } else {
                        findRoundDarkness(other, current);
                        return;
                    }
                }
                XR2 = ar > 0 ? other.getX() : other.getXEnd();
            }
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if (XR1 > source.getX() || (XR1 == other.getX() && XR1 == current.getXEnd()) || (XR1 == other.getXEnd() && XR1 == current.getX())) { // dodaj światło
                    if (other.getYEnd() == current.getYEnd()) {
                        tempShadow = null;
                        for (int i = 0; i < other.getShadowCount(); i++) {
                            if (other.getShadow(i).type == BRIGHTEN && other.getShadow(i).caster.getYEnd() == other.getYEnd()) {
                                if (FastMath.abs(current.getX() - other.getX()) > FastMath.abs(other.getShadow(i).caster.getX() - other.getX())) {
                                    return;
                                } else {
                                    tempShadow = other.getShadow(i);
                                }
                            }
                        }
                        if (tempShadow != null) {
                            other.removeShadow(tempShadow);
                        }
                    }
                    other.addShadow(BRIGHTEN, XR1 - other.getX(), XR2 - other.getX(), current);
                    if (XR1 != other.getX()) {
                        XR2 = ar <= 0 ? other.getX() : other.getXEnd();
                        other.addShadow(DARKEN, XR1 - other.getX(), XR2 - other.getX(), current);
                    }
                    checked = true;
                    if (DEBUG) {
                        System.out.println("Right Round Light XR1 " + (XR1) + " XR2 " + (XR2));
                    }
                } else if (shadowPoints[1].getY() > other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX()) || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XR1 - other.getX(), XR2 - other.getX());
                    checked = true;
                    if (DEBUG) {
                        System.out.println("Right Round Shade XR1 " + (XR1 - other.getX()) + " XR2 " + (XR2 - other.getX()));
                    }
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
                YOR = ar * other.getX() + br;
                YOR2 = ar * other.getXEnd() + br;
                if (((source.getX() != current.getXEnd() && source.getX() != current.getX() && other.getYEnd() != current.getYEnd()) || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd()) || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd()))) {
                    if (XR1 > current.getXEnd()) {
                        other.addShadow(BRIGHT);
                        if (DEBUG) {
                            System.out.println("Right Round Lightness - first");
                        }
                    } else if ((source.getX() >= current.getX() && current.getX() >= other.getXEnd()) || (source.getX() <= current.getXEnd() && current.getXEnd() <= other.getX())) {
                        other.addShadow(DARK);
                        checked = true;
                        if (DEBUG) {
                            System.out.println("Right Round Darkness - first");
                        }
                    }
                }
            }
        }
    }

    private static void calculateLeftRoundWallFromTop(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XL1 > other.getX() && XL1 < other.getXEnd())) {
                Point cross = getXIntersetctionFromTop(al, bl, shadowPoints[0].getX(), shadowPoints[0].getY(), shadowPoints[2].getX(), shadowPoints[2].getY(), other, current);
                if (cross != null && cross.getY() >= other.getYEnd() - Place.tileSize && cross.getY() <= other.getYEnd()) {
                    XL1 = cross.getX();
                } else {
                    if ((XL1 < other.getX() || XL1 > other.getXEnd()) && shadowPoints[3].getY() > current.getYEnd() && shadowPoints[2].getY() > current.getYEnd() && other.getYEnd() >= current.getYEnd()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadowPoints[1].getX() < current.getX() && YOL2 >= other.getYEnd() - Place.tileSize && YOL2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadowPoints[3].getX() > current.getYEnd() && YOL >= other.getYEnd() - Place.tileSize && YOL <= other.getYEnd()))) {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Left Round Top Darkness");
                        }
                    } else {
                        findRoundDarknessFromTop((RoundRectangle) other, current, source);
                    }
                    return;
                }
            }
            if (XL1 > other.getX() && XL1 < other.getXEnd()) {
                if (((shadowPoints[2].getY() > current.getYEnd() && shadowPoints[3].getY() > current.getYEnd()))) { // dodaj światło
                    other.addShadow(DARKEN, XL1 - other.getX(), XL1 < current.getX() ? 0 : Place.tileSize);
                    if (DEBUG) {
                        System.out.println("Left Round Top Light XL1: " + (XL1 - other.getX()));
                    }
                } else if (shadowPoints[2].getY() > current.getYEnd()) { //dodaj cień
                    other.addShadow(DARKEN, XL1 - other.getX(), XL1 > current.getX() ? 0 : Place.tileSize);
                    if (DEBUG) {
                        System.out.println("Left Round Top Shade XL1: " + (XL1 - other.getX()));
                    }
                } else {
                    findRoundDarknessFromTop((RoundRectangle) other, current, source);
                }
            } else {
                findRoundDarknessFromTop((RoundRectangle) other, current, source);
            }
        }
    }

    private static void calculateRightRoundWallFromTop(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XR1 > other.getX() && XR1 < other.getXEnd())) {
                Point cross = getXIntersetctionFromTop(ar, br, shadowPoints[1].getX(), shadowPoints[1].getY(), shadowPoints[3].getX(), shadowPoints[3].getY(), other, current);
                if (cross != null && cross.getY() >= other.getYEnd() - Place.tileSize && cross.getY() <= other.getYEnd()) {
                    XR1 = cross.getX();
                } else {
                    if ((XR1 < other.getX() || XR1 > other.getXEnd()) && shadowPoints[3].getY() > current.getYEnd() && shadowPoints[2].getY() > current.getYEnd() && other.getYEnd() >= current.getYEnd() && current.getX() != other.getXEnd() && current.getXEnd() != other.getX()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadowPoints[3].getX() < current.getX() && YOR2 >= other.getYEnd() - Place.tileSize && YOR2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadowPoints[3].getX() > current.getYEnd() && YOR >= other.getYEnd() - Place.tileSize && YOR <= other.getYEnd()))) {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Right Round Top Darkness");
                        }
                    } else {
                        findRoundDarknessFromTop((RoundRectangle) other, current, source);
                    }
                    return;
                }
            }
            if (XR1 > other.getX() && XR1 < other.getXEnd()) {
                if (((shadowPoints[2].getY() > current.getYEnd() && shadowPoints[3].getY() > current.getYEnd()))) { // dodaj światło
                    other.addShadow(DARKEN, XR1 - other.getX(), XR1 > current.getXEnd() ? Place.tileSize : 0);
                    if (DEBUG) {
                        System.out.println("Right Round Top Light");
                    }
                } else if (shadowPoints[3].getY() > current.getYEnd()) { //dodaj cień
                    other.addShadow(DARKEN, XR1 - other.getX(), XR1 < current.getXEnd() ? Place.tileSize : 0);
                    if (DEBUG) {
                        System.out.println("Right Round Top Shade");
                    }
                } else {
                    findRoundDarknessFromTop((RoundRectangle) other, current, source);
                }
            } else {
                findRoundDarknessFromTop((RoundRectangle) other, current, source);
            }
        }
    }

    private static void calculateLeftObject(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getOwner().getStartX() + other.getWidth();
            } else {
                XL1 = Methods.roundDouble((other.getYOwnerEnd() - bl) / al);
                XL2 = current.getX() > XL1 ? other.getOwner().getStartX() : other.getOwner().getStartX() + other.getWidth();
            }
            if (XL1 >= other.getXOwnerBegin() && XL1 < other.getXOwnerEnd()) {
                if (XL1 < current.getX()) { //dodaj światło
                    other.addShadow(BRIGHTEN_OBJECT, XL1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XL2);
                    if (DEBUG) {
                        System.out.println("Left Light " + (XL1 - other.getXOwnerBegin() + other.getOwner().getStartX()) + " XL2 " + XL2);
                    }
                    checked = true;
                } else { //dodaj cień
                    if (shadowPoints[3].getX() > shadowPoints[2].getX() || shadowPoints[3].getY() < shadowPoints[2].getY()) {
                        XL2 = other.getOwner().getStartX() + other.getWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XL1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XL2);
                    if (DEBUG) {
                        System.out.println("Left Shade " + (XL1 - other.getXOwnerBegin() + other.getOwner().getStartX()) + " XL2 " + XL2);
                    }
                    checked = true;
                }
            }
//            else if (shadowPoints[0].getX() != shadowPoints[2].getX() && source.getY() > current.getY()) { // rysuj zaciemniony
//                YOL = Methods.roundDouble(al * other.getXOwnerBegin() + bl);
//                YOL2 = Methods.roundDouble(al * other.getXOwnerEnd() + bl);
//                XL2 = Methods.roundDouble((other.getYOwnerBegin() - bl) / al);
//                if ((XL1 != current.getX() && XL1 != other.getXOwnerBegin() && XL1 != other.getXOwnerEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) && ((YOL >= other.getYOwnerBegin() && YOL <= other.getYOwnerEnd() && other.getX() > current.getX() && source.getX() > current.getX()) || (YOL2 >= other.getYOwnerBegin() && YOL2 <= other.getYOwnerEnd() && other.getX() < current.getXEnd() && source.getX() < current.getXEnd()))) {
//                    if (XL2 > other.getXOwnerEnd() || XL2 < other.getXOwnerBegin()) {
//                        other.addShadow(DARK);
//                        System.out.println("Left Dark");
//                        checked = true;
//                    } else {
//                        //  other.addShadow(shadow1);
//                    }
//                }
//            }
        }
    }

    private static void calculateRightObject(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getOwner().getStartX();
            } else {
                XR1 = Methods.roundDouble((other.getYOwnerEnd() - br) / ar);
                XR2 = XR1 < current.getXEnd() ? other.getOwner().getStartX() : other.getOwner().getStartX() + other.getWidth();
            }
            if (XR1 >= other.getXOwnerBegin() && XR1 <= other.getXOwnerEnd()) {
                if (XR1 >= current.getXEnd()) { // dodaj światło
                    other.addShadow(BRIGHTEN_OBJECT, XR1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XR2);
                    if (DEBUG) {
                        System.out.println("Object Right Light XR1 " + (XR1 - other.getXOwnerBegin() + other.getOwner().getStartX()) + " XR2 " + XR2);
                    }
                    checked = true;
                } else { //dodaj cień
                    if (shadowPoints[3].getX() < shadowPoints[2].getX() || shadowPoints[3].getY() > shadowPoints[2].getY()) {
                        XR2 = other.getOwner().getStartX() + other.getWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XR1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XR2);
                    if (DEBUG) {
                        System.out.println("Object Right Shade " + (XR1 - other.getXOwnerBegin() + other.getOwner().getStartX()) + " XR2 " + XR2);
                    }
                    checked = true;
                }
            }
//            else if (shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony                
//                YOR = Methods.roundDouble(ar * other.getXOwnerBegin() + br);
//                YOR2 = Methods.roundDouble(ar * other.getXOwnerEnd() + br);
//                XR2 = Methods.roundDouble((other.getYOwnerBegin() - br) / ar);
//                if ((XR1 != current.getXEnd() && XR1 != other.getXOwnerBegin() && XR1 != other.getXOwnerEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) && ((YOR >= other.getYOwnerBegin() && YOR <= other.getYOwnerEnd() && other.getX() > current.getX() && source.getX() > current.getX()) || (YOR2 >= other.getYOwnerBegin() && YOR2 <= other.getYOwnerEnd() && other.getX() < current.getXEnd() && source.getX() < current.getXEnd()))) {
//                    if (XR2 > other.getXOwnerEnd() || XR2 < other.getXOwnerBegin()) {
//                        other.addShadow(DARK);
//                        System.out.println("Right Dark");
//                        checked = true;
//                    } else {
//                        //   other.addShadow(shadow1);
//                    }
//                }
//            }
        }
    }

    private static void findDarkness(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (current.getY() == other.getYEnd() && ((current.getX() == other.getXEnd() && source.getX() > current.getXEnd()) || (current.getXEnd() == other.getX() && source.getX() < current.getX())) && source.getY() < current.getYEnd()) {
                other.addShadow(DARK);
                if (DEBUG) {
                    System.out.println("Darkness Special Case...");
                }
            } else {
                if (polygon.contains(other.getX(), other.getYEnd() - Place.tileSize, other.getWidth(), Place.tileSize)) {
                    other.addShadow(DARK);
                    if (DEBUG) {
                        System.out.println("Darkness...");
                    }
                }
            }

        }
    }

    private static void findRoundDarkness(RoundRectangle other, Figure current) {
        // liczenie raz polygona
        if (!checked) {
            if ((current.isBottomRounded() && current.getYEnd() != other.getYEnd()) || (!current.isBottomRounded() && current.getYEnd() - Place.tileSize != other.getYEnd() && ((other.isLeftBottomRound() && other.getX() == current.getXEnd()) || (other.isRightBottomRound() && other.getXEnd()== current.getX()))) || (other.getX() != current.getXEnd() && other.getX() + Place.tileSize != current.getX())
                    || (current instanceof RoundRectangle && current.getYEnd() == other.getYEnd() && ((current.getX() < other.getX() && other.isLeftBottomRound()) || (current.getX() > other.getX() && other.isRightBottomRound())))) {
                if (other.isLeftBottomRound()) {
                    if (polygon.contains(other.getX() + 2, other.getYEnd() - Place.tileSize + 1, Place.tileSize - 2, 1) && polygon.contains(other.getX() + Place.tileSize - 1, other.getYEnd() - Place.tileSize + 1, 1, Place.tileSize - 2)) {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Round Left Darkness...");
                        }
                    }
                } else {
                    if (polygon.contains(other.getX(), other.getYEnd() - Place.tileSize + 1, Place.tileSize - 2, 1) && polygon.contains(other.getX() + 1, other.getYEnd() - Place.tileSize + 1, 1, Place.tileSize - 2)) {
                        other.addShadow(DARK);
                        if (DEBUG) {
                            System.out.println("Round Right Darkness...");
                        }
                    }
                }
            }
            checked = true;
        }
    }

    private static void findObjectDarkness(Figure other, Figure current, Light source) {
        if (!checked && other.getY() <= source.getY() && (other.getY() <= current.getY() || other.getYEnd() <= current.getYEnd())) {
            if (polygon.contains(other.getX(), other.getYOwnerEnd() - 1, 1, 1)) {
                other.addShadow(DARK);
                if (DEBUG) {
                    System.out.println("Object Darkness...");
                }
            }
        }
    }

    private static void findRoundDarknessFromTop(RoundRectangle other, Figure current, Light source) {
        if (current.getYEnd() >= source.getY() && (other.getY() >= current.getY() || other.getYEnd() >= current.getYEnd())) {
            if (other.isLeftBottomRound()) {
                if (polygon.contains(other.getX() + other.getPushValueOfCorner(LEFT_BOTTOM).getX() - 1, other.getYEnd() - other.getPushValueOfCorner(LEFT_BOTTOM).getY() - 1, 1, 1)) {
                    other.addShadow(DARK);
                    if (DEBUG) {
                        System.out.println("LeftRounded Top Darkness...");
                    }
                }
            } else {
                if (polygon.contains(other.getX() + Place.tileSize - other.getPushValueOfCorner(RIGHT_BOTTOM).getX() - 1, other.getYEnd() - other.getPushValueOfCorner(RIGHT_BOTTOM).getY() - 1, 1, 1)) {
                    other.addShadow(DARK);
                    if (DEBUG) {
                        System.out.println("RightRound Top Darkness...");
                    }
                }
            }
        }
    }

    private static void drawShadow(Light emitter) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());
        glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawShadowFromConcave(Light emitter, RoundRectangle shaded) {
        corner.set(shaded.getX() + (shaded.isLeftBottomRound() ? Place.tileSize : 0), shaded.getY());

        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);

        glBegin(GL_TRIANGLES);
        glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());

        glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());

        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());

        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawShade(Light emitter, Figure shade, Point point) {
        firstShadowPoint = shade.getYEnd();
        secondShadowPoint = shade.getY() - shade.getShadowHeight();
        glColor3f(0, 0, 0);
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(point.getX(), firstShadowPoint);
        glVertex2f(point.getX(), secondShadowPoint);
        glVertex2f(point.getY(), secondShadowPoint);
        glVertex2f(point.getY(), firstShadowPoint);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawShadeLit(Light emitter, Figure shade, Point point) {
        firstShadowPoint = shade.getYEnd();
        secondShadowPoint = shade.getY() - shade.getShadowHeight();
        glColor3f(1, 1, 1);
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(point.getX(), firstShadowPoint);
        glVertex2f(point.getX(), secondShadowPoint);
        glVertex2f(point.getY(), secondShadowPoint);
        glVertex2f(point.getY(), firstShadowPoint);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static Point getXIntersetction(double a, double b, int xStart, int yStart, int xEnd, int yEnd, RoundRectangle other) {
        if (other.isTriangular()) {
            if (other.isLeftBottomRound()) {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getX(), other.getYEnd() - Place.tileSize, other.getXEnd(), other.getYEnd());
            } else {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getXEnd(), other.getYEnd() - Place.tileSize, other.getX(), other.getYEnd());
            }
        } else {
            if (other.isConcave()) {
                if (other.isLeftBottomRound()) {
                    return Methods.getTopCircleLineIntersection(-a, -b, other.getX(), other.getYEnd());
                } else {
                    return Methods.getTopCircleLineIntersection(-a, -b, other.getXEnd(), other.getYEnd());
                }
            } else {
                if (other.isLeftBottomRound()) {
                    return Methods.getBottomCircleLineIntersection(-a, -b, other.getXEnd(), other.getYEnd() - Place.tileSize);
                } else {
                    return Methods.getBottomCircleLineIntersection(-a, -b, other.getX(), other.getYEnd() - Place.tileSize);
                }
            }
        }
    }

    private static Point getXIntersetctionFromTop(double a, double b, int xStart, int yStart, int xEnd, int yEnd, RoundRectangle other, Figure current) {
        if (other.isTriangular()) {
            if (other.isLeftBottomRound()) {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getX(), other.getYEnd() - Place.tileSize, other.getXEnd(), other.getYEnd());
            } else {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getXEnd(), other.getYEnd() - Place.tileSize, other.getX(), other.getYEnd());
            }
        } else {
            if (other.isConcave()) {
                if (other.isLeftBottomRound()) {
                    return Methods.getBottomCircleLineIntersection(-a, -b, other.getX(), other.getYEnd());
                } else {
                    return Methods.getBottomCircleLineIntersection(-a, -b, other.getXEnd(), other.getYEnd());
                }
            } else {
                if (other.isLeftBottomRound()) {
                    return Methods.getTopCircleLineIntersection(-a, -b, other.getXEnd(), other.getYEnd() - Place.tileSize);
                } else {
                    return Methods.getTopCircleLineIntersection(-a, -b, other.getX(), other.getYEnd() - Place.tileSize);
                }
            }
        }
    }

    private interface shadeRenderer {

        void render(Light emitter, Figure shade, Point point);
    }
}
