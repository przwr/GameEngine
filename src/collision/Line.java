/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Wojtek
 */
public class Line extends Figure {

    private final int xVector;
    private final int yVector;

    public static Line create(int dx, int dy, GameObject owner) {
        return new Line(0, 0, dx, dy, owner);
    }

    public static Line create(int xStart, int yStart, int dx, int dy, GameObject owner) {
        return new Line(xStart, yStart, dx, dy, owner);
    }

    private Line(int xStart, int yStart, int dx, int dy, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticProperties.IN_SHADE_NO_SHADOW));     /// do poprawy
        xVector = dx;
        yVector = dy;
        points.add(new Point(-1, -1));
        points.add(new Point(-1, -1));
        points.trimToSize();
        centralize();
    }

    public Line(int dx, int dy, GameObject owner) {
        super(0, 0, owner, OpticProperties.create(OpticProperties.IN_SHADE_NO_SHADOW));     /// do poprawy
        xVector = dx;
        yVector = dy;
        centralize();
    }

    private void centralize() {
        width = xVector;
        height = yVector;
        xCentr = xVector / 2;
        yCentr = yVector / 2;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            return rectangleCollision(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollision(int x, int y, Figure figure) {
        ArrayList<Point> points = (ArrayList< Point>) figure.getPoints();
        int[] w = {getX(x), getY(y), getX(x) + xVector, getY(y) + yVector};
        return (Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(0).getX(), points.get(0).getY(), points.get(1).getX(), points.get(1).getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(1).getX(), points.get(1).getY(), points.get(2).getX(), points.get(2).getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(2).getX(), points.get(2).getY(), points.get(3).getX(), points.get(3).getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(3).getX(), points.get(3).getY(), points.get(0).getX(), points.get(0).getY()));
    }

    private boolean circleCollision(int x, int y, Figure figure) {
        Circle circle = (Circle) figure;
        return (Line2D.ptSegDist(getX(x), getY(y), getX(x) + xVector, getY(y) + yVector, circle.getX(), circle.getY()) <= circle.getRadius());
    }

    private boolean lineCollision(int x, int y, Figure figure) {
        Line line = (Line) figure;
        return (Line2D.linesIntersect(getX(x), getY(y), getX(x) + xVector, getY(y) + yVector,
                line.getX(), line.getY(), line.getX() + line.getXVector(), line.getY() + line.getYVector()));
    }

    @Override
    public Collection<Point> getPoints() {
        points.clear();
        points.get(0).set(getX(), getY());
        points.get(1).set(getX() + xVector, getY() + yVector);
        return points;
    }

    public int getXVector() {
        return xVector;
    }

    public int getYVector() {
        return yVector;
    }
}
