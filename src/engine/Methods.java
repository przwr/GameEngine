package engine;

import java.awt.geom.Line2D;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;
import net.jodk.lang.FastMath;
import org.lwjgl.input.Keyboard;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wojtek
 */
public class Methods {

    private static double AO, OB, AB;
    private static int xOA, yOA, xOB, yOB, xBA, yBA;

    public static double xRadius(double angle, double rad) {
        return FastMath.cos(FastMath.toRadians(angle)) * rad;
    }

    public static double yRadius(double angle, double rad) {
        return FastMath.sin(FastMath.toRadians(angle)) * rad;
    }

    public static int pointDistance(int x, int y, int xa, int ya) {
        int dx = xa - x;
        int dy = ya - y;
        return (int) FastMath.sqrt(dx * dx + dy * dy);
    }

    public static int pointDistanceSimple(int x, int y, int xa, int ya) {
        return (int) (FastMath.abs(xa - x) + FastMath.abs(ya - y));
    }

    public static int pointDifference(int x, int y, int xa, int ya) {
        return FastMath.min(xa - x, ya - y);
    }

    public static double pointAngle(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        return FastMath.atan2(deltaY, deltaX) * 180 / FastMath.PI;
    }

    public static double pointAngle360(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        double ret = FastMath.atan2(deltaY, deltaX) * 180 / FastMath.PI;
        return ret >= 0 ? ret : ret + 360;
    }

    public static double threePointAngle(int xA, int yA, int xB, int yB, int xO, int yO) {
        xOA = xO - xA;
        yOA = yO - yA;
        xOB = xO - xB;
        yOB = yO - yB;
        xBA = xB - xA;
        yBA = yB - yA;
        AO = FastMath.sqrt((xOA * xOA) + (yOA * yOA));
        OB = FastMath.sqrt((xOB * xOB) + (yOB * yOB));
        AB = FastMath.sqrt((xBA * xBA) + (yBA * yBA));
        return FastMath.acos(((OB * OB) + (AO * AO) - (AB * AB)) / (2 * OB * AO));
    }

    public static int interval(int leftBorder, int x, int rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static double interval(double leftBorder, double x, double rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static float interval(float leftBorder, float x, float rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static void exception(Exception exception) {
        String error = "";
        error += exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        System.out.println(error);
        Main.addMessage(error);
    }

    public static void error(String message) {
        System.out.println(message);
        Main.addMessage(message);
    }

    public static void javaError(String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(null, message, "Problem!", 0);
    }

    public static void javaException(Exception exception) {
        String error = "";
        error += exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        System.out.println(error);
        JOptionPane.showMessageDialog(null, error, "Problem!", 0);
    }

    public static int roundDouble(double number) {
        return FastMath.round((float) number);
    }

    public static Point getIntersectionPoint(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return null;
        }
        double rx = x2 - x1,
                ry = y2 - y1,
                sx = x4 - x3,
                sy = y4 - y3;

        double det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            double z = (sx * (y3 - y1) + sy * (x1 - x3)) / det;
            if (z == 0 || z == 1) {
                return null;  // intersection at end point!
            }
            return new Point(roundDouble(x1 + z * rx), roundDouble(y1 + z * ry));
        }
    }

    public Point getIntersectionPoint(Line2D firstLine, Line2D secondLine) {
        if (!firstLine.intersectsLine(secondLine)) {
            return null;
        }
        double px = firstLine.getX1(),
                py = firstLine.getY1(),
                rx = firstLine.getX2() - px,
                ry = firstLine.getY2() - py;
        double qx = secondLine.getX1(),
                qy = secondLine.getY1(),
                sx = secondLine.getX2() - qx,
                sy = secondLine.getY2() - qy;

        double det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            double z = (sx * (qy - py) + sy * (px - qx)) / det;
            if (z == 0 || z == 1) {
                return null;  // intersection at end point!
            }
            return new Point(roundDouble((px + z * rx)), roundDouble((py + z * ry)));
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

    public static String editWithKeyboard(String text) {
        String character = "";
        if (Keyboard.next() && Keyboard.getEventKeyState()) {
            int charNum = Keyboard.getEventKey();
            if ((charNum >= 16 && charNum <= 25) || //Keyboard's first row Q - P
                    (charNum >= 30 && charNum <= 38) || //A - L
                    (charNum >= 44 && charNum <= 50) || //Z - M
                    (charNum >= 2 && charNum <= 11)) {  //numbers 1 - 0
                character += Keyboard.getKeyName(charNum);
            } else if (text.length() != 0 && (charNum == Keyboard.KEY_DELETE
                    || charNum == Keyboard.KEY_BACK)) {
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
}
