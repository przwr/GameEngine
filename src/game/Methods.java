package game;

import javax.swing.JOptionPane;

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

    public static double xRadius(double angle, double rad) {
        return Math.cos(Math.toRadians(angle)) * rad;
    }

    public static double yRadius(double angle, double rad) {
        return Math.sin(Math.toRadians(angle)) * rad;
    }

    public static double PointDistance(int x, int y, int xa, int ya) {
        return Math.sqrt(Math.pow(xa - x, 2) + Math.pow(ya - y, 2));
    }

    public static int PointDifference(int x, int y, int xa, int ya) {
        return Math.min(xa - x, ya - y);
    }

    public static double PointAngle(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        return Math.atan2(deltaY, deltaX) * 180 / Math.PI;
    }

    public static double PointAngle360(int xSt, int ySt, int xEn, int yEn) {
        int deltaX = xEn - xSt;
        int deltaY = yEn - ySt;
        double ret = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
        return ret >= 0 ? ret : ret + 360;
    }

    public static int Interval(int leftBorder, int x, int rightBorder) {
        return Math.max(leftBorder, Math.min(rightBorder, x));
    }

    public static double Interval(double leftBorder, double x, double rightBorder) {
        return Math.max(leftBorder, Math.min(rightBorder, x));
    }

    public static float Interval(float leftBorder, float x, float rightBorder) {
        return Math.max(leftBorder, Math.min(rightBorder, x));
    }

    public static void Exception(Exception ex) {
        String err = "";
        err += ex + "\n";
        for (StackTraceElement stackTrace : ex.getStackTrace()) {
            err += stackTrace + "\n";
        }
        JOptionPane.showMessageDialog(null, err, "Problem!", 0);
    }

    public static void Error(String message) {
        JOptionPane.showMessageDialog(null, message, "Problem!", 0);
    }
}
