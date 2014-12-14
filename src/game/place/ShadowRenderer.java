/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import collision.Figure;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.place.cameras.Camera;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 *
 * @author przemek
 */
public class ShadowRenderer {

    private static final int w = Display.getWidth(), h = Display.getHeight();
    private static final Figure[] shades = new Figure[4096];
    private static GameObject emitter;
    private static final Point center = new Point(0, 0);
    private static final Point[] tempPoints = new Point[32], points = new Point[4];
    private static Figure shade, tmp, other, left, right;
    private static int nrShades, shDif, nrPoints, lightX, lightY, distOther, distThis, shP1, shP2, shX, shY, XL1, XL2, XR1, XR2, YL, shadMax, shadMin;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Shadow tmpShadow, tmpShadow1, tmpShadowMax, tmpShadowMin;
    private static final Shadow shadow0 = new Shadow(0), shadow1 = new Shadow(1);
    private static final ArrayList<Shadow> tmpShadows2 = new ArrayList<>(), tmpShadows3 = new ArrayList<>();
    private static final renderShadow[] shads = new renderShadow[4];
    private static float LH1o2;

    public static void preRendLight(AbstractPlace place, int l) {
        emitter = place.visibleLights[l];
        findShades(emitter, place);
        emitter.getLight().fbo.activate();
        clearFBO(1);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);

        LH1o2 = (float) (emitter.getHeight() >> 1);
        for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
            shade = shades[f];
            if (shade != emitter.getCollision()) {
                if (shade.canGiveShadow()) {
                    calculateShadow(emitter, shade);
                    drawShadow(emitter);
                    calculateWalls(shade, emitter);
                    if (shade.canBeLit() && emitter.getY() >= shade.getEndY()) {
                        shade.shadowColor = (emitter.getY() - shade.getEndY()) / LH1o2;
                        shade.getOwner().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                                (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), true, shade.shadowColor, shade);
//                        shade.shadows.add(shadow1);
                    } else {
                        shade.shadows.add(shadow0);
                    }
                } else if (shade.canBeLit() && emitter.getY() >= shade.getEndY()) {
                    calculateObjectShade(shade, emitter);
//                    shads[shade.shadows.get(0).type].render(emitter, shade, shade.shadows.get(0).points);
//                    shade.shadows.clear();
                } else {
                    shade.shadows.add(shadow0);
                }
            } else {
                shade.shadowColor = 1;
                shade.shadows.add(shadow1);
            }
        }
        for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
            shade = shades[f];
            solveShadows(shade);
            for (Shadow sh : shade.shadows) {
                shads[sh.type].render(emitter, shade, sh.points);
            }
            shade.shadows.clear();
        }
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        emitter.getLight().render(h - emitter.getLight().getSY());
        emitter.getLight().fbo.deactivate();
    }

    private static void solveShadows(Figure sh) {
        tmpShadow1 = tmpShadowMin = tmpShadowMax = null;
        tmpShadows2.clear();
        tmpShadows3.clear();
        for (Shadow shad : sh.shadows) {
            switch (shad.type) {
                case 0:
                    tmpShadow = shad;
                    sh.shadows.clear();
                    sh.shadows.add(tmpShadow);
                    return;
                case 1:
                    tmpShadow1 = shad;
                    break;
                case 2:
                    tmpShadows2.add(shad);
                    break;
                case 3:
                    tmpShadows3.add(shad);
                    break;
            }
        }
        sh.shadows.clear();
        if (tmpShadow1 != null && tmpShadows2.isEmpty() && tmpShadows3.isEmpty()) {
            sh.shadows.add(tmpShadow1);
        }
        if (!tmpShadows2.isEmpty()) {
            if (tmpShadows2.size() == 1) {
                sh.shadows.add(tmpShadows2.get(0));
            } else {
                shadMin = 2147483647;
                shadMax = 0;
                for (Shadow shad : tmpShadows2) {
                    if (shad.points[3].getX() == sh.getX()) {
                        if (shadMin > shad.points[0].getX()) {
                            shadMin = shad.points[0].getX();
                            tmpShadowMin = shad;
                        }
                    } else if (shadMax < shad.points[0].getX()) {
                        shadMax = shad.points[0].getX();
                        tmpShadowMax = shad;
                    }
                }
                if (tmpShadowMax != null) {
                    sh.shadows.add(tmpShadowMax);
                }
                if (tmpShadowMin != null) {
                    sh.shadows.add(tmpShadowMin);
                }
            }
        }
        if (!tmpShadows3.isEmpty()) {
            if (tmpShadows3.size() == 1) {
                sh.shadows.add(tmpShadows3.get(0));
            } else {
                shadMin = 2147483647;
                shadMax = 0;
                tmpShadowMin = tmpShadowMax = null;
                for (Shadow shad : tmpShadows3) {
                    if (shad.points[3].getX() == sh.getX()) {
                        if (shadMax < shad.points[0].getX()) {
                            shadMax = shad.points[0].getX();
                            tmpShadowMax = shad;
                        }
                    } else if (shadMin > shad.points[0].getX()) {
                        shadMin = shad.points[0].getX();
                        tmpShadowMin = shad;
                    }
                }
                if (tmpShadowMax != null) {
                    sh.shadows.add(tmpShadowMax);
                }
                if (tmpShadowMin != null) {
                    sh.shadows.add(tmpShadowMin);
                }
            }
        }
    }

    private static void findShades(GameObject src, AbstractPlace place) {
        // Powinno sortować według wysoskości - najpierw te, które są najwyżej na planszy, a później coraz niższe,
        // obiekty tej samej wysokości powinny być renderowane w kolejności od najdalszych od źródła, do najbliższych.
        nrShades = 0;
        for (Area a : place.areas) { //iteracja po Shades - tych co dają cień
            if (!a.isBorder()) {
                if (a.isWhole) {
                    tmp = a.getCollision();
                    if (tmp != null && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getHeight() >> 1))
                            && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getWidth() >> 1))) {
                        shades[nrShades++] = tmp;
                        tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                    }
                    for (Figure tmp : a.getParts()) {
                        if (tmp != null && !tmp.canBeLit() && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getHeight() >> 1))
                                && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getWidth() >> 1))) {
                            shades[nrShades++] = tmp;
                            tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                        }
                    }
                } else {
                    for (Figure tmp : a.getParts()) {
                        if (tmp != null && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getHeight() >> 1))
                                && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getWidth() >> 1))) {
                            shades[nrShades++] = tmp;
                            tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                        }
                    }
                }
            }
        }
        for (GameObject go : place.depthObj) {   // FGTiles muszą mieć Collision
            tmp = go.getCollision();
            if ((FastMath.abs(tmp.getOwner().getY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getOwner().getHeight() >> 1))
                    && (FastMath.abs(tmp.getOwner().getX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getOwner().getWidth() >> 1))) {
                shades[nrShades++] = tmp;
                tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
            }
        }
        Arrays.sort(shades, 0, nrShades);
