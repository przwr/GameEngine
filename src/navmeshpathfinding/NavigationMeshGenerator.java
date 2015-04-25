/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Block;
import collision.Figure;
import collision.RoundRectangle;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.LEFT_TOP;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT_TOP;
import engine.BlueArray;
import engine.Point;
import static game.place.Area.X_IN_TILES;
import static game.place.Area.Y_IN_TILES;
import game.place.Place;
import game.place.Tile;
import java.awt.geom.Line2D;
import java.util.Arrays;
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

    private static int i, x, y, xMod, yMod, yStartBound = -1, yEndBound = -1, xStartBound = -1, xEndBound = -1, xSTemp, ySTemp, xETemp, yETemp, lineXStart = 0, lineYStart = 0;
    private static BlueArray<Point> pointsToRemove = new BlueArray<>();
    private static Set<Line> tempLines = new HashSet<>();
    private static Set<Point> pointsToConnect = new HashSet<>();
    private static Point tempPoint1 = new Point(), tempPoint2 = new Point(), start, end, xModed, yModed;
    private static PriorityQueue<Line> sortedLines = new PriorityQueue<>();
    private static BlueArray<Line> linesToConnect = new BlueArray<>();
    private static BlueArray<Line> linesToRemove = new BlueArray<>();
    private static BlueArray<Line> linesToAdd = new BlueArray<>();
    private static BlueArray<Line> diagonalLines = new BlueArray<>();
    private static BlueArray<Line> secondDiagonalLines = new BlueArray<>();
    private static BitSet spots = new BitSet();
    private static Figure figure;
    private static boolean intersects, inline;
    private static int sharedPoints;
    private static boolean isWindow;

    public static void generateNavigationMesh(Tile[] tiles, List<Block> blocks) {
      //  long startTime = System.nanoTime();
        // DON'T change order. It's crucial and uses static fields!
        findBoundsAndSetCollisionSpots(tiles, blocks);
        createLinesFromSpots();
        addDiagonalLines(blocks);
        connectDiagonalLines();
        collectPointsFromLines();
        connectPoints();
        solveLines();
//            findPoints(tiles, blocks);
//            connectPoints();
//            solveLines();
       // long endTime = System.nanoTime();
      //  System.out.println("Time: " + ((endTime - startTime)) + " ns");
//            System.out.println("Points To Connect: " + pointsToConnect.size());
//            System.out.println("Lines : " + tempLines.size());
//            System.out.println("Lines To Connect: " + linesToConnect.size());

//        if (!isWindow) {
//            Line[] lines = new Line[linesToConnect.size()];
//            int i = 0;
//            for (Line line : linesToConnect) {
//                lines[i] = line;
//                i++;
//            }
//            LineWindow win = new LineWindow();
//            win.addVariables(Arrays.asList(lines));
//            win.setVisible(true);
////                isWindow = true;
//        }
    }

    private static void findBoundsAndSetCollisionSpots(Tile[] tiles, List<Block> blocks) {
        findBoundsFromTiles(tiles);
        setCollisionSpotsFromTiles(tiles);
        findBoundsAndSetCollisionSpotsFromBlocks(blocks);
    }

    private static void findBoundsFromTiles(Tile[] tiles) {
        findYStartBound(tiles);
        findYEndBound(tiles);
        findXStartBound(tiles);
        findXEndBound(tiles);
    }

    private static void findYStartBound(Tile[] tiles) {
        for (i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                yStartBound = getYFromIndex(i);
                break;
            }
        }
    }

    private static void findYEndBound(Tile[] tiles) {
        for (i = tiles.length - 1; i >= 0; i--) {
            if (tiles[i] != null) {
                yEndBound = getYFromIndex(i);
                break;
            }
        }
    }

    private static void findXStartBound(Tile[] tiles) {
        XStart:
        for (x = 0; x < X_IN_TILES; x++) {
            for (y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xStartBound = x;
                    break XStart;
                }
            }
        }
    }

    private static void findXEndBound(Tile[] tiles) {
        XEnd:
        for (x = X_IN_TILES - 1; x >= 0; x--) {
            for (y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xEndBound = x;
                    break XEnd;
                }
            }
        }
    }

    private static void setCollisionSpotsFromTiles(Tile[] tiles) {
        if (yStartBound != -1) {
            for (x = xStartBound; x <= xEndBound; x++) {
                for (y = yStartBound; y <= yEndBound; y++) {
                    if (getTile(x, y, tiles) == null) {
                        spots.set(getIndex(x, y));
                    }
                }
            }
        }
    }

    private static void findBoundsAndSetCollisionSpotsFromBlocks(List<Block> blocks) {
        blocks.stream().forEach((block) -> {
            figure = block.getCollision();
            xSTemp = figure.getX() / Place.tileSize;
            ySTemp = figure.getY() / Place.tileSize;
            xETemp = figure.getXEnd() / Place.tileSize;
            yETemp = figure.getYEnd() / Place.tileSize;
            findBoundsFromBlocks();
            setCollisionSpotsFromBlocks();
        });
    }

    private static void findBoundsFromBlocks() {
        if (xSTemp < xStartBound) {
            xStartBound = xSTemp;
        }
        if (xETemp > xEndBound) {
            xEndBound = xETemp;
        }
        if (ySTemp < yStartBound) {
            yStartBound = ySTemp;
        }
        if (yETemp > yEndBound) {
            yEndBound = yETemp;
        }
    }

    private static void setCollisionSpotsFromBlocks() {
        for (x = xSTemp; x < xETemp; x++) {
            for (y = ySTemp; y < yETemp; y++) {
                spots.set(getIndex(x, y));
            }
        }
    }

    private static void createLinesFromSpots() {
        linesToConnect.clear();
        createHorisontalLines();
        createVerticalLines();
    }

    private static void createHorisontalLines() {
        inline = false;
        for (int y = yStartBound - 1; y <= yEndBound; y++) {
            for (int x = xStartBound; x <= xEndBound + 1; x++) {
                if (isCollision(x, y) != isCollision(x, y + 1)) {
                    if (!inline) {
                        lineXStart = x * Place.tileSize;
                        lineYStart = (y + 1) * Place.tileSize;
                        inline = true;
                    }
                } else if (inline) {
                    linesToConnect.add(new Line(new Point(lineXStart, lineYStart), new Point(x * Place.tileSize, (y + 1) * Place.tileSize)));
                    inline = false;
                }
            }
        }
    }

    private static void createVerticalLines() {
        inline = false;
        for (int x = xStartBound - 1; x <= xEndBound; x++) {
            for (int y = yStartBound - 1; y <= yEndBound + 1; y++) {
                if (isCollision(x, y) != isCollision(x + 1, y)) {
                    if (!inline) {
                        lineXStart = (x + 1) * Place.tileSize;
                        lineYStart = y * Place.tileSize;
                        inline = true;
                    }
                } else if (inline) {
                    linesToConnect.add(new Line(new Point(lineXStart, lineYStart), new Point((x + 1) * Place.tileSize, y * Place.tileSize)));
                    inline = false;
                }
            }
        }
    }

    private static boolean isCollision(int x, int y) {
        if (y > yEndBound || x > xEndBound || y < yStartBound || x < xStartBound) {
            return true;
        }
        return spots.get(getIndex(x, y));
    }

    private static void addDiagonalLines(List<Block> blocks) {
        diagonalLines.clear();
        secondDiagonalLines.clear();
        RoundRectangle figure;
        for (Block block : blocks) {
            if (block.getCollision() instanceof RoundRectangle) {
                figure = (RoundRectangle) block.getCollision();
                for (int i = 0; i < 4; i++) {
                    if (figure.isCornerPushed(i)) {
                        if (figure.isCornerTriangular(i) || figure.isCornerConcave(i)) {
                            if (shouldCorrect(i, figure)) {
                                correctLine(i, figure);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean shouldCorrect(int corner, RoundRectangle figure) {
        switch (corner) {
            case LEFT_TOP:
                if (isCollisionFromCoordinates(figure.getX(), figure.getY() - Place.tileSize) || isCollisionFromCoordinates(figure.getX() - Place.tileSize, figure.getY())) {
                    return false;
                }
                break;
            case LEFT_BOTTOM:
                if (isCollisionFromCoordinates(figure.getX(), figure.getYEnd()) || isCollisionFromCoordinates(figure.getX() - Place.tileSize, figure.getYEnd() - Place.tileSize)) {
                    return false;
                }
                break;
            case RIGHT_BOTTOM:
                if (isCollisionFromCoordinates(figure.getX(), figure.getYEnd()) || isCollisionFromCoordinates(figure.getXEnd(), figure.getYEnd() - Place.tileSize)) {
                    return false;
                }
                break;
            case RIGHT_TOP:
                if (isCollisionFromCoordinates(figure.getX(), figure.getY() - Place.tileSize) || isCollisionFromCoordinates(figure.getXEnd(), figure.getY())) {
                    return false;
                }
                break;
        }
        return true;
    }

    private static boolean isCollisionFromCoordinates(int x, int y) {
        x /= Place.tileSize;
        y /= Place.tileSize;
        if (y > yEndBound || x > xEndBound || y < yStartBound || x < xStartBound) {
            return true;
        }
        return spots.get(getIndex(x, y));
    }

    private static void correctLine(int corner, RoundRectangle collision) {
        linesToRemove.clear();
        linesToAdd.clear();
        setCornerPoints(corner, collision);
        for (Line line : linesToConnect) {
            if (line.getEnd().equals(tempPoint1)) {
                cornerAtEndOfLine(line);
            } else if (line.getStart().equals(tempPoint1)) {
                cornerAtStartOfLine(line);
            } else {
                cornerOnTheLine(corner, line);
            }
        }
        Line tempLine = new Line(xModed, yModed);
        diagonalLines.add(tempLine);
        secondDiagonalLines.add(tempLine);
        linesToConnect.add(tempLine);
        linesToConnect.removeAll(linesToRemove);
        linesToConnect.addAll(linesToAdd);
    }

    private static void setCornerPoints(int corner, RoundRectangle collision) {
        switch (corner) {
            case LEFT_TOP:
                x = collision.getX();
                y = collision.getY();
                xMod = x + Place.tileSize;
                yMod = y + Place.tileSize;
                break;
            case LEFT_BOTTOM:
                x = collision.getX();
                y = collision.getYEnd();
                xMod = x + Place.tileSize;
                yMod = y - Place.tileSize;
                break;
            case RIGHT_BOTTOM:
                x = collision.getXEnd();
                y = collision.getYEnd();
                xMod = x - Place.tileSize;
                yMod = y - Place.tileSize;
                break;
            case RIGHT_TOP:
                x = collision.getXEnd();
                y = collision.getY();
                xMod = x - Place.tileSize;
                yMod = y + Place.tileSize;
                break;
        }
        xModed = new Point(xMod, y);
        yModed = new Point(x, yMod);
        tempPoint1.set(x, y);
    }

    private static void cornerAtEndOfLine(Line line) {
        if (line.isHorisontal()) {
            if (line.getStart().getX() - Place.tileSize == line.getEnd().getX()) {
                linesToRemove.add(line);
            } else {
                line.setXEnd(line.getEnd().getX() - Place.tileSize);
            }
        } else if (line.isVertical()) {
            if (line.getStart().getY() - Place.tileSize == line.getEnd().getY()) {
                linesToRemove.add(line);
            } else {
                line.setYEnd(line.getEnd().getY() - Place.tileSize);
            }
        }
    }

    private static void cornerAtStartOfLine(Line line) {
        if (line.isHorisontal()) {
            if (line.getStart().getX() + Place.tileSize == line.getEnd().getX()) {
                linesToRemove.add(line);
            } else {
                line.setXStart(line.getStart().getX() + Place.tileSize);
            }
        } else if (line.isVertical()) {
            if (line.getStart().getY() + Place.tileSize == line.getEnd().getY()) {
                linesToRemove.add(line);
            } else {
                line.setYStart(line.getStart().getY() + Place.tileSize);
            }
        }
    }

    private static void cornerOnTheLine(int corner, Line line) {
        switch (corner) {
            case LEFT_TOP:
                if (line.isHorisontal()) {
                    if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                        if (xMod != line.getEnd().getX()) {
                            linesToAdd.add(new Line(new Point(xMod, y), new Point(line.getEnd().getX(), y)));
                        }
                        if (line.getStart().getX() != x) {
                            line.setXEnd(x);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                } else if (line.isVertical()) {
                    if (line.getEnd().getX() == x && line.getStart().getY() < y && y < line.getEnd().getY()) {
                        if (yMod != line.getEnd().getY()) {
                            linesToAdd.add(new Line(new Point(x, yMod), new Point(x, line.getEnd().getY())));
                        }
                        if (line.getStart().getY() != y) {
                            line.setYEnd(y);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                }
                break;
            case LEFT_BOTTOM:
                if (line.isHorisontal()) {
                    if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                        if (xMod != line.getEnd().getX()) {
                            linesToAdd.add(new Line(new Point(xMod, y), new Point(line.getEnd().getX(), y)));
                        }
                        if (line.getStart().getX() != x) {
                            line.setXEnd(x);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                } else if (line.isVertical()) {
                    if (line.getEnd().getX() == x && line.getStart().getY() < y && y < line.getEnd().getY()) {
                        if (y != line.getEnd().getY()) {
                            linesToAdd.add(new Line(new Point(x, y), new Point(x, line.getEnd().getY())));
                        }
                        if (line.getStart().getY() != yMod) {
                            line.setYEnd(yMod);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                }
                break;
            case RIGHT_BOTTOM:
                if (line.isHorisontal()) {
                    if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                        if (x != line.getEnd().getX()) {
                            linesToAdd.add(new Line(new Point(x, y), new Point(line.getEnd().getX(), y)));
                        }
                        if (line.getStart().getX() != xMod) {
                            line.setXEnd(xMod);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                } else if (line.isVertical()) {
                    if (line.getEnd().getX() == x && line.getStart().getY() < y && y < line.getEnd().getY()) {
                        if (y != line.getEnd().getY()) {
                            linesToAdd.add(new Line(new Point(x, y), new Point(x, line.getEnd().getY())));
                        }
                        if (line.getStart().getY() != yMod) {
                            line.setYEnd(yMod);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                }
                break;
            case RIGHT_TOP:
                if (line.isHorisontal()) {
                    if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                        if (x != line.getEnd().getX()) {
                            linesToAdd.add(new Line(new Point(x, y), new Point(line.getEnd().getX(), y)));
                        }
                        if (line.getStart().getX() != xMod) {
                            line.setXEnd(xMod);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                } else if (line.isVertical()) {
                    if (line.getEnd().getX() == x && line.getStart().getY() < y && y < line.getEnd().getY()) {
                        if (yMod != line.getEnd().getY()) {
                            linesToAdd.add(new Line(new Point(x, yMod), new Point(x, line.getEnd().getY())));
                        }
                        if (line.getStart().getY() != y) {
                            line.setYEnd(y);
                        } else {
                            linesToRemove.add(line);
                        }
                    }
                }
                break;
        }
    }

    private static void connectDiagonalLines() {
        linesToRemove.clear();
        boolean changed = true;
        Line tempLine = null;
        while (changed) {
            changed = false;
            if (tempLine != null) {
                diagonalLines.remove(tempLine);
                tempLine = null;
            }
            mainloop:
            for (Line line1 : diagonalLines) {
                for (Line line2 : diagonalLines) {
                    if (!line1.equals(line2)) {
                        double a = line1.getDirectional();
                        if (line1.getStart().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getEnd());
                            linesToRemove.add(line1);
                            tempLine = line1;
                            changed = true;
                            break mainloop;
                        } else if (line1.getStart().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getEnd());
                            linesToRemove.add(line1);
                            tempLine = line1;
                            changed = true;
                            break mainloop;
                        } else if (line1.getEnd().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getStart());
                            linesToRemove.add(line1);
                            tempLine = line1;
                            changed = true;
                            break mainloop;
                        } else if (line1.getEnd().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getStart());
                            linesToRemove.add(line1);
                            tempLine = line1;
                            changed = true;
                            break mainloop;
                        }
                    }
                }
            }
        }
        linesToConnect.addAll(linesToRemove);
    }

    private static void collectPointsFromLines() {
        pointsToConnect.clear();
        linesToConnect.stream().forEach((line) -> {
            pointsToConnect.add(line.getStart());
            pointsToConnect.add(line.getEnd());
        });
    }

//    private static void addNotRedundantLines() {
//        linesToConnect.clear();
//        for (Line line1 : tempLines) {
//            start = line1.getStart();
//            end = line1.getEnd();
//            intersects = false;
//            if (start.getX() == end.getX()) {
//                for (Line line2 : tempLines) {
//                    if (line2.getStart().getX() == start.getX()) {
//                        if (solveXOverlappingLines(line1, line2)) {
//                            intersects = true;
//                        }
//                    }
//                }
//            } else if (start.getY() == end.getY()) {
//                for (Line line2 : tempLines) {
//                    if (line2.getStart().getY() == start.getY()) {
//                        solveYOverlappingLines(start, end, line2);
//                    }
//                }
//            }
//            if (!intersects) {
//                linesToConnect.add(line1);
//            }
//        }
//    }
//
//    private static boolean solveXOverlappingLines(Line line1, Line line2) {
//        Point two, three, four;
//        int startY = start.getY();
//        if (line2.getStart().getY() == start.getY()) {
//            two = start;
//            three = line2.getEnd();
//            four = end;
//            if (solveXOverlappingLines(line1, line2, two, three, four)) {
//                return true;
//            }
//        } else if (line2.getStart().getY() == end.getY()) {
//            two = end;
//            three = line2.getEnd();
//            four = start;
//            if (solveXOverlappingLines(line1, line2, two, three, four)) {
//                return true;
//            }
//        } else if (line2.getEnd().getY() == end.getY()) {
//            two = end;
//            three = line2.getStart();
//            four = start;
//            if (solveXOverlappingLines(line1, line2, two, three, four)) {
//                return true;
//            }
//        } else if (line2.getEnd().getY() == start.getY()) {
//            two = end;
//            three = line2.getStart();
//            four = start;
//            if (solveXOverlappingLines(line1, line2, two, three, four)) {
//                return true;
//            }
//        } else {
//            if (line2.getStart().getY() < line2.getEnd().getY()) {
//                if (start.getY() > line2.getStart().getY() && start.getY() < line2.getEnd().getY()) {
//
////                    linesToConnect.add(new Line(end, line2.getStart()));
////                    linesToConnect.add(new Line(start, line2.getEnd()));
//                } else if (end.getY() > line2.getStart().getY() && end.getY() < line2.getEnd().getY()) {
//
////                    linesToConnect.add(new Line(end, line2.getStart()));
////                    linesToConnect.add(new Line(start, line2.getEnd()));
//                }
//            } else {
//                if (start.getY() > line2.getEnd().getY() && start.getY() < line2.getStart().getY()) {
//
//                } else if (end.getY() > line2.getEnd().getY() && end.getY() < line2.getStart().getY()) {
//
//                }
//            }
//        }
//        return false;
//    }
//
//    private static boolean solveXOverlappingLines(Line line1, Line line2, Point start1, Point end2, Point end1) {
//        if (end2.getY() > start1.getY() && end1.getY() > start1.getY()) {
//            linesToConnect.add(end2.getY() < end1.getY() ? line2 : line1);
//            linesToConnect.add(new Line(end2, end1));
//            return true;
//        } else if (end2.getY() < start1.getY() && end1.getY() < start1.getY()) {
//            linesToConnect.add(end2.getY() > end1.getY() ? line2 : line1);
//            linesToConnect.add(new Line(end2, end1));
//            return true;
//        }
//        return false;
//    }
//
//    private static void solveYOverlappingLines(Point start, Point end, Line line2) {
//
//    }

//    private static void removeRedundantPoints() {
//        // Za dużo usuwa - jeśli bloczki są od siebie oddalone o 1 tile
//
//        pointsToRemove.clear();
//        for (Point point : pointsToConnect) {
//            tempPoint1.set(point.getX() + Place.tileSize, point.getY());
//            tempPoint2.set(point.getX() - Place.tileSize, point.getY());
//            if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//                pointsToRemove.add(point);
//                continue;
//            }
//            tempPoint1.set(point.getX(), point.getY() + Place.tileSize);
//            tempPoint2.set(point.getX(), point.getY() - Place.tileSize);
//            if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//                pointsToRemove.add(point);
//            }
//        }
//
//        tempPoint1.set(xSTemp + Place.tileSize, ySTemp);
//        tempPoint2.set(xSTemp, ySTemp + Place.tileSize);
//        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//            pointsToRemove.add(new Point(xSTemp, ySTemp));
//        }
//
//        tempPoint1.set(xSTemp + Place.tileSize, yETemp);
//        tempPoint2.set(xSTemp, yETemp - Place.tileSize);
//        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//            pointsToRemove.add(new Point(xSTemp, yETemp));
//        }
//
//        tempPoint1.set(xETemp - Place.tileSize, ySTemp);
//        tempPoint2.set(xETemp, ySTemp + Place.tileSize);
//        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//            pointsToRemove.add(new Point(xETemp, ySTemp));
//        }
//
//        tempPoint1.set(xETemp - Place.tileSize, yETemp);
//        tempPoint2.set(xETemp, yETemp - Place.tileSize);
//        if (pointsToConnect.contains(tempPoint1) && pointsToConnect.contains(tempPoint2)) {
//            pointsToRemove.add(new Point(xETemp, yETemp));
//        }
//
//        pointsToRemove.stream().forEach((point) -> {
//            pointsToConnect.remove(point);
//        });
//    }

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
        if (!point1.equals(point2)) {
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
