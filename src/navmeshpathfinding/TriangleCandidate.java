/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;

/**
 *
 * @author przemek
 */
public class TriangleCandidate {

    private final Point[] points = new Point[3];

    public TriangleCandidate(Point point1, Point point2, Point point3) {
        this.points[0] = point1;
        this.points[1] = point2;
        this.points[2] = point3;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TriangleCandidate)) {
            return false;
        }
        int sharedPoints = 0, checkedPoints = 0;
        for (Point point : points) {
            for (Point otherPoint : ((TriangleCandidate) o).points) {
                if (point.equals(otherPoint)) {
                    sharedPoints++;
                    break;
                }
            }
            checkedPoints++;
            if (sharedPoints != checkedPoints) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int point1Hashcode = points[0].hashCode();
        int point2Hashcode = points[1].hashCode();
        int point3Hashcode = points[2].hashCode();
        return 83 * (point1Hashcode * point1Hashcode + point2Hashcode * point2Hashcode + point3Hashcode * point3Hashcode);
    }
}