//        System.out.println("Posortowana: ");
//        for (int i = 0; i < nrShades; i++) {
//            System.out.println("Y: " + shades[i].getEndY() + " X: " + shades[i].getDistFromLight() + " - Klasa:" + shades[i].getOwner().getClass());
//        }
//        System.out.print("\n");
    }

    private static void calculateShadow(GameObject src, Figure thisShade) {
        findPoints(src, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
    }

    private static void findPoints(GameObject src, Figure thisShade) {
        center.set(src.getX(), src.getY());
        nrPoints = thisShade.listPoints().length;
        System.arraycopy(thisShade.listPoints(), 0, tempPoints, 0, nrPoints);
        angle = 0;
        for (int p = 0; p < nrPoints; p++) {
            for (int s = p + 1; s < nrPoints; s++) {
                temp = Methods.ThreePointAngle(tempPoints[p].getX(), tempPoints[p].getY(), tempPoints[s].getX(), tempPoints[s].getY(), center.getX(), center.getY());
                if (temp > angle) {
                    angle = temp;
                    shP1 = p;
                    shP2 = s;
                }
            }
        }
        points[0] = tempPoints[shP1];
        points[1] = tempPoints[shP2];
        shDif = (src.getLight().getSX() + src.getLight().getSY()) << 2;
    }

    private static void findLeftSideOfShadow() {
        if (points[0].getX() == center.getX()) {
            points[2].set(points[0].getX(), points[0].getY() + (points[0].getY() > center.getY() ? shDif : -shDif));
        } else if (points[0].getY() == center.getY()) {
            points[2].set(points[0].getX() + (points[0].getX() > center.getX() ? shDif : -shDif), points[0].getY());
        } else {
            al1 = (center.getY() - points[0].getY()) / (double) (center.getX() - points[0].getX());
            bl1 = points[0].getY() - al1 * points[0].getX();
            if (al1 > 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al1 * shX + bl1);
            } else if (al1 < 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al1 * shX + bl1);
            } else {
                shX = points[0].getX();
                shY = points[0].getY() + (points[0].getY() > center.getY() ? shDif : -shDif);
            }
            points[2].set(shX, shY);
        }
    }

    private static void findRightSideOfShadow() {
        if (points[1].getX() == center.getX()) {
            points[3].set(points[1].getX(), points[1].getY() + (points[1].getY() > center.getY() ? shDif : -shDif));
        } else if (points[1].getY() == center.getY()) {
            points[3].set(points[1].getX() + (points[1].getX() > center.getX() ? shDif : -shDif), points[1].getY());
        } else {
            al2 = (center.getY() - points[1].getY()) / (double) (center.getX() - points[1].getX());
            bl2 = points[1].getY() - al2 * points[1].getX();
            if (al2 > 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al2 * shX + bl2);
            } else if (al2 < 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al2 * shX + bl2);
            } else {
                shX = points[1].getX();
                shY = points[1].getY() + (points[1].getY() > center.getY() ? shDif : -shDif);
            }
            points[3].set(shX, shY);
        }
    }

    private static void calculateObjectShade(Figure f, GameObject src) {
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (other.canGiveShadow() && other != f) {
                if (Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getEndX(), other.getY())
                        || Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getEndX(), other.getEndY())
                        || Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getX(), other.getEndY())) {
                    f.shadows.add(shadow0);
                } else {
                    f.shadowColor = (emitter.getY() - shade.getEndY()) / LH1o2;
                    f.shadows.add(shadow1);
                }
            }
        }
    }

    private static void calculateWalls(Figure f, GameObject src) {
        findWalls(f, src);
        calculateLeftWall(f, src);
        calculateRightWall(f, src);
    }

    private static void findWalls(Figure f, GameObject src) {
        left = right = null;
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (f.getY() < src.getY() && other.canGiveShadow() && other != f) {
                XOL = ((other.getEndY() - bl1) / al1);
                XOR = ((other.getEndY() - bl2) / al2);
                YOL = (al1 * other.getX() + bl1);
                YOL2 = (al1 * other.getEndX() + bl1);
                YOR = (al2 * other.getX() + bl2);
                YOR2 = (al2 * other.getEndX() + bl2);
                if (points[0].getX() != points[2].getX() && points[0].getY() > points[2].getY() && points[0].getY() > other.getY() - other.shadowHeight() && ((XOL >= other.getX() && XOL <= other.getEndX()) || (YOL >= other.getY() - other.shadowHeight() && YOL <= other.getEndY()) || (YOL2 >= other.getY() - other.shadowHeight() && YOL2 <= other.getEndY()))) {
                    if (left != null) {
                        distOther = Methods.PointDistanceSimple(other.getCentralX(), other.getCentralY(), f.getCentralX(), f.getCentralY());
                        distThis = Methods.PointDistanceSimple(left.getCentralX(), left.getCentralY(), f.getCentralX(), f.getCentralY());
                        left = (distOther > distThis) ? left : other;
                    } else {
                        left = other;
                    }
                    findLeftDark(f, src);
                }
                if (points[1].getX() != points[3].getX() && points[1].getY() > points[3].getY() && points[1].getY() > other.getY() - other.shadowHeight() && ((XOR >= other.getX() && XOR <= other.getEndX()) || (YOR >= other.getY() - other.shadowHeight() && YOR <= other.getEndY()) || (YOR2 >= other.getY() - other.shadowHeight() && YOR2 <= other.getEndY()))) {
                    if (right != null) {
                        distOther = Methods.PointDistanceSimple(other.getCentralX(), other.getCentralY(), f.getCentralX(), f.getCentralY());
                        distThis = Methods.PointDistanceSimple(right.getCentralX(), right.getCentralY(), f.getCentralX(), f.getCentralY());
                        right = (distOther > distThis) ? right : other;
                    } else {
                        right = other;
                    }
                    findRightDark(f, src);
                }
//                if (other.getEndY() < f.getY()) {
//                    if ((points[0].getY() > points[2].getY() && points[1].getY() > points[3].getY() && ((XOL < other.getX() && XOR > other.getEndX()) || (XOR < other.getX() && XOL > other.getEndX())))
//                            || (points[0].getY() >= points[2].getY() && points[1].getY() <= points[3].getY() && ((XOL < other.getX() && points[0].getX() < other.getX() && points[2].getX() > other.getEndX()) || (XOL > other.getEndX() && points[0].getX() > other.getEndX() && points[2].getX() < other.getX())))
//                            || (points[1].getY() >= points[3].getY() && points[0].getY() <= points[2].getY() && ((XOR < other.getX() && points[1].getX() < other.getX() && points[3].getX() > other.getEndX()) || (XOR > other.getEndX() && points[1].getX() > other.getEndX() && points[3].getX() < other.getX())))) {
//                        other.shadows.add(shadow0);
//                    }
//                }
            }
        }
    }

    private static void calculateLeftWall(Figure f, GameObject src) {
        if (left != null && left.getEndY() < src.getY() && (left.getY() - left.shadowHeight() < f.getY() || left.getEndY() < f.getEndY())) {
            XL1 = Methods.RoundHU((left.getEndY() - bl1) / al1);
            if (XL1 >= left.getX() && XL1 <= left.getEndX()) {
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
                if (FastMath.abs(al1) > 0 && XL1 < f.getX()) { //dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XL1, left.getY() - left.shadowHeight()), new Point(XL1, left.getEndY()),
                            new Point(XL2, left.getEndY()), new Point(XL2, left.getY() - left.shadowHeight()));
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Light");
                } else if (XL1 != f.getX()) { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XL1, left.getEndY()), new Point(XL1, left.getY() - left.shadowHeight()),
                            new Point(XL2, left.getY() - left.shadowHeight()), new Point(XL2, left.getEndY()));
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Shade");
                }
            } else { // rysuj zaciemniony
                YOL = Methods.RoundHU(al1 * left.getX() + bl1);
                YOL2 = Methods.RoundHU(al1 * left.getEndX() + bl1);
                if ((XL1 != f.getX() && XL1 != left.getX() && XL1 != left.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOL > left.getY() - left.shadowHeight() && YOL < left.getEndY()) || (YOL2 > left.getY() - left.shadowHeight() && YOL2 < left.getEndY())) {
                    if (FastMath.abs(al1) >= 0 && XL1 < f.getX()) {
                        left.shadows.add(shadow1);
                    } else {
                        left.shadows.add(shadow0);
                    }
//                    System.out.println("Left Dark");
                }
            }
        }
    }

    private static void findLeftDark(Figure f, GameObject src) {
        if (other != null && other.getEndY() < src.getY() && (other.getY() - other.shadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            XL1 = Methods.RoundHU((other.getEndY() - bl1) / al1);
            if (((FastMath.abs(al1) <= 0 || XL1 < other.getX()) || XL1 > other.getEndX()) || XL1 >= f.getX() && ((XL1 == f.getX() || XL1 < other.getX()) || XL1 > other.getEndX())) {
                YOL = Methods.RoundHU(al1 * other.getX() + bl1);
                YOL2 = Methods.RoundHU(al1 * other.getEndX() + bl1);
                if ((XL1 != f.getX() && XL1 != other.getX() && XL1 != other.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOL > YL && YOL < other.getEndY()) || (YOL2 > YL && YOL2 < other.getEndY())) {
                    if (FastMath.abs(al1) < 0 || XL1 >= f.getX()) {
                        other.shadows.add(shadow0);
                    }
//                    System.out.println("Left Darkness");
                }
            }
        }
    }

    private static void calculateRightWall(Figure f, GameObject src) {
        if (right != null && right.getEndY() < src.getY() && (right.getY() - right.shadowHeight() < f.getY() || right.getEndY() < f.getEndY())) {
            XR1 = Methods.RoundHU((right.getEndY() - bl2) / al2);
            if (XR1 >= right.getX() && XR1 <= right.getEndX()) {
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
                if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) { // dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XR1, right.getY() - right.shadowHeight()), new Point(XR1, right.getEndY()),
                            new Point(XR2, right.getEndY()), new Point(XR2, right.getY() - right.shadowHeight()));
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Light");
                } else if (XR1 != f.getEndX()) { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XR1, right.getEndY()), new Point(XR1, right.getY() - right.shadowHeight()),
                            new Point(XR2, right.getY() - right.shadowHeight()), new Point(XR2, right.getEndY()));
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Shade");
                }
            } else { // rysuj zaciemniony
                YOR = Methods.RoundHU(al2 * right.getX() + bl2);
                YOR2 = Methods.RoundHU(al2 * right.getEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != right.getX() && XR1 != right.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > right.getY() - right.shadowHeight() && YOR < right.getEndY()) || (YOR2 > right.getY() - right.shadowHeight() && YOR2 < right.getEndY())) {
                    if (FastMath.abs(al2) >= 0 && XR1 > f.getEndX()) {
                        right.shadows.add(shadow1);
                    } else {
                        right.shadows.add(shadow0);
                    }
//                    System.out.println("Right Dark");
                }
            }
        }
    }

    private static void findRightDark(Figure f, GameObject src) {
        if (other != null && other.getEndY() < src.getY() && (other.getY() - other.shadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            XR1 = Methods.RoundHU((other.getEndY() - bl2) / al2);
            if (((FastMath.abs(al2) <= 0 || XR1 < other.getX()) || XR1 > other.getEndX()) || XR1 <= f.getEndX() && ((XR1 == f.getEndX() || XR1 < other.getX()) || XR1 > other.getEndX())) {
                YOR = Methods.RoundHU(al2 * other.getX() + bl2);
                YOR2 = Methods.RoundHU(al2 * other.getEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != other.getX() && XR1 != other.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > other.getY() && YOR < other.getEndY()) || (YOR2 > other.getY() && YOR2 < other.getEndY())) {
                    if (FastMath.abs(al2) < 0 || XR1 <= f.getEndX()) {
                        other.shadows.add(shadow0);
                    }
//                    System.out.println("Right Darkness");
                }
            }
        }
    }

    private static void drawWall(GameObject emitter, Point[] points, float color) {
        int lX = emitter.getLight().getSX();
        int lY = emitter.getLight().getSY();
        glDisable(GL_TEXTURE_2D);
        glColor3f(color, color, color);
        glPushMatrix();
        glTranslatef((lX >> 1) - emitter.getX(), (lY >> 1) - emitter.getY() + h - lY, 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawShadow(GameObject emitter) {
        lightY = emitter.getLight().getSY();
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef((emitter.getLight().getSX() >> 1) - emitter.getX(), (lightY >> 1) - emitter.getY() + h - lightY, 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glVertex2f(points[1].getX(), points[1].getY());
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
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawLight(int textureHandle, GameObject emitter, Camera cam) {
        lightX = emitter.getLight().getSX();
        lightY = emitter.getLight().getSY();
        glPushMatrix();
        glTranslatef(emitter.getX() - (emitter.getLight().getSX() >> 1) + cam.getXOffEffect(), emitter.getY() - (emitter.getLight().getSY() >> 1) + cam.getYOffEffect(), 0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(1, 1);
        glVertex2f(lightX, 0);
        glTexCoord2f(1, 0);
        glVertex2f(lightX, lightY);
        glTexCoord2f(0, 0);
        glVertex2f(0, lightY);
        glEnd();
        glPopMatrix();
    }

    public static void initVariables(AbstractPlace place) {
        points[0] = new Point(0, 0);
        points[1] = new Point(0, 0);
        points[2] = new Point(0, 0);
        points[3] = new Point(0, 0);
        shads[0] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shade, Point[] points) {
                shade.getOwner().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                        (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), false, 0, shade);
            }
        };
        shads[1] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shade, Point[] points) {
                shade.getOwner().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                        (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), true, shade.shadowColor, shade);
            }
        };
        shads[2] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shade, Point[] points) {
                drawWall(emitter, points, shade.shadowColor);
            }
        };
        shads[3] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shad, Point[] points) {
                drawWall(emitter, points, 0);
            }
        };
    }

    private interface renderShadow {

        void render(GameObject emitter, Figure shad, Point[] points);
    }

    private ShadowRenderer() {
    }
}
