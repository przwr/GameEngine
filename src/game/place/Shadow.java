/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Point;

/**
 *
 * @author przemek
 */
public class Shadow implements Comparable<Object> {

    public int type; // 0 ciemy, 1 światło, 2 przyciemniony, 3 doświetony
   // public Figure source; // źródło cienia, jeśli null to znaczy, że źródłem jest światło, jeśli źródłem jest obiek, który posiada ten cień, to znaczy, że obiekt jest źródłem światła
    public Point[] points;

    public Shadow(int type) {
        this.type = type;
    }

    public void addPoints(Point p1, Point p2, Point p3, Point p4) {
        points = new Point[4];
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
    }

    @Override
    public int compareTo(Object o) {
        return type - ((Shadow) o).type;
    }
}
