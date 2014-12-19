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
import java.awt.Polygon;
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
    private static int nrShades, shDif, nrPoints, lightX, lightY, distOther, distThis, shP1, shP2, shX, shY, XL1, XL2, XR1, XR2, YL;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Shadow tmpShadow;
    private static final Shadow shadow0 = new Shadow(0), shadow1 = new Shadow(1);
    private static final ArrayList<Shadow> tmpShadows2 = new ArrayList<>(), tmpShadows3 = new ArrayList<>();
    private static final renderShadow[] shads = new renderShadow[6];
    private static float LH1o2;
    private static boolean isChecked;
    private static final Polygon poly = new Polygon();

    public static void preRendLight(Place place, int l) {
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
                        shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                                (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), true, shade.shadowColor, shade);
                        shade.shadows.add(shadow1);
                    } else {
                        shade.shadows.add(shadow0);
                    }
                } else if (shade.canBeLit() && emitter.getY() >= shade.getEndY()) {
                    shade.shadowColor = (emitter.getY() - shade.getEndY()) / LH1o2;
                    shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                            (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), true, shade.shadowColor, shade);
                    shade.shadows.add(shadow1);
                } else {
                    shade.shadows.add(shadow0);
                }
            } else {
                shade.shadowColor = 1;
                shade.shadows.add(shadow1);
            }
        }
//        int drawed = 0;
        for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień           
            shade = shades[f];
            solveShadows(shade);
            for (Shadow sh : shade.shadows) {
                shads[sh.type].render(emitter, shade, sh.points);
//                drawed++;
            }
            shade.shadows.clear();
        }
