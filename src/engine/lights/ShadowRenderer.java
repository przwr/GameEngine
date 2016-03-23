/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Block;
import collision.Figure;
import collision.OpticProperties;
import collision.RoundRectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.map.Area;
import game.place.map.Map;
import net.jodk.lang.FastMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static collision.OpticProperties.NO_SHADOW;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static engine.lights.Shadow.*;
import static engine.lights.ShadowDrawer.*;
import static engine.utilities.Drawer.clearScreen;
import static engine.utilities.Drawer.displayHeight;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class ShadowRenderer {

    private static final boolean DEBUG = false, OBJECT_DEBUG = false;
    private static final ArrayList<Figure> shades = new ArrayList<>(512);
    private static final Point[] shadowPoints = {new Point(), new Point(), new Point(), new Point()};
    private static final Polygon polygon = new Polygon();
    private static final ShadowContainer darkenSpots = new ShadowContainer(), brightenSpots = new ShadowContainer();
    private static final Point casting = new Point();

    private static boolean checked;
    private static int shX, shY, xc, yc, range, XL1, XL2, XR1, XR2, lightYEnd, lightYStart, lightXEnd, lightXStart, centerX, centerY,
            lightXCentralShifted, lightYCentralShifted, shadow0X, shadow0Y, shadow1X, shadow1Y, shadow2X, shadow2Y, shadow3X, shadow3Y;
    private static float al, bl, ar, br, as, bs, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static double angle;
    private static Shadow tempShadow, minShadow, maxShadow;
    private static Point tempPoint;
    private static Area area;

    private static void DEBUG(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    private static void OBJECT_DEBUG(String message) {
        if (OBJECT_DEBUG) {
            System.out.println(message);
        }
    }

    //    static long sum = 0;
//    static int count = 0;
    public static void preRenderLight(Map map, Light light) {
//        long start = System.nanoTime();
        prepareToFindShades(light);
        findShades(light, map);
        prepareToPreRender(light);
        calculateShadows(light);
        renderShadows(light);
        endPreRender(light);
//        long end = System.nanoTime();
//        sum += (end - start);
//        count++;
//        if (count == 200) {
//            System.out.println("Time: " + (sum / 200000f));
//            count = 0;
//            sum = 0;
//        }
    }

    private static void prepareToFindShades(Light light) {
        int lightHeightHalf = light.getHeight() >> 1;
        lightYEnd = light.getY() + lightHeightHalf;
        lightYStart = light.getY() - lightHeightHalf;
        int lightWidthHalf = light.getWidth() >> 1;
        lightXEnd = light.getX() + lightWidthHalf;
        lightXStart = light.getX() - lightWidthHalf;
        lightXCentralShifted = light.getXCenterShift() - light.getX();
        lightYCentralShifted = light.getYCenterShift() - light.getY() + displayHeight - light.getHeight();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Drawer.shadowShader.start();
    }

    private static void prepareToPreRender(Light light) {
        light.getFrameBufferObject().activate();
        clearScreen(1);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void findShades(Light light, Map map) {
        shades.clear();
        area = map.getArea(map.getAreaIndex(light.getX(), light.getY()));
        searchBlocks(light);
        searchForegroundTiles();
        searchObjects();
        area = null;
        Collections.sort(shades);
    }

    private static void searchBlocks(Light light) {
        for (Block block : area.getNearBlocks()) {
            Figure tempShade = block.getCollision();
            if (tempShade != null && tempShade.getType() != NO_SHADOW) {
                if (tempShade.getX() <= lightXEnd && tempShade.getXEnd() >= lightXStart
                        && tempShade.getY() - FastMath.abs(tempShade.getShadowHeight()) - Place.tileSize <= lightYEnd && tempShade.getYEnd() >= lightYStart) {
                    tempShade.setLightDistance(FastMath.abs(tempShade.getXCentral() - light.getX()));
                    shades.add(tempShade);
                }
                for (Figure top : block.getTop()) {
                    if (top != null && !top.isLitable() && top.getX() <= lightXEnd && top.getXEnd() >= lightXStart
                            && top.getY() - FastMath.abs(tempShade.getShadowHeight() + tempShade.getShadowHeight()) - tempShade.getHeight() <= lightYEnd) {
                        shades.add(top);
                    }
                }
            }
        }
    }

    private static void searchForegroundTiles() {
        for (GameObject fgTile : area.getNearForegroundTiles()) {
            if (!fgTile.isInBlock()) {
                Figure tempShade = fgTile.getCollision();
                if (tempShade != null && (tempShade.isLitable() || tempShade.getType() == OpticProperties.IN_SHADE_NO_SHADOW)
                        && tempShade.getOwner().getAppearance() != null
                        && fgTile.getY() - tempShade.getActualHeight() + tempShade.getHeightHalf() <= lightYEnd
                        && fgTile.getY() + tempShade.getActualHeight() - tempShade.getHeightHalf() >= lightYStart
                        && fgTile.getX() - tempShade.getActualWidth() / 2 <= lightXEnd
                        && fgTile.getX() + tempShade.getActualWidth() / 2 >= lightXStart) {
                    shades.add(tempShade);
                }
            }
        }
    }

    private static void searchObjects() {
        for (GameObject object : area.getNearDepthObjects()) {
            Figure tempShade = object.getCollision();
            if (tempShade != null && tempShade.isLitable() && tempShade.getOwner().getAppearance() != null && object.isVisible()
                    && object.getYSpriteBegin() <= lightYEnd
                    && object.getYSpriteEnd() >= lightYStart
                    && object.getXSpriteBegin() <= lightXEnd
                    && object.getXSpriteEnd() >= lightXStart) {
                shades.add(tempShade);
            }
        }
    }

    private static void calculateShadows(Light light) {
        for (Figure figure : shades) {
            figure.calculateShadows(light);
        }
    }

    public static void calculateShadowAndWalls(Light light, Figure shaded) {
        calculateShadow(light, shaded);
        if (shaded.isConcave()) {
            drawShadowFromConcave(shaded, shadowPoints, lightXCentralShifted, lightYCentralShifted);
        } else {
            drawShadow(shadowPoints, lightXCentralShifted, lightYCentralShifted);
        }
        calculateVerticalShadows(shaded, light);
    }

    private static void calculateShadow(Light source, Figure thisShade) {
        findPoints(source, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
        setPolygonShape();
    }

    private static void findPoints(Light source, Figure shade) {
        centerX = source.getX();
        centerY = source.getY();
        Methods.getCastingPointsIndexes(centerX, centerY, shade, casting);
        shadowPoints[0] = shade.getPoint(casting.getFirst());
        shadowPoints[1] = shade.getPoint(casting.getSecond());
        switchSidesIfNeeded(shade);
        shadow0X = shadowPoints[0].getX();
        shadow0Y = shadowPoints[0].getY();
        shadow1X = shadowPoints[1].getX();
        shadow1Y = shadowPoints[1].getY();
    }

    private static void switchSidesIfNeeded(Figure shade) {
        if (centerY > shade.getY()) {
            if (shadowPoints[0].getY() < centerY && shadowPoints[1].getY() < centerY) {
                checkBothUp(shade);
            } else if (shadowPoints[0].getY() < centerY) {
                checkOneDown(shade);
            }
        }
    }

    private static void checkBothUp(Figure shade) {
        if (shadowPoints[0].getX() == shadowPoints[1].getX()) {
            if (centerX > shadowPoints[0].getX() && centerX > shadowPoints[1].getX()) {
                if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                    switchSides(shade);
                }
            } else if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
                switchSides(shade);
            }
        } else if (centerX > shadowPoints[0].getX() && centerX > shadowPoints[1].getX()) {
            if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                switchSides(shade);
            }
        } else if (centerX < shadowPoints[0].getX() && centerX < shadowPoints[1].getX()) {
            if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
                switchSides(shade);
            }
        } else if (shadowPoints[0].getX() > shadowPoints[1].getX()) {
            switchSides(shade);
        }
    }

    private static void checkOneDown(Figure shade) {
        if (centerX > shadowPoints[0].getX()) {
            if (shadowPoints[0].getY() < shadowPoints[1].getY()) {
                switchSides(shade);
            }
        } else if (shadowPoints[0].getY() > shadowPoints[1].getY()) {
            switchSides(shade);
        }
    }

    private static void switchSides(Figure shade) {
        shadowPoints[0] = shade.getPoint(casting.getSecond());
        shadowPoints[1] = shade.getPoint(casting.getFirst());
    }

    private static void findLeftSideOfShadow() {
        if (shadow0X == centerX) {
            shadowPoints[2].set(shadow0X, shadow0Y + (shadow0Y > centerY ? shadowLength : -shadowLength));
        } else if (shadow0Y == centerY) {
            shadowPoints[2].set(shadow0X + (shadow0X > centerX ? shadowLength : -shadowLength), shadow0Y);
        } else {
            al = ((centerY - shadow0Y)) / (float) (centerX - shadow0X);
            bl = shadow0Y - al * shadow0X;
            shX = shadow0X;
            if (al == 0) {
                shY = shadow0Y + (shadow0Y > centerY ? shadowLength : -shadowLength);
            } else {
                shX += (al > 0 ? (shadow0Y > centerY ? shadowLength : -shadowLength) : (shadow0Y > centerY ? -shadowLength : shadowLength));
                shY = (int) (al * shX + bl);
            }
            shadowPoints[2].set(shX, shY);
        }
        shadow2X = shadowPoints[2].getX();
        shadow2Y = shadowPoints[2].getY();
    }

    private static void findRightSideOfShadow() {
        if (shadow1X == centerX) {
            shadowPoints[3].set(shadow1X, shadow1Y + (shadow1Y > centerY ? shadowLength : -shadowLength));
        } else if (shadow1Y == centerY) {
            shadowPoints[3].set(shadow1X + (shadow1X > centerX ? shadowLength : -shadowLength), shadow1Y);
        } else {
            ar = ((centerY - shadow1Y)) / (float) (centerX - shadow1X);
            br = shadow1Y - ar * shadow1X;
            shX = shadow1X;
            if (ar == 0) {
                shY = shadow1Y + (shadow1Y > centerY ? shadowLength : -shadowLength);
            } else {
                shX += (ar > 0 ? (shadow1Y > centerY ? shadowLength : -shadowLength) : (shadow1Y > centerY ? -shadowLength : shadowLength));
                shY = (int) (ar * shX + br);
            }
            shadowPoints[3].set(shX, shY);
        }
        shadow3X = shadowPoints[3].getX();
        shadow3Y = shadowPoints[3].getY();
    }

    private static void setPolygonShape() {
        polygon.reset();
        polygon.addPoint(shadow0X, shadow0Y);
        polygon.addPoint(shadow1X, shadow1Y);
        polygon.addPoint(shadow3X, shadow3Y);
        polygon.addPoint(shadow2X, shadow2Y);
    }

    public static void calculateAndDrawSelfShadow(Light emitter, Figure shaded) {
        if (shaded.isConcave()) {
            calculateConcaveSelfShadow(emitter, shaded);
        } else if (!shaded.isTriangular()) {
            calculateConvexSelfShadow(emitter, shaded);
        }
    }

    private static void calculateConcaveSelfShadow(Light light, Figure shaded) {
        if (shaded.isLeftBottomRound()) {
            if ((light.getX() > shaded.getX() + Place.tileSize && light.getY() > shaded.getYEnd())) {
                calculateConcaveLeftFromBottomSelfShadow(light, shaded);
            } else if ((light.getX() < shaded.getX() && light.getY() < shaded.getYEnd() - Place.tileSize)) {
                calculateConcaveLeftFromTopSelfShadow(light, shaded);
            } else if (light.getX() > shaded.getX() + Place.tileSize && light.getY() <= shaded.getYEnd()) {
                shaded.addShadowType(DARK);
            }
        } else if (shaded.isRightBottomRound()) {
            if ((light.getX() < shaded.getX() && light.getY() > shaded.getYEnd())) {
                calculateConcaveRightFromBottomSelfShadow(light, shaded);
            } else if (light.getX() > shaded.getX() + Place.tileSize && light.getY() < shaded.getYEnd() - Place.tileSize) {
                calculateConcaveRightFromTopSelfShadow(light, shaded);
            } else if (light.getX() < shaded.getX() && light.getY() <= shaded.getYEnd()) {
                shaded.addShadowType(DARK);
            }
        }
    }

    private static void calculateConcaveLeftFromBottomSelfShadow(Light light, Figure shaded) {
        as = (light.getY() - shaded.getYEnd()) / (float) (light.getX() - shaded.getX() - Place.tileSize);
        bs = shaded.getYEnd() - as * (shaded.getX() + Place.tileSize);
        yc = shaded.getYEnd() - Place.tileSize;
        xc = Math.round((yc - bs) / as);
        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
            drawLeftConcaveBottom(shaded, xc, yc, lightXCentralShifted, lightYCentralShifted);
        }
        as *= as;
        xc = Math.round((Place.tileSize * (as - 1)) / (1 + as));
        if (xc <= 0) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, xc, Place.tileSize);
        }
        DEBUG("SelfShadow Left From Bottom");
    }

    private static void calculateConcaveLeftFromTopSelfShadow(Light emitter, Figure shaded) {
        as = (emitter.getY() - shaded.getYEnd() + Place.tileSize) / (float) (emitter.getX() - shaded.getX());
        bs = shaded.getYEnd() - Place.tileSize - as * shaded.getX();
        xc = shaded.getX() + Place.tileSize;
        yc = Math.round(as * xc + bs);
        if (yc >= shaded.getYEnd() - Place.tileSize) {
            drawConcaveTop(shaded, xc, yc, lightXCentralShifted, lightYCentralShifted);
        }
        yc = shaded.getYEnd() - yc;
        xc = Methods.roundDouble(FastMath.sqrt(Place.tileSquared - yc * yc));
        if (xc >= Place.tileSize || yc < 0) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, 0, xc);
        }
        DEBUG("SelfShadow Left From Top");
    }

    private static void calculateConcaveRightFromBottomSelfShadow(Light light, Figure shaded) {
        as = (light.getY() - shaded.getYEnd()) / (float) (light.getX() - shaded.getX());
        bs = shaded.getYEnd() - as * shaded.getX();
        yc = shaded.getYEnd() - Place.tileSize;
        xc = Math.round((yc - bs) / as);
        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
            drawRightConcaveBottom(shaded, xc, yc, lightXCentralShifted, lightYCentralShifted);
        }
        xc = Math.round((Place.tileDoubleSize) / (1 + as * as));
        if (xc >= Place.tileSize) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, 0, xc);
        }
        DEBUG("SelfShadow Right From Bottom");
    }

    private static void calculateConcaveRightFromTopSelfShadow(Light light, Figure shaded) {
        as = (light.getY() - shaded.getYEnd() + Place.tileSize) / (float) (light.getX() - shaded.getX() - Place.tileSize);
        bs = shaded.getYEnd() - Place.tileSize - as * (shaded.getX() + Place.tileSize);
        xc = shaded.getX();
        yc = Math.round(as * xc + bs);
        if (yc >= shaded.getYEnd() - Place.tileSize) {
            drawConcaveTop(shaded, xc, yc, lightXCentralShifted, lightYCentralShifted);
        }
        yc = shaded.getYEnd() - yc;
        xc = Methods.roundDouble(Place.tileSize - FastMath.sqrt(Place.tileSquared - yc * yc));
        if (xc <= 0 || yc < 0) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, xc, Place.tileSize);
        }
        DEBUG("SelfShadow Right From Top");
    }

    private static void calculateConvexSelfShadow(Light light, Figure shaded) {
        range = 50;
        if (shaded.isLeftBottomRound()) {
            calculateConvexLeftSelfShadow(light, shaded);
        } else if (shaded.isRightBottomRound()) {
            calculateConvexRightSelfShadow(light, shaded);
        }
    }

    private static void calculateConvexLeftSelfShadow(Light emitter, Figure shaded) {
        angle = 45 - Methods.pointAngle(emitter.getX(), emitter.getY(),
                shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(),
                shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
        if (angle > 0) {
            if (angle < range) {
                shaded.addShadow(DARKEN, Methods.roundDouble((angle / range) * Place.tileSize), Place.tileSize);
            } else {
                range = 68;
                if (angle > 222 - range && angle <= 222) {
                    shaded.addShadow(DARKEN, 0, Methods.roundDouble(((angle - 222 + range) / (range)) * Place.tileSize));
                }
            }
        }
    }

    private static void calculateConvexRightSelfShadow(Light emitter, Figure shaded) {
        angle = -135 + Methods.pointAngleClockwise(emitter.getX(), emitter.getY(),
                shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(),
                shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
        if (angle > 0) {
            if (angle < range) {
                shaded.addShadow(DARKEN, 0, Methods.roundDouble(((range - angle) / range) * Place.tileSize));
            } else {
                range = 68;
                if (angle > (222 - range) && angle <= 222) {
                    shaded.addShadow(DARKEN, Methods.roundDouble(((222 - angle) / (range)) * Place.tileSize), Place.tileSize);
                }
            }
        }
    }

    private static void renderShadows(Light light) {
        for (Figure shaded : shades) {
            solveShadows(shaded);
            drawAllShadows(shaded, lightXCentralShifted, lightYCentralShifted);
            shaded.clearShadows();
        }
    }

    private static void endPreRender(Light light) {
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        Drawer.regularShader.start();
        light.render(displayHeight);
        light.getFrameBufferObject().deactivate();
    }

    public static void calculateShadowShade(Light light, Figure shaded) {
        if (shaded.isLitable()) {
            if (shaded instanceof RoundRectangle) {
                calculateRoundShade(shaded, light);
            } else {
                calculateRegularShade(shaded, light);
            }
        } else {
            shaded.addShadowType(DARK);
        }
    }

    private static void calculateRoundShade(Figure shaded, Light light) {
        if (isRoundInLight(shaded, light)) {
            shaded.getOwner().renderShadowLit(lightXCentralShifted, lightYCentralShifted, shaded);
            shaded.addShadowType(BRIGHT);
        } else {
            shaded.addShadowType(DARK);
        }
    }

    private static boolean isRoundInLight(Figure shaded, Light light) {
        if (shaded.isBottomRounded()) {
            if (shaded.isTriangular()) {
                return isTriangularInLight(shaded, light);
            } else {
                return isConcaveConvexInLight(shaded, light);
            }
        } else {
            return light.getY() >= shaded.getYEnd();
        }
    }

    private static boolean isTriangularInLight(Figure shaded, Light light) {
        if (shaded.isLeftBottomRound()) {
            angle = 45 - Methods.pointAngle(light.getX(), light.getY(),
                    shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(),
                    shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
            return angle > 0.5 && angle < 179.5;
        } else {
            angle = -135 + Methods.pointAngleClockwise(light.getX(), light.getY(),
                    shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(),
                    shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
            return angle > 0.5 && angle < 179.5;
        }
    }

    private static boolean isConcaveConvexInLight(Figure shaded, Light light) {
        if (shaded.isLeftBottomRound()) {
            angle = 45 - Methods.pointAngle(light.getX(), light.getY(),
                    shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(),
                    shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
            return angle > 0 && angle <= 222;
        } else {
            angle = -135 + Methods.pointAngleClockwise(light.getX(), light.getY(),
                    shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(),
                    shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
            return angle > 0 && angle <= 222;
        }
    }

    private static void calculateRegularShade(Figure shaded, Light light) {
        if (light.getY() > shaded.getYEnd()) {
            shaded.getOwner().renderShadowLit(lightXCentralShifted, lightYCentralShifted, shaded);
            shaded.addShadowType(BRIGHT);
        } else {
            shaded.addShadowType(DARK);
        }
    }

    private static void solveShadows(Figure shaded) {
        if (isFullyShadedSeparateShadowsByType(shaded)) {
            shaded.clearShadows();
            shaded.addShadowType(DARK);
            return;
        }
        shaded.clearShadows();
        if (tempShadow != null && brightenSpots.isEmpty()) {
            shaded.addShadowType(BRIGHT);
        }
        if (shaded.isLitable() && brightenSpots.size() > 1) {
            solveBrightenSpots();
            addSolvedBrightenSpots(shaded);
        } else {
            shaded.addAllShadows(brightenSpots);
        }
        shaded.addAllShadows(darkenSpots);
    }

    private static void solveBrightenSpots() {
        minShadow = maxShadow = null;
        int minValue = Integer.MAX_VALUE;
        int maxValue = -1;
        int xS, xE;
        for (int i = 0; i < brightenSpots.size(); i++) {
            xS = brightenSpots.get(i).xS;
            xE = brightenSpots.get(i).xE;
            if (xS < xE) {
                if (xS > maxValue) {
                    maxValue = xS;
                    maxShadow = brightenSpots.get(i);
                }
            } else if (xS > xE) {
                if (minValue > xS) {
                    minValue = xS;
                    minShadow = brightenSpots.get(i);
                }
            } else if (xS == Place.tileSize) {
                maxValue = Integer.MAX_VALUE;
                maxShadow = null;
            } else {
                minValue = -1;
                minShadow = null;
            }
        }
    }

    private static boolean isFullyShadedSeparateShadowsByType(Figure shaded) {
        tempShadow = null;
        darkenSpots.clear();
        brightenSpots.clear();
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            switch (shaded.getShadow(i).type) {
                case DARK:
                    return true;
                case BRIGHT:
                    tempShadow = shaded.getShadow(i);
                    break;
                case DARKEN:
                    darkenSpots.add(shaded.getShadow(i));
                    break;
                case DARKEN_OBJECT:
                    darkenSpots.add(shaded.getShadow(i));
                    break;
                case BRIGHTEN:
                    brightenSpots.add(shaded.getShadow(i));
                    break;
                case BRIGHTEN_OBJECT:
                    brightenSpots.add(shaded.getShadow(i));
                    break;
            }
        }
        return false;
    }

    private static void addSolvedBrightenSpots(Figure shaded) {
        if (minShadow != null) {
            shaded.addShadow(minShadow.type, minShadow.xS, minShadow.xE);
        }
        if (maxShadow != null) {
            shaded.addShadow(maxShadow.type, maxShadow.xS, maxShadow.xE);
        }
    }

    private static void calculateVerticalShadows(Figure current, Light source) {
        for (Figure other : shades) {
            if (other != current && other != source.getOwnerCollision()) {
                checked = false;
                if (other.getOwner() instanceof Block) {
                    if (other instanceof RoundRectangle && other.isBottomRounded()) {
                        if (other.getYEnd() < source.getY()) {
                            calculateRoundBlockFromBottomVertical(other, current, source);
                        } else {
                            calculateRoundBlockFromTopVertical(other, current, source);
                        }
                    } else if (current.getY() <= source.getY() && current.getYEnd() != other.getYEnd()) {
                        calculateRegularBlockVertical(other, current, source);
                    }
                } else if (other.isLitable()) {
                    calculateObjectVertical(other, current, source);
                }
            }
        }
    }

    private static void calculateRoundBlockFromBottomVertical(Figure other, Figure current, Light source) {
        calculateIntersectionBounds(other);
        if (shouldCalculateLeftRoundBlock(other)) {
            calculateLeftRoundBlock(other, current, source);
        }
        if (shouldCalculateRightRoundBlock(other)) {
            calculateRightRoundBlock(other, current, source);
        }
        findRoundDarkness(source, other, current);
    }

    private static boolean shouldCalculateLeftRoundBlock(Figure other) {
        return (shadow0Y > shadow2Y && shadow0Y > other.getY() - other.getShadowHeight())
                && ((shadow0X != shadow2X && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd())))
                || (shadow0X == shadow2X && XOL >= other.getX() && XOL <= other.getXEnd()));
    }

    private static boolean shouldCalculateRightRoundBlock(Figure other) {
        return (shadow1Y > shadow3Y && shadow1Y > other.getY() - other.getShadowHeight())
                && ((shadow1X != shadow3X && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd())))
                || (shadow1X == shadow3X && XOR >= other.getX() && XOR <= other.getXEnd()));
    }

    private static void calculateRoundBlockFromTopVertical(Figure other, Figure current, Light source) {
        calculateIntersectionBounds(other);
        if (shouldCalculateLeftRoundBlockFromTop(other, current)) {
            calculateLeftRoundBlockFromTop(other, current, source);
        }
        if (shouldCalculateRightRoundBlockFromTop(other, current)) {
            calculateRightRoundBlockFromTop(other, current, source);
        }
        findRoundDarknessFromTop(other, current, source);
    }

    private static boolean shouldCalculateLeftRoundBlockFromTop(Figure other, Figure current) {
        return (shadow0Y < shadow2Y && shadow0Y < other.getYEnd())
                && ((shadow0X != shadow2X && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))))
                && !(current.getX() < other.getX() && current.getYEnd() + Place.tileSize == other.getYEnd());
    }

    private static boolean shouldCalculateRightRoundBlockFromTop(Figure other, Figure current) {
        return (shadow1Y < shadow3Y && shadow1Y < other.getYEnd())
                && ((shadow1X != shadow3X && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))))
                && !(current.getX() > other.getX() && current.getYEnd() + Place.tileSize == other.getYEnd());
    }

    private static void calculateRegularBlockVertical(Figure other, Figure current, Light source) {
        calculateIntersectionBounds(other);
        if (shouldCalculateLeftBlock(other)) {
            calculateLeftBlock(other, current, source);
        }
        if (shouldCalculateRightBlock(other)) {
            calculateRightBlock(other, current, source);
        }
        findDarkness(other, current, source);
    }

    private static boolean shouldCalculateLeftBlock(Figure other) {
        return (shadow0Y > shadow2Y && shadow0Y > other.getY() - other.getShadowHeight())
                && ((shadow0X != shadow2X && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd())))
                || (shadow0X == shadow2X && XOL >= other.getX() && XOL <= other.getXEnd()));
    }

    private static boolean shouldCalculateRightBlock(Figure other) {
        return (shadow1Y > shadow3Y && shadow1Y > other.getY() - other.getShadowHeight())
                && ((shadow1X != shadow3X && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd())))
                || (shadow1X == shadow3X && XOR >= other.getX() && XOR <= other.getXEnd()));
    }

    private static void calculateIntersectionBounds(Figure other) {
        if (shadow0X == shadow2X) {
            XOL = shadow0X;
        } else {
            XOL = ((other.getYEnd() - bl) / al);
            YOL = (al * other.getX() + bl);
            YOL2 = (al * other.getXEnd() + bl);
        }
        if (shadow1X == shadow3X) {
            XOR = shadow1X;
        } else {
            XOR = ((other.getYEnd() - br) / ar);
            YOR = (ar * other.getX() + br);
            YOR2 = (ar * other.getXEnd() + br);
        }
    }

    private static void calculateObjectVertical(Figure other, Figure current, Light source) {
        if (current.getY() < source.getY()) {
            if (shouldCalculateLeftObject(other)) {
                calculateLeftObject(other, current, source);
            }
            if (shouldCalculateRightObject(other)) {
                calculateRightObject(other, current, source);
            }
        }
        findObjectDarkness(other, current, source);
    }

    private static boolean shouldCalculateLeftObject(Figure other) {
        if (shadow0X == shadow2X) {
            XOL = shadow0X;
        } else {
            XOL = ((other.getYEnd() - bl) / al);
            YOL = (al * other.getXSpriteBegin(false) + bl);
            YOL2 = (al * other.getXSpriteEnd(false) + bl);
        }
        return (shadow0Y > shadow2Y && shadow0Y > other.getY())
                && ((shadow0X != shadow2X &&
                ((XOL >= other.getXSpriteBegin(false) && XOL <= other.getXSpriteEnd(false)) ||
                        (YOL > other.getYSpriteBegin(false) && YOL < other.getYEnd()) || (YOL2 > other.getYSpriteBegin(false) && YOL2 < other.getYEnd())))
                || (shadow0X == shadow2X && XOL >= other.getXSpriteBegin(false) && XOL <= other.getXSpriteEnd(false)));
    }

    private static boolean shouldCalculateRightObject(Figure other) {
        if (shadow1X == shadow3X) {
            XOR = shadow1X;
        } else {
            XOR = ((other.getYEnd() - br) / ar);
            YOR = (ar * other.getXSpriteBegin(false) + br);
            YOR2 = (ar * other.getXSpriteEnd(false) + br);
        }
        return (shadow1Y > shadow3Y && shadow1Y > other.getY())
                && ((shadow1X != shadow3X &&
                ((XOR >= other.getXSpriteBegin(false) && XOR <= other.getXSpriteEnd(false))
                        || (YOR > other.getYSpriteBegin(false) && YOR < other.getYEnd()) || (YOR2 > other.getYSpriteBegin(false) && YOR2 < other.getYEnd())))
                || (shadow1X == shadow3X && XOR >= other.getXSpriteBegin(false) && XOR <= other.getXSpriteEnd(false)));
    }

    private static void calculateLeftBlock(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadow0Y > other.getYEnd()
                && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setLeftBlockVariables(other, source);
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if (shouldAddLightLeftBlock(other, current, source)) {
                    addLightLeftBlock(other, current, source);
                } else {
                    addShadowLeftBlock(other, current);
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadow0X != shadow2X) { // rysuj
                // zaciemniony
                addDarkenLeftBlock(other, current, source);
            }
        }
    }

    private static void setLeftBlockVariables(Figure other, Light source) {
        if (shadow0X == shadow2X) {
            XL1 = shadow0X;
            XL2 = other.getXEnd();
        } else {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
            XL2 = XL1 <= source.getX() ? other.getX() : other.getXEnd();
        }
    }

    private static boolean shouldAddLightLeftBlock(Figure other, Figure current, Light source) {
        return (XL1 < source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX()
                && source.getY() < current.getYEnd() && shadow0X < shadow2X)
                || (XL1 == other.getXEnd() && XL1 == current.getX()));
    }

    private static void addLightLeftBlock(Figure other, Figure current, Light source) {
        addBrightenLeft(other);
        XL2 = source.getX() < XL1 ? other.getX() : other.getXEnd();
        if (shadow0X != shadow2X) {
            if (shadow1X == shadow3X) {
                XR1 = shadow1X;
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
            }
            if (XR1 < XL2 && XR1 > other.getX() && shadow3Y < current.getYEnd()) {
                XL2 = XR1;
            }
            addDarkenLeft(other);
        }
        DEBUG("Left Light");
    }

    private static void addShadowLeftBlock(Figure other, Figure current) {
        if (current.getX() == XL1) {
            XL2 = other.getXEnd();
        }
        if (shadow1X == shadow3X) {
            XR1 = shadow1X;
        } else {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
        }
        if (XR1 < XL2 && XR1 > other.getX() && shadow3Y < current.getYEnd()) {
            XL2 = XR1;
        }
        addDarkenLeft(other);
        DEBUG("Left Shade " + al + " XL1 " + XL1 + " figure.X " + current.getX());
    }

    private static void addDarkenLeftBlock(Figure other, Figure current, Light source) {
        YOL = al * other.getX() + bl;
        YOL2 = al * other.getXEnd() + bl;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX())
                || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd())
                || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd()))) {
            if (XL1 < current.getXEnd()) {
                other.addShadowType(BRIGHT);
                DEBUG("Left Lightness - first");
            } else {
                other.addShadowType(DARK);
                DEBUG("Left Darkness - first");
            }
        }
    }

    private static void addBrightenLeft(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(BRIGHTEN, XL1, XL2);
        } else {
            other.addShadow(BRIGHTEN_OBJECT, XL1 - other.getXSpriteBegin(false), XL2);
        }
    }

    private static void addDarkenLeft(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(DARKEN, XL1, XL2);
        } else {
            other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(false), XL2);
        }
    }

    private static void calculateRightBlock(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadow1Y > other.getYEnd()
                && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setRightBlockVariables(other, source);
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if (shouldAddLightRightBlock(other, current, source)) { // dodaj światło
                    addLightRightBLock(other, current, source);
                } else {
                    addShadowRightBlock(other, current);
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadow1X != shadow3X) { //
                // rysuj zaciemniony
                addDarkenRightBlock(other, current, source);
            }
        }
    }

    private static void setRightBlockVariables(Figure other, Light source) {
        if (shadow1X == shadow3X) {
            XR1 = shadow1X;
            XR2 = other.getX();
        } else {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
            XR2 = XR1 < source.getX() ? other.getX() : other.getXEnd();
        }
    }

    private static boolean shouldAddLightRightBlock(Figure other, Figure current, Light source) {
        return (XR1 > source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX()
                && source.getY() < current.getYEnd() && shadow1X > shadow3X)
                || (XR1 == other.getX() && XR1 == current.getXEnd()));
    }

    private static void addLightRightBLock(Figure other, Figure current, Light source) {
        addBrightenRight(other);
        XR2 = XR1 >= source.getX() ? other.getX() : other.getXEnd();
        if (shadow1X != shadow3X) {
            if (shadow0X == shadow2X) {
                XL1 = shadow0X;
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
            }
            if (XL1 > XR2 && XL1 < other.getXEnd() && shadow2Y < current.getYEnd()) {
                XR2 = XL1;
            }
            addDarkenRight(other);
        }
        DEBUG("Right Light XR1 " + XR1);
    }

    private static void addShadowRightBlock(Figure other, Figure current) {
        if (XR1 == current.getXEnd()) {
            XR2 = other.getX();
        }
        if (shadow0X == shadow2X) {
            XL1 = shadow0X;
        } else {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
        }
        if (XL1 > XR2 && XL1 < other.getXEnd() && shadow2Y < current.getYEnd()) {
            XR2 = XL1;
        }
        addDarkenRight(other);
        DEBUG("Right Shade " + ar + " XR1 " + XR1 + " figure.X " + current.getX());
    }

    private static void addDarkenRightBlock(Figure other, Figure current, Light source) {
        YOR = ar * other.getX() + br;
        YOR2 = ar * other.getXEnd() + br;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX())
                || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd())
                || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd()))) {
            if (XR1 > current.getX()) { // check this shitty condition
                other.addShadowType(BRIGHT);
                DEBUG("Right Lightness - first");
            } else {
                other.addShadowType(DARK);
                DEBUG("Right Darkness - first");
            }
        }
    }

    private static void addBrightenRight(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(BRIGHTEN, XR1, XR2);
        } else {
            other.addShadow(BRIGHTEN_OBJECT, XR1 - other.getXSpriteBegin(false), XR2);
        }
    }

    private static void addDarkenRight(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(DARKEN, XR1, XR2);
        } else {
            other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(false), XR2);
        }
    }

    private static void calculateLeftRoundBlock(Figure other, Figure current, Light source) {
        if (shouldCalculateLeftRoundBlock(other, current, source)) {
            if (shadow0X == shadow2X) {
                XL1 = shadow0X;
                XL2 = other.getXEnd();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX())
                        || (XL1 > other.getX() && XL1 < other.getXEnd())) {
                    tempPoint = Methods.getXIntersection(al, bl, shadow0X, shadow0Y, shadow2X, shadow2Y, other);
                    if (tempPoint != null && other.getYEnd() <= current.getYEnd() && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY()
                            <= other.getYEnd()) {
                        XL1 = tempPoint.getX();
                    } else {
                        findRoundDarkness(source, other, current);
                        return;
                    }
                }
                XL2 = al > 0 ? other.getX() : other.getXEnd();
            }
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if (XL1 < source.getX() || (XL1 == other.getXEnd() && XL1 == current.getX()) || (XL1 == other.getX() && XL1 == current.getXEnd())) { //dodaj
                    // światło
                    if (other.getYEnd() == current.getYEnd()) {
                        tempShadow = null;
                        for (int i = 0; i < other.getShadowCount(); i++) {
                            if (other.getShadow(i).type == BRIGHTEN && other.getShadow(i).yC == other.getYEnd()) {
                                if (FastMath.abs(current.getX() - other.getX()) > FastMath.abs(other.getShadow(i).xC - other.getX())) {
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
                    // special case for corner to corner situation
                    if ((other.getX() == current.getXEnd() && current.getY() == other.getYEnd() && !other.isLeftBottomRound() && source.getX() > current
                            .getXEnd())
                            || (other.getXEnd() == current.getX() && current.getY() == other.getYEnd() && !other.isRightBottomRound() && source.getX() <
                            current.getX())) {
                        checked = true;
                        return;
                    }
                    other.addShadowWithCaster(BRIGHTEN, XL1 - other.getX(), XL2 - other.getX(), current);
                    addDarkenLeftRoundBlockIfNeeded(other, current);
                    checked = true;
                    DEBUG("Left Round Light XL1 " + (XL1 - other.getX()) + " XL2 " + (XL2 - other.getX()));
                } else if (shadow0Y >= other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX())
                        || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX());
                    checked = true;
                    DEBUG("Left Round Shade XL1 " + (XL1 - other.getX()) + " XL2 " + (XL2 - other.getX()));
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadow0X != shadow2X) { // rysuj
                // zaciemniony
                addLightnessLeftRoundBlock(other, current, source);
            }
        }
    }

    private static void addDarkenLeftRoundBlockIfNeeded(Figure other, Figure current) {
        if (XL1 != other.getXEnd()) {
            if (al <= 0 || (XL1 == other.getX() && other.getYEnd() < current.getYEnd())) {
                XL2 = other.getXEnd();
            } else {
                XL2 = other.getXEnd();
            }
            other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX());
        }
    }

    private static boolean shouldCalculateLeftRoundBlock(Figure other, Figure current, Light source) {
        return other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd())
                || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX())
                || (other.isRightBottomRound() && other.getX() < current.getX()))));
    }

    private static void addLightnessLeftRoundBlock(Figure other, Figure current, Light source) {
        YOL = al * other.getX() + bl;
        YOL2 = al * other.getXEnd() + bl;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX() && other.getYEnd() != current.getYEnd())
                || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd())
                || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd()))) {
            if (XL1 < current.getX()) {
                other.addShadowType(BRIGHT);
                DEBUG("Left Round Lightness - second");
            }
        }
    }

    private static void calculateRightRoundBlock(Figure other, Figure current, Light source) {
        if (shouldCalculateRightRoundBlock(other, current, source)) {
            if (shadow1X == shadow3X) {
                XR1 = shadow1X;
                XR2 = other.getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX())
                        || (XR1 > other.getX() && XR1 < other.getXEnd())) {
                    tempPoint = Methods.getXIntersection(ar, br, shadow1X, shadow1Y, shadow3X, shadow3Y, other);
                    if (tempPoint != null && other.getYEnd() <= current.getYEnd() && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY()
                            <= other.getYEnd()) {
                        XR1 = tempPoint.getX();
                    } else {
                        findRoundDarkness(source, other, current);
                        return;
                    }
                }
                XR2 = ar > 0 ? other.getX() : other.getXEnd();
            }
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if (XR1 > source.getX() || (XR1 == other.getX() && XR1 == current.getXEnd()) || (XR1 == other.getXEnd() && XR1 == current.getX())) { // dodaj
                    // światło
                    if (other.getYEnd() == current.getYEnd()) {
                        tempShadow = null;
                        for (int i = 0; i < other.getShadowCount(); i++) {
                            if (other.getShadow(i).type == BRIGHTEN && other.getShadow(i).yC == other.getYEnd()) {
                                if (FastMath.abs(current.getX() - other.getX()) > FastMath.abs(other.getShadow(i).xC - other.getX())) {
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
                    // special case for corner to corner situation
                    if ((other.getX() == current.getXEnd() && current.getY() == other.getYEnd() && !other.isLeftBottomRound() && source.getX() > current
                            .getXEnd())
                            || (other.getXEnd() == current.getX() && current.getY() == other.getYEnd() && !other.isRightBottomRound() && source.getX() <
                            current.getX())) {
                        checked = true;
                        return;
                    }
                    other.addShadowWithCaster(BRIGHTEN, XR1 - other.getX(), XR2 - other.getX(), current);
                    addDarkenRightRoundBlockIfNeeded(other, current);
                    checked = true;
                    DEBUG("Right Round Light XR1 " + (XR1) + " XR2 " + (XR2));
                } else if (shadow1Y >= other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX())
                        || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XR1 - other.getX(), XR2 - other.getX());
                    checked = true;
                    DEBUG("Right Round Shade XR1 " + (XR1 - other.getX()) + " XR2 " + (XR2 - other.getX()));
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadow1X != shadow3X) {
                addLightnessRightRoundBlock(other, current, source);
            }
        }
    }

    private static void addDarkenRightRoundBlockIfNeeded(Figure other, Figure current) {
        if (XR1 != other.getX()) {
            if (ar <= 0 || (XR1 == other.getXEnd() && other.getYEnd() < current.getYEnd())) {
                XR2 = other.getX();
            } else {
                XR2 = other.getXEnd();
            }
            if (XR1 != XR2) {
                other.addShadow(DARKEN, XR1 - other.getX(), XR2 - other.getX());
            }
        }
    }

    private static boolean shouldCalculateRightRoundBlock(Figure other, Figure current, Light source) {
        return other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd())
                || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX())
                || (other.isRightBottomRound() && other.getX() < current.getX()))));
    }

    private static void addLightnessRightRoundBlock(Figure other, Figure current, Light source) {
        YOR = ar * other.getX() + br;
        YOR2 = ar * other.getXEnd() + br;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX() && other.getYEnd() != current.getYEnd())
                || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd())
                || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd() - Place.tileSize))) {
            if (XR1 > current.getXEnd()) {
                other.addShadowType(BRIGHT);
                DEBUG("Right Round Lightness - first");
            }
        }
    }

    private static void calculateLeftRoundBlockFromTop(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XL1 > other
                    .getX() && XL1 < other.getXEnd())) {
                tempPoint = Methods.getXIntersectionFromTop(al, bl, shadow0X, shadow0Y, shadow2X, shadow2Y, other);
                if (tempPoint != null && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY() <= other.getYEnd()) {
                    XL1 = tempPoint.getX();
                } else {
                    if ((XL1 < other.getX() || XL1 > other.getXEnd()) && shadow3Y > current.getYEnd() && shadow2Y > current
                            .getYEnd() && other.getYEnd() >= current.getYEnd()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadow1X < current.getX() && YOL2 >= other
                            .getYEnd() - Place.tileSize && YOL2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadow3X > current.getYEnd() && YOL >= other
                            .getYEnd() - Place.tileSize && YOL <= other.getYEnd()))) {
                        other.addShadowType(DARK);
                        DEBUG("Left Round Top Darkness");
                        checked = true;
                    } else {
                        findRoundDarknessFromTop(other, current, source);
                    }
                    return;
                }
            }
            addProperShadowLeftRoundBlock(other, current, source);
        }
    }

    private static void addProperShadowLeftRoundBlock(Figure other, Figure current, Light source) {
        if (XL1 > other.getX() && XL1 < other.getXEnd()) {
            if (((shadow2Y > current.getYEnd() && shadow3Y > current.getYEnd()))) { // dodaj światło
                other.addShadow(DARKEN, XL1 - other.getX(), XL1 < current.getX() ? 0 : Place.tileSize);
                DEBUG("Left Round Top Light XL1: " + (XL1 - other.getX()));
                checked = true;
            } else if (shadow2Y > current.getYEnd()) { //dodaj cień
                other.addShadow(DARKEN, XL1 - other.getX(), XL1 > current.getX() ? 0 : Place.tileSize);
                DEBUG("Left Round Top Shade XL1: " + (XL1 - other.getX()));
                checked = true;
            } else {
                findRoundDarknessFromTop(other, current, source);
            }
        } else {
            findRoundDarknessFromTop(other, current, source);
        }
    }

    private static void calculateRightRoundBlockFromTop(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XR1 > other
                    .getX() && XR1 < other.getXEnd())) {
                tempPoint = Methods.getXIntersectionFromTop(ar, br, shadow1X, shadow1Y, shadow3X, shadow3Y, other);
                if (tempPoint != null && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY() <= other.getYEnd()) {
                    XR1 = tempPoint.getX();
                } else {
                    if ((XR1 < other.getX() || XR1 > other.getXEnd()) && shadow3Y > current.getYEnd() && shadow2Y > current
                            .getYEnd() && other.getYEnd() >= current.getYEnd() && current.getX() != other.getXEnd() && current.getXEnd() != other.getX()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadow3X < current.getX() && YOR2 >= other
                            .getYEnd() - Place.tileSize && YOR2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadow3X > current.getYEnd() && YOR >= other
                            .getYEnd() - Place.tileSize && YOR <= other.getYEnd()))) {
                        other.addShadowType(DARK);
                        DEBUG("Right Round Top Darkness");
                        checked = true;
                    } else {
                        findRoundDarknessFromTop(other, current, source);
                    }
                    return;
                }
            }
            addProperShadowRightRoundBlock(other, current, source);
        }
    }

    private static void addProperShadowRightRoundBlock(Figure other, Figure current, Light source) {
        if (XR1 > other.getX() && XR1 < other.getXEnd()) {
            if (((shadow2Y > current.getYEnd() && shadow3Y > current.getYEnd()))) { // dodaj światło
                other.addShadow(DARKEN, XR1 - other.getX(), XR1 > current.getXEnd() ? Place.tileSize : 0);
                DEBUG("Right Round Top Light");
                checked = true;
            } else if (shadow3Y > current.getYEnd()) { //dodaj cień
                other.addShadow(DARKEN, XR1 - other.getX(), XR1 < current.getXEnd() ? Place.tileSize : 0);
                DEBUG("Right Round Top Shade");
                checked = true;
            } else {
                findRoundDarknessFromTop(other, current, source);
            }
        } else {
            findRoundDarknessFromTop(other, current, source);
        }
    }

    private static void calculateLeftObject(Figure other, Figure current, Light source) {
        if (((other.getXEnd() > current.getX() && current.getX() > source.getX()) || (other.getX() < current.getX() && current.getX() < source.getX()))
                && other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setLeftObjectVariables(other, current);
            if (XL1 >= other.getXSpriteBegin(false) && XL1 < other.getXSpriteEnd(false)) {
                if (XL1 < current.getX()) { //dodaj światło
                    other.addShadow(BRIGHTEN_OBJECT, XL1 - other.getXSpriteBegin(false), XL2);
                    XL2 = current.getX() <= XL1 ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
                    other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(false), XL2);
                    OBJECT_DEBUG("Object Left Light " + (XL1 - other.getXSpriteBegin(false)) + " XL2 " + XL2);
                    checked = true;
                } else { //dodaj cień
                    if (shadow3X > shadow2X /*|| shadow3Y < shadow2Y*/) {
                        XL2 = other.getXSpriteOffsetWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(false), XL2);
                    OBJECT_DEBUG("Object Left Shade " + (XL1 - other.getXSpriteBegin(false)) + " XL2 " + XL2);
                    checked = true;
                }
            }
        }
    }

    private static void setLeftObjectVariables(Figure other, Figure current) {
        if (shadow0X == shadow2X) {
            XL1 = shadow0X;
            XL2 = other.getXSpriteOffsetWidth();
        } else {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
            XL2 = current.getX() > XL1 ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
        }
    }

    private static void calculateRightObject(Figure other, Figure current, Light source) {
        if (((other.getXEnd() > current.getXEnd() && current.getXEnd() > source.getX()) || (other.getX() < current.getXEnd() && current.getXEnd() < source
                .getX())) && other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setRightObjectVariables(other, current);
            if (XR1 >= other.getXSpriteBegin(false) && XR1 <= other.getXSpriteEnd(false)) {
                if (XR1 > current.getXEnd()) { // dodaj światło
                    other.addShadow(BRIGHTEN_OBJECT, XR1 - other.getXSpriteBegin(false), XR2);
                    XR2 = XR1 > current.getXEnd() ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
                    other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(false), XR2);
                    OBJECT_DEBUG("Object Right Light XR1 " + (XR1 - other.getXSpriteBegin(false)) + " XR2 " + XR2 + other.getActualWidth() + " " + other
                            .getXSpriteOffset());
                    checked = true;
                } else { //dodaj cień
                    if (shadow3X < shadow2X /*|| shadow3Y > shadow2Y*/) {
                        XR2 = other.getXSpriteOffsetWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(false), XR2);
                    OBJECT_DEBUG("Object Right Shade XR1 " + (XR1 - other.getXSpriteBegin(false)) + " XR2 " + XR2);
                    checked = true;
                }
            }
        }
    }

    private static void setRightObjectVariables(Figure other, Figure current) {
        if (shadow1X == shadow3X) {
            XR1 = shadow1X;
            XR2 = other.getXSpriteOffset();
        } else {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
            XR2 = XR1 <= current.getXEnd() ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
        }
    }

    private static void findDarkness(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY()) {
            if (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd()) {
                if (current.getY() == other.getYEnd() && ((current.getX() == other.getXEnd() && source.getX() > current.getXEnd()) || (current.getXEnd() ==
                        other.getX() && source.getX() < current.getX())) && source.getY() < current.getYEnd()) {
                    other.addShadowType(DARK);
                    DEBUG("Darkness Special Case...");
                } else {
                    int points = 0;
                    if (polygon.contains(other.getX() + 1, other.getYEnd() - 1)) {
                        points++;
                    }
                    if (polygon.contains(other.getXEnd() - 1, other.getYEnd() - 1)) {
                        points++;
                    }
                    if (points == 2) {
                        other.addShadowType(DARK);
                        DEBUG("Darkness...");
                        return;
                    }
                }
            }
            if (other.getYEnd() != current.getY()
                    && ((shadow0Y == other.getYEnd() && shadow0Y >= source.getY() - 2
                    && shadow0Y <= source.getY() + 2) || (shadow1Y == other.getYEnd()
                    && shadow1Y >= source.getY() - 2 && shadow1Y <= source.getY() + 2))) {
                other.addShadowType(DARK);
                DEBUG("Darkness other...");
            }
        }
    }

    private static void findRoundDarkness(Light source, Figure other, Figure current) {
        if (!checked) {
            if ((current.getYEnd() != other.getYEnd()) || (!current.isBottomRounded() && current.getYEnd() - Place.tileSize != other.getYEnd() && ((other
                    .isLeftBottomRound() && other.getX() == current.getXEnd()) || (other.isRightBottomRound() && other.getXEnd() == current.getX()))) ||
                    (other.getX() != current.getXEnd() && other.getX() + Place.tileSize != current.getX()) || (current instanceof RoundRectangle && ((current
                    .getX() < other.getX() && other.isLeftBottomRound()) || (current.getX() > other.getX() && other.isRightBottomRound())))) {
                int points = 0;
                if (polygon.contains(other.getX() + 1, other.getYEnd() - Place.tileSize + 1)) {
                    points++;
                } else if (polygon.contains(other.getXEnd() - 1, other.getYEnd() - Place.tileSize + 1)) {
                    points++;
                }
                if (other.isLeftBottomRound()) {
                    if (polygon.contains(other.getXEnd() - 1, other.getYEnd() - 1)) {
                        points++;
                    }
                } else if (polygon.contains(other.getX() + 1, other.getYEnd() - 1)) {
                    points++;
                }
                if (points == 2) {
                    other.addShadowType(DARK);
                    DEBUG("Round Darkness...");
                } else if (source.getY() > other.getYEnd() && current.getY() == other.getYEnd() &&
                        ((source.getX() < current.getX() && current.getXEnd() == other.getX())
                                || (source.getX() > current.getXEnd() && current.getX() == other.getXEnd()))) {
                    other.addShadowType(DARK);
                    DEBUG("Round Darkness... - corner case");
                }
            }
            checked = true;
        }
    }

    private static void findObjectDarkness(Figure other, Figure current, Light source) {
        if (!checked) {
            if (other.getY() <= source.getY() && (other.getY() <= current.getY() || other.getYEnd() <= current.getYEnd())) {
                if (polygon.contains(other.getX() + 1, other.getYEnd() - 1) && polygon.contains(other.getXEnd() - 1, other.getYEnd() - 1)) {
                    other.addShadowType(DARK);
                    OBJECT_DEBUG("Object Darkness...");
                }
            }
            checked = true;
        }
    }

    private static void findRoundDarknessFromTop(Figure other, Figure current, Light source) {
        if (!checked) {
            if (current.getYEnd() >= source.getY() && (other.getYEnd() >= current.getY() || other.getYEnd() >= current.getYEnd())) {
                if (other.isLeftBottomRound()) {
                    if (polygon.contains(other.getX() + other.getPushValueOfCorner(LEFT_BOTTOM).getX() + 1,
                            other.getYEnd() - other.getPushValueOfCorner(LEFT_BOTTOM).getY() - 1)
                            || polygon.contains(other.getX() + other.getPushValueOfCorner(LEFT_BOTTOM).getX() - 1,
                            other.getYEnd() - other.getPushValueOfCorner(LEFT_BOTTOM).getY() + 1)) {
                        other.addShadowType(DARK);
                        DEBUG("LeftRounded Top Darkness...");
                    }
                } else if (polygon.contains(other.getX() + Place.tileSize - other.getPushValueOfCorner(RIGHT_BOTTOM).getX() - 1,
                        other.getYEnd() - other.getPushValueOfCorner(RIGHT_BOTTOM).getY() - 1)
                        || polygon.contains(other.getX() + Place.tileSize - other.getPushValueOfCorner(RIGHT_BOTTOM).getX() + 1,
                        other.getYEnd() - other.getPushValueOfCorner(RIGHT_BOTTOM).getY() + 1)) {
                    other.addShadowType(DARK);
                    DEBUG("RightRound Top Darkness...");
                }
            }
            checked = true;
        }
    }
}
