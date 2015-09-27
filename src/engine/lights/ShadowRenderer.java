/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Block;
import collision.Figure;
import collision.RoundRectangle;
import engine.utilities.BlueArray;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.ShadowContainer;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.map.Map;
import net.jodk.lang.FastMath;

import java.awt.*;
import java.util.Collections;

import static collision.OpticProperties.TRANSPARENT;
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

    private static final BlueArray<Figure> shades = new BlueArray<>(512);
    private static final Point center = new Point();
    private static final Point[] shadowPoints = {new Point(), new Point(), new Point(), new Point()};
    private static final Polygon polygon = new Polygon();
    private static final boolean DEBUG = false, OBJECT_DEBUG = false;
    private static final ShadowContainer darkenSpots = new ShadowContainer(), brightenSpots = new ShadowContainer();
    private static boolean checked;
    private static int shX, shY, xc, yc, range, XL1, XL2, XR1, XR2, lightHeightHalf, lightWidthHalf, points;
    private static double angle, al, bl, ar, br, as, bs, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Shadow tempShadow, minShadow, maxShadow;
    private static Figure tempShade;
    private static engine.utilities.Point tempPoint, casting = new Point();

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

    public static void preRenderLight(Map map, Light light) {
        findShades(light, map);
        prepareToPreRender(light);
        calculateShadows(light);
        renderShadows(light);
        endPreRender(light);
    }

    private static void findShades(Light light, Map map) {
        shades.clear();
        searchBlocks(light, map);
        searchObjects(light, map);
        Collections.sort(shades);
    }

    private static void searchBlocks(Light light, Map map) {
        for (Block block : map.getArea(light.getX(), light.getY()).getNearBlocks()) {
            tempShade = block.getCollision();
            if (tempShade != null && tempShade.getType() != TRANSPARENT) {
                if (tempShade.getY() - FastMath.abs(tempShade.getShadowHeight()) - Place.tileSize <= light.getY() + lightHeightHalf
                        && tempShade.getYEnd() >= light.getY() - lightHeightHalf
                        && tempShade.getX() <= light.getX() + lightWidthHalf
                        && tempShade.getXEnd() >= light.getX() - lightWidthHalf) {
                    tempShade.setLightDistance(FastMath.abs(tempShade.getXCentral() - light.getX()));
                    shades.add(tempShade);
                }
                for (Figure top : block.getTop()) {
                    if (top != null && !top.isLitable() && top.getX() <= light.getX() + lightWidthHalf && top.getXEnd() >= light.getX() - lightWidthHalf
                            && top.getY() - 2 * FastMath.abs(tempShade.getShadowHeight()) - tempShade.getHeight() <= light.getY() + lightHeightHalf) {
                        shades.add(top);
                    }
                }
            }
        }
    }

    private static void searchObjects(Light light, Map map) {
        for (GameObject object : map.getArea(light.getX(), light.getY()).getNearDepthObjects()) {
            tempShade = object.getCollision();
            if (tempShade != null && tempShade.isLitable()
                    && object.getY() - tempShade.getActualHeight() + tempShade.getHeight() / 2 <= light.getY() + lightHeightHalf
                    && object.getY() + tempShade.getActualHeight() - tempShade.getHeight() / 2 >= light.getY() - lightHeightHalf
                    && object.getX() - tempShade.getActualWidth() / 2 <= light.getX() + lightWidthHalf
                    && object.getX() + tempShade.getActualWidth() / 2 >= light.getX() - lightWidthHalf) {
                shades.add(tempShade);
            }
        }
    }

    private static void prepareToPreRender(Light light) {
        lightHeightHalf = light.getHeight() >> 1;
        lightWidthHalf = light.getWidth() >> 1;
        light.getFrameBufferObject().activate();
        clearScreen(1);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void calculateShadows(Light light) {
        shades.stream().forEach((shaded) -> {
                    if (shaded != light.getOwnerCollision()) {
                        if (shaded.isGiveShadow()) {
                            calculateShadowAndWalls(light, shaded);
                            if (shaded instanceof RoundRectangle) {
                                calculateAndDrawSelfShadow(light, (RoundRectangle) shaded);
                            }
                        }
                        calculateShadowShade(shaded, light);
                    } else {
                        shaded.addShadowType(BRIGHT);
                    }
                }
        );
    }

    private static void calculateShadowAndWalls(Light light, Figure shaded) {
        calculateShadow(light, shaded);
        if (shaded.isConcave()) {
            drawShadowFromConcave(light, (RoundRectangle) shaded, shadowPoints);
        } else {
            drawShadow(light, shadowPoints);
        }
        calculateVerticalShadows(shaded, light);
    }

    private static void calculateShadow(Light source, Figure thisShade) {
        findPoints(source, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
        setPolygonShape();
    }

    private static void findPoints(Light source, Figure thisShade) {
        center.set(source.getX(), source.getY());
        Methods.getCastingPointsIndexes(center.getX(), center.getY(), thisShade, casting);
        shadowPoints[0] = thisShade.getPoint(casting.getFirst());
        shadowPoints[1] = thisShade.getPoint(casting.getSecond());
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

    private static void setPolygonShape() {
        polygon.reset();
        polygon.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
        polygon.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
        polygon.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
        polygon.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
    }

    private static void calculateAndDrawSelfShadow(Light emitter, RoundRectangle shaded) {
        if (shaded.isConcave()) {
            calculateConcaveSelfShadow(emitter, shaded);
        } else if (!shaded.isTriangular()) {
            calculateConvexSelfShadow(emitter, shaded);
        }
    }

    private static void calculateConcaveSelfShadow(Light emitter, RoundRectangle shaded) {
        if (shaded.isLeftBottomRound()) {
            if ((emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() > shaded.getYEnd())) {
                calculateConcaveLeftFromBottomSelfShadow(emitter, shaded);
            } else if ((emitter.getX() < shaded.getX() && emitter.getY() < shaded.getYEnd() - Place.tileSize)) {
                calculateConcaveLeftFromTopSelfShadow(emitter, shaded);
            } else if (emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() <= shaded.getYEnd()) {
                shaded.addShadowType(DARK);
            }
        } else if (shaded.isRightBottomRound()) {
            if ((emitter.getX() < shaded.getX() && emitter.getY() > shaded.getYEnd())) {
                calculateConcaveRightFromBottomSelfShadow(emitter, shaded);
            } else if (emitter.getX() > shaded.getX() + Place.tileSize && emitter.getY() < shaded.getYEnd() - Place.tileSize) {
                calculateConcaveRightFromTopSelfShadow(emitter, shaded);
            } else if (emitter.getX() < shaded.getX() && emitter.getY() <= shaded.getYEnd()) {
                shaded.addShadowType(DARK);
            }
        }
    }

    private static void calculateConcaveLeftFromBottomSelfShadow(Light emitter, RoundRectangle shaded) {
        as = (emitter.getY() - shaded.getYEnd()) / (double) (emitter.getX() - shaded.getX() - Place.tileSize);
        bs = shaded.getYEnd() - as * (shaded.getX() + Place.tileSize);
        yc = shaded.getYEnd() - Place.tileSize;
        xc = Methods.roundDouble((yc - bs) / as);
        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
            drawLeftConcaveBottom(emitter, shaded, xc, yc);
        }
        as *= as;
        xc = Methods.roundDouble((Place.tileSize * (as - 1)) / (1 + as));
        if (xc <= 0) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, xc, Place.tileSize);
        }
        DEBUG("SelfShadow Left From Bottom");
    }

    private static void calculateConcaveLeftFromTopSelfShadow(Light emitter, RoundRectangle shaded) {
        as = (emitter.getY() - shaded.getYEnd() + Place.tileSize) / (double) (emitter.getX() - shaded.getX());
        bs = shaded.getYEnd() - Place.tileSize - as * shaded.getX();
        xc = shaded.getX() + Place.tileSize;
        yc = Methods.roundDouble(as * xc + bs);
        if (yc >= shaded.getYEnd() - Place.tileSize) {
            drawConcaveTop(emitter, shaded, xc, yc);
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

    private static void calculateConcaveRightFromBottomSelfShadow(Light emitter, RoundRectangle shaded) {
        as = (emitter.getY() - shaded.getYEnd()) / (double) (emitter.getX() - shaded.getX());
        bs = shaded.getYEnd() - as * shaded.getX();
        yc = shaded.getYEnd() - Place.tileSize;
        xc = Methods.roundDouble((yc - bs) / as);
        if (xc >= shaded.getX() && xc <= shaded.getX() + Place.tileSize) {
            drawRightConcaveBottom(emitter, shaded, xc, yc);
        }
        xc = Methods.roundDouble((2 * Place.tileSize) / (1 + as * as));
        if (xc >= Place.tileSize) {
            shaded.addShadowType(DARK);
        } else {
            shaded.addShadow(DARKEN, 0, xc);
        }
        DEBUG("SelfShadow Right From Bottom");
    }

    private static void calculateConcaveRightFromTopSelfShadow(Light emitter, RoundRectangle shaded) {
        as = (emitter.getY() - shaded.getYEnd() + Place.tileSize) / (double) (emitter.getX() - shaded.getX() - Place.tileSize);
        bs = shaded.getYEnd() - Place.tileSize - as * (shaded.getX() + Place.tileSize);
        xc = shaded.getX();
        yc = Methods.roundDouble(as * xc + bs);
        if (yc >= shaded.getYEnd() - Place.tileSize) {
            drawConcaveTop(emitter, shaded, xc, yc);
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

    private static void calculateConvexSelfShadow(Light emitter, RoundRectangle shaded) {
        range = 50;
        if (shaded.isLeftBottomRound()) {
            calculateConvexLeftSelfShadow(emitter, shaded);
        } else if (shaded.isRightBottomRound()) {
            calculateConvexRightSelfShadow(emitter, shaded);
        }
    }

    private static void calculateConvexLeftSelfShadow(Light emitter, RoundRectangle shaded) {
        angle = 45 - Methods.pointAngle(emitter.getX(), emitter.getY(),
                shaded.getX() + shaded.getPushValueOfCorner(LEFT_BOTTOM).getX(),
                shaded.getYEnd() - shaded.getPushValueOfCorner(LEFT_BOTTOM).getY());
        if (angle > 0) {
            if (angle < range) {
                int shift = Methods.roundDouble((angle / range) * Place.tileSize);
                shaded.addShadow(DARKEN, shift, Place.tileSize);
            } else {
                range = 68;
                if (angle > 222 - range && angle <= 222) {
                    int shift = Methods.roundDouble(((angle - 222 + range) / (range)) * Place.tileSize);
                    shaded.addShadow(DARKEN, 0, shift);
                }
            }
        }
    }

    private static void calculateConvexRightSelfShadow(Light emitter, RoundRectangle shaded) {
        angle = -135 + Methods.pointAngleClockwise(emitter.getX(), emitter.getY(),
                shaded.getX() + Place.tileSize - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getX(),
                shaded.getYEnd() - shaded.getPushValueOfCorner(RIGHT_BOTTOM).getY());
        if (angle > 0) {
            if (angle < range) {
                int shift = Methods.roundDouble(((range - angle) / range) * Place.tileSize);
                shaded.addShadow(DARKEN, 0, shift);
            } else {
                range = 68;
                if (angle > (222 - range) && angle <= 222) {
                    int shift = Methods.roundDouble(((222 - angle) / (range)) * Place.tileSize);
                    shaded.addShadow(DARKEN, shift, Place.tileSize);
                }
            }
        }
    }

    private static void renderShadows(Light light) {
        glEnable(GL_TEXTURE_2D);
        shades.stream().forEach((shaded) -> {
            solveShadows(shaded);
            drawAllShadows(light, shaded);
            shaded.clearShadows();
        });
    }

    private static void endPreRender(Light light) {
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        light.render(displayHeight);
        light.getFrameBufferObject().deactivate();
    }

    private static void calculateShadowShade(Figure shaded, Light light) {
        if (shaded.isLitable()) {
            if (shaded instanceof RoundRectangle) {
                calculateRoundShade((RoundRectangle) shaded, light);
            } else {
                calculateRegularShade(shaded, light);
            }
        } else {
            shaded.addShadowType(DARK);
        }
    }

    private static void calculateRoundShade(RoundRectangle shaded, Light light) {
        if (isRoundInLight(shaded, light)) {
            shaded.getOwner().renderShadowLit((light.getXCenterShift()) - (light.getX()),
                    (light.getYCenterShift()) - (light.getY()) + displayHeight - light.getHeight(), shaded);
            shaded.addShadowType(BRIGHT);
        } else {
            shaded.addShadowType(DARK);
        }
    }

    private static boolean isRoundInLight(RoundRectangle shaded, Light light) {
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

    private static boolean isTriangularInLight(RoundRectangle shaded, Light light) {
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

    private static boolean isConcaveConvexInLight(RoundRectangle shaded, Light light) {
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

    private static void calculateRegularShade(Figure shaded, Light emitter) {
        if (emitter.getY() > shaded.getYEnd()) {
            shaded.getOwner().renderShadowLit(emitter.getXCenterShift() - emitter.getX(),
                    (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shaded);
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
        for (int i = 0; i < brightenSpots.size(); i++) {
            tempPoint = brightenSpots.get(i).point;
            if (tempPoint.getX() < tempPoint.getY()) {
                if (tempPoint.getX() > maxValue) {
                    maxValue = tempPoint.getX();
                    maxShadow = brightenSpots.get(i);
                }
            } else if (tempPoint.getX() > tempPoint.getY()) {
                if (minValue > tempPoint.getX()) {
                    minValue = tempPoint.getX();
                    minShadow = brightenSpots.get(i);
                }
            } else if (tempPoint.getX() == Place.tileSize) {
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
            shaded.addShadow(minShadow.type, minShadow.point.getX(), minShadow.point.getY());
        }
        if (maxShadow != null) {
            shaded.addShadow(maxShadow.type, maxShadow.point.getX(), maxShadow.point.getY());
        }
    }

    private static void calculateVerticalShadows(Figure current, Light source) {
        shades.stream().filter((other) -> (other != current && other != source.getOwnerCollision())).forEach((other) -> {
            checked = false;
            if (other.getOwner() instanceof Block) {
                if (other instanceof RoundRectangle && other.isBottomRounded()) {
                    if (other.getYEnd() < source.getY()) {
                        calculateRoundBlockFromBottomVertical((RoundRectangle) other, current, source);
                    } else {
                        calculateRoundBlockFromTopVertical((RoundRectangle) other, current, source);
                    }
                } else if (current.getY() <= source.getY() && current.getYEnd() != other.getYEnd()) {
                    calculateRegularBlockVertical(other, current, source);
                }
            } else if (other.isLitable()) {
                calculateObjectVertical(other, current, source);
            }
        });
    }

    private static void calculateRoundBlockFromBottomVertical(RoundRectangle other, Figure current, Light source) {
        calculateIntersectionBounds(other);
        if (shouldCalculateLeftRoundBlock(other)) {
            calculateLeftRoundBlock(other, current, source);
        }
        if (shouldCalculateRightRoundBlock(other)) {
            calculateRightRoundBlock(other, current, source);
        }
        findRoundDarkness(other, current);
    }

    private static boolean shouldCalculateLeftRoundBlock(RoundRectangle other) {
        return (shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight())
                && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd())))
                || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()));
    }

    private static boolean shouldCalculateRightRoundBlock(RoundRectangle other) {
        return (shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight())
                && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd())))
                || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()));
    }

    private static void calculateRoundBlockFromTopVertical(RoundRectangle other, Figure current, Light source) {
        calculateIntersectionBounds(other);
        if (shouldCalculateLeftRoundBlockFromTop(other)) {
            calculateLeftRoundBlockFromTop(other, current, source);
        }
        if (shouldCalculateRightRoundBlockFromTop(other)) {
            calculateRightRoundBlockFromTop(other, current, source);
        }
        findRoundDarknessFromTop(other, current, source);
    }

    private static boolean shouldCalculateLeftRoundBlockFromTop(RoundRectangle other) {
        return (shadowPoints[0].getY() < shadowPoints[2].getY() && shadowPoints[0].getY() < other.getYEnd())
                && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))));
    }

    private static boolean shouldCalculateRightRoundBlockFromTop(RoundRectangle other) {
        return (shadowPoints[1].getY() < shadowPoints[3].getY() && shadowPoints[1].getY() < other.getYEnd())
                && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))));
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
        return (shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight())
                && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd())
                || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd())
                || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd())))
                || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()));
    }

    private static boolean shouldCalculateRightBlock(Figure other) {
        return (shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight())
                && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd())
                || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd())
                || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd())))
                || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()));
    }

    private static void calculateIntersectionBounds(Figure other) {
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
        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
            XOL = shadowPoints[0].getX();
        } else {
            XOL = ((other.getYEnd() - bl) / al);
            YOL = (al * other.getXSpriteBegin() + bl);
            YOL2 = (al * other.getXSpriteEnd() + bl);
        }
        return (shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY())
                && ((shadowPoints[0].getX() != shadowPoints[2].getX()
                && ((XOL >= other.getXSpriteBegin() && XOL <= other.getXSpriteEnd())
                || (YOL > other.getYSpriteBegin() && YOL < other.getYEnd()) || (YOL2 > other.getYSpriteBegin() && YOL2 < other.getYEnd())))
                || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getXSpriteBegin() && XOL <= other.getXSpriteEnd()));
    }

    private static boolean shouldCalculateRightObject(Figure other) {
        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
            XOR = shadowPoints[1].getX();
        } else {
            XOR = ((other.getYEnd() - br) / ar);
            YOR = (ar * other.getXSpriteBegin() + br);
            YOR2 = (ar * other.getXSpriteEnd() + br);
        }
        return (shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY())
                && ((shadowPoints[1].getX() != shadowPoints[3].getX()
                && ((XOR >= other.getXSpriteBegin() && XOR <= other.getXSpriteEnd())
                || (YOR > other.getYSpriteBegin() && YOR < other.getYEnd()) || (YOR2 > other.getYSpriteBegin() && YOR2 < other.getYEnd())))
                || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getXSpriteBegin() && XOR <= other.getXSpriteEnd()));
    }

    private static void calculateLeftBlock(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadowPoints[0].getY() > other.getYEnd()
                && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setLeftRoundVariables(other, source);
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if (shouldAddLightLeftBlock(other, current, source)) {
                    addLightLeftBLock(other, current, source);
                } else {
                    addShadowLeftBlock(other, current);
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj
                // zaciemniony
                addDarkenLeftBlock(other, current, source);
            }
        }
    }

    private static void setLeftRoundVariables(Figure other, Light source) {
        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
            XL1 = shadowPoints[0].getX();
            XL2 = other.getXEnd();
        } else {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
            XL2 = XL1 <= source.getX() ? other.getX() : other.getXEnd();
        }
    }

    private static boolean shouldAddLightLeftBlock(Figure other, Figure current, Light source) {
        return (XL1 < source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX()
                && source.getY() < current.getYEnd() && shadowPoints[0].getX() < shadowPoints[2].getX())
                || (XL1 == other.getXEnd() && XL1 == current.getX()));
    }

    private static void addLightLeftBLock(Figure other, Figure current, Light source) {
        addBrightenLeft(other);
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
            addDarkenLeft(other);
        }
        DEBUG("Left Light");
    }

    private static void addShadowLeftBlock(Figure other, Figure current) {
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
        addDarkenLeft(other);
        DEBUG("Left Shade " + al + " XL1 " + XL1 + " figure.X " + current.getX());
    }

    private static void addDarkenLeftBlock(Figure other, Figure current, Light source) {
        YOL = al * other.getX() + bl;
        YOL2 = al * other.getXEnd() + bl;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX())
                || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd())
                || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd()))) {
            if (XL1 < current.getX()) {
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
            other.addShadow(BRIGHTEN_OBJECT, XL1 - other.getXSpriteBegin(), XL2);
        }
    }

    private static void addDarkenLeft(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(DARKEN, XL1, XL2);
        } else {
            other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(), XL2);
        }
    }

    private static void calculateRightBlock(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && shadowPoints[1].getY() > other.getYEnd()
                && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setRightBlockVariables(other, source);
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if (shouldAddLightRightBlock(other, current, source)) { // dodaj światło
                    addLightRightBLock(other, current, source);
                } else {
                    addShadowRightBlock(other, current);
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadowPoints[1].getX() != shadowPoints[3].getX()) { //
                // rysuj zaciemniony
                addDarkenRightBlock(other, current, source);
            }
        }
    }

    private static void setRightBlockVariables(Figure other, Light source) {
        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
            XR1 = shadowPoints[1].getX();
            XR2 = other.getX();
        } else {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar);
            XR2 = XR1 < source.getX() ? other.getX() : other.getXEnd();
        }
    }

    private static boolean shouldAddLightRightBlock(Figure other, Figure current, Light source) {
        return (XR1 > source.getX() || (current.getX() < source.getX() && current.getXEnd() > source.getX()
                && source.getY() < current.getYEnd() && shadowPoints[1].getX() > shadowPoints[3].getX())
                || (XR1 == other.getX() && XR1 == current.getXEnd()));
    }

    private static void addLightRightBLock(Figure other, Figure current, Light source) {
        addBrightenRight(other);
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
            addDarkenRight(other);
        }
        DEBUG("Right Light XR1 " + XR1);
    }

    private static void addShadowRightBlock(Figure other, Figure current) {
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
        addDarkenRight(other);
        DEBUG("Right Shade " + ar + " XR1 " + XR1 + " figure.X " + current.getX());
    }

    private static void addDarkenRightBlock(Figure other, Figure current, Light source) {
        YOR = ar * other.getX() + br;
        YOR2 = ar * other.getXEnd() + br;
        if (((source.getX() != current.getXEnd() && source.getX() != current.getX())
                || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd())
                || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd()))) {
            if (XR1 > current.getXEnd()) { // check this shitty condition
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
            other.addShadow(BRIGHTEN_OBJECT, XR1 - other.getXSpriteBegin(), XR2);
        }
    }

    private static void addDarkenRight(Figure other) {
        if (other.getOwner() instanceof Block) {
            other.addShadow(DARKEN, XR1, XR2);
        } else {
            other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(), XR2);
        }
    }

    private static void calculateLeftRoundBlock(RoundRectangle other, Figure current, Light source) {
        if (shouldCalculateLeftRoundBlock(other, current, source)) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getXEnd();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii      
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX())
                        || (XL1 > other.getX() && XL1 < other.getXEnd())) {
                    tempPoint = Methods.getXIntersection(al, bl, shadowPoints[0].getX(), shadowPoints[0].getY(), shadowPoints[2].getX(), shadowPoints[2].getY
                            (), other);
                    if (tempPoint != null && other.getYEnd() <= current.getYEnd() && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY()
                            <= other.getYEnd()) {
                        XL1 = tempPoint.getX();
                    } else {
                        findRoundDarkness(other, current);
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
                } else if (shadowPoints[0].getY() >= other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX())
                        || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX());
                    checked = true;
                    DEBUG("Left Round Shade XL1 " + (XL1 - other.getX()) + " XL2 " + (XL2 - other.getX()));
                }
            } else if (XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj
                // zaciemniony
                addLightnessLeftRoundBlock(other, current, source);
            }
        }
    }

    private static void addDarkenLeftRoundBlockIfNeeded(RoundRectangle other, Figure current) {
        if (XL1 != other.getXEnd()) {
            if (al <= 0 || (XL1 == other.getX() && other.getYEnd() < current.getYEnd())) {
                XL2 = other.getXEnd();
            } else {
                XL2 = other.getXEnd();
            }
            other.addShadow(DARKEN, XL1 - other.getX(), XL2 - other.getX());
        }
    }

    private static boolean shouldCalculateLeftRoundBlock(RoundRectangle other, Figure current, Light source) {
        return other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd())
                || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX())
                || (other.isRightBottomRound() && other.getX() < current.getX()))));
    }

    private static void addLightnessLeftRoundBlock(RoundRectangle other, Figure current, Light source) {
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

    private static void calculateRightRoundBlock(RoundRectangle other, Figure current, Light source) {
        if (shouldCalculateRightRoundBlock(other, current, source)) {
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
                if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX())
                        || (XR1 > other.getX() && XR1 < other.getXEnd())) {
                    tempPoint = Methods.getXIntersection(ar, br, shadowPoints[1].getX(), shadowPoints[1].getY(), shadowPoints[3].getX(), shadowPoints[3].getY
                            (), other);
                    if (tempPoint != null && other.getYEnd() <= current.getYEnd() && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY()
                            <= other.getYEnd()) {
                        XR1 = tempPoint.getX();
                    } else {
                        findRoundDarkness(other, current);
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
                } else if (shadowPoints[1].getY() >= other.getYEnd() && ((source.getX() >= current.getX() && current.getXEnd() >= other.getX())
                        || (source.getX() <= current.getXEnd() && current.getX() <= other.getXEnd()))) { //dodaj cień
                    other.addShadow(DARKEN, XR1 - other.getX(), XR2 - other.getX());
                    checked = true;
                    DEBUG("Right Round Shade XR1 " + (XR1 - other.getX()) + " XR2 " + (XR2 - other.getX()));
                }
            } else if (XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && shadowPoints[1].getX() != shadowPoints[3].getX()) {
                addLightnessRightRoundBlock(other, current, source);
            }
        }
    }

    private static void addDarkenRightRoundBlockIfNeeded(RoundRectangle other, Figure current) {
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

    private static boolean shouldCalculateRightRoundBlock(RoundRectangle other, Figure current, Light source) {
        return other != null && other.getYEnd() < source.getY() && ((other.getYEnd() < current.getYEnd())
                || (other.getYEnd() - Place.tileSize <= current.getYEnd() && ((other.isLeftBottomRound() && other.getX() > current.getX())
                || (other.isRightBottomRound() && other.getX() < current.getX()))));
    }

    private static void addLightnessRightRoundBlock(RoundRectangle other, Figure current, Light source) {
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

    private static void calculateLeftRoundBlockFromTop(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XL1 > other
                    .getX() && XL1 < other.getXEnd())) {
                tempPoint = Methods.getXIntersectionFromTop(al, bl, shadowPoints[0].getX(), shadowPoints[0].getY(), shadowPoints[2].getX(), shadowPoints[2]
                        .getY(), other);
                if (tempPoint != null && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY() <= other.getYEnd()) {
                    XL1 = tempPoint.getX();
                } else {
                    if ((XL1 < other.getX() || XL1 > other.getXEnd()) && shadowPoints[3].getY() > current.getYEnd() && shadowPoints[2].getY() > current
                            .getYEnd() && other.getYEnd() >= current.getYEnd()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadowPoints[1].getX() < current.getX() && YOL2 >= other
                            .getYEnd() - Place.tileSize && YOL2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadowPoints[3].getX() > current.getYEnd() && YOL >= other
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

    private static void addProperShadowLeftRoundBlock(RoundRectangle other, Figure current, Light source) {
        if (XL1 > other.getX() && XL1 < other.getXEnd()) {
            if (((shadowPoints[2].getY() > current.getYEnd() && shadowPoints[3].getY() > current.getYEnd()))) { // dodaj światło
                other.addShadow(DARKEN, XL1 - other.getX(), XL1 < current.getX() ? 0 : Place.tileSize);
                DEBUG("Left Round Top Light XL1: " + (XL1 - other.getX()));
                checked = true;
            } else if (shadowPoints[2].getY() > current.getYEnd()) { //dodaj cień
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

    private static void calculateRightRoundBlockFromTop(RoundRectangle other, Figure current, Light source) {
        if (other != null && other.getYEnd() > source.getY() && other.getYEnd() >= current.getY()
                && ((other.isLeftBottomRound() && other.getX() > current.getX()) || (other.isRightBottomRound() && other.getX() < current.getX()))) {
            XR1 = Methods.roundDouble((other.getYEnd() - br) / ar); // liczenie przecięcia linii
            if ((other.isRightBottomRound() && other.getX() < current.getX()) || (other.isLeftBottomRound() && other.getX() > current.getX()) || (XR1 > other
                    .getX() && XR1 < other.getXEnd())) {
                tempPoint = Methods.getXIntersectionFromTop(ar, br, shadowPoints[1].getX(), shadowPoints[1].getY(), shadowPoints[3].getX(), shadowPoints[3]
                        .getY(), other);
                if (tempPoint != null && tempPoint.getY() >= other.getYEnd() - Place.tileSize && tempPoint.getY() <= other.getYEnd()) {
                    XR1 = tempPoint.getX();
                } else {
                    if ((XR1 < other.getX() || XR1 > other.getXEnd()) && shadowPoints[3].getY() > current.getYEnd() && shadowPoints[2].getY() > current
                            .getYEnd() && other.getYEnd() >= current.getYEnd() && current.getX() != other.getXEnd() && current.getXEnd() != other.getX()
                            && ((other.isRightBottomRound() && other.getX() < current.getX() && shadowPoints[3].getX() < current.getX() && YOR2 >= other
                            .getYEnd() - Place.tileSize && YOR2 <= other.getYEnd())
                            || (other.isLeftBottomRound() && other.getX() > current.getX() && shadowPoints[3].getX() > current.getYEnd() && YOR >= other
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

    private static void addProperShadowRightRoundBlock(RoundRectangle other, Figure current, Light source) {
        if (XR1 > other.getX() && XR1 < other.getXEnd()) {
            if (((shadowPoints[2].getY() > current.getYEnd() && shadowPoints[3].getY() > current.getYEnd()))) { // dodaj światło
                other.addShadow(DARKEN, XR1 - other.getX(), XR1 > current.getXEnd() ? Place.tileSize : 0);
                DEBUG("Right Round Top Light");
                checked = true;
            } else if (shadowPoints[3].getY() > current.getYEnd()) { //dodaj cień
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
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setLeftObjectVariables(other, current);
            if (XL1 >= other.getXSpriteBegin() && XL1 < other.getXSpriteEnd()) {
                if (XL1 < current.getX()) { //dodaj światło
                    other.addShadow(BRIGHTEN_OBJECT, XL1 - other.getXSpriteBegin(), XL2);
                    XL2 = current.getX() <= XL1 ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
                    other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(), XL2);
                    OBJECT_DEBUG("Object Left Light " + (XL1 - other.getXSpriteBegin()) + " XL2 " + XL2);
                    checked = true;
                } else { //dodaj cień
                    if (shadowPoints[3].getX() > shadowPoints[2].getX() || shadowPoints[3].getY() < shadowPoints[2].getY()) {
                        XL2 = other.getXSpriteOffsetWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XL1 - other.getXSpriteBegin(), XL2);
                    OBJECT_DEBUG("Object Left Shade " + (XL1 - other.getXSpriteBegin()) + " XL2 " + XL2);
                    checked = true;
                }
            }
        }
    }

    private static void setLeftObjectVariables(Figure other, Figure current) {
        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
            XL1 = shadowPoints[0].getX();
            XL2 = other.getXSpriteOffsetWidth();
        } else {
            XL1 = Methods.roundDouble((other.getYEnd() - bl) / al);
            XL2 = current.getX() > XL1 ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
        }
    }

    private static void calculateRightObject(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            setRightObjectVariables(other, current);
            if (XR1 >= other.getXSpriteBegin() && XR1 <= other.getXSpriteEnd()) {
                if (XR1 > current.getXEnd()) { // dodaj światło                    
                    other.addShadow(BRIGHTEN_OBJECT, XR1 - other.getXSpriteBegin(), XR2);
                    XR2 = XR1 > current.getXEnd() ? other.getXSpriteOffset() : other.getXSpriteOffsetWidth();
                    other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(), XR2);
                    OBJECT_DEBUG("Object Right Light XR1 " + (XR1 - other.getXSpriteBegin()) + " XR2 " + XR2 + other.getActualWidth() + " " + other
                            .getXSpriteOffset());
                    checked = true;
                } else { //dodaj cień
                    if (shadowPoints[3].getX() < shadowPoints[2].getX() || shadowPoints[3].getY() > shadowPoints[2].getY()) {
                        XR2 = other.getXSpriteOffsetWidth();
                    }
                    other.addShadow(DARKEN_OBJECT, XR1 - other.getXSpriteBegin(), XR2);
                    OBJECT_DEBUG("Object Right Shade XR1 " + (XR1 - other.getXSpriteBegin()) + " XR2 " + XR2);
                    checked = true;
                }
            }
        }
    }

    private static void setRightObjectVariables(Figure other, Figure current) {
        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
            XR1 = shadowPoints[1].getX();
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
                    points = 0;
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
                    && ((shadowPoints[0].getY() == other.getYEnd() && shadowPoints[0].getY() >= source.getY() - 2
                    && shadowPoints[0].getY() <= source.getY() + 2) || (shadowPoints[1].getY() == other.getYEnd()
                    && shadowPoints[1].getY() >= source.getY() - 2 && shadowPoints[1].getY() <= source.getY() + 2))) {
                other.addShadowType(DARK);
                DEBUG("Darkness other...");
            }
        }
    }

    private static void findRoundDarkness(RoundRectangle other, Figure current) {
        if (!checked) {
            if ((current.getYEnd() != other.getYEnd()) || (!current.isBottomRounded() && current.getYEnd() - Place.tileSize != other.getYEnd() && ((other
                    .isLeftBottomRound() && other.getX() == current.getXEnd()) || (other.isRightBottomRound() && other.getXEnd() == current.getX()))) ||
                    (other.getX() != current.getXEnd() && other.getX() + Place.tileSize != current.getX()) || (current instanceof RoundRectangle && ((current
                    .getX() < other.getX() && other.isLeftBottomRound()) || (current.getX() > other.getX() && other.isRightBottomRound())))) {
                points = 0;
                if (polygon.contains(other.getX() + 2, other.getYEnd() - Place.tileSize + 1)) {
                    points++;
                } else if (polygon.contains(other.getXEnd() - 2, other.getYEnd() - Place.tileSize + 1)) {
                    points++;
                }
                if (other.isLeftBottomRound()) {
                    if (polygon.contains(other.getXEnd() - 1, other.getYEnd() - 2)) {
                        points++;
                    }
                } else if (polygon.contains(other.getX() + 1, other.getYEnd() - 2)) {
                    points++;
                }
                if (points == 2) {
                    other.addShadowType(DARK);
                    DEBUG("Round Darkness...");
                }
            }
            checked = true;
        }
    }

    private static void findObjectDarkness(Figure other, Figure current, Light source) {
        if (!checked) {
            if (other.getY() <= source.getY() && (other.getY() <= current.getY() || other.getYEnd() <= current.getYEnd())) {
                if (polygon.contains(other.getOwner().getX(), other.getYEnd() - 1)) {
                    other.addShadowType(DARK);
                    OBJECT_DEBUG("Object Darkness...");
                }
            }
            checked = true;
        }
    }

    private static void findRoundDarknessFromTop(RoundRectangle other, Figure current, Light source) {
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
                } else if (polygon.contains(other.getX() + Place.tileSize - other.getPushValueOfCorner(RIGHT_BOTTOM).getX() + 1,
                        other.getYEnd() - other.getPushValueOfCorner(RIGHT_BOTTOM).getY() + 1)) {
                    other.addShadowType(DARK);
                    DEBUG("RightRound Top Darkness...");
                }
            }
            checked = true;
        }
    }
}
