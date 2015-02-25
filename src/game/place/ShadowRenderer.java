/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import collision.RoundRectangle;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
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

    private static final int displayWidth = Display.getWidth(), displayHeight = Display.getHeight();
    private static final ArrayList<Figure> shades = new ArrayList<>(4096);
    private static final Point center = new Point(0, 0);
    private static final Point[] shadowPoints = new Point[4];
    private static Figure tempShade, left, right;
    private static int firstShadowPoint, secondShadowPoint, shX, shY, XL1, XL2, XR1, XR2, shadowLength = 32768;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Shadow tempShadow;
    private static final Shadow shadow0 = new Shadow(0), shadow1 = new Shadow(1);
    private static final ArrayList<Shadow> shadowsDarken = new ArrayList<>(), shadowsBrighten = new ArrayList<>();
    private static final renderShadow[] shads = new renderShadow[6];
    private static float lightOwnerHeightHalf;
    private static boolean isChecked;
    private static final Polygon polygon = new Polygon();

    public static void prerenderLight(Map map, Light emitter) {
        findShades(emitter, map);
        emitter.getFrameBufferObject().activate();
        clearFBO(1);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);

        lightOwnerHeightHalf = (float) (emitter.getOwnerCollisionHeight() / 2);
        shades.stream().forEach((shade) -> {	//iteracja po Shades - tych co dają cień
            if (shade != emitter.getOwnerCollision()) {
                if (shade.isGiveShadow()) {
                    calculateShadow(emitter, shade);
                    drawShadow(emitter);
                    calculateWalls(shade, emitter);
                    if (shade.isLittable() && emitter.getY() >= shade.getYEnd()) {
                        shade.setShadowColor((emitter.getY() - shade.getYEnd()) / lightOwnerHeightHalf);
                        shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()),
                                (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade.getShadowColor(), shade);
                        shade.addShadow(shadow1);
                    } else {
                        shade.addShadow(shadow0);
                    }
                } else if (shade.isLittable() && emitter.getY() >= shade.getYEnd()) {
                    shade.setShadowColor((emitter.getY() - shade.getYEnd()) / lightOwnerHeightHalf);
                    shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()),
                            (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade.getShadowColor(), shade);
                    shade.addShadow(shadow1);
                } else {
                    shade.addShadow(shadow0);
                }
            } else {
                shade.setShadowColor(1);
                shade.addShadow(shadow1);
            }
        });

        for (Figure shade : shades) {
            solveShadows(shade);
            shade.getShadows().stream().forEach((shadowShade) -> {
                shads[shadowShade.type].render(emitter, shade, shadowShade.points);
            });
            shade.clearShadows();
        }
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        emitter.render(displayHeight - emitter.getHeight());
        emitter.getFrameBufferObject().deactivate();
    }

    private static void solveShadows(Figure shaded) {
        tempShadow = null;
        shadowsDarken.clear();
        shadowsBrighten.clear();
        for (Shadow shad : shaded.getShadows()) {
            switch (shad.type) {
                case DARK:
                    tempShadow = shad;
                    shaded.clearShadows();
                    shaded.addShadow(tempShadow);
                    return;
                case BRIGHT:
                    tempShadow = shad;
                    break;
                case DARKEN:
                case DARKEN_OBJECT:
                    shadowsDarken.add(shad);
                    break;
                case BRIGHTEN:
                case BRIGHTEN_OBJECT:
                    shadowsBrighten.add(shad);
            }
        }
        shaded.clearShadows();
        if (tempShadow != null && shadowsDarken.isEmpty()) {
            shaded.addShadow(tempShadow);
        }
        shadowsDarken.stream().forEach((shadow) -> {
            shaded.addShadow(shadow);
        });
        shadowsBrighten.stream().forEach((shadow) -> {
            shaded.addShadow(shadow);
        });
    }

    private static void findShades(Light source, Map map) {// TODO optimize source.getHeight *2 - poprawić - wielkość całego światła ma być!
        shades.clear();
        map.areas.stream().forEach((area) -> {
            tempShade = area.getCollision();
            if (tempShade != null
                    && (FastMath.abs(tempShade.getYCentral() - source.getY()) <= (source.getHeightWholeLight() + tempShade.getHeight()) / 2)
                    && (FastMath.abs(tempShade.getXCentral() - source.getX()) <= (source.getWidthWholeLight() + tempShade.getWidth()) / 2)) {
                shades.add(tempShade);
            }
            for (Figure top : area.getTop()) {
                if (top != null && !top.isLittable()
                        && (FastMath.abs(top.getYCentral() - source.getY()) <= (source.getHeightWholeLight() + top.getHeight()) / 2)
                        && (FastMath.abs(top.getXCentral() - source.getX()) <= (source.getWidthWholeLight() + top.getWidth()) / 2)) {
                    shades.add(top);
                }
            }
        });
        for (GameObject object : map.getForegroundTiles()) {
            tempShade = object.getCollision();
            if (tempShade != null && !tempShade.isLittable()
                    && (FastMath.abs(tempShade.getYCentral() - source.getY()) <= (source.getHeightWholeLight() + tempShade.getHeight()))
                    && (FastMath.abs(tempShade.getXCentral() - source.getX()) <= (source.getWidthWholeLight() + tempShade.getWidth()))) {
                shades.add(tempShade);
            }
        }
        for (GameObject object : map.getDepthObjects()) {
            tempShade = object.getCollision();
            if (tempShade != null && tempShade.isLittable()
                    && ((FastMath.abs(tempShade.getOwner().getY() - source.getY()) <= (source.getHeightWholeLight() + tempShade.getOwner().getCollisionHeight()) / 2)
                    && (FastMath.abs(tempShade.getOwner().getX() - source.getX()) <= (source.getWidthWholeLight() + tempShade.getOwner().getCollisionWidth()) / 2))) {
                shades.add(tempShade);
            }
        }
        Collections.sort(shades);
    }

    private static void calculateShadow(Light source, Figure thisShade) {
        findPoints(source, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
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
    }

    private static void findLeftSideOfShadow() {
        if (shadowPoints[0].getX() == center.getX()) {
            shadowPoints[2].set(shadowPoints[0].getX(), shadowPoints[0].getY() + (shadowPoints[0].getY() > center.getY() ? shadowLength : -shadowLength));
        } else if (shadowPoints[0].getY() == center.getY()) {
            shadowPoints[2].set(shadowPoints[0].getX() + (shadowPoints[0].getX() > center.getX() ? shadowLength : -shadowLength), shadowPoints[0].getY());
        } else {
            al1 = (center.getY() - shadowPoints[0].getY()) / (double) (center.getX() - shadowPoints[0].getX());
            bl1 = shadowPoints[0].getY() - al1 * shadowPoints[0].getX();
            if (al1 > 0) {
                shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? shadowLength : -shadowLength);
                shY = (int) (al1 * shX + bl1);
            } else if (al1 < 0) {
                shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? -shadowLength : shadowLength);
                shY = (int) (al1 * shX + bl1);
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
            al2 = (center.getY() - shadowPoints[1].getY()) / (double) (center.getX() - shadowPoints[1].getX());
            bl2 = shadowPoints[1].getY() - al2 * shadowPoints[1].getX();
            if (al2 > 0) {
                shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? shadowLength : -shadowLength);
                shY = (int) (al2 * shX + bl2);
            } else if (al2 < 0) {
                shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? -shadowLength : shadowLength);
                shY = (int) (al2 * shX + bl2);
            } else {
                shX = shadowPoints[1].getX();
                shY = shadowPoints[1].getY() + (shadowPoints[1].getY() > center.getY() ? shadowLength : -shadowLength);
            }
            shadowPoints[3].set(shX, shY);
        }
    }

    private static void calculateWalls(Figure current, Light source) {
        left = right = null;
        for (Figure other : shades) {
            isChecked = false;
            if (current.getY() < source.getY() && other != current && other != source.getOwnerCollision()) {
                if (other.isGiveShadow() && current.getYEnd() != other.getYEnd()) {
                    if (current instanceof RoundRectangle) {
                        // TO DO obsługa rogów bloczków
                        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                            XOL = shadowPoints[0].getX();
                        } else {
                            XOL = ((other.getYEnd() - bl1) / al1);
                            YOL = (al1 * other.getX() + bl1);
                            YOL2 = (al1 * other.getXEnd() + bl1);
                        }
                        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                            XOR = shadowPoints[1].getX();
                        } else {
                            XOR = ((other.getYEnd() - bl2) / al2);
                            YOR = (al2 * other.getX() + bl2);
                            YOR2 = (al2 * other.getXEnd() + bl2);
                        }
                        if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()))) {
                            calculateLeftWall(other, current, source);
                        }
                        if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()))) {
                            calculateRightWall(other, current, source);
                        }
                        findDarkness(other, current, source);
                    } else {
                        if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                            XOL = shadowPoints[0].getX();
                        } else {
                            XOL = ((other.getYEnd() - bl1) / al1);
                            YOL = (al1 * other.getX() + bl1);
                            YOL2 = (al1 * other.getXEnd() + bl1);
                        }
                        if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                            XOR = shadowPoints[1].getX();
                        } else {
                            XOR = ((other.getYEnd() - bl2) / al2);
                            YOR = (al2 * other.getX() + bl2);
                            YOR2 = (al2 * other.getXEnd() + bl2);
                        }
                        if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getX() && XOL <= other.getXEnd()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getYEnd()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getYEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getX() && XOL <= other.getXEnd()))) {
                            calculateLeftWall(other, current, source);
                        }
                        if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()))) {
                            calculateRightWall(other, current, source);
                        }
                        findDarkness(other, current, source);
                    }
                } else if (other.isLittable() && !other.isGiveShadow()) {
                    if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                        XOL = shadowPoints[0].getX();
                    } else {
                        XOL = ((other.getYOwnerEnd() - bl1) / al1);
                        YOL = (al1 * other.getXOwnerBegin() + bl1);
                        YOL2 = (al1 * other.getXOwnerEnd() + bl1);
                    }
                    if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                        XOR = shadowPoints[1].getX();
                    } else {
                        XOR = ((other.getYOwnerEnd() - bl2) / al2);
                        YOR = (al2 * other.getXOwnerBegin() + bl2);
                        YOR2 = (al2 * other.getXOwnerEnd() + bl2);
                    }
                    if ((shadowPoints[0].getY() > shadowPoints[2].getY() && shadowPoints[0].getY() > other.getY()) && ((shadowPoints[0].getX() != shadowPoints[2].getX() && ((XOL >= other.getXOwnerBegin() && XOL <= other.getXOwnerEnd()) || (YOL > other.getYOwnerBegin() && YOL < other.getYOwnerEnd()) || (YOL2 > other.getYOwnerBegin() && YOL2 < other.getYOwnerEnd()))) || (shadowPoints[0].getX() == shadowPoints[2].getX() && XOL >= other.getXOwnerBegin() && XOL <= other.getXOwnerEnd()))) {
                        isChecked = true;
                        calculateLeftObject(other, current, source);
                    }
                    if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()) || (YOR > other.getYOwnerBegin() && YOR < other.getYOwnerEnd()) || (YOR2 > other.getYOwnerBegin() && YOR2 < other.getYOwnerEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()))) {
                        isChecked = true;
                        calculateRightObject(other, current, source);
                    }
                    findObjectDarkness(other, current, source);
                }
            }
        }
    }

    private static final boolean DEBUG = false;

    private static void calculateLeftWall(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getXEnd();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl1) / al1);
                XL2 = al1 > 0 ? other.getX() : other.getXEnd();
            }
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - bl2) / al2);
            }
            if (XL1 >= other.getX() && XL1 <= other.getXEnd()) {
                if (FastMath.abs(al1) > 0 && (XL1 < current.getX() || XL1 == other.getXEnd())) { //dodaj światło
                    tempShadow = new Shadow(2);
                    tempShadow.addPoints(new Point(XL1, other.getY() - other.getShadowHeight()), new Point(XL1, other.getYEnd()),
                            new Point(XL2, other.getYEnd()), new Point(XL2, other.getY() - other.getShadowHeight()));
                    other.addShadow(tempShadow);
                    if (DEBUG) {
                        System.out.println("Left Light");
                    }
                } else { //dodaj cień
                    if (XR1 < XL2 && XR1 > other.getX() && shadowPoints[3].getY() < current.getYEnd()) {
                        XL2 = XR1;
                    }
                    tempShadow = new Shadow(3);
                    tempShadow.addPoints(new Point(XL1, other.getYEnd()), new Point(XL1, other.getY() - other.getShadowHeight()),
                            new Point(XL2, other.getY() - other.getShadowHeight()), new Point(XL2, other.getYEnd()));
                    other.addShadow(tempShadow);
                    if (DEBUG) {
                        System.out.println("Left Shade " + al1 + " XL1 " + XL1 + " figure.X " + current.getX());
                    }
                }
            } else if (shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
                YOL = Methods.roundDouble(al1 * other.getX() + bl1);
                YOL2 = Methods.roundDouble(al1 * other.getXEnd() + bl1);
                if ((XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOL > other.getY() - other.getShadowHeight() && YOL < other.getYEnd()) || (YOL2 > other.getY() - other.getShadowHeight() && YOL2 < other.getYEnd())) {
                    if (FastMath.abs(al1) >= 0 && XL1 < current.getX()) {
                        other.addShadow(shadow1);
                        if (DEBUG) {
                            System.out.println("Left Lightness - first");
                        }
                    } else {
                        other.addShadow(shadow0);
                        if (DEBUG) {
                            System.out.println("Left Darkness - first");
                        }
                    }
                }
            }
        }
    }

    private static void calculateLeftObject(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
                XL2 = other.getOwner().getStartX() + other.getWidth();
            } else {
                XL1 = Methods.roundDouble((other.getYOwnerEnd() - bl1) / al1);
                XL2 = al1 > 0 ? other.getOwner().getStartX() : other.getOwner().getStartX() + other.getWidth();
            }
            if (XL1 >= other.getXOwnerBegin() && XL1 <= other.getXOwnerEnd()) {
                if (FastMath.abs(al1) > 0 && XL1 < current.getX()) { //dodaj światło
                    tempShadow = new Shadow(4);
                    tempShadow.addPoints(new Point(XL1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XL2), null, null, null);
                    other.addShadow(tempShadow);
//                    System.out.println("Left Light");
                } else { //dodaj cień
                    tempShadow = new Shadow(5);
                    tempShadow.addPoints(new Point(XL1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XL2), null, null, null);
                    other.addShadow(tempShadow);
//                    System.out.println("Left Shade");
                }
            } else if (shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
                YOL = Methods.roundDouble(al1 * other.getXOwnerBegin() + bl1);
                YOL2 = Methods.roundDouble(al1 * other.getXOwnerEnd() + bl1);
                if ((XL1 != current.getX() && XL1 != other.getXOwnerBegin() && XL1 != other.getXOwnerEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOL > other.getYOwnerBegin() && YOL < other.getYOwnerEnd()) || (YOL2 > other.getYOwnerBegin() && YOL2 < other.getYOwnerEnd())) {
                    if (FastMath.abs(al1) >= 0 && XL1 < current.getX()) {
                        other.addShadow(shadow1);
                    } else {
                        other.addShadow(shadow0);
                    }
//                    System.out.println("Left Dark");
                }
            }
        }
    }

    private static void calculateRightWall(Figure other, Figure current, Light source) {
        if (other != null && other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getX();
            } else {
                XR1 = Methods.roundDouble((other.getYEnd() - bl2) / al2);
                XR2 = al2 > 0 ? other.getX() : other.getXEnd();
            }
            if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
                XL1 = shadowPoints[0].getX();
            } else {
                XL1 = Methods.roundDouble((other.getYEnd() - bl1) / al1);
            }
            if (XR1 >= other.getX() && XR1 <= other.getXEnd()) {
                if (FastMath.abs(al2) > 0 && (XR1 > current.getXEnd() || XR1 == other.getX())) { // dodaj światło
                    tempShadow = new Shadow(2);
                    tempShadow.addPoints(new Point(XR1, other.getY() - other.getShadowHeight()), new Point(XR1, other.getYEnd()),
                            new Point(XR2, other.getYEnd()), new Point(XR2, other.getY() - other.getShadowHeight()));
                    other.addShadow(tempShadow);
                    if (DEBUG) {
                        System.out.println("Right Light " + " XR1 " + XR1);
                    }
                } else { //dodaj cień
                    if (XL1 > XR2 && XL1 < other.getXEnd() && shadowPoints[2].getY() < current.getYEnd()) {
                        XR2 = XL1;
                    }
                    tempShadow = new Shadow(3);
                    tempShadow.addPoints(new Point(XR1, other.getYEnd()), new Point(XR1, other.getY() - other.getShadowHeight()),
                            new Point(XR2, other.getY() - other.getShadowHeight()), new Point(XR2, other.getYEnd()));
                    other.addShadow(tempShadow);
                    if (DEBUG) {
                        System.out.println("Right Shade " + al2 + " XR1 " + XR1 + " figure.X " + current.getX());
                    }
                }
            } else if (shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
                YOR = Methods.roundDouble(al2 * other.getX() + bl2);
                YOR2 = Methods.roundDouble(al2 * other.getXEnd() + bl2);
                if ((XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOR > other.getY() - other.getShadowHeight() && YOR < other.getYEnd()) || (YOR2 > other.getY() - other.getShadowHeight() && YOR2 < other.getYEnd())) {
                    if (FastMath.abs(al2) >= 0 && XR1 > current.getXEnd()) {
                        other.addShadow(shadow1);
                        if (DEBUG) {
                            System.out.println("Right Lightness - first");
                        }
                    } else {
                        other.addShadow(shadow0);
                        if (DEBUG) {
                            System.out.println("Right Darkness - first");
                        }
                    }

                }
            }
        }
    }

    private static void calculateRightObject(Figure other, Figure current, Light source) {
        if (other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            if (shadowPoints[1].getX() != shadowPoints[3].getX()) {
                XR1 = Methods.roundDouble((other.getYOwnerEnd() - bl2) / al2);
                XR2 = al2 > 0 ? other.getOwner().getStartX() : other.getOwner().getStartX() + other.getWidth();
            } else {
                XR1 = shadowPoints[1].getX();
                XR2 = other.getOwner().getStartX();
            }
            if (XR1 >= other.getXOwnerBegin() && XR1 <= other.getXOwnerEnd()) {
                if (FastMath.abs(al2) > 0 && XR1 > current.getXEnd()) { // dodaj światło
                    tempShadow = new Shadow(4);
                    tempShadow.addPoints(new Point(XR1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XR2), null, null, null);
                    other.addShadow(tempShadow);
//                    System.out.println("Right Light");
                } else { //dodaj cień
                    tempShadow = new Shadow(5);
                    tempShadow.addPoints(new Point(XR1 - other.getXOwnerBegin() + other.getOwner().getStartX(), XR2), null, null, null);
                    other.addShadow(tempShadow);
//                    System.out.println("Right Shade");
                }
            } else if (shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
                YOR = Methods.roundDouble(al2 * other.getXOwnerBegin() + bl2);
                YOR2 = Methods.roundDouble(al2 * other.getXOwnerEnd() + bl2);
                if ((XR1 != current.getXEnd() && XR1 != other.getXOwnerBegin() && XR1 != other.getXOwnerEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOR > other.getYOwnerBegin() && YOR < other.getYOwnerEnd()) || (YOR2 > other.getYOwnerBegin() && YOR2 < other.getYOwnerEnd())) {
                    if (FastMath.abs(al2) > 0 && XR1 > current.getXEnd()) {
                        other.addShadow(shadow1);
                    } else {
                        other.addShadow(shadow0);
                    }
//                    System.out.println("Right Dark");
                }
            }
        }
    }

    private static void findDarkness(Figure other, Figure current, Light source) {
        if (other != left && other != right && other.getYEnd() < source.getY() && (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd())) {
            polygon.reset();
            polygon.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
            polygon.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
            polygon.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
            polygon.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
            if (polygon.contains(other.getX(), other.getY() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
                other.addShadow(shadow0);
                if (DEBUG) {
                    System.out.println("Darkness...");
                }
            }
        }
    }

    private static void findObjectDarkness(Figure other, Figure current, Light source) {
        if (!isChecked && other.getYEnd() < source.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
            polygon.reset();
            polygon.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
            polygon.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
            polygon.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
            polygon.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
            if (polygon.contains(other.getXOwnerBegin(), other.getYOwnerBegin() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
                other.addShadow(shadow0);
//                System.out.println("Darkness...");
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

    private static void renderShadow(Light emitter, Point[] points) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void renderShadowLit(Light emitter, float shadowColor, Point[] points) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(shadowColor, shadowColor, shadowColor);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + Display.getHeight() - emitter.getHeight(), 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public static void clearFBO(float color) {
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

    public static void initializeRenderer() {
        shadowPoints[0] = new Point(0, 0);
        shadowPoints[1] = new Point(0, 0);
        shadowPoints[2] = new Point(0, 0);
        shadowPoints[3] = new Point(0, 0);
        shads[0] = (Light emitter, Figure shade, Point[] points) -> {
            shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade);
        };
        shads[1] = (Light emitter, Figure shade, Point[] points) -> {
            shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade.getShadowColor(), shade);
        };
        shads[2] = (Light emitter, Figure shade, Point[] points) -> {
            if (shade.getOwner().isSimpleLighting()) {
                renderShadowLit(emitter, shade.getShadowColor(), points);
            } else {
                shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade.getShadowColor(), shade, points[0].getX(), points[2].getX());
            }
        };
        shads[3] = (Light emitter, Figure shade, Point[] points) -> {
            if (shade.getOwner().isSimpleLighting()) {
                renderShadow(emitter, points);
            } else {
                shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, points[0].getX(), points[2].getX());
            }
        };
        shads[4] = (Light emitter, Figure shade, Point[] points) -> {
            shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade.getShadowColor(), shade, points[0].getX(), points[0].getY());
        };
        shads[5] = (Light emitter, Figure shade, Point[] points) -> {
            shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade, points[0].getX(), points[0].getY());
        };

    }

    private interface renderShadow {

        void render(Light emitter, Figure shade, Point[] points);
    }

    private ShadowRenderer() {
    }
}
