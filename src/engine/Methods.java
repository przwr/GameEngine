package engine;

import game.gameobject.GameObject;
import game.place.Place;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static double A, B, AB, delta, X1, Y1, X2, Y2, rx, ry, sx, sy, det, z;
    private static int xOA, yOA, xOB, yOB, xBA, yBA, xDelta, yDelta;
    private static final Point point = new Point(0, 0);
    private static File file;

    public static double xRadius(double angle, double rad) {
        return FastMath.cos(FastMath.toRadians(angle)) * rad;
    }

    public static double yRadius(double angle, double rad) {
        return FastMath.sin(FastMath.toRadians(angle)) * rad;
    }

    public static int pointDistance(int x, int y, int xa, int ya) {
        xDelta = xa - x;
        yDelta = ya - y;
        return (int) FastMath.sqrt(xDelta * xDelta + yDelta * yDelta);
    }

    public static int pointDistanceSimple(int x, int y, int xa, int ya) {
        return (int) (FastMath.abs(xa - x) + FastMath.abs(ya - y));
    }

    public static int pointDistanceSimple2(int x, int y, int xa, int ya) {
        xDelta = xa - x;
        yDelta = ya - y;
        return xDelta * xDelta + yDelta * yDelta;
    }

    public static int pointDifference(int x, int y, int xa, int ya) {
        return FastMath.min(xa - x, ya - y);
    }

    public static double pointAngle(int xSt, int ySt, int xEn, int yEn) {
        xDelta = xEn - xSt;
        yDelta = yEn - ySt;
        return FastMath.atan2(yDelta, xDelta) * 180 / FastMath.PI;
    }

    public static double pointAngle360(int xSt, int ySt, int xEn, int yEn) {
        xDelta = xEn - xSt;
        yDelta = yEn - ySt;
        det = FastMath.atan2(yDelta, xDelta) * 180 / FastMath.PI;
        return det >= 0 ? det : det + 360;
    }

    public static double threePointAngle(int xA, int yA, int xB, int yB, int xO, int yO) {
        xOA = xO - xA;
        yOA = yO - yA;
        xOB = xO - xB;
        yOB = yO - yB;
        xBA = xB - xA;
        yBA = yB - yA;
        A = FastMath.sqrt((xOA * xOA) + (yOA * yOA));
        B = FastMath.sqrt((xOB * xOB) + (yOB * yOB));
        AB = FastMath.sqrt((xBA * xBA) + (yBA * yBA));
        return FastMath.acos(((B * B) + (A * A) - (AB * AB)) / (2 * B * A));
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

    public static int roundDouble(double number) {
        return FastMath.round((float) number);
    }

    public static Point getTwoLinesIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return null;
        }
        rx = x2 - x1;
        ry = y2 - y1;
        sx = x4 - x3;
        sy = y4 - y3;
        det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            z = (sx * (y3 - y1) + sy * (x1 - x3)) / det;
            if (z == 0 || z == 1) {
                return null;  // intersection at end point!
            }
            point.set(roundDouble(x1 + z * rx), roundDouble(y1 + z * ry));
            return point;
        }
    }

    public static Point getXTwoLinesIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            return null;
        }
        rx = x2 - x1;
        ry = y2 - y1;
        sx = x4 - x3;
        sy = y4 - y3;
        det = sx * ry - sy * rx;
        if (det == 0) {
            return null;
        } else {
            z = (sx * (y3 - y1) + sy * (x1 - x3)) / det;
            if (z == 0 || z == 1) {
                return null;  // intersection at end point!
            }
            point.set(roundDouble(x1 + z * rx), roundDouble(y1 + z * ry));
            return point;
        }
    }

    public static Point getTopCircleLineIntersection(double a, double b, double xc, double yc) { // tylko dla okręgu o promienu 64
        calculateDelta(a, b, xc, yc);
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A *= 2;
        X1 = ((-B - delta) / A);
        Y1 = (a * X1 + b);
        X2 = ((-B + delta) / A);
        Y2 = (a * X2 + b);
        if (Y2 < Y1) {
            point.set(roundDouble(X1), -roundDouble(Y1));
        } else {
            point.set(roundDouble(X2), -roundDouble(Y2));
        }
        return point;
    }

    public static Point getBottomCircleLineIntersection(double a, double b, double xc, double yc) { // tylko dla okręgu o średnicy 64
        calculateDelta(a, b, xc, yc);
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A *= 2;
        X1 = ((-B - delta) / A);
        Y1 = (a * X1 + b);
        X2 = ((-B + delta) / A);
        Y2 = (a * X2 + b);
        if (Y2 > Y1) {
            point.set(roundDouble(X1), -roundDouble(Y1));
        } else {
            point.set(roundDouble(X2), -roundDouble(Y2));
        }
        return point;
    }

    public static Point getLeftCircleLineIntersection(double a, double b, double xc, double yc) { // tylko dla okręgu o promienu 64
        calculateDelta(a, b, xc, yc);
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A *= 2;
        X1 = ((-B - delta) / A);
        X2 = ((-B + delta) / A);
        if (X1 < X2) {
            point.set(roundDouble(X1), -roundDouble(a * X1 + b));
        } else {
            point.set(roundDouble(X2), -roundDouble(a * X2 + b));
        }
        return point;
    }

    public static Point getRightCircleLineIntersection(double a, double b, double xc, double yc) { // tylko dla okręgu o promienu 64
        calculateDelta(a, b, xc, yc);
        if (delta < 0) {
            return null;
        }
        delta = FastMath.sqrt(delta);
        A *= 2;
        X1 = ((-B - delta) / A);
        X2 = ((-B + delta) / A);
        if (X1 > X2) {
            point.set(roundDouble(X1), -roundDouble(a * X1 + b));
        } else {
            point.set(roundDouble(X2), -roundDouble(a * X2 + b));
        }
        return point;
    }

    private static void calculateDelta(double a, double b, double xc, double yc) {
        A = 1 + a * a;
        AB = yc + b;
        B = 2 * ((a * AB) - xc);
        delta = (B * B) - 4 * A * ((xc * xc) - Place.tileSquared + (AB * AB)); // Place.tileArea is radius squared
    }

    public static boolean isPointOnTheLeftToLine(int xb, int yb, int xe, int ye, int xp, int yp) {
        return ((xe - xb) * (yp - yb) - (ye - yb) * (xp - xb)) >= 0;
    }

    public static boolean isPointOnTheRightToLine(int xb, int yb, int xe, int ye, int xp, int yp) {
        return ((xe - xb) * (yp - yb) - (ye - yb) * (xp - xb)) <= 0;
    }

    public static void insort(List<GameObject> list) {  //sortuje od najmniejszych do najwiekszych
        int i, j, newValue;
        GameObject object;
        for (i = 1; i < list.size(); i++) {
            object = list.get(i);
            newValue = object.getDepth();
            j = i;
            while (j > 0 && list.get(j - 1).getDepth() > newValue) {
                list.set(j, list.get(j - 1));
                j--;
            }
            list.set(j, object);
        }
    }

    public static void merge(List<GameObject> l1, List<GameObject> l2) {
        for (int index1 = 0, index2 = 0; index2 < l2.size(); index1++) {
            if (index1 == l1.size() || l1.get(index1).getDepth() > l2.get(index2).getDepth()) {
                l1.add(index1, l2.get(index2++));
            }
        }
    }

    public static void merge(List<GameObject> l1, GameObject l2) {
        boolean added = false;
        for (int i = 0; !added; i++) {
            if (i == l1.size() || l1.get(i).getDepth() > l2.getDepth()) {
                l1.add(i, l2);
                added = true;
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

    public static void exception(Exception exception) {
        String error = "";
        error += exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        Main.addMessage(error);
        logAndPrint("\n" + error + "\n");
    }

    public static void error(String message) {
        Main.addMessage(message);
        logAndPrint("\n" + message + "\n");
    }

    public static void javaError(String message) {
        JOptionPane.showMessageDialog(null, message, "Problem!", 0);
        logAndPrint("\n" + message + "\n");
    }

    public static void javaException(Exception exception) {
        String error = "";
        error += exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        JOptionPane.showMessageDialog(null, error, "Problem!", 0);
        logAndPrint("\n" + error + "\n");
    }

    public static void logAndPrint(String string) {
        System.err.print(string);
        errorToFile(string);
    }

    public static void logToFile(String string) {
        file = new File("logs/log_" + Main.STARTED_DATE + ".txt");
        if (file.exists() && !file.isDirectory()) {
            log(string);
        } else {
            try {
                try (FileWriter writer = new FileWriter("logs/log_" + Main.STARTED_DATE + ".txt")) {
                    writer.write(string);
                }
            } catch (IOException ex) {
                Logger.getLogger(Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void errorToFile(String string) {
        file = new File("logs/error_" + Main.STARTED_DATE + ".txt");
        if (file.exists() && !file.isDirectory()) {
            log(string);
        } else {
            try {
                try (FileWriter writer = new FileWriter("logs/error_" + Main.STARTED_DATE + ".txt")) {
                    writer.write(string);
                }
            } catch (IOException ex) {
                Logger.getLogger(Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void log(String string) {
        try {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"))) {
                writer.append(string);
            }
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(Methods.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
