package game.logic.betweenareapathfinding;

import engine.utilities.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by przemek on 14.02.16.
 */
public class AreaConnection {

    private int firstAreaIndex;
    private int secondAreaIndex;
    private int maxConnectionSize;
    private ArrayList<Point> firstConnectionPoints;
    private ArrayList<Point> secondConnectionPoints;


    public AreaConnection(int firstAreaIndex, int secondAreaIndex) {
        firstConnectionPoints = new ArrayList<>(9);
        secondConnectionPoints = new ArrayList<>(9);
        this.firstAreaIndex = firstAreaIndex;
        this.secondAreaIndex = secondAreaIndex;
    }

    public void setMaxConnectionSize(int maxConnectionSize) {
        this.maxConnectionSize = maxConnectionSize;
    }

    public void trim() {
        if (firstConnectionPoints != null)
            firstConnectionPoints.trimToSize();
        if (secondConnectionPoints != null)
            secondConnectionPoints.trimToSize();
    }

    public void addPoints(Point firstAreaPoint, Point secondAreaPoint) {
        firstConnectionPoints.add(firstAreaPoint);
        secondConnectionPoints.add(secondAreaPoint);
    }

    public List<Point> getConnectionPointsForArea(int area) {
        return firstAreaIndex == area ? firstConnectionPoints : (secondAreaIndex == area ? secondConnectionPoints : null);
    }


    public List<Point> getFirstConnectionPoints() {
        return firstConnectionPoints;
    }

    public List<Point> getSecondConnectionPoints() {
        return secondConnectionPoints;
    }

    public int getFirstAreaIndex() {
        return firstAreaIndex;
    }

    public int getSecondAreaIndex() {
        return secondAreaIndex;
    }
}
