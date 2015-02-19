/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import static game.place.Shadow.*;
import game.place.cameras.Camera;
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
	private static int shDif, lightX, lightY, distOther, distThis, firstShadowPoint, secondShadowPoint, shX, shY, XL1, XL2, XR1, XR2, YL;
	private static double angle, temp, al1, bl1, al2, bl2, XOL, XOR, YOL, YOL2, YOR, YOR2;
	private static Shadow tempShadow;
	private static final Shadow shadow0 = new Shadow(0), shadow1 = new Shadow(1);
	private static final ArrayList<Shadow> shadowsDarken = new ArrayList<>(), shadowsBrighten = new ArrayList<>();
	private static final renderShadow[] shads = new renderShadow[6];
	private static float lightHeaightHalf;
	private static boolean isChecked;
	private static final Polygon poly = new Polygon();

	public static void prerenderLight(Map map, GameObject emitter) {
		findShades(emitter, map);
		emitter.getLight().frameBufferObject.activate();
		clearFBO(1);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);

		lightHeaightHalf = (float) (emitter.getCollisionHeight() / 2);
		for (Figure shade : shades) {			//iteracja po Shades - tych co dają cień
			if (shade != emitter.getCollision()) {
				if (shade.isGiveShadow()) {
					calculateShadow(emitter, shade);
					drawShadow(emitter);
					calculateWalls(shade, emitter);
					if (shade.isLittable() && emitter.getY() >= shade.getYEnd()) {
						shade.setShadowColor((emitter.getY() - shade.getYEnd()) / lightHeaightHalf);
						shade.getOwner().renderShadowLit((emitter.getLight().getWidth() / 2) - (emitter.getX()),
								(emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade.getShadowColor(), shade);
						shade.addShadow(shadow1);
					} else {
						shade.addShadow(shadow0);
					}
				} else if (shade.isLittable() && emitter.getY() >= shade.getYEnd()) {
					shade.setShadowColor((emitter.getY() - shade.getYEnd()) / lightHeaightHalf);
					shade.getOwner().renderShadowLit((emitter.getLight().getWidth() / 2) - (emitter.getX()),
							(emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade.getShadowColor(), shade);
					shade.addShadow(shadow1);
				} else {
					shade.addShadow(shadow0);
				}
			} else {
				shade.setShadowColor(1);
				shade.addShadow(shadow1);
			}
		}

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
		emitter.getLight().render(displayHeight - emitter.getLight().getHeight());
		emitter.getLight().frameBufferObject.deactivate();
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

	private static void findShades(GameObject source, Map map) {
		shades.clear();
		map.areas.stream().forEach((area) -> {
			tempShade = area.getCollision();
			if (tempShade != null && (FastMath.abs(tempShade.getYCentral() - source.getY()) <= (source.getLight().getHeight() + tempShade.getHeight()) / 2)
					&& (FastMath.abs(tempShade.getXCentral() - source.getX()) <= (source.getLight().getWidth() + tempShade.getWidth()) / 2)) {
				shades.add(tempShade);
			}
		});
		for (GameObject object : map.getForegroundTiles()) {
			tempShade = object.getCollision();
			if (tempShade != null && !tempShade.isLittable() && (FastMath.abs(tempShade.getYCentral() - source.getY()) <= (source.getLight().getHeight() + tempShade.getHeight()))
					&& (FastMath.abs(tempShade.getXCentral() - source.getX()) <= (source.getLight().getWidth() + tempShade.getWidth()))) {
				shades.add(tempShade);
			}
		}
		for (GameObject object : map.getDepthObjects()) {
			tempShade = object.getCollision();
			if (tempShade != null && tempShade.isLittable() && ((FastMath.abs(tempShade.getOwner().getY() - source.getY()) <= (source.getLight().getHeight() + tempShade.getOwner().getCollisionHeight()) / 2)
					&& (FastMath.abs(tempShade.getOwner().getX() - source.getX()) <= (source.getLight().getWidth() + tempShade.getOwner().getCollisionWidth()) / 2))) {
				shades.add(tempShade);
			}
		}
		Collections.sort(shades);
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
		shadowPoints[0] = thisShade.getPoint(firstShadowPoint);
		shadowPoints[1] = thisShade.getPoint(secondShadowPoint);
		shDif = 32768;
	}

	private static void findLeftSideOfShadow() {
		if (shadowPoints[0].getX() == center.getX()) {
			shadowPoints[2].set(shadowPoints[0].getX(), shadowPoints[0].getY() + (shadowPoints[0].getY() > center.getY() ? shDif : -shDif));
		} else if (shadowPoints[0].getY() == center.getY()) {
			shadowPoints[2].set(shadowPoints[0].getX() + (shadowPoints[0].getX() > center.getX() ? shDif : -shDif), shadowPoints[0].getY());
		} else {
			al1 = (center.getY() - shadowPoints[0].getY()) / (double) (center.getX() - shadowPoints[0].getX());
			bl1 = shadowPoints[0].getY() - al1 * shadowPoints[0].getX();
			if (al1 > 0) {
				shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? shDif : -shDif);
				shY = (int) (al1 * shX + bl1);
			} else if (al1 < 0) {
				shX = shadowPoints[0].getX() + (shadowPoints[0].getY() > center.getY() ? -shDif : shDif);
				shY = (int) (al1 * shX + bl1);
			} else {
				shX = shadowPoints[0].getX();
				shY = shadowPoints[0].getY() + (shadowPoints[0].getY() > center.getY() ? shDif : -shDif);
			}
			shadowPoints[2].set(shX, shY);
		}
	}

	private static void findRightSideOfShadow() {
		if (shadowPoints[1].getX() == center.getX()) {
			shadowPoints[3].set(shadowPoints[1].getX(), shadowPoints[1].getY() + (shadowPoints[1].getY() > center.getY() ? shDif : -shDif));
		} else if (shadowPoints[1].getY() == center.getY()) {
			shadowPoints[3].set(shadowPoints[1].getX() + (shadowPoints[1].getX() > center.getX() ? shDif : -shDif), shadowPoints[1].getY());
		} else {
			al2 = (center.getY() - shadowPoints[1].getY()) / (double) (center.getX() - shadowPoints[1].getX());
			bl2 = shadowPoints[1].getY() - al2 * shadowPoints[1].getX();
			if (al2 > 0) {
				shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? shDif : -shDif);
				shY = (int) (al2 * shX + bl2);
			} else if (al2 < 0) {
				shX = shadowPoints[1].getX() + (shadowPoints[1].getY() > center.getY() ? -shDif : shDif);
				shY = (int) (al2 * shX + bl2);
			} else {
				shX = shadowPoints[1].getX();
				shY = shadowPoints[1].getY() + (shadowPoints[1].getY() > center.getY() ? shDif : -shDif);
			}
			shadowPoints[3].set(shX, shY);
		}
	}

	private static void calculateWalls(Figure figure, GameObject source) {
		left = right = null;
		for (Figure other : shades) {
			isChecked = false;
			if (figure.getY() < source.getY() && other.isGiveShadow() && other != figure && other != source.getCollision() && figure.getYEnd() != other.getYEnd()) {
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
					Figure same = left;
					if (left != null) {
						distOther = Methods.pointDistanceSimple(other.getXCentral(), other.getYCentral(), figure.getXCentral(), figure.getYCentral());
						distThis = Methods.pointDistanceSimple(left.getXCentral(), left.getYCentral(), figure.getXCentral(), figure.getYCentral());
						left = (distOther > distThis) ? left : other;
					} else {
						left = other;
					}
					if (left != same) {
						calculateLeftWall(figure, source);
					}
				}
				if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY() - other.getShadowHeight()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getX() && XOR <= other.getXEnd()) || (YOR >= other.getY() - other.getShadowHeight() && YOR <= other.getYEnd()) || (YOR2 >= other.getY() - other.getShadowHeight() && YOR2 <= other.getYEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getX() && XOR <= other.getXEnd()))) {
					Figure same = right;
					if (right != null) {
						distOther = Methods.pointDistanceSimple(other.getXCentral(), other.getYCentral(), figure.getXCentral(), figure.getYCentral());
						distThis = Methods.pointDistanceSimple(right.getXCentral(), right.getYCentral(), figure.getXCentral(), figure.getYCentral());
						right = (distOther > distThis) ? right : other;
					} else {
						right = other;
					}
					if (right != same) {
						calculateRightWall(figure, source);
					}
				}
				findDarkness(figure, other, source);
			} else if (figure.getY() < source.getY() && !other.isGiveShadow() && other.isLittable() && other != figure && other != source.getCollision()) {
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
					calculateLeftObject(other, figure, source);
				}
				if ((shadowPoints[1].getY() > shadowPoints[3].getY() && shadowPoints[1].getY() > other.getY()) && ((shadowPoints[1].getX() != shadowPoints[3].getX() && ((XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()) || (YOR > other.getYOwnerBegin() && YOR < other.getYOwnerEnd()) || (YOR2 > other.getYOwnerBegin() && YOR2 < other.getYOwnerEnd()))) || (shadowPoints[1].getX() == shadowPoints[3].getX() && XOR >= other.getXOwnerBegin() && XOR <= other.getXOwnerEnd()))) {
					isChecked = true;
					calculateRightObject(other, figure, source);
				}
				findObjectDarkness(figure, other, source);
			}
		}
	}

	private static final boolean DEBUG = false;

	private static void calculateLeftWall(Figure figure, GameObject source) {
		if (left != null && left.getYEnd() < source.getY() && (left.getY() - left.getShadowHeight() < figure.getY() || left.getYEnd() < figure.getYEnd())) {
			if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
				XL1 = shadowPoints[0].getX();
				XL2 = left.getXEnd();
			} else {
				XL1 = Methods.roundHalfUp((left.getYEnd() - bl1) / al1);
				XL2 = al1 > 0 ? left.getX() : left.getXEnd();
			}
			if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
				XR1 = shadowPoints[1].getX();
			} else {
				XR1 = Methods.roundHalfUp((left.getYEnd() - bl2) / al2);
			}
			if (XL1 >= left.getX() && XL1 <= left.getXEnd()) {
				if (FastMath.abs(al1) > 0 && (XL1 < figure.getX() || XL1 == left.getXEnd())) { //dodaj światło
					tempShadow = new Shadow(2);
					tempShadow.addPoints(new Point(XL1, left.getY() - left.getShadowHeight()), new Point(XL1, left.getYEnd()),
							new Point(XL2, left.getYEnd()), new Point(XL2, left.getY() - left.getShadowHeight()));
					left.addShadow(tempShadow);
					if (DEBUG) {
						System.out.println("Left Light");
					}
				} else { //dodaj cień
					if (XR1 < XL2 && XR1 > left.getX() && shadowPoints[3].getY() < figure.getYEnd()) {
						XL2 = XR1;
					}
					tempShadow = new Shadow(3);
					tempShadow.addPoints(new Point(XL1, left.getYEnd()), new Point(XL1, left.getY() - left.getShadowHeight()),
							new Point(XL2, left.getY() - left.getShadowHeight()), new Point(XL2, left.getYEnd()));
					left.addShadow(tempShadow);
					if (DEBUG) {
						System.out.println("Left Shade " + al1 + " XL1 " + XL1 + " figure.X " + figure.getX());
					}
				}
			} else if (shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
				YOL = Methods.roundHalfUp(al1 * left.getX() + bl1);
				YOL2 = Methods.roundHalfUp(al1 * left.getXEnd() + bl1);
				if ((XL1 != figure.getX() && XL1 != left.getX() && XL1 != left.getXEnd() && source.getX() != figure.getXEnd() && source.getX() != figure.getX()) || (YOL > left.getY() - left.getShadowHeight() && YOL < left.getYEnd()) || (YOL2 > left.getY() - left.getShadowHeight() && YOL2 < left.getYEnd())) {
					if (FastMath.abs(al1) >= 0 && XL1 < figure.getX()) {
						left.addShadow(shadow1);
						if (DEBUG) {
							System.out.println("Left Lightness - first");
						}
					} else {
						left.addShadow(shadow0);
						if (DEBUG) {
							System.out.println("Left Darkness - first");
						}
					}
				}
			}
		}
	}

	private static void calculateLeftObject(Figure left, Figure figure, GameObject source) {
		if (left.getYEnd() < source.getY() && (left.getY() < figure.getY() || left.getYEnd() < figure.getYEnd())) {
			if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
				XL1 = shadowPoints[0].getX();
				XL2 = left.getOwner().getStartX() + left.getWidth();
			} else {
				XL1 = Methods.roundHalfUp((left.getYOwnerEnd() - bl1) / al1);
				XL2 = al1 > 0 ? left.getOwner().getStartX() : left.getOwner().getStartX() + left.getWidth();
			}
			if (XL1 >= left.getXOwnerBegin() && XL1 <= left.getXOwnerEnd()) {
				if (FastMath.abs(al1) > 0 && XL1 < figure.getX()) { //dodaj światło
					tempShadow = new Shadow(4);
					tempShadow.addPoints(new Point(XL1 - left.getXOwnerBegin() + left.getOwner().getStartX(), XL2), null, null, null);
					left.addShadow(tempShadow);
//                    System.out.println("Left Light");
				} else { //dodaj cień
					tempShadow = new Shadow(5);
					tempShadow.addPoints(new Point(XL1 - left.getXOwnerBegin() + left.getOwner().getStartX(), XL2), null, null, null);
					left.addShadow(tempShadow);
//                    System.out.println("Left Shade");
				}
			} else if (shadowPoints[0].getX() != shadowPoints[2].getX()) { // rysuj zaciemniony
				YOL = Methods.roundHalfUp(al1 * left.getXOwnerBegin() + bl1);
				YOL2 = Methods.roundHalfUp(al1 * left.getXOwnerEnd() + bl1);
				if ((XL1 != figure.getX() && XL1 != left.getXOwnerBegin() && XL1 != left.getXOwnerEnd() && source.getX() != figure.getXEnd() && source.getX() != figure.getX()) || (YOL > left.getYOwnerBegin() && YOL < left.getYOwnerEnd()) || (YOL2 > left.getYOwnerBegin() && YOL2 < left.getYOwnerEnd())) {
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

	private static void findLeftDark(Figure current, Figure other, GameObject source) {
		if (other != null && other.getYEnd() < source.getY() && (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd())) {
			XL1 = Methods.roundHalfUp((other.getYEnd() - bl1) / al1);
			if (shadowPoints[0].getX() != shadowPoints[2].getX()) {
				if (((FastMath.abs(al1) <= 0 || XL1 < other.getX()) || XL1 > other.getXEnd()) || XL1 >= current.getX() && ((XL1 == current.getX() || XL1 < other.getX()) || XL1 > other.getXEnd())) {
					YOL = Methods.roundHalfUp(al1 * other.getX() + bl1);
					YOL2 = Methods.roundHalfUp(al1 * other.getXEnd() + bl1);
					if ((XL1 != current.getX() && XL1 != other.getX() && XL1 != other.getXEnd() && source.getX() != current.getXEnd() && source.getX() != current.getX()) || (YOL > YL && YOL < other.getYEnd()) || (YOL2 > YL && YOL2 < other.getYEnd())) {
						if (FastMath.abs(al1) < 0 || XL1 >= current.getX()) {
							other.addShadow(shadow0);
							if (DEBUG) {
								System.out.println("Left Darkness");
							}
						}
					}
//                } else if (XL1 == other.getX()) {
//                    other.addShadow(shadow0);
//                    System.out.println("Left Darkness - patched");
				}
//            } else if (XL1 == other.getXEnd()) {
//                other.addShadow(shadow0);
//                System.out.println("Left Darkness - patched 2");
			}
		}
	}

	private static void calculateRightWall(Figure figure, GameObject source) {
		if (right != null && right.getYEnd() < source.getY() && (right.getY() - right.getShadowHeight() < figure.getY() || right.getYEnd() < figure.getYEnd())) {
			if (shadowPoints[1].getX() == shadowPoints[3].getX()) {
				XR1 = shadowPoints[1].getX();
				XR2 = right.getX();
			} else {
				XR1 = Methods.roundHalfUp((right.getYEnd() - bl2) / al2);
				XR2 = al2 > 0 ? right.getX() : right.getXEnd();
			}
			if (shadowPoints[0].getX() == shadowPoints[2].getX()) {
				XL1 = shadowPoints[0].getX();
			} else {
				XL1 = Methods.roundHalfUp((right.getYEnd() - bl1) / al1);
			}
			if (XR1 >= right.getX() && XR1 <= right.getXEnd()) {
				if (FastMath.abs(al2) > 0 && (XR1 > figure.getXEnd() || XR1 == right.getX())) { // dodaj światło

					tempShadow = new Shadow(2);
					tempShadow.addPoints(new Point(XR1, right.getY() - right.getShadowHeight()), new Point(XR1, right.getYEnd()),
							new Point(XR2, right.getYEnd()), new Point(XR2, right.getY() - right.getShadowHeight()));
					right.addShadow(tempShadow);
					if (DEBUG) {
						System.out.println("Right Light " + " XR1 " + XR1);
					}
				} else { //dodaj cień
					if (XL1 > XR2 && XL1 < right.getXEnd() && shadowPoints[2].getY() < figure.getYEnd()) {
						XR2 = XL1;
					}
					tempShadow = new Shadow(3);
					tempShadow.addPoints(new Point(XR1, right.getYEnd()), new Point(XR1, right.getY() - right.getShadowHeight()),
							new Point(XR2, right.getY() - right.getShadowHeight()), new Point(XR2, right.getYEnd()));
					right.addShadow(tempShadow);
					if (DEBUG) {
						System.out.println("Right Shade " + al2 + " XR1 " + XR1 + " figure.X " + figure.getX());
					}
				}
			} else if (shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
				YOR = Methods.roundHalfUp(al2 * right.getX() + bl2);
				YOR2 = Methods.roundHalfUp(al2 * right.getXEnd() + bl2);
				if ((XR1 != figure.getXEnd() && XR1 != right.getX() && XR1 != right.getXEnd() && source.getX() != figure.getXEnd() && source.getX() != figure.getX()) || (YOR > right.getY() - right.getShadowHeight() && YOR < right.getYEnd()) || (YOR2 > right.getY() - right.getShadowHeight() && YOR2 < right.getYEnd())) {
					if (FastMath.abs(al2) >= 0 && XR1 > figure.getXEnd()) {
						right.addShadow(shadow1);
						if (DEBUG) {
							System.out.println("Right Lightness - first");
						}
					} else {
						right.addShadow(shadow0);
						if (DEBUG) {
							System.out.println("Right Darkness - first");
						}
					}

				}
			}
		}
	}

	private static void calculateRightObject(Figure right, Figure f, GameObject src) {
		if (right.getYEnd() < src.getY() && (right.getY() < f.getY() || right.getYEnd() < f.getYEnd())) {
			if (shadowPoints[1].getX() != shadowPoints[3].getX()) {
				XR1 = Methods.roundHalfUp((right.getYOwnerEnd() - bl2) / al2);
				XR2 = al2 > 0 ? right.getOwner().getStartX() : right.getOwner().getStartX() + right.getWidth();
			} else {
				XR1 = shadowPoints[1].getX();
				XR2 = right.getOwner().getStartX();
			}
			if (XR1 >= right.getXOwnerBegin() && XR1 <= right.getXOwnerEnd()) {
				if (FastMath.abs(al2) > 0 && XR1 > f.getXEnd()) { // dodaj światło
					tempShadow = new Shadow(4);
					tempShadow.addPoints(new Point(XR1 - right.getXOwnerBegin() + right.getOwner().getStartX(), XR2), null, null, null);
					right.addShadow(tempShadow);
//                    System.out.println("Right Light");
				} else { //dodaj cień
					tempShadow = new Shadow(5);
					tempShadow.addPoints(new Point(XR1 - right.getXOwnerBegin() + right.getOwner().getStartX(), XR2), null, null, null);
					right.addShadow(tempShadow);
//                    System.out.println("Right Shade");
				}
			} else if (shadowPoints[1].getX() != shadowPoints[3].getX()) { // rysuj zaciemniony
				YOR = Methods.roundHalfUp(al2 * right.getXOwnerBegin() + bl2);
				YOR2 = Methods.roundHalfUp(al2 * right.getXOwnerEnd() + bl2);
				if ((XR1 != f.getXEnd() && XR1 != right.getXOwnerBegin() && XR1 != right.getXOwnerEnd() && src.getX() != f.getXEnd() && src.getX() != f.getX()) || (YOR > right.getYOwnerBegin() && YOR < right.getYOwnerEnd()) || (YOR2 > right.getYOwnerBegin() && YOR2 < right.getYOwnerEnd())) {
					if (FastMath.abs(al2) > 0 && XR1 > f.getXEnd()) {
						right.addShadow(shadow1);
					} else {
						right.addShadow(shadow0);
					}
//                    System.out.println("Right Dark");
				}
			}
		}
	}

	private static void findRightDark(Figure current, Figure other, GameObject src) {
		if (other != null && other.getYEnd() < src.getY() && (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd())) {
			if (shadowPoints[1].getX() != shadowPoints[3].getX()) {
				XR1 = Methods.roundHalfUp((other.getYEnd() - bl2) / al2);
				if (((FastMath.abs(al2) <= 0 || XR1 < other.getX()) || XR1 > other.getXEnd()) || XR1 <= current.getXEnd() && ((XR1 == current.getXEnd() || XR1 < other.getX()) || XR1 > other.getXEnd())) {
					YOR = Methods.roundHalfUp(al2 * other.getX() + bl2);
					YOR2 = Methods.roundHalfUp(al2 * other.getXEnd() + bl2);
					if ((XR1 != current.getXEnd() && XR1 != other.getX() && XR1 != other.getXEnd() && src.getX() != current.getXEnd() && src.getX() != current.getX()) || (YOR > other.getY() && YOR < other.getYEnd()) || (YOR2 > other.getY() && YOR2 < other.getYEnd())) {
						if (FastMath.abs(al2) < 0 || XR1 <= current.getXEnd()) {
							other.addShadow(shadow0);
							if (DEBUG) {
								System.out.println("Right Darkness");
							}
						}
					}
//                } else if (XR1 == other.getXEnd()) {
//                    other.addShadow(shadow0);
//                    System.out.println("Right Darkness - patched");
				}
//            } else if ((points[1].getX() == other.getX() || points[1].getX() == other.getXEnd()) && points[1].getX() < points[3].getX() ) {
//                other.addShadow(shadow0);
//                System.out.println("Right Darkness - patched 2");
			}

		}
	}

	private static void findDarkness(Figure current, Figure other, GameObject src) {
		if (other != left && other != right && other.getYEnd() < src.getY() && (other.getY() - other.getShadowHeight() < current.getY() || other.getYEnd() < current.getYEnd())) {
			poly.reset();
			poly.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
			poly.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
			poly.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
			poly.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
			if (poly.contains(other.getX(), other.getY() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
				other.addShadow(shadow0);
				if (DEBUG) {
					System.out.println("Darkness...");
				}
			}
		}
	}

	private static void findObjectDarkness(Figure current, Figure other, GameObject src) {
		if (!isChecked && other.getYEnd() < src.getY() && (other.getY() < current.getY() || other.getYEnd() < current.getYEnd())) {
			poly.reset();
			poly.addPoint(shadowPoints[0].getX(), shadowPoints[0].getY());
			poly.addPoint(shadowPoints[1].getX(), shadowPoints[1].getY());
			poly.addPoint(shadowPoints[3].getX(), shadowPoints[3].getY());
			poly.addPoint(shadowPoints[2].getX(), shadowPoints[2].getY());
			if (poly.contains(other.getXOwnerBegin(), other.getYOwnerBegin() - other.getShadowHeight(), other.getWidth(), other.getHeight() + other.getShadowHeight())) {
				other.addShadow(shadow0);
//                System.out.println("Darkness...");
			}
		}
	}

	private static void drawWall(GameObject emitter, Point[] points, float color) {
		int xLight = emitter.getLight().getWidth();
		int yLight = emitter.getLight().getHeight();
		glDisable(GL_TEXTURE_2D);
		glColor3f(color, color, color);
		glPushMatrix();

		glTranslatef((xLight / 2) - emitter.getX(), (yLight / 2) - emitter.getY() + displayHeight - yLight, 0);
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
		lightY = emitter.getLight().getHeight();
		glDisable(GL_TEXTURE_2D);
		glColor3f(0, 0, 0);
		glPushMatrix();
		glTranslatef((emitter.getLight().getWidth() / 2) - emitter.getX(), (lightY / 2) - emitter.getY() + displayHeight - lightY, 0);
		glBegin(GL_QUADS);
		glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
		glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
		glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());
		glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
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

	public static void drawLight(int textureHandle, GameObject emitter, Camera cam) {
		lightX = emitter.getLight().getWidth();
		lightY = emitter.getLight().getHeight();
		glPushMatrix();
		glTranslatef(emitter.getX() - (lightX / 2) + cam.getXOffsetEffect(), emitter.getY() - (lightY / 2) + cam.getYOffsetEffect(), 0);
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

	public static void initializeRenderer() {
		shadowPoints[0] = new Point(0, 0);
		shadowPoints[1] = new Point(0, 0);
		shadowPoints[2] = new Point(0, 0);
		shadowPoints[3] = new Point(0, 0);
		shads[0] = (GameObject emitter, Figure shade, Point[] points) -> {
			shade.getOwner().renderShadow((emitter.getLight().getWidth() / 2) - (emitter.getX()), (emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade);
		};
		shads[1] = (GameObject emitter, Figure shade, Point[] points) -> {
			shade.getOwner().renderShadowLit((emitter.getLight().getWidth() / 2) - (emitter.getX()), (emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade.getShadowColor(), shade);
		};
		shads[2] = (GameObject emitter, Figure shade, Point[] points) -> {
			drawWall(emitter, points, shade.getShadowColor());
		};
		shads[3] = (GameObject emitter, Figure shade, Point[] points) -> {
			drawWall(emitter, points, 0);
		};
		shads[4] = (GameObject emitter, Figure shade, Point[] points) -> {
			shade.getOwner().renderShadowLit((emitter.getLight().getWidth() / 2) - (emitter.getX()), (emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade.getShadowColor(), shade, points[0].getX(), points[0].getY());
		};
		shads[5] = (GameObject emitter, Figure shade, Point[] points) -> {
			shade.getOwner().renderShadow((emitter.getLight().getWidth() / 2) - (emitter.getX()), (emitter.getLight().getHeight() / 2) - (emitter.getY()) + displayHeight - emitter.getLight().getHeight(), shade, points[0].getX(), points[0].getY());
		};

	}

	private interface renderShadow {

		void render(GameObject emitter, Figure shad, Point[] points);
	}

	private ShadowRenderer() {
	}
}
