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
    private ArrayList<Point> connectionPoints;


    public AreaConnection(int firstAreaIndex, int secondAreaIndex) {
        connectionPoints = new ArrayList<>(9);
        this.firstAreaIndex = firstAreaIndex;
        this.secondAreaIndex = secondAreaIndex;
    }

    public void trim() {
        if (connectionPoints != null)
            connectionPoints.trimToSize();
    }

    public void addPoint(Point firstAreaPoint) {
        connectionPoints.add(firstAreaPoint);
    }

    public List<Point> getConnectionPoints() {
        return connectionPoints;
    }

    public Point getCentralPoint() {
        return connectionPoints.get((connectionPoints.size() - 1) / 2);
    }


    public int getFirstAreaIndex() {
        return firstAreaIndex;
    }

    public int getSecondAreaIndex() {
        return secondAreaIndex;
    }

    public int getConnectedAreaIndex(int areaIndex) {
        return firstAreaIndex == areaIndex ? secondAreaIndex : firstAreaIndex;
    }
}
