package engine.utilities;

import collision.Figure;
import collision.Rectangle;
import collision.RoundRectangle;
import game.place.Place;
import net.jodk.lang.FastMath;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Line2D;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Wojtek
 */
public class Methods {

    public static double SQRT_ROOT_OF_2 = Math.sqrt(2);
    public static double ONE_BY_SQRT_ROOT_OF_2 = 1 / SQRT_ROOT_OF_2;

    public static double xRadius(double angle, double rad) {
        return FastMath.cos(FastMath.toRadians(angle)) * rad;
    }

    public static double yRadius(double angle, double rad) {
        return FastMath.sin(FastMath.toRadians(angle)) * rad;
    }

    public static double xRadius8Directions(int angle, double rad) {
        switch (angle) {
            case 0:
                return rad;
            case 1:
                return rad * ONE_BY_SQRT_ROOT_OF_2;
            case 2:
                return 0;
            case 3:
                return -rad * ONE_BY_SQRT_ROOT_OF_2;
            case 4:
                return -rad;
            case 5:
                return -rad * ONE_BY_SQRT_ROOT_OF_2;
            case 6:
                return 0;
            case 7:
                return rad * ONE_BY_SQRT_ROOT_OF_2;
            default:
                return 0;
        }
    }

    public static double yRadius8Directions(int angle, double rad) {
        switch (angle) {
            case 0:
                return 0;
            case 1:
                return rad * ONE_BY_SQRT_ROOT_OF_2;
            case 2:
                return rad;
            case 3:
                return rad * ONE_BY_SQRT_ROOT_OF_2;
            case 4:
                return 0;
            case 5:
                return -rad * ONE_BY_SQRT_ROOT_OF_2;
            case 6:
                return -rad;
            case 7:
                return -rad * ONE_BY_SQRT_ROOT_OF_2;
            default:
                return 0;
        }
    }

    public static int angleDifference(int angleA, int angleB) {
        return (angleA = angleB - angleA) > 180 ? angleA - 360 : (angleA < -180 ? angleA + 360 : angleA);
    }

    public static int angleDifference8Directions(int angleA, int angleB) {
        return (angleA = angleB - angleA) > 4 ? angleA - 8 : (angleA < -4 ? angleA + 8 : angleA);
    }

    public static int pointDistance(int x, int y, int xa, int ya) {
        int xDDelta = xa - x;
        int yDDelta = ya - y;
        return (int) FastMath.sqrt(xDDelta * xDDelta + yDDelta * yDDelta);
    }

    public static int pointDistanceSimple(int x, int y, int xa, int ya) {
        return FastMath.abs(xa - x) + FastMath.abs(ya - y);
    }

    public static int pointDistanceSimple2(int x, int y, int xa, int ya) {
        int yIDelta = ya - y;
        int xIDelta = xa - x;
        return xIDelta * xIDelta + yIDelta * yIDelta;
    }

    public static int pointDifference(int x, int y, int xa, int ya) {
        return FastMath.max(Math.abs(xa - x), Math.abs(ya - y));
    }

    public static double pointAngle(int xSt, int ySt, int xEn, int yEn) {
        return FastMath.atan2(yEn - ySt, xEn - xSt) * 180 / FastMath.PI;
    }

    public static int pointAngle8Directions(double xSt, double ySt, double xEn, double yEn) {
        double temp = -FastMath.atan2(yEn - ySt, xEn - xSt) * 4 / FastMath.PI + 0.5;
        temp = temp >= 0 ? temp : (temp + 8) % 8;
        return (int) temp;
    }

    public static double pointAngleCounterClockwise(double xSt, double ySt, double xEn, double yEn) {    //0 <=> PRAWO; 90 <=> GÓRA; 180 <=> LEWO; 270 <=> DÓŁ;
        double det = -FastMath.atan2(yEn - ySt, xEn - xSt) * 180.0 / FastMath.PI;
        return det >= 0 ? det : det + 360;
    }

    public static double pointAngleClockwise(double xSt, double ySt, double xEn, double yEn) {     //0 <=> PRAWO; 90 <=> DÓŁ; 180 <=> LEWO; 270 <=> GÓRA; czy
        double det = FastMath.atan2(yEn - ySt, xEn - xSt) * 180.0 / FastMath.PI;
        return det >= 0 ? det : det + 360;
    }

