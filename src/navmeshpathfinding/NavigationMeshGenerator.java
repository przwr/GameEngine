/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Block;
import collision.Figure;
import engine.BlueArray;
import engine.Point;
import static game.place.Area.X_IN_TILES;
import static game.place.Area.Y_IN_TILES;
import game.place.Place;
import game.place.Tile;
import java.awt.geom.Line2D;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author przemek
 */
public class NavigationMeshGenerator {

    private static int yStartBound = -1, yEndBound = -1, xStartBound = -1, xEndBound = -1, xSTemp, ySTemp, xETemp, yETemp;
    private static BlueArray<Point> pointsToRemove = new BlueArray<>();
    private static Set<Line> tempLines = new HashSet<>();
    private static Set<Point> pointsToConnect = new HashSet<>();
    private static Point tempPoint1 = new Point(), tempPoint2 = new Point(), start, end;
    private static PriorityQueue<Line> sortedLines = new PriorityQueue<>();
    private static BlueArray<Line> linesToConnect = new BlueArray<>();
    private static Tile tile;
    private static BitSet spots = new BitSet();
    private static boolean intersects;
    private static int sharedPoints;

    // Uwaga! Generowanie źle uwzględnia NULL Tile w środku Area - powinny być zakryte bloczkiem, lub nie powinno ich być wcale
    public static void generateNavigationMesh(Tile[] tiles, List<Block> blocks) {
        long startTime = System.currentTimeMillis();
        if (findBounds(tiles)) {
            findPoints(tiles, blocks);
            connectPoints();
            solveLines();

//            long endTime = System.currentTimeMillis();
//            System.out.println("Time: " + ((endTime - startTime)) + " ms");
//            System.out.println("Points To Connect: " + pointsToConnect.size());
//            System.out.println("Lines : " + tempLines.size());
//            System.out.println("Lines To Connect: " + linesToConnect.size());
//            LineWindow win = new LineWindow();
//            win.addVariables(linesToConnect);
//            win.setVisible(true);
        }

    }

    private static boolean findBounds(Tile[] tiles) {
        if (!findYBounds(tiles)) {
            return false;
        }
        findXBounds(tiles);
        return true;
    }