//        System.out.println("Narysowano dodatkowych cieni: " + drawed);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
        glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        emitter.getLight().render(h - emitter.getLight().getSY());
        emitter.getLight().fbo.deactivate();
    }

    private static void solveShadows(Figure sh) {
        tmpShadow = null;
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
                    tmpShadow = shad;
                    break;
                case 2:
                    tmpShadows2.add(shad);
                    break;
                case 3:
                    tmpShadows3.add(shad);
                    break;
                case 4:
                    tmpShadows2.add(shad);
                    break;
                case 5:
                    tmpShadows3.add(shad);
                    break;
            }
        }
        sh.shadows.clear();
        if (tmpShadow != null && tmpShadows2.isEmpty()) {
            sh.shadows.add(tmpShadow);
        }
        for (Shadow shadow : tmpShadows2) {
            sh.shadows.add(shadow);
        }
        for (Shadow shadow : tmpShadows3) {
            sh.shadows.add(shadow);
        }
    }

    private static void findShades(GameObject src, Place place) {
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
            if ((FastMath.abs(tmp.own().getY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.own().getHeight() >> 1))
                    && (FastMath.abs(tmp.own().getX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.own().getWidth() >> 1))) {
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

//    private static void calculateObjectShade(Figure f, GameObject src) {
//        for (int i = 0; i < nrShades; i++) {
//            other = shades[i];
//            if (other.canGiveShadow() && other != f) {
//                if (Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getEndX(), other.getY())
//                        || Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getEndX(), other.getEndY())
//                        || Line2D.linesIntersect(f.getCentralX(), f.getCentralY(), src.getX(), src.getY(), other.getX(), other.getY(), other.getX(), other.getEndY())) {
//                    f.shadows.add(shadow0);
//                } else {
//                    f.shadowColor = (emitter.getY() - shade.getEndY()) / LH1o2;
//                    f.shadows.add(shadow1);
//                }
//            }
//        }
//    }
    private static void calculateWalls(Figure f, GameObject src) {
        findWalls(f, src);
        calculateLeftWall(f, src);
        calculateRightWall(f, src);
    }

    private static void findWalls(Figure f, GameObject src) {
        left = right = null;
        for (int i = 0; i < nrShades; i++) {
            isChecked = false;
            other = shades[i];
            if (f.getY() < src.getY() && other.canGiveShadow() && other != f && other != src.getCollision()) {
                if (points[0].getX() == points[2].getX()) {
                    XOL = points[0].getX();
                } else {
                    XOL = ((other.getEndY() - bl1) / al1);
                    YOL = (al1 * other.getX() + bl1);
                    YOL2 = (al1 * other.getEndX() + bl1);
                }
                if (points[1].getX() == points[3].getX()) {
                    XOR = points[1].getX();
                } else {
                    XOR = ((other.getEndY() - bl2) / al2);
                    YOR = (al2 * other.getX() + bl2);
                    YOR2 = (al2 * other.getEndX() + bl2);
                }
                if ((points[0].getY() > points[2].getY() && points[0].getY() > other.getY() - other.shadowHeight()) && ((points[0].getX() != points[2].getX() && ((XOL >= other.getX() && XOL <= other.getEndX()) || (YOL >= other.getY() - other.shadowHeight() && YOL <= other.getEndY()) || (YOL2 >= other.getY() - other.shadowHeight() && YOL2 <= other.getEndY()))) || (points[0].getX() == points[2].getX() && XOL >= other.getX() && XOL <= other.getEndX()))) {
                    if (left != null) {
                        distOther = Methods.PointDistanceSimple(other.getCentralX(), other.getCentralY(), f.getCentralX(), f.getCentralY());
                        distThis = Methods.PointDistanceSimple(left.getCentralX(), left.getCentralY(), f.getCentralX(), f.getCentralY());
                        left = (distOther > distThis) ? left : other;
                    } else {
                        left = other;
                    }
                    findLeftDark(f, src);
                }
                if ((points[1].getY() > points[3].getY() && points[1].getY() > other.getY() - other.shadowHeight()) && ((points[1].getX() != points[3].getX() && ((XOR >= other.getX() && XOR <= other.getEndX()) || (YOR >= other.getY() - other.shadowHeight() && YOR <= other.getEndY()) || (YOR2 >= other.getY() - other.shadowHeight() && YOR2 <= other.getEndY()))) || (points[1].getX() == points[3].getX() && XOR >= other.getX() && XOR <= other.getEndX()))) {
                    if (right != null) {
                        distOther = Methods.PointDistanceSimple(other.getCentralX(), other.getCentralY(), f.getCentralX(), f.getCentralY());
                        distThis = Methods.PointDistanceSimple(right.getCentralX(), right.getCentralY(), f.getCentralX(), f.getCentralY());
                        right = (distOther > distThis) ? right : other;
                    } else {
                        right = other;
                    }
                    findRightDark(f, src);
                }
                findDarkness(f, src);
            } else if (f.getY() < src.getY() && !other.canGiveShadow() && other.canBeLit() && other != f && other != src.getCollision()) {
                if (points[0].getX() == points[2].getX()) {
                    XOL = points[0].getX();
                } else {
                    XOL = ((other.getOwnEndY() - bl1) / al1);
                    YOL = (al1 * other.getOwnBegX() + bl1);
                    YOL2 = (al1 * other.getOwnEndX() + bl1);
                }
                if (points[1].getX() == points[3].getX()) {
                    XOR = points[1].getX();
                } else {
                    XOR = ((other.getOwnEndY() - bl2) / al2);
                    YOR = (al2 * other.getOwnBegX() + bl2);
                    YOR2 = (al2 * other.getOwnEndX() + bl2);
                }
                if ((points[0].getY() > points[2].getY() && points[0].getY() > other.getY()) && ((points[0].getX() != points[2].getX() && ((XOL >= other.getOwnBegX() && XOL <= other.getOwnEndX()) || (YOL > other.getOwnBegY() && YOL < other.getOwnEndY()) || (YOL2 > other.getOwnBegY() && YOL2 < other.getOwnEndY()))) || (points[0].getX() == points[2].getX() && XOL >= other.getOwnBegX() && XOL <= other.getOwnEndX()))) {
                    isChecked = true;
                    calculateLeftObject(other, f, src);
                }
                if ((points[1].getY() > points[3].getY() && points[1].getY() > other.getY()) && ((points[1].getX() != points[3].getX() && ((XOR >= other.getOwnBegX() && XOR <= other.getOwnEndX()) || (YOR > other.getOwnBegY() && YOR < other.getOwnEndY()) || (YOR2 > other.getOwnBegY() && YOR2 < other.getOwnEndY()))) || (points[1].getX() == points[3].getX() && XOR >= other.getOwnBegX() && XOR <= other.getOwnEndX()))) {
                    isChecked = true;
                    calculateRightObject(other, f, src);
                }
                findObjectDarkness(f, src);
            }
        }
    }

    private static void calculateLeftWall(Figure f, GameObject src) {
        if (left != null && left.getEndY() < src.getY() && (left.getY() - left.shadowHeight() < f.getY() || left.getEndY() < f.getEndY())) {
            if (points[0].getX() == points[2].getX()) {
                XL1 = points[0].getX();
                XL2 = left.getEndX();
            } else {
                XL1 = Methods.RoundHU((left.getEndY() - bl1) / al1);
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
            }
            if (XL1 >= left.getX() && XL1 <= left.getEndX()) {
                if (FastMath.abs(al1) > 0 && XL1 < f.getX()) { //dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XL1, left.getY() - left.shadowHeight()), new Point(XL1, left.getEndY()),
                            new Point(XL2, left.getEndY()), new Point(XL2, left.getY() - left.shadowHeight()));
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XL1, left.getEndY()), new Point(XL1, left.getY() - left.shadowHeight()),
                            new Point(XL2, left.getY() - left.shadowHeight()), new Point(XL2, left.getEndY()));
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Shade");
                }
            } else if (points[0].getX() != points[2].getX()) { // rysuj zaciemniony
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

    private static void calculateLeftObject(Figure left, Figure f, GameObject src) {
        if (left.getEndY() < src.getY() && (left.getY() < f.getY() || left.getEndY() < f.getEndY())) {
            if (points[0].getX() != points[2].getX()) {
                XL1 = Methods.RoundHU((left.getOwnEndY() - bl1) / al1);
                XL2 = al1 > 0 ? left.own().getStartX() : left.own().getStartX() + left.getWidth();
            } else {
                XL1 = points[0].getX();
                XL2 = left.own().getStartX() + left.getWidth();
            }
            if (XL1 >= left.getOwnBegX() && XL1 <= left.getOwnEndX()) {
                if (FastMath.abs(al1) > 0 && XL1 < f.getX()) { //dodaj światło
                    tmpShadow = new Shadow(4);
                    tmpShadow.addPoints(new Point(XL1 - left.getOwnBegX() + left.own().getStartX(), XL2), null, null, null);
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(5);
                    tmpShadow.addPoints(new Point(XL1 - left.getOwnBegX() + left.own().getStartX(), XL2), null, null, null);
                    left.shadows.add(tmpShadow);
//                    System.out.println("Left Shade");
                }
            } else if (points[0].getX() != points[2].getX()) { // rysuj zaciemniony
                YOL = Methods.RoundHU(al1 * left.getOwnBegX() + bl1);
                YOL2 = Methods.RoundHU(al1 * left.getOwnEndX() + bl1);
                if ((XL1 != f.getX() && XL1 != left.getOwnBegX() && XL1 != left.getOwnEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOL > left.getOwnBegY() && YOL < left.getOwnEndY()) || (YOL2 > left.getOwnBegY() && YOL2 < left.getOwnEndY())) {
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
        if (points[0].getX() != points[2].getX() && other != null && other.getEndY() < src.getY() && (other.getY() - other.shadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
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
            if (points[1].getX() != points[3].getX()) {
                XR1 = Methods.RoundHU((right.getEndY() - bl2) / al2);
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
            } else {
                XR1 = points[1].getX();
                XR2 = right.getX();
            }
            if (XR1 >= right.getX() && XR1 <= right.getEndX()) {
                if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) { // dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XR1, right.getY() - right.shadowHeight()), new Point(XR1, right.getEndY()),
                            new Point(XR2, right.getEndY()), new Point(XR2, right.getY() - right.shadowHeight()));
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XR1, right.getEndY()), new Point(XR1, right.getY() - right.shadowHeight()),
                            new Point(XR2, right.getY() - right.shadowHeight()), new Point(XR2, right.getEndY()));
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Shade");
                }
            } else if (points[1].getX() != points[3].getX()) { // rysuj zaciemniony
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

    private static void calculateRightObject(Figure right, Figure f, GameObject src) {
        if (right.getEndY() < src.getY() && (right.getY() < f.getY() || right.getEndY() < f.getEndY())) {
            if (points[1].getX() != points[3].getX()) {
                XR1 = Methods.RoundHU((right.getOwnEndY() - bl2) / al2);
                XR2 = al2 > 0 ? right.own().getStartX() : right.own().getStartX() + right.getWidth();
            } else {
                XR1 = points[1].getX();
                XR2 = right.own().getStartX();
            }
            if (XR1 >= right.getOwnBegX() && XR1 <= right.getOwnEndX()) {
                if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) { // dodaj światło
                    tmpShadow = new Shadow(4);
                    tmpShadow.addPoints(new Point(XR1 - right.getOwnBegX() + right.own().getStartX(), XR2), null, null, null);
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(5);
                    tmpShadow.addPoints(new Point(XR1 - right.getOwnBegX() + right.own().getStartX(), XR2), null, null, null);
                    right.shadows.add(tmpShadow);
//                    System.out.println("Right Shade");
                }
            } else if (points[1].getX() != points[3].getX()) { // rysuj zaciemniony
                YOR = Methods.RoundHU(al2 * right.getOwnBegX() + bl2);
                YOR2 = Methods.RoundHU(al2 * right.getOwnEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != right.getOwnBegX() && XR1 != right.getOwnEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > right.getOwnBegY() && YOR < right.getOwnEndY()) || (YOR2 > right.getOwnBegY() && YOR2 < right.getOwnEndY())) {
                    if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) {
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
        if (points[1].getX() != points[3].getX() && other != null && other.getEndY() < src.getY() && (other.getY() - other.shadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            XR1 = Methods.RoundHU((other.getEndY() - bl2) / al2);
            if (((FastMath.abs(al2) <= 0 || XR1 < other.getX()) || XR1 > other.getEndX()) || XR1 <= f.getEndX() && ((XR1 == f.getEndX() || XR1 < other.getX()) || XR1 > other.getEndX())) {
                YOR = Methods.RoundHU(al2 * other.getX() + bl2);
                YOR2 = Methods.RoundHU(al2 * other.getEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != other.getX() && XR1 != other.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > other.getY() && YOR < other.getEndY()) || (YOR2 > other.getY() && YOR2 < other.getEndY())) {
                    if (FastMath.abs(al2) < 0 || XR1 <= f.getEndX()) {
                        other.shadows.add(shadow0);
//                        System.out.println("Right Darkness");
                    }
                }
            }
        }
    }

    private static void findDarkness(Figure f, GameObject src) {
        if (other != left && other != right && other.getEndY() < src.getY() && (other.getY() - other.shadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            poly.reset();
            poly.addPoint(points[0].getX(), points[0].getY());
            poly.addPoint(points[1].getX(), points[1].getY());
            poly.addPoint(points[3].getX(), points[3].getY());
            poly.addPoint(points[2].getX(), points[2].getY());
            if (poly.contains(other.getX(), other.getY() - other.shadowHeight(), other.getWidth(), other.getHeight() + other.shadowHeight())) {
                other.shadows.add(shadow0);
//                System.out.println("Darkness...");
            }
        }
    }

    private static void findObjectDarkness(Figure f, GameObject src) {
        if (!isChecked && other.getEndY() < src.getY() && (other.getY() < f.getY() || other.getEndY() < f.getEndY())) {
            poly.reset();
            poly.addPoint(points[0].getX(), points[0].getY());
            poly.addPoint(points[1].getX(), points[1].getY());
            poly.addPoint(points[3].getX(), points[3].getY());
            poly.addPoint(points[2].getX(), points[2].getY());
            if (poly.contains(other.getOwnBegX(), other.getOwnBegY() - other.shadowHeight(), other.getWidth(), other.getHeight() + other.shadowHeight())) {
                other.shadows.add(shadow0);
//                System.out.println("Darkness...");
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
        glTranslatef(emitter.getX() - (lightX >> 1) + cam.getXOffEffect(), emitter.getY() - (lightY >> 1) + cam.getYOffEffect(), 0);
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

    public static void initRenderer(Place place) {
        points[0] = new Point(0, 0);
        points[1] = new Point(0, 0);
        points[2] = new Point(0, 0);
        points[3] = new Point(0, 0);
        shads[0] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shade, Point[] points) {
                shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                        (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), false, 0, shade);
            }
        };
        shads[1] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shade, Point[] points) {
                shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
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
        shads[4] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shad, Point[] points) {
                shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                        (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), true, shade.shadowColor, shade, points[0].getX(), points[0].getY());
            }
        };
        shads[5] = new renderShadow() {
            @Override
            public void render(GameObject emitter, Figure shad, Point[] points) {
                shade.own().renderShadow((emitter.getLight().getSX() >> 1) - (emitter.getX()),
                        (emitter.getLight().getSY() >> 1) - (emitter.getY()) + h - emitter.getLight().getSY(), false, 0, shade, points[0].getX(), points[0].getY());
            }
        };

    }

    private interface renderShadow {

        void render(GameObject emitter, Figure shad, Point[] points);
    }

    private ShadowRenderer() {
    }
}
