package game.logic.betweenareapathfinding;

import collision.Figure;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.PointContainer;
import game.gameobject.entities.Entity;
import game.logic.navmeshpathfinding.PathData;
import game.place.map.Area;
import game.place.map.Map;

import java.util.*;

import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;

/**
 * Created by przemek on 28.02.16.
 */
public class BetweenAreaPathFinder {


    public static PointContainer findInDifferentAreas(Map map, int area, int x, int y, int xStart, int yStart, int xDestination, int yDestination, Figure
            collision) {
        int endArea = map.getAreaIndex(xDestination, yDestination);
        Set<AreaNode> closedList = new HashSet<>();
        PriorityQueue<AreaNode> openList = new PriorityQueue<>(24, (AreaNode n1, AreaNode n2) -> n1.getFCost() - n2.getFCost());
        AreaNode currentNode = null;

        AreaConnection start = new AreaConnection(area, area);
        start.addPoint(new Point(xStart, yStart));
        AreaNode beginning = new AreaNode(start);

        AreaConnection end = new AreaConnection(endArea, endArea);
        end.addPoint(new Point(xDestination, yDestination));
        AreaNode ending = new AreaNode(end);

        beginning.setGHCosts(0, countHCost(beginning, ending));
        closedList.add(beginning);
        for (AreaConnection connection : map.getAreaConnectors()[area].getConnections()) {
            if (beginning.getConnection() != connection && existsConnection(map, area, beginning, connection, collision)) {
                AreaNode node = new AreaNode(connection, beginning);
                node.setGHCosts(countGCost(node, beginning), countHCost(node, ending));
                openList.add(node);
            }
        }
        int currentArea = area;
        while (!openList.isEmpty()) {
            currentNode = openList.poll();
            closedList.add(currentNode);
            if (currentNode.connectsWithArea(endArea) && existsConnection(map, endArea, currentNode, end, collision)) {
                break;
            }
            currentArea = currentNode.getConnectedAreaIndex(currentArea);
            for (AreaConnection connection : map.getAreaConnectors()[currentArea].getConnections()) {
                AreaNode next = closedListContains(closedList, connection);
                if (currentNode.getConnection() != connection) {
                    if (next.getFCost() != Integer.MAX_VALUE) { // już istniał
                        int temp = countGCost(next, currentNode);
                        if (temp + currentNode.getGCost() < next.getGCost() && existsConnection(map, currentArea, currentNode, next.getConnection(),
                                collision)) {
                            next.setParent(currentNode);
                            next.setGCost(temp);
                        }
                    } else if (existsConnection(map, currentArea, currentNode, next.getConnection(), collision)) {
                        next.setParent(currentNode);
                        next.setGHCosts(countGCost(next, currentNode), countHCost(next, ending));
                        openList.add(next);
                    }
                }
            }
        }
        if (currentNode != null) {
            AreaNode prev = currentNode;
            while (currentNode.getParent() != null) {
                prev = currentNode;
                currentNode = currentNode.getParent();
            }
            PointContainer solution = getBetweenAreaSolution(map, area, endArea, x, y, xStart, yStart, xDestination, yDestination, prev.getConnection(),
                    collision);
            if (solution != null) {
                return solution;
            }
        }
        return Map.NO_SOLUTION;
    }

    private static boolean existsConnection(Map map, int area, AreaNode start, AreaConnection end, Figure collision) {
        Area a = map.getArea(area);
        int x = a.getXInPixels();
        int y = a.getYInPixels();
        Point first = start.getCentralPoint();
        Point second = end.getCentralPoint();
        int firstTestX = first.getX() - x;
        int firstTestY = first.getY() - y;
        if (first.getX() % (X_IN_TILES * 64) == 0) {
            firstTestX += first.getX() > x ? -collision.getWidthHalf() : collision.getWidthHalf();
        }
        if (first.getY() % (Y_IN_TILES * 64) == 0) {
            firstTestY += first.getY() > y ? -collision.getWidthHalf() : collision.getWidthHalf();
        }
        int secondTestX = second.getX() - x;
        int secondTestY = second.getY() - y;
        if (second.getX() % (X_IN_TILES * 64) == 0) {
            secondTestX += second.getX() > x ? -collision.getWidthHalf() : collision.getWidthHalf();
        }
        if (second.getY() % (Y_IN_TILES * 64) == 0) {
            secondTestY += second.getY() > y ? -collision.getWidthHalf() : collision.getWidthHalf();
        }
        return a.pathExists(firstTestX, firstTestY, secondTestX, secondTestY, collision);
    }