    private static boolean findYBounds(Tile[] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                yStartBound = getYFromIndex(i);
                break;
            }
        }
        if (yStartBound == -1) {
            return false;
        }
        for (int i = tiles.length - 1; i >= 0; i--) {
            if (tiles[i] != null) {
                yEndBound = getYFromIndex(i);
                break;
            }
        }
        return true;
    }

    private static void findXBounds(Tile[] tiles) {
        XStart:
        for (int x = 0; x < X_IN_TILES; x++) {
            for (int y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xStartBound = x;
                    break XStart;
                }
            }
        }
        XEnd:
        for (int x = X_IN_TILES - 1; x >= 0; x--) {
            for (int y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xEndBound = x;
                    break XEnd;
                }
            }
        }
    }

    private static void findPoints(Tile[] tiles, List<Block> blocks) {
        addPointsAndSureLinesFromBlocks(blocks);
        // addNotRedundantLines();
        addPointsFromTiles(tiles);
        removeRedundantPoints();
    }

    private static void addPointsAndSureLinesFromBlocks(List<Block> blocks) {
        tempLines.clear();
        for (Block block : blocks) {
            Figure figure = block.getCollision();
            xSTemp = figure.getX();
            ySTemp = figure.getY();
            xETemp = figure.getXEnd();
            yETemp = figure.getYEnd();
            Point point1 = new Point(xSTemp, ySTemp);
            Point point2 = new Point(xSTemp, yETemp);
            Point point3 = new Point(xETemp, yETemp);
            Point point4 = new Point(xETemp, ySTemp);
            pointsToConnect.add(point1);
            pointsToConnect.add(point2);
            pointsToConnect.add(point3);
            pointsToConnect.add(point4);

            //Lines from Blocks
//            tempLines.add(new Line(point1, point2));
//            tempLines.add(new Line(point2, point3));
//            tempLines.add(new Line(point3, point4));
//            tempLines.add(new Line(point4, point1));
            xSTemp /= Place.tileSize;
            ySTemp /= Place.tileSize;
            xETemp /= Place.tileSize;
            yETemp /= Place.tileSize;
            for (int x = xSTemp; x < xETemp; x++) {
                for (int y = ySTemp; y < yETemp; y++) {
                    spots.set(getIndex(x, y));
                }
            }
        }
    }

    private static void addNotRedundantLines() {
        linesToConnect.clear();
        for (Line line1 : tempLines) {
            start = line1.getStart();
            end = line1.getEnd();
            intersects = false;
            if (start.getX() == end.getX()) {
                for (Line line2 : tempLines) {
                    if (line2.getStart().getX() == start.getX()) {
                        if (solveXOverlappingLines(line1, line2)) {
                            intersects = true;
                        }
                    }
                }
            } else if (start.getY() == end.getY()) {
                for (Line line2 : tempLines) {
                    if (line2.getStart().getY() == start.getY()) {
                        solveYOverlappingLines(start, end, line2);
                    }
                }
            }
            if (!intersects) {
                linesToConnect.add(line1);
            }
        }
    }

    private static boolean solveXOverlappingLines(Line line1, Line line2) {
        Point two, three, four;
        int startY = start.getY();
        if (line2.getStart().getY() == start.getY()) {
            two = start;
            three = line2.getEnd();
            four = end;
            if (solveXOverlappingLines(line1, line2, two, three, four)) {
                return true;
            }
        } else if (line2.getStart().getY() == end.getY()) {
            two = end;
            three = line2.getEnd();
            four = start;
            if (solveXOverlappingLines(line1, line2, two, three, four)) {
                return true;
            }
        } else if (line2.getEnd().getY() == end.getY()) {
            two = end;
            three = line2.getStart();
            four = start;
            if (solveXOverlappingLines(line1, line2, two, three, four)) {
                return true;
            }
        } else if (line2.getEnd().getY() == start.getY()) {
            two = end;
            three = line2.getStart();
            four = start;
            if (solveXOverlappingLines(line1, line2, two, three, four)) {
                return true;
            }
        } else {
            if (line2.getStart().getY() < line2.getEnd().getY()) {
                if (start.getY() > line2.getStart().getY() && start.getY() < line2.getEnd().getY()) {

//                    linesToConnect.add(new Line(end, line2.getStart()));
//                    linesToConnect.add(new Line(start, line2.getEnd()));
                } else if (end.getY() > line2.getStart().getY() && end.getY() < line2.getEnd().getY()) {

//                    linesToConnect.add(new Line(end, line2.getStart()));
//                    linesToConnect.add(new Line(start, line2.getEnd()));
                }
            } else {
                if (start.getY() > line2.getEnd().getY() && start.getY() < line2.getStart().getY()) {

                } else if (end.getY() > line2.getEnd().getY() && end.getY() < line2.getStart().getY()) {

                }
            }
        }
        return false;
    }

    private static boolean solveXOverlappingLines(Line line1, Line line2, Point start1, Point end2, Point end1) {
        if (end2.getY() > start1.getY() && end1.getY() > start1.getY()) {
            linesToConnect.add(end2.getY() < end1.getY() ? line2 : line1);
            linesToConnect.add(new Line(end2, end1));
            return true;
        } else if (end2.getY() < start1.getY() && end1.getY() < start1.getY()) {
            linesToConnect.add(end2.getY() > end1.getY() ? line2 : line1);
            linesToConnect.add(new Line(end2, end1));
            return true;
        }
        return false;
    }

    private static void solveYOverlappingLines(Point start, Point end, Line line2) {

    }

    private static void addPointsFromTiles(Tile[] tiles) {
        for (int x = xStartBound; x <= xEndBound; x++) {
            for (int y = yStartBound; y <= yEndBound; y++) {
                tile = getTile(x, y, tiles);
                if (tile == null) {
                    spots.set(getIndex(x, y));
                    xSTemp = x * Place.tileSize;
                    ySTemp = y * Place.tileSize;
                    xETemp = (x + 1) * Place.tileSize;
                    yETemp = (y + 1) * Place.tileSize;
                    pointsToConnect.add(new Point(xSTemp, ySTemp));
                    pointsToConnect.add(new Point(xSTemp, yETemp));
                    pointsToConnect.add(new Point(xETemp, ySTemp));
                    pointsToConnect.add(new Point(xETemp, yETemp));
                }
            }
        }

        xSTemp = xStartBound * Place.tileSize;
        ySTemp = yStartBound * Place.tileSize;
        xETemp = (xEndBound + 1) * Place.tileSize;
        yETemp = (yEndBound + 1) * Place.tileSize;

        pointsToConnect.add(new Point(xSTemp, ySTemp));
        pointsToConnect.add(new Point(xSTemp, yETemp));
        pointsToConnect.add(new Point(xETemp, ySTemp));
        pointsToConnect.add(new Point(xETemp, yETemp));
    }

    private static void removeRedundantPoints() {
        // Za dużo usuwa - jeśli bloczki są od siebie oddalone o 1 tile
        
        pointsToRemove.clear();
        for (Point point : pointsToConnect) {
            tempPoint1.set(point.getX() + Place.tileSize, point.getY());
            tempPoint2.set(point.getX() - Place.tileSize, point.getY());
            if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
                pointsToRemove.add(point);
                continue;
            }
            tempPoint1.set(point.getX(), point.getY() + Place.tileSize);
            tempPoint2.set(point.getX(), point.getY() - Place.tileSize);
            if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
                pointsToRemove.add(point);
            }
        }

        tempPoint1.set(xSTemp + Place.tileSize, ySTemp);
        tempPoint2.set(xSTemp, ySTemp + Place.tileSize);
        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
            pointsToRemove.add(new Point(xSTemp, ySTemp));
        }

        tempPoint1.set(xSTemp + Place.tileSize, yETemp);
        tempPoint2.set(xSTemp, yETemp - Place.tileSize);
        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
            pointsToRemove.add(new Point(xSTemp, yETemp));
        }

        tempPoint1.set(xETemp - Place.tileSize, ySTemp);
        tempPoint2.set(xETemp, ySTemp + Place.tileSize);
        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
            pointsToRemove.add(new Point(xETemp, ySTemp));
        }

        tempPoint1.set(xETemp - Place.tileSize, yETemp);
        tempPoint2.set(xETemp, yETemp - Place.tileSize);
        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
            pointsToRemove.add(new Point(xETemp, yETemp));
        }

        pointsToRemove.stream().forEach((point) -> {
            pointsToConnect.remove(point);
        });
    }

    private static void connectPoints() {
        tempLines.clear();
        for (Point point1 : pointsToConnect) {
            for (Point point2 : pointsToConnect) {
                if (point1 != point2) {
                    if (isConnectsWalkable(point1, point2)) {
                        tempLines.add(new Line(point1, point2));
                    }
                }
            }
        }
    }

    private static boolean isConnectsWalkable(Point point1, Point point2) {
        if (point1.getX() != point2.getX() && point1.getY() != point2.getY()) {
            if (spots.get(getIndex(((point1.getX() + point2.getX()) / 2) / Place.tileSize, ((point1.getY() + point2.getY()) / 2) / Place.tileSize))) {
                return false;
            }
        }
        return true;
    }

    private static void solveLines() {
        sortedLines.clear();
        tempLines.stream().forEach((line) -> {
            sortedLines.add(line);
        });
        while (!sortedLines.isEmpty()) {
            Line line1 = sortedLines.poll();
            start = line1.getStart();
            end = line1.getEnd();
            intersects = false;
            for (Line line2 : linesToConnect) {
                sharedPoints = 0;
                if (start.equals(line2.getStart()) || start.equals(line2.getEnd())) {
                    sharedPoints++;
                }
                if (end.equals(line2.getStart()) || end.equals(line2.getEnd())) {
                    sharedPoints++;
                }
                if (sharedPoints == 0) {
                    if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), line2.getStart().getX(), line2.getStart().getY(), line2.getEnd().getX(), line2.getEnd().getY())) {
                        intersects = true;
                        break;
                    }
                }
            }
            if (!intersects) {
                linesToConnect.add(line1);
            }
        }
    }

    private static Tile getTile(int x, int y, Tile[] tiles) {
        return tiles[x + y * X_IN_TILES];
    }

    private static Tile getTile(int index, Tile[] tiles) {
        return tiles[index];
    }

    private static int getIndex(int x, int y) {
        return x + y * X_IN_TILES;
    }

    private static int getXFromIndex(int index) {
        return index % X_IN_TILES;
    }

    private static int getYFromIndex(int index) {
        return index / X_IN_TILES;
    }

}
