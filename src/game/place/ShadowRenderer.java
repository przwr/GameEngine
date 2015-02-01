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
    private static final Point[] points = new Point[4];
    private static Figure shade, tmp, other, left, right;
    private static int nrShades, shDif, lightX, lightY, distOther, distThis, firstShadowPoint, secondShadowPoint, shX, shY, XL1, XL2, XR1, XR2, YL;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XOR, YOL, YOL2, YOR, YOR2;
    private static Shadow tmpShadow;
    private static final Shadow shadow0 = new Shadow(0), shadow1 = new Shadow(1);
    private static final ArrayList<Shadow> tmpShadows2 = new ArrayList<>(), tmpShadows3 = new ArrayList<>();
    private static final renderShadow[] shads = new renderShadow[6];
    private static float LH1o2;
    private static boolean isChecked;
    private static final Polygon poly = new Polygon();

    public static void preRendLight(Map map, GameObject emitter) {
        findShades(emitter, map);
        emitter.getLight().fbo.activate();
        clearFBO(1);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);

        LH1o2 = (float) (emitter.getCollisionHeight() / 2);
        for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
            shade = shades[f];
            if (shade != emitter.getCollision()) {
                if (shade.isGiveShadow()) {
                    calculateShadow(emitter, shade);
                    drawShadow(emitter);
                    calculateWalls(shade, emitter);
                    if (shade.isLittable() && emitter.getY() >= shade.getEndY()) {
                        shade.setShadowColor((emitter.getY() - shade.getEndY()) / LH1o2);
                        shade.getOwner().renderShadowLit((emitter.getLight().getSX() / 2) - (emitter.getX()),
                                (emitter.getLight().getSY() / 2) - (emitter.getY()) + h - emitter.getLight().getSY(), shade.getShadowColor(), shade);
                        shade.addShadow(shadow1);
                    } else {
                        shade.addShadow(shadow0);
                    }
                } else if (shade.isLittable() && emitter.getY() >= shade.getEndY()) {
                    shade.setShadowColor((emitter.getY() - shade.getEndY()) / LH1o2);
                    shade.getOwner().renderShadowLit((emitter.getLight().getSX() / 2) - (emitter.getX()),
                            (emitter.getLight().getSY() / 2) - (emitter.getY()) + h - emitter.getLight().getSY(), shade.getShadowColor(), shade);
                    shade.addShadow(shadow1);
                } else {
                    shade.addShadow(shadow0);
                }
            } else {
                shade.setShadowColor(1);
                shade.addShadow(shadow1);
            }
        }

        for (int f = 0; f < nrShades; f++) {
            shade = shades[f];
            solveShadows(shade);
            for (Shadow sh : shade.getShadows()) {
                shads[sh.type].render(emitter, shade, sh.points);
            }
            shade.clearShadows();
        }
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
        for (Shadow shad : sh.getShadows()) {
            switch (shad.type) {
                case 0:
                    tmpShadow = shad;
                    sh.clearShadows();
                    sh.addShadow(tmpShadow);
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
        sh.clearShadows();
        if (tmpShadow != null && tmpShadows2.isEmpty()) {
            sh.addShadow(tmpShadow);
        }
        for (Shadow shadow : tmpShadows2) {
            sh.addShadow(shadow);
        }
        for (Shadow shadow : tmpShadows3) {
            sh.addShadow(shadow);
        }
    }

    private static void findShades(GameObject src, Map map) {
        // Powinno sortować według wysoskości - najpierw te, które są najwyżej na planszy, a później coraz niższe,
        // obiekty tej samej wysokości powinny być renderowane w kolejności od najdalszych od źródła, do najbliższych.
        nrShades = 0;
        for (Area area : map.areas) { //iteracja po Shades - tych co dają cień
            if (!area.isBorder()) {
                if (area.isWhole()) {
                    tmp = area.getCollision();
                    if (tmp != null && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() / 2) + (tmp.getHeight() / 2))
                            && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() / 2) + (tmp.getWidth() / 2))) {
                        shades[nrShades++] = tmp;
                        tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                    }
                    for (Figure tmp : area.getParts()) {
                        if (tmp != null && !tmp.isLittable() && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() / 2) + (tmp.getHeight() / 2))
                                && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() / 2) + (tmp.getWidth() / 2))) {
                            shades[nrShades++] = tmp;
                            tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                        }
                    }
                } else {
                    for (Figure tmp : area.getParts()) {
                        if (tmp != null && (FastMath.abs(tmp.getCentralY() - src.getY()) <= (src.getLight().getSY() / 2) + (tmp.getHeight() / 2))
                                && (FastMath.abs(tmp.getCentralX() - src.getX()) <= (src.getLight().getSX() / 2) + (tmp.getWidth() / 2))) {
                            shades[nrShades++] = tmp;
                            tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
                        }
                    }
                }
            }
        }
        for (GameObject go : map.getDepthObjects()) {   // FGTiles muszą mieć Collision
            tmp = go.getCollision();
            if (tmp != null && tmp.isLittable() && ((FastMath.abs(tmp.getOwner().getY() - src.getY()) <= (src.getLight().getSY() / 2) + (tmp.getOwner().getCollisionHeight() / 2))
                    && (FastMath.abs(tmp.getOwner().getX() - src.getX()) <= (src.getLight().getSX() / 2) + (tmp.getOwner().getCollisionWidth() / 2)))) {
                shades[nrShades++] = tmp;
                tmp.setDistFromLight((src.getCollision() == tmp) ? -1 : FastMath.abs(src.getX() - tmp.getCentralX()));
            }
        }
        Arrays.sort(shades, 0, nrShades);
    }

    private static void calculateShadow(GameObject src, Figure thisShade) {
        findPoints(src, thisShade);
        findLeftSideOfShadow();
        findRightSideOfShadow();
    }

    private static void findPoints(GameObject src, Figure thisShade) {
        center.set(src.getX(), src.getY());
        angle = 0;
        for (int p = 0; p < thisShade.getPoints().size(); p++) {
            for (int s = p + 1; s < thisShade.getPoints().size(); s++) {
                temp = Methods.threePointAngle(thisShade.getPoint(p).getX(), thisShade.getPoint(p).getY(), thisShade.getPoint(s).getX(), thisShade.getPoint(s).getY(), center.getX(), center.getY());
                if (temp > angle) {
                    angle = temp;
                    firstShadowPoint = p;
                    secondShadowPoint = s;
                }
            }
        }
        points[0] = thisShade.getPoint(firstShadowPoint);
        points[1] = thisShade.getPoint(secondShadowPoint);
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

    private static void calculateWalls(Figure f, GameObject src) {
        findWalls(f, src);
        calculateLeftWall(f, src);
        calculateRightWall(f, src);
    }

    private static void findWalls(Figure figure, GameObject src) {
        left = right = null;
        for (int i = 0; i < nrShades; i++) {
            isChecked = false;
            other = shades[i];
            if (figure.getY() < src.getY() && other.isGiveShadow() && other != figure && other != src.getCollision()) {
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
                if ((points[0].getY() > points[2].getY() && points[0].getY() > other.getY() - other.getShadowHeight()) && ((points[0].getX() != points[2].getX() && ((XOL >= other.getX() && XOL <= other.getEndX()) || (YOL >= other.getY() - other.getShadowHeight() && YOL <= other.getEndY()) || (YOL2 >= other.getY() - other.getShadowHeight() && YOL2 <= other.getEndY()))) || (points[0].getX() == points[2].getX() && XOL >= other.getX() && XOL <= other.getEndX()))) {
                    if (left != null) {
                        distOther = Methods.pointDistanceSimple(other.getCentralX(), other.getCentralY(), figure.getCentralX(), figure.getCentralY());
                        distThis = Methods.pointDistanceSimple(left.getCentralX(), left.getCentralY(), figure.getCentralX(), figure.getCentralY());
                        left = (distOther > distThis) ? left : other;
                    } else {
                        left = other;
                    }
                    findLeftDark(figure, src);
                }
                if ((points[1].getY() > points[3].getY() && points[1].getY() > other.getY() - other.getShadowHeight()) && ((points[1].getX() != points[3].getX() && ((XOR >= other.getX() && XOR <= other.getEndX()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getEndY()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getEndY()))) || (points[1].getX() == points[3].getX() && XOR >= other.getX() && XOR <= other.getEndX()))) {
                    if (right != null) {
                        distOther = Methods.pointDistanceSimple(other.getCentralX(), other.getCentralY(), figure.getCentralX(), figure.getCentralY());
                        distThis = Methods.pointDistanceSimple(right.getCentralX(), right.getCentralY(), figure.getCentralX(), figure.getCentralY());
                        right = (distOther > distThis) ? right : other;
                    } else {
                        right = other;
                    }
                    findRightDark(figure, src);
                }
                findDarkness(figure, src);
            } else if (figure.getY() < src.getY() && !other.isGiveShadow() && other.isLittable() && other != figure && other != src.getCollision()) {
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
                    calculateLeftObject(other, figure, src);
                }
                if ((points[1].getY() > points[3].getY() && points[1].getY() > other.getY()) && ((points[1].getX() != points[3].getX() && ((XOR >= other.getOwnBegX() && XOR <= other.getOwnEndX()) || (YOR > other.getOwnBegY() && YOR < other.getOwnEndY()) || (YOR2 > other.getOwnBegY() && YOR2 < other.getOwnEndY()))) || (points[1].getX() == points[3].getX() && XOR >= other.getOwnBegX() && XOR <= other.getOwnEndX()))) {
                    isChecked = true;
                    calculateRightObject(other, figure, src);
                }
                findObjectDarkness(figure, src);
            }
        }
    }

    private static void calculateLeftWall(Figure f, GameObject src) {
        if (left != null && left.getEndY() < src.getY() && (left.getY() - left.getShadowHeight() < f.getY() || left.getEndY() < f.getEndY())) {
            if (points[0].getX() == points[2].getX()) {
                XL1 = points[0].getX();
                XL2 = left.getEndX();
            } else {
                XL1 = Methods.roundHalfUp((left.getEndY() - bl1) / al1);
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
            }
            if (XL1 >= left.getX() && XL1 <= left.getEndX()) {
                if (FastMath.abs(al1) > 0 && XL1 < f.getX()) { //dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XL1, left.getY() - left.getShadowHeight()), new Point(XL1, left.getEndY()),
                            new Point(XL2, left.getEndY()), new Point(XL2, left.getY() - left.getShadowHeight()));
                    left.addShadow(tmpShadow);
//                    System.out.println("Left Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XL1, left.getEndY()), new Point(XL1, left.getY() - left.getShadowHeight()),
                            new Point(XL2, left.getY() - left.getShadowHeight()), new Point(XL2, left.getEndY()));
                    left.addShadow(tmpShadow);
//                    System.out.println("Left Shade");
                }
            } else if (points[0].getX() != points[2].getX()) { // rysuj zaciemniony
                YOL = Methods.roundHalfUp(al1 * left.getX() + bl1);
                YOL2 = Methods.roundHalfUp(al1 * left.getEndX() + bl1);
                if ((XL1 != f.getX() && XL1 != left.getX() && XL1 != left.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOL > left.getY() - left.getShadowHeight() && YOL < left.getEndY()) || (YOL2 > left.getY() - left.getShadowHeight() && YOL2 < left.getEndY())) {
                    if (FastMath.abs(al1) >= 0 && XL1 < f.getX()) {
                        left.addShadow(shadow1);
                    } else {
                        left.addShadow(shadow0);
                    }
//                    System.out.println("Left Dark");
                }
            }
        }
    }

    private static void calculateLeftObject(Figure left, Figure figure, GameObject src) {
        if (left.getEndY() < src.getY() && (left.getY() < figure.getY() || left.getEndY() < figure.getEndY())) {
            if (points[0].getX() != points[2].getX()) {
                XL1 = Methods.roundHalfUp((left.getOwnEndY() - bl1) / al1);
                XL2 = al1 > 0 ? left.getOwner().getStartX() : left.getOwner().getStartX() + left.getWidth();
            } else {
                XL1 = points[0].getX();
                XL2 = left.getOwner().getStartX() + left.getWidth();
            }
            if (XL1 >= left.getOwnBegX() && XL1 <= left.getOwnEndX()) {
                if (FastMath.abs(al1) > 0 && XL1 < figure.getX()) { //dodaj światło
                    tmpShadow = new Shadow(4);
                    tmpShadow.addPoints(new Point(XL1 - left.getOwnBegX() + left.getOwner().getStartX(), XL2), null, null, null);
                    left.addShadow(tmpShadow);
//                    System.out.println("Left Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(5);
                    tmpShadow.addPoints(new Point(XL1 - left.getOwnBegX() + left.getOwner().getStartX(), XL2), null, null, null);
                    left.addShadow(tmpShadow);
//                    System.out.println("Left Shade");
                }
            } else if (points[0].getX() != points[2].getX()) { // rysuj zaciemniony
                YOL = Methods.roundHalfUp(al1 * left.getOwnBegX() + bl1);
                YOL2 = Methods.roundHalfUp(al1 * left.getOwnEndX() + bl1);
                if ((XL1 != figure.getX() && XL1 != left.getOwnBegX() && XL1 != left.getOwnEndX() && src.getX() != figure.getEndX() && src.getX() != figure.getX()) || (YOL > left.getOwnBegY() && YOL < left.getOwnEndY()) || (YOL2 > left.getOwnBegY() && YOL2 < left.getOwnEndY())) {
                    if (FastMath.abs(al1) >= 0 && XL1 < figure.getX()) {
                        left.addShadow(shadow1);
                    } else {
                        left.addShadow(shadow0);
                    }
//                    System.out.println("Left Dark");
                }
            }
        }
    }

    private static void findLeftDark(Figure figure, GameObject src) {
        if (points[0].getX() != points[2].getX() && other != null && other.getEndY() < src.getY() && (other.getY() - other.getShadowHeight() < figure.getY() || other.getEndY() < figure.getEndY())) {
            XL1 = Methods.roundHalfUp((other.getEndY() - bl1) / al1);
            if (((FastMath.abs(al1) <= 0 || XL1 < other.getX()) || XL1 > other.getEndX()) || XL1 >= figure.getX() && ((XL1 == figure.getX() || XL1 < other.getX()) || XL1 > other.getEndX())) {
                YOL = Methods.roundHalfUp(al1 * other.getX() + bl1);
                YOL2 = Methods.roundHalfUp(al1 * other.getEndX() + bl1);
                if ((XL1 != figure.getX() && XL1 != other.getX() && XL1 != other.getEndX() && src.getX() != figure.getEndX() && src.getX() != figure.getX()) || (YOL > YL && YOL < other.getEndY()) || (YOL2 > YL && YOL2 < other.getEndY())) {
                    if (FastMath.abs(al1) < 0 || XL1 >= figure.getX()) {
                        other.addShadow(shadow0);
                    }
//                    System.out.println("Left Darkness");
                }
            }
        }
    }

    private static void calculateRightWall(Figure f, GameObject src) {
        if (right != null && right.getEndY() < src.getY() && (right.getY() - right.getShadowHeight() < f.getY() || right.getEndY() < f.getEndY())) {
            if (points[1].getX() != points[3].getX()) {
                XR1 = Methods.roundHalfUp((right.getEndY() - bl2) / al2);
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
            } else {
                XR1 = points[1].getX();
                XR2 = right.getX();
            }
            if (XR1 >= right.getX() && XR1 <= right.getEndX()) {
                if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) { // dodaj światło
                    tmpShadow = new Shadow(2);
                    tmpShadow.addPoints(new Point(XR1, right.getY() - right.getShadowHeight()), new Point(XR1, right.getEndY()),
                            new Point(XR2, right.getEndY()), new Point(XR2, right.getY() - right.getShadowHeight()));
                    right.addShadow(tmpShadow);
//                    System.out.println("Right Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(3);
                    tmpShadow.addPoints(new Point(XR1, right.getEndY()), new Point(XR1, right.getY() - right.getShadowHeight()),
                            new Point(XR2, right.getY() - right.getShadowHeight()), new Point(XR2, right.getEndY()));
                    right.addShadow(tmpShadow);
//                    System.out.println("Right Shade");
                }
            } else if (points[1].getX() != points[3].getX()) { // rysuj zaciemniony
                YOR = Methods.roundHalfUp(al2 * right.getX() + bl2);
                YOR2 = Methods.roundHalfUp(al2 * right.getEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != right.getX() && XR1 != right.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > right.getY() - right.getShadowHeight() && YOR < right.getEndY()) || (YOR2 > right.getY() - right.getShadowHeight() && YOR2 < right.getEndY())) {
                    if (FastMath.abs(al2) >= 0 && XR1 > f.getEndX()) {
                        right.addShadow(shadow1);
                    } else {
                        right.addShadow(shadow0);
                    }
//                    System.out.println("Right Dark");
                }
            }
        }
    }

    private static void calculateRightObject(Figure right, Figure f, GameObject src) {
        if (right.getEndY() < src.getY() && (right.getY() < f.getY() || right.getEndY() < f.getEndY())) {
            if (points[1].getX() != points[3].getX()) {
                XR1 = Methods.roundHalfUp((right.getOwnEndY() - bl2) / al2);
                XR2 = al2 > 0 ? right.getOwner().getStartX() : right.getOwner().getStartX() + right.getWidth();
            } else {
                XR1 = points[1].getX();
                XR2 = right.getOwner().getStartX();
            }
            if (XR1 >= right.getOwnBegX() && XR1 <= right.getOwnEndX()) {
                if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) { // dodaj światło
                    tmpShadow = new Shadow(4);
                    tmpShadow.addPoints(new Point(XR1 - right.getOwnBegX() + right.getOwner().getStartX(), XR2), null, null, null);
                    right.addShadow(tmpShadow);
//                    System.out.println("Right Light");
                } else { //dodaj cień
                    tmpShadow = new Shadow(5);
                    tmpShadow.addPoints(new Point(XR1 - right.getOwnBegX() + right.getOwner().getStartX(), XR2), null, null, null);
                    right.addShadow(tmpShadow);
//                    System.out.println("Right Shade");
                }
            } else if (points[1].getX() != points[3].getX()) { // rysuj zaciemniony
                YOR = Methods.roundHalfUp(al2 * right.getOwnBegX() + bl2);
                YOR2 = Methods.roundHalfUp(al2 * right.getOwnEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != right.getOwnBegX() && XR1 != right.getOwnEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > right.getOwnBegY() && YOR < right.getOwnEndY()) || (YOR2 > right.getOwnBegY() && YOR2 < right.getOwnEndY())) {
                    if (FastMath.abs(al2) > 0 && XR1 > f.getEndX()) {
                        right.addShadow(shadow1);
                    } else {
                        right.addShadow(shadow0);
                    }
//                    System.out.println("Right Dark");
                }
            }
        }
    }

    private static void findRightDark(Figure f, GameObject src) {
        if (points[1].getX() != points[3].getX() && other != null && other.getEndY() < src.getY() && (other.getY() - other.getShadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            XR1 = Methods.roundHalfUp((other.getEndY() - bl2) / al2);
            if (((FastMath.abs(al2) <= 0 || XR1 < other.getX()) || XR1 > other.getEndX()) || XR1 <= f.getEndX() && ((XR1 == f.getEndX() || XR1 < other.getX()) || XR1 > other.getEndX())) {
                YOR = Methods.roundHalfUp(al2 * other.getX() + bl2);
                YOR2 = Methods.roundHalfUp(al2 * other.getEndX() + bl2);
                if ((XR1 != f.getEndX() && XR1 != other.getX() && XR1 != other.getEndX() && src.getX() != f.getEndX() && src.getX() != f.getX()) || (YOR > other.getY() && YOR < other.getEndY()) || (YOR2 > other.getY() && YOR2 < other.getEndY())) {
                    if (FastMath.abs(al2) < 0 || XR1 <= f.getEndX()) {
                        other.addShadow(shadow0);
//                        System.out.println("Right Darkness");
                    }
                }
            }
        }
    }

    private static void findDarkness(Figure f, GameObject src) {
        if (other != left && other != right && other.getEndY() < src.getY() && (other.getY() - other.getShadowHeight() < f.getY() || other.getEndY() < f.getEndY())) {
            poly.reset();
            poly.addPoint(points[0].getX(), points[0].getY());
            poly.addPoint(points[1].getX(), points[1].getY());
            poly.addPoint(points[3].getX(), points[3].getY());
            poly.addPoint(points[2].getX(), points[2].getY());
            if (poly.contains(other.getX(), other.getY() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
                other.addShadow(shadow0);
//                System.out.println("Darkness...");
            }
        }
    }

    private static void findObjectDarkness(Figure figure, GameObject src) {
        if (!isChecked && other.getEndY() < src.getY() && (other.getY() < figure.getY() || other.getEndY() < figure.getEndY())) {
            poly.reset();
            poly.addPoint(points[0].getX(), points[0].getY());
            poly.addPoint(points[1].getX(), points[1].getY());
            poly.addPoint(points[3].getX(), points[3].getY());
            poly.addPoint(points[2].getX(), points[2].getY());
            if (poly.contains(other.getOwnBegX(), other.getOwnBegY() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
                other.addShadow(shadow0);
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
        glTranslatef((lX / 2) - emitter.getX(), (lY / 2) - emitter.getY() + h - lY, 0);
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
        glTranslatef((emitter.getLight().getSX() / 2) - emitter.getX(), (lightY / 2) - emitter.getY() + h - lightY, 0);
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
        glTranslatef(emitter.getX() - (lightX / 2) + cam.getXOffEffect(), emitter.getY() - (lightY / 2) + cam.getYOffEffect(), 0);
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
        shads[0] = (GameObject emitter1, Figure shade1, Point[] points1) -> {
            shade1.getOwner().renderShadow((emitter1.getLight().getSX() / 2) - (emitter1.getX()), (emitter1.getLight().getSY() / 2) - (emitter1.getY()) + h - emitter1.getLight().getSY(), shade1);
        };
        shads[1] = (GameObject emitter1, Figure shade1, Point[] points1) -> {
            shade1.getOwner().renderShadowLit((emitter1.getLight().getSX() / 2) - (emitter1.getX()), (emitter1.getLight().getSY() / 2) - (emitter1.getY()) + h - emitter1.getLight().getSY(), shade1.getShadowColor(), shade1);
        };
        shads[2] = (GameObject emitter1, Figure shade1, Point[] points1) -> {
            drawWall(emitter1, points1, shade1.getShadowColor());
        };
        shads[3] = (GameObject emitter1, Figure shad, Point[] points1) -> {
            drawWall(emitter1, points1, 0);
        };
        shads[4] = (GameObject emitter1, Figure shad, Point[] points1) -> {
            shade.getOwner().renderShadowLit((emitter1.getLight().getSX() / 2) - (emitter1.getX()), (emitter1.getLight().getSY() / 2) - (emitter1.getY()) + h - emitter1.getLight().getSY(), shade.getShadowColor(), shade, points1[0].getX(), points1[0].getY());
        };
        shads[5] = (GameObject emitter1, Figure shad, Point[] points1) -> {
            shade.getOwner().renderShadow((emitter1.getLight().getSX() / 2) - (emitter1.getX()), (emitter1.getLight().getSY() / 2) - (emitter1.getY()) + h - emitter1.getLight().getSY(), shade, points1[0].getX(), points1[0].getY());
        };

    }

    private interface renderShadow {

        void render(GameObject emitter, Figure shad, Point[] points);
    }

    private ShadowRenderer() {
    }
}
