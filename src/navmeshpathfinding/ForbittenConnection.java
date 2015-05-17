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
public class ForbittenConnection {

    private Point point1, point2;

    public ForbittenConnection(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public void setPoints(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ForbittenConnection)) {
            return false;
        }
        return (point1.equals(((ForbittenConnection) o).point1) && point2.equals(((ForbittenConnection) o).point2)) || (point1.equals(((ForbittenConnection) o).point2) && point2.equals(((ForbittenConnection) o).point1));
    }

    @Override
    public int hashCode() {
        int point1Hashcode = point1.hashCode();
        int point2Hashcode = point2.hashCode();
        return 83 * (point1Hashcode * point1Hashcode + point2Hashcode * point2Hashcode);
    }
}