    private static AreaNode closedListContains(Set<AreaNode> closedList, AreaConnection connection) {
        Iterator<AreaNode> iterator = closedList.iterator();
        AreaNode node;
        while (iterator.hasNext()) {
            node = iterator.next();
            if (node.getConnection() == connection) {
                return node;
            }
        }
        return new AreaNode(connection);
    }

    private static int countGCost(AreaNode currentNode, AreaNode parentNode) {
        Point current = currentNode.getCentralPoint();
        Point parent = parentNode.getCentralPoint();
        int x = parent.getX() - current.getX();
        int y = parent.getY() - current.getY();
        return 2686976;
    }

    private static int countHCost(AreaNode currentNode, AreaNode endNode) {
        Point current = currentNode.getCentralPoint();
        Point parent = endNode.getCentralPoint();
        int x = parent.getX() - current.getX();
        int y = parent.getY() - current.getY();
        return (x * x + y * y);
    }

    private static int calculateValueOfPoint(Point point, int xStart, int yStart) {
        return Methods.pointDistanceSimple2(point.getX(), point.getY(), xStart, yStart);
    }

    private static int calculateValueOfLastPoint(Point point, int xStart, int yStart) {
        return Methods.pointDistanceSimple2(point.getX(), point.getY(), xStart, yStart);
    }

    private static PointContainer getBetweenAreaSolution(Map map, int area, int endArea, int x, int y, int xStart, int yStart, int xDestination, int
            yDestination, AreaConnection connection, Figure collision) {
        List<Point> currentAreaPoints = connection.getConnectionPoints();
        List<Point> tempPoints = new ArrayList<>(currentAreaPoints);
        PathData data = ((Entity) collision.getOwner()).getPathData();
        if (connection.getConnectedAreaIndex(area) == endArea) {
            tempPoints.sort((o1, o2) -> calculateValueOfLastPoint(o1, xDestination, yDestination) - calculateValueOfLastPoint(o2, xDestination, yDestination));
        } else {
            tempPoints.sort((o1, o2) -> calculateValueOfPoint(o1, xStart, yStart) - calculateValueOfPoint(o2, xStart, yStart));
        }
        PointContainer solution;
        for (Point currentPoint : tempPoints) {
            int testX = currentPoint.getX() - x;
            int testY = currentPoint.getY() - y;
            if (currentPoint.getX() % (X_IN_TILES * 64) == 0) {
                testX += currentPoint.getX() > x ? -collision.getWidthHalf() : collision.getWidthHalf();
            }
            if (currentPoint.getY() % (Y_IN_TILES * 64) == 0) {
                testY += currentPoint.getY() > y ? -collision.getWidthHalf() : collision.getWidthHalf();
            }
            solution = map.getArea(area).findPath(xStart - x, yStart - y, testX, testY, collision);
            if (solution != null) {
                int changeX = currentPoint.getX() - x;
                int changeY = currentPoint.getY() - y;

                if (currentPoint.getX() % (X_IN_TILES * 64) == 0) {
                    changeX += currentPoint.getX() > x ? collision.getWidthHalf() + data.getScope() / 2 : -collision.getWidthHalf() - data.getScope() / 2;
                }
                if (currentPoint.getY() % (Y_IN_TILES * 64) == 0) {
                    changeY += currentPoint.getY() > y ? collision.getWidthHalf() + data.getScope() / 2 : -collision.getWidthHalf() - data.getScope() / 2;
                }
                solution.add(new Point(changeX, changeY));
                return solution;
            }
        }
        return null;
    }
}