    public static double threePointAngle(int xA, int yA, int xB, int yB, int xO, int yO) {
        int xOA = xO - xA;
        int yOA = yO - yA;
        int xOB = xO - xB;
        int yOB = yO - yB;
        int xBA = xB - xA;
        int yBA = yB - yA;
        double A = FastMath.sqrt((xOA * xOA) + (yOA * yOA));
        double B = FastMath.sqrt((xOB * xOB) + (yOB * yOB));
        double AB = FastMath.sqrt((xBA * xBA) + (yBA * yBA));
        return FastMath.acos(((B * B) + (A * A) - (AB * AB)) / (2 * B * A));
    }

    public static int makeDivisibleBy(int value, int divider) {
        return (value / divider) * divider;
    }

    public static int roundUpToBinaryNumber(int num) {
        int ret = 2;
        while (num > ret) {
            ret *= 2;
        }
        return ret;
    }

    public static int interval(int leftBorder, int x, int rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static double interval(double leftBorder, double variable, double rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, variable));
    }

    public static float interval(float leftBorder, float x, float rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static int roundDouble(double number) {
        return FastMath.round((float) number);
    }

    public static Point getTwoLinesIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return null;
        }
        float rx = x2 - x1;
        float ry = y2 - y1;
        float sx = x4 - x3;
        float sy = y4 - y3;
        float det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            float z = (sx * (y3 - y1) + sy * (x1 - x3)) / det;
            if (z == 0 || z == 1) {
                return null; // intersection at end point!
            }
            return new Point(Math.round(x1 + z * rx), Math.round(y1 + z * ry));
        }
    }

    private static Point getXTwoLinesIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return null;
        }
        float rx = x2 - x1;
        float ry = y2 - y1;
        float sx = x4 - x3;
        float sy = y4 - y3;
        float det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            float z = (sx * (y3 - y1) + sy * (x1 - x3)) / det;
            if (z == 0 || z == 1) {
                return null; // intersection at end point!
            }
            return new Point(Math.round(x1 + z * rx), Math.round(y1 + z * ry));
        }
    }

    // only for 64 diameter
    private static Point getTopCircleLineIntersection(double a, double b, double xc, double yc) {
        double A = 1 + a * a;
        double AB = yc + b;
        double B = 2 * ((a * AB) - xc);
        double delta = (B * B) - 4 * A * ((xc * xc) - Place.tileSquared + (AB * AB));
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A += A;
        double X1 = ((-B - delta) / A);
        double Y1 = (a * X1 + b);
        double X2 = ((-B + delta) / A);
        double Y2 = (a * X2 + b);
        if (Y2 < Y1) {
            return new Point(roundDouble(X1), -roundDouble(Y1));
        } else {
            return new Point(roundDouble(X2), -roundDouble(Y2));
        }
    }

    // only for 64 diameter
    private static Point getBottomCircleLineIntersection(double a, double b, double xc, double yc) {
        double A = 1 + a * a;
        double AB = yc + b;
        double B = 2 * ((a * AB) - xc);
        double delta = (B * B) - 4 * A * ((xc * xc) - Place.tileSquared + (AB * AB));
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A += A;
        double X1 = ((-B - delta) / A);
        double Y1 = (a * X1 + b);
        double X2 = ((-B + delta) / A);
        double Y2 = (a * X2 + b);
        if (Y2 > Y1) {
            return new Point(roundDouble(X1), -roundDouble(Y1));
        } else {
            return new Point(roundDouble(X2), -roundDouble(Y2));
        }
    }

    // only for 64 diameter
    public static Point getLeftCircleLineIntersection(double a, double b, double xc, double yc) {
        double A = 1 + a * a;
        double AB = yc + b;
        double B = 2 * ((a * AB) - xc);
        double delta = (B * B) - 4 * A * ((xc * xc) - Place.tileSquared + (AB * AB));
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A += A;
        double X1 = ((-B - delta) / A);
        double X2 = ((-B + delta) / A);
        if (X1 < X2) {
            return new Point(roundDouble(X1), -roundDouble(a * X1 + b));
        } else {
            return new Point(roundDouble(X2), -roundDouble(a * X2 + b));
        }
    }

    // only for 64 diameter
    public static Point getRightCircleLineIntersection(double a, double b, double xc, double yc) {
        double A = 1 + a * a;
        double AB = yc + b;
        double B = 2 * ((a * AB) - xc);
        double delta = (B * B) - 4 * A * ((xc * xc) - Place.tileSquared + (AB * AB));
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A += A;
        double X1 = ((-B - delta) / A);
        double X2 = ((-B + delta) / A);
        if (X1 > X2) {
            return new Point(roundDouble(X1), -roundDouble(a * X1 + b));
        } else {
            return new Point(roundDouble(X2), -roundDouble(a * X2 + b));
        }
    }

    public static void getCastingPointsIndexes(int x, int y, Figure figure, Point result) {
        if (figure instanceof Rectangle) {
            getCastingPointsFromRectangle(x, y, figure, result);
        } else if (figure instanceof RoundRectangle) {
            getCastingPointsFromRest(x, y, figure.getPoints(), result);
        } else {
            getCastingPointsFromRest(x, y, figure.getPoints(), result);
        }
    }

    private static void getCastingPointsFromRest(int x, int y, List<Point> points, Point result) {
        double angle = 0;
        double temp = 0;
        int first = 0, second = 0;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                temp = Methods.threePointAngle(points.get(i).getX(), points.get(i).getY(), points.get(j).getX(), points
                        .get(j).getY(), x, y);
                if (temp > angle) {
                    angle = temp;
                    first = i;
                    second = j;
                }
            }
        }
        result.set(first, second);
    }

    public static void getCastingPoints(int x, int y, int xS, int xE, int yS, int yE, Point[] result) {
        if (x > xE) {
            if (y > yE) {
                result[0].set(xS, yE);
                result[1].set(xE, yS);
            } else if (y > yS) {
                result[0].set(xE, yS);
                result[1].set(xE, yE);
            } else {
                result[0].set(xS, yS);
                result[1].set(xE, yE);
            }
        } else if (x > xS) {
            if (y > yE) {
                result[0].set(xS, yE);
                result[1].set(xE, yE);
            } else if (y > yS) {    // shit case - point inside Rectangle
                result[0].set(xS, yS);
                result[1].set(xE, yE);
            } else {
                result[0].set(xS, yS);
                result[1].set(xE, yS);
            }
        } else {
            if (y > yE) {
                result[0].set(xS, yS);
                result[1].set(xE, yE);
            } else if (y > yS) {
                result[0].set(xS, yS);
                result[1].set(xS, yE);
            } else {
                result[0].set(xS, yE);
                result[1].set(xE, yS);
            }
        }
    }

    private static void getCastingPointsFromRectangle(int x, int y, Figure rectangle, Point result) {
        int xS = rectangle.getX();
        int xE = rectangle.getXEnd();
        int yS = rectangle.getY();
        int yE = rectangle.getYEnd();
        if (x > xE) {
            if (y > yE) {
                result.set(1, 3);
            } else if (y > yS) {
                result.set(3, 2);
            } else {
                result.set(0, 2);
            }
        } else if (x > xS) {
            if (y > yE) {
                result.set(1, 2);
            } else if (y > yS) {   // shit case - point inside Rectangle
                result.set(0, 2);
            } else {
                result.set(0, 3);
            }
        } else {
            if (y > yE) {
                result.set(0, 2);
            } else if (y > yS) {
                result.set(0, 1);
            } else {
                result.set(1, 3);
            }
        }
    }

    public static Point getClosestPointToRectangle(int x, int y, Figure figure) {
        int xS = figure.getX();
        int xE = figure.getXEnd();
        int yS = figure.getY();
        int yE = figure.getYEnd();
        if (x > xE) {
            if (y > yE) {
                return new Point(xE, yE);
            } else if (y > yS) {
                return new Point(xE, y);
            } else {
                return new Point(xE, yS);
            }
        } else if (x > xS) {
            if (y > yE) {
                return new Point(x, yE);
            } else if (y > yS) {   // shit case - point inside
                return new Point(xS + figure.getWidthHalf(), yS + figure.getHeightHalf());
            } else {
                return new Point(x, yS);
            }
        } else {
            if (y > yE) {
                return new Point(xS, yE);
            } else if (y > yS) {
                return new Point(xS, y);
            } else {
                return new Point(xS, yS);
            }
        }
    }

    public static boolean isPointOnTheLeftToLine(int xb, int yb, int xe, int ye, int xp, int yp) {
        return ((xe - xb) * (yp - yb) - (ye - yb) * (xp - xb)) >= 0;
    }

    public static boolean isPointOnTheRightToLine(int xb, int yb, int xe, int ye, int xp, int yp) {
        return ((xe - xb) * (yp - yb) - (ye - yb) * (xp - xb)) <= 0;
    }

    public static Point getXIntersection(double a, double b, int xStart, int yStart, int xEnd, int yEnd, Figure other) {
        if (other.isTriangular()) {
            if (other.isLeftBottomRound()) {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getX(), other.getYEnd() - Place.tileSize, other.getXEnd(), other
                        .getYEnd());
            } else {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getXEnd(), other.getYEnd() - Place.tileSize, other.getX(), other
                        .getYEnd());
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

    public static Point getXIntersectionFromTop(double a, double b, int xStart, int yStart, int xEnd, int yEnd, Figure other) {
        if (other.isTriangular()) {
            if (other.isLeftBottomRound()) {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getX(), other.getYEnd() - Place.tileSize, other.getXEnd(), other
                        .getYEnd());
            } else {
                return Methods.getXTwoLinesIntersection(xStart, yStart, xEnd, yEnd, other.getXEnd(), other.getYEnd() - Place.tileSize, other.getX(), other
                        .getYEnd());
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

    public static int sizeInBytes(Object obj) throws java.io.IOException {
        ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        byteObject.close();
        return byteObject.toByteArray().length;
    }

    public static Color createHSVColor(float hue, float saturation, float value) {
        return changeColorWithHSV(new Color(0), hue, saturation, value);
    }

    public static Color changeColorWithHSV(Color color, float hue, float saturation, float value) {
        if (value == 0) {
            color.r = 0;
            color.g = 0;
            color.b = 0;
        } else {
            hue = (hue % 360) / 60;
            int i = (int) hue;
            float f = hue - i;
            float p = value * (1 - saturation);
            float q = value * (1 - (saturation * f));
            float t = value * (1 - (saturation * (1 - f)));
            switch (i) {
                case 0:
                    color.r = value;
                    color.g = t;
                    color.b = p;
                    break;
                case 1:
                    color.r = q;
                    color.g = value;
                    color.b = p;
                    break;
                case 2:
                    color.r = p;
                    color.g = value;
                    color.b = t;
                    break;
                case 3:
                    color.r = p;
                    color.g = q;
                    color.b = value;
                    break;
                case 4:
                    color.r = t;
                    color.g = p;
                    color.b = value;
                    break;
                case 5:
                    color.r = value;
                    color.g = p;
                    color.b = q;
                    break;
                default:
                    color.r = 0;
                    color.g = 0;
                    color.b = 0;
            }
        }
        return color;
    }

    public static String editWithKeyboard(String text) {
        String character = "";
        if (Keyboard.next() && Keyboard.getEventKeyState()) {
            int charNum = Keyboard.getEventKey();
            if ((charNum >= 16 && charNum <= 25) || // Keyboard's first row Q -
                    // P
                    (charNum >= 30 && charNum <= 38) || // A - L
                    (charNum >= 44 && charNum <= 50) || // Z - M
                    (charNum >= 2 && charNum <= 11)) { // numbers 1 - 0
                character += Keyboard.getKeyName(charNum);
            } else if (text.length() != 0 && (charNum == Keyboard.KEY_DELETE || charNum == Keyboard.KEY_BACK)) {
                text = text.substring(0, text.length() - 1);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            switch (character) {
                case "S":
                    character = "Ś";
                    break;
                case "C":
                    character = "Ć";
                    break;
                case "E":
                    character = "Ę";
                    break;
                case "A":
                    character = "Ą";
                    break;
                case "L":
                    character = "Ł";
                    break;
                case "O":
                    character = "Ó";
                    break;
                case "Z":
                    character = "Ż";
                    break;
                case "X":
                    character = "Ź";
                    break;
                case "N":
                    character = "Ń";
                    break;
            }
        }
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            character = character.toLowerCase();
        }
        return text + character;
    }

    public static void pasteToClipBoard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    public static void print(Object... list) {
        String print = "";
        for (Object o : list) {
            print += o.toString() + " ";
        }
        System.out.println(print);
    }

    public static float[] concatAll(float[] first, float[]... rest) {
        int totalLength = first.length;
        for (float[] array : rest) {
            totalLength += array.length;
        }
        float[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (float[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static float[] toArray(ArrayList<Float> list) {
        float[] result = new float[list.size()];
        int i = 0;
        for (Float n : list) {
            result[i++] = n;
        }
        return result;
    }

    public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
        }
    }
}
