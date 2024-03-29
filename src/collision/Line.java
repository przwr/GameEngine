/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.utilities.Point;
import game.gameobject.GameObject;

import java.awt.geom.Line2D;
import java.util.List;

/**
 * @author Wojtek
 */
public class Line extends Figure {

    // Class never used
    private final int xVector;
    private final int yVector;

    private Line(int xStart, int yStart, int xVector, int yVector, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticProperties.TRANSPARENT));     /// do poprawy
        this.xVector = xVector;
        this.yVector = yVector;
        points.add(new Point(getX(), getY()));
        points.add(new Point(getX() + xVector, getY() + yVector));
        points.trimToSize();
        centralize();
    }

    public Line(int xVector, int yVector, GameObject owner) {
        super(0, 0, owner, OpticProperties.create(OpticProperties.TRANSPARENT));     /// do poprawy
        this.xVector = xVector;
        this.yVector = yVector;
        centralize();
    }

    public static Line create(int dx, int dy, GameObject owner) {
        return new Line(0, 0, dx, dy, owner);
    }

    public static Line create(int xStart, int yStart, int xVector, int yVector, GameObject owner) {
        return new Line(xStart, yStart, xVector, yVector, owner);
    }

    private void centralize() {
        width = xVector;
        height = yVector;
        widthHalf = xVector / 2;
        heightHalf = yVector / 2;
        xCenter = xVector / 2;
        yCenter = yVector / 2;
    }

    @Override
    protected boolean isCollideSingleImplementation(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            return rectangleCollision(x, y, figure);
        } else if (figure instanceof RoundRectangle) {
            return roundRectangleCollision(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollision(int x, int y, Figure figure) {
        List<Point> tmpPts = figure.getPoints();
        int[] w = {getX(x), getY(y), getX(x) + xVector, getY(y) + yVector};
        return (Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(0).getX(), tmpPts.get(0).getY(), tmpPts.get(1).getX(), tmpPts.get(1)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(1).getX(), tmpPts.get(1).getY(), tmpPts.get(2).getX(), tmpPts.get(2)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(2).getX(), tmpPts.get(2).getY(), tmpPts.get(3).getX(), tmpPts.get(3)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(3).getX(), tmpPts.get(3).getY(), tmpPts.get(0).getX(), tmpPts.get(0)
                .getY()));
    }

    private boolean roundRectangleCollision(int x, int y, Figure figure) {      //TO DO
        System.out.println("Simplified Version of Collision with RoundRectangle. In Line");
        List<Point> tmpPts = figure.getPoints();
        int[] w = {getX(x), getY(y), getX(x) + xVector, getY(y) + yVector};
        return (Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(0).getX(), tmpPts.get(0).getY(), tmpPts.get(1).getX(), tmpPts.get(1)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(1).getX(), tmpPts.get(1).getY(), tmpPts.get(2).getX(), tmpPts.get(2)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(2).getX(), tmpPts.get(2).getY(), tmpPts.get(3).getX(), tmpPts.get(3)
                .getY())
                || Line2D.linesIntersect(w[0], w[1], w[2], w[3], tmpPts.get(3).getX(), tmpPts.get(3).getY(), tmpPts.get(0).getX(), tmpPts.get(0)
                .getY()));
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
    public List<Point> getPoints() {
        if (isMobile()) {
            updatePoints();
        }
        return points;
    }

    @Override
    public void updatePoints() {
        points.get(0).set(getX(), getY());
        points.get(1).set(getX() + xVector, getY() + yVector);
    }

    public int getXVector() {
        return xVector;
    }

    public int getYVector() {
        return yVector;
    }
}
