package engine;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import net.jodk.lang.FastMath;

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

    public static int PointDistance(int x, int y, int xa, int ya) {
        return (int) FastMath.sqrt(FastMath.pow(xa - x, 2) + FastMath.pow(ya - y, 2));
    }

    public static int PointDistanceSimple(int x, int y, int xa, int ya) {
        return ((xa - x) * (xa - x) + (ya - y) * (ya - y));
    }

    public static int PointDifference(int x, int y, int xa, int ya) {
        return FastMath.min(xa - x, ya - y);
    }

    public static double PointAngle(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        return FastMath.atan2(deltaY, deltaX) * 180 / FastMath.PI;
    }

    public static double PointAngle360(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        double ret = FastMath.atan2(deltaY, deltaX) * 180 / FastMath.PI;
        return ret >= 0 ? ret : ret + 360;
    }

    public static double ThreePointAngle(int xA, int yA, int xB, int yB, int xO, int yO) {
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

    public static int Interval(int leftBorder, int x, int rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static double Interval(double leftBorder, double x, double rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static float Interval(float leftBorder, float x, float rightBorder) {
        return FastMath.max(leftBorder, FastMath.min(rightBorder, x));
    }

    public static void Exception(Exception ex) {
        String err = "";
        err += ex + "\n";
        for (StackTraceElement stackTrace : ex.getStackTrace()) {
            err += stackTrace + "\n";
        }
        System.out.println(err);
        Main.addMessage(err);
        //JOptionPane.showMessageDialog(null, err, "Problem!", 0);
    }

    public static void Error(String message) {
        System.out.println(message);
        Main.addMessage(message);
        //JOptionPane.showMessageDialog(null, message, "Problem!", 0);
    }

    public static int RoundHU(double d) {
        double dAbs = FastMath.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
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

    private Methods() {
    }
}
