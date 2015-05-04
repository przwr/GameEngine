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
import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import game.place.Tile;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author przemek
 */
public class NavigationMeshGenerator {

    private static int x, y, xMod, yMod, yStartBound = -1, yEndBound = -1, xStartBound = -1, xEndBound = -1, xSTemp, ySTemp, xETemp, yETemp, lineXStart = 0, lineYStart = 0;
    private static double xd, yd;
    private static final BitSet spots = new BitSet();
    private static final byte[] diagonal = new byte[X_IN_TILES * Y_IN_TILES];
    private static final Set<Line> newLines = new HashSet<>();
    private static final PriorityQueue<Line> sortedLines = new PriorityQueue<>();
    private static final BlueArray<Line> linesToRemove = new BlueArray<>();
    private static final BlueArray<Line> linesToAdd = new BlueArray<>();
    private static final BlueArray<Line> sureLines = new BlueArray<>();
    private static final Set<Line> linesToCheck = new HashSet<>();
    private static final Set<Point> pointsToConnect = new HashSet<>();
    private static final Set<Triangle> triangles = new HashSet<>();
    private static final Map<Line, NeightbourTriangles> linesTriangles = new HashMap<>();

    private static final Point tempPoint = new Point();
    private static Point start, end, xModed, yModed;
    private static Line tempLine1, tempLine2, tempLine3, currentLine;
    private static Triangle currentTriangle;
    private static NeightbourTriangles neightbours;
    private static Figure figure;
    private static RoundRectangle round;
    private static boolean intersects, inline, changed;
    private static int sharedPoints;
    private static NavigationMesh navigationMesh;

    private static boolean showMesh = false, showLines = false, copy;
    private static int areaNumberToShow = 0;
    public static long fullTime = 0;
    public static int areas = 0;

    public static NavigationMesh generateNavigationMesh(Tile[] tiles, Set<Block> blocks, int xArea, int yArea) {
        while (copy) {
            System.out.println("Loading ...");
        }
        long startTime = System.nanoTime();

        // DON'T change order. It's crucial and uses static fields!
        findBoundsAndSetCollisionSpots(tiles, blocks, xArea, yArea);
        createLinesFromSpots();
        createAndAddDiagonalLines(blocks);
        collectPointsFromLines();
        connectPoints();
        solveLines();
        createTriangles();
        generateNavigationMesh();
//
//
//       _  _ __ _  __ _ _ _ _ __ _  FOR TESTING __ _ _ _ _ _ _ __ _ __ 
        long endTime = System.nanoTime();
        long diffrence = endTime - startTime;
        fullTime += diffrence;
//            System.out.println("Time: " + diffrence + " ns " + (diffrence / 1000000d) + " ms");
//            System.out.println("Points: " + pointsToConnect.size());
//            System.out.println("Lines: " + sureLines.size());
//            System.out.println("Triangles " + triangles.size());
        if ((showMesh || showLines) && areas == areaNumberToShow) {
            if (showLines) {
                copy = true;
                Line[] lines = new Line[sureLines.size()];
                int i = 0;
                for (Line line : sureLines) {
                    lines[i++] = line;
                }
                copy = false;
                LineWindow win = new LineWindow();
                win.addVariables(Arrays.asList(lines));
                win.setVisible(true);
                showLines = false;
            }
            if (showMesh) {
                start = new Point(64, 64);
                end = new Point(1600, 1216);
                Window mesh = new Window();
                mesh.addVariables(navigationMesh, start, end, PathFinder.findPath(navigationMesh, start, end));
                mesh.setVisible(true);
                showMesh = false;
            }
        }
        areas++;
        ///__ __ _ _ _ _ _ __ _ _ _ __ _ _ _ __ _ _ __ _ _ _ __ _  __ _ 
        //
        //
        //
        return navigationMesh;
    }

    private static void findBoundsAndSetCollisionSpots(Tile[] tiles, Set<Block> blocks, int xArea, int yArea) {
        yStartBound = yEndBound = xStartBound = xEndBound = -1;
        lineXStart = lineYStart = 0;
        spots.clear();
        findBoundsFromTiles(tiles);
        setCollisionSpotsFromTiles(tiles);
        findBoundsAndSetCollisionSpotsFromBlocks(blocks, xArea, yArea);
    }

    private static void findBoundsFromTiles(Tile[] tiles) {
        findYStartBound(tiles);
        findYEndBound(tiles);
        findXStartBound(tiles);
        findXEndBound(tiles);
    }

    private static void findYStartBound(Tile[] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                yStartBound = getYFromIndex(i);
                break;
            }
        }
    }

    private static void findYEndBound(Tile[] tiles) {
        for (int i = tiles.length - 1; i >= 0; i--) {
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

    private static void findBoundsAndSetCollisionSpotsFromBlocks(Set<Block> blocks, int xArea, int yArea) {
        int xA = xArea * X_IN_TILES;
        int yA = yArea * Y_IN_TILES;
        blocks.stream().forEach((block) -> {
            figure = block.getCollision();
            setXSTemp(xA);
            setYSTemp(yA);
            setXETemp(xA);
            setYETemp(yA);
            setCollisionSpotsFromBlocks();
            checkBoundsRange();
            findBoundsFromBlocks();
        });
    }

    private static void setXSTemp(int xA) {
        xSTemp = (figure.getX() / Place.tileSize);
        if (xSTemp < xA) {
            xSTemp = 0;
        } else if (xSTemp >= xA + X_IN_TILES) {
            xSTemp = X_IN_TILES;
        } else {
            xSTemp %= X_IN_TILES;
        }
    }

    private static void setYSTemp(int yA) {
        ySTemp = (figure.getY() / Place.tileSize);
        if (ySTemp < yA) {
            ySTemp = 0;
        } else if (ySTemp >= yA + Y_IN_TILES) {
            ySTemp = Y_IN_TILES;
        } else {
            ySTemp %= Y_IN_TILES;
        }
    }

    private static void setXETemp(int xA) {
        xETemp = (figure.getXEnd() / Place.tileSize);
        if (xETemp < xA) {
            xETemp = 0;
        } else if (xETemp >= xA + X_IN_TILES) {
            xETemp = X_IN_TILES;
        } else {
            xETemp %= X_IN_TILES;
        }
    }

    private static void setYETemp(int yA) {
        yETemp = (figure.getYEnd() / Place.tileSize);
        if (yETemp < yA) {
            yETemp = 0;
        } else if (yETemp >= yA + Y_IN_TILES) {
            yETemp = Y_IN_TILES;
        } else {
            yETemp %= Y_IN_TILES;
        }
    }

    private static void setCollisionSpotsFromBlocks() {
        for (x = xSTemp; x < xETemp; x++) {
            for (y = ySTemp; y < yETemp; y++) {
                spots.set(getIndex(x, y));
            }
        }
    }

    private static void checkBoundsRange() {
        if (xSTemp < 0) {
            xSTemp = 0;
        }
        if (ySTemp < 0) {
            ySTemp = 0;
        }
        if (xETemp > X_IN_TILES - 1) {
            xETemp = X_IN_TILES - 1;
        }
        if (yETemp > Y_IN_TILES - 1) {
            yETemp = Y_IN_TILES - 1;
        }
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

    private static void createLinesFromSpots() {
        newLines.clear();
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
                    newLines.add(new Line(new Point(lineXStart, lineYStart), new Point(x * Place.tileSize, (y + 1) * Place.tileSize)));
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
                    newLines.add(new Line(new Point(lineXStart, lineYStart), new Point((x + 1) * Place.tileSize, y * Place.tileSize)));
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

    private static void createAndAddDiagonalLines(Set<Block> blocks) {
        sureLines.clear();
        sureLines.addAll(newLines);
        newLines.clear();
        createDiagonalLines(blocks);
        connectDiagonalLines();
        sureLines.addAll(newLines);
    }

    private static void createDiagonalLines(Set<Block> blocks) {
        clearDiagonal();
        blocks.stream().forEach((block) -> {
            if (block.getCollision() instanceof RoundRectangle) {
                round = (RoundRectangle) block.getCollision();
                for (int i = 0; i < 4; i++) {
                    if (round.isCornerPushed(i) && (round.isCornerTriangular(i) || round.isCornerConcave(i))) {
                        if (shouldCorrect(i, round)) {
                            correctLine(i, round);
                        }
                    }
                }
            }
        });
    }

    private static void clearDiagonal() {
        for (int i = 0; i < diagonal.length; i++) {
            diagonal[i] = -1;
        }
    }

    private static boolean shouldCorrect(int corner, RoundRectangle figure) {
        xSTemp = (figure.getX() % xAreaInPixels) / Place.tileSize;
        ySTemp = ((figure.getY()) % yAreaInPixels) / Place.tileSize;
        xETemp = ((figure.getXEnd()) % xAreaInPixels) / Place.tileSize;
        yETemp = ((figure.getYEnd()) % yAreaInPixels) / Place.tileSize;
        return checkByCorner(corner);
    }

    private static boolean checkByCorner(int corner) {
        switch (corner) {
            case LEFT_TOP:
                if (isCollisionFromCoordinates(xSTemp, ySTemp - 1) || isCollisionFromCoordinates(xSTemp - 1, ySTemp)) {
                    return false;
                }
                diagonal[getIndex(xSTemp, ySTemp)] = LEFT_TOP;
                return true;
            case LEFT_BOTTOM:
                if (isCollisionFromCoordinates(xSTemp, yETemp) || isCollisionFromCoordinates(xSTemp - 1, yETemp - 1)) {
                    return false;
                }
                diagonal[getIndex(xSTemp, yETemp - 1)] = LEFT_BOTTOM;
                return true;
            case RIGHT_BOTTOM:
                if (isCollisionFromCoordinates(xSTemp, yETemp) || isCollisionFromCoordinates(xETemp, yETemp - 1)) {
                    return false;
                }
                diagonal[getIndex(xSTemp, yETemp - 1)] = RIGHT_BOTTOM;
                return true;
            case RIGHT_TOP:
                if (isCollisionFromCoordinates(xSTemp, ySTemp - 1) || isCollisionFromCoordinates(xETemp, ySTemp)) {
                    return false;
                }
                diagonal[getIndex(xSTemp, ySTemp)] = RIGHT_TOP;
                return true;
        }
        return true;
    }

    private static boolean isCollisionFromCoordinates(int x, int y) {
        if (y > yEndBound || x > xEndBound || y < yStartBound || x < xStartBound) {
            return true;
        }
        return spots.get(getIndex(x, y));
    }

    private static void correctLine(int corner, RoundRectangle collision) {
        linesToRemove.clear();
        linesToAdd.clear();
        setCornerPoints(corner, collision);
        sureLines.stream().forEach((line) -> {
            if (line.getEnd().equals(tempPoint)) {
                cornerAtEndOfLine(line);
            } else if (line.getStart().equals(tempPoint)) {
                cornerAtStartOfLine(line);
            } else {
                cornerOnTheLine(corner, line);
            }
        });
        newLines.add(new Line(xModed, yModed));
        sureLines.removeAll(linesToRemove);
        sureLines.addAll(linesToAdd);
    }

    private static void setCornerPoints(int corner, RoundRectangle collision) {
        switch (corner) {
            case LEFT_TOP:
                x = collision.getX() % xAreaInPixels;
                y = collision.getY() % yAreaInPixels;
                xMod = x + Place.tileSize;
                yMod = y + Place.tileSize;
                break;
            case LEFT_BOTTOM:
                x = collision.getX() % xAreaInPixels;
                y = collision.getYEnd() % yAreaInPixels;
                xMod = x + Place.tileSize;
                yMod = y - Place.tileSize;
                break;
            case RIGHT_BOTTOM:
                x = collision.getXEnd() % xAreaInPixels;
                y = collision.getYEnd() % yAreaInPixels;
                xMod = x - Place.tileSize;
                yMod = y - Place.tileSize;
                break;
            case RIGHT_TOP:
                x = collision.getXEnd() % xAreaInPixels;
                y = collision.getY() % yAreaInPixels;
                xMod = x - Place.tileSize;
                yMod = y + Place.tileSize;
                break;
        }
        xModed = new Point(xMod, y);
        yModed = new Point(x, yMod);
        tempPoint.set(x, y);
    }

    private static void cornerAtEndOfLine(Line line) {
        if (line.isHorisontal()) {
            if (line.getStart().getX() == line.getEnd().getX() - Place.tileSize) {
                linesToRemove.add(line);
            } else {
                line.setXEnd(line.getEnd().getX() - Place.tileSize);
            }
        } else if (line.isVertical()) {
            if (line.getStart().getY() == line.getEnd().getY() - Place.tileSize) {
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
                leftTopCorner(line);
                break;
            case LEFT_BOTTOM:
                leftBottomCorner(line);
                break;
            case RIGHT_BOTTOM:
                rightBottomCorner(line);
                break;
            case RIGHT_TOP:
                rightTopCorner(line);
                break;
        }
    }

    private static void leftTopCorner(Line line) {
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
    }

    private static void leftBottomCorner(Line line) {
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
    }

    private static void rightBottomCorner(Line line) {
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
    }

    private static void rightTopCorner(Line line) {
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
    }

    private static void connectDiagonalLines() {
        changed = true;
        tempLine1 = null;
        while (changed) {
            changed = false;
            if (tempLine1 != null) {
                newLines.remove(tempLine1);
                tempLine1 = null;
            }
            mainloop:
            for (Line line1 : newLines) {
                for (Line line2 : newLines) {
                    if (!line1.equals(line2)) {
                        double a = line1.getDirectional();
                        if (line1.getStart().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getEnd());
                            removeLine(line1);
                            break mainloop;
                        } else if (line1.getStart().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getEnd());
                            removeLine(line1);
                            break mainloop;
                        } else if (line1.getEnd().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getStart());
                            removeLine(line1);
                            break mainloop;
                        } else if (line1.getEnd().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getStart());
                            removeLine(line1);
                            break mainloop;
                        }
                    }
                }
            }
        }
    }

    private static void removeLine(Line line1) {
        tempLine1 = line1;
        changed = true;
    }

    private static void collectPointsFromLines() {
        pointsToConnect.clear();
        sureLines.stream().forEach((line) -> {
            pointsToConnect.add(line.getStart());
            pointsToConnect.add(line.getEnd());
        });
//		if (areas == areaNumberToShow) {
//			System.out.println(pointsToConnect.size());
//		}
    }

    private static void connectPoints() {
        newLines.clear();
        pointsToConnect.stream().forEach((point1) -> {
            pointsToConnect.stream().forEach((point2) -> {
                if (isConnectsWalkable(point1, point2)) {
                    newLines.add(new Line(point1, point2));
                }
            });
        });
    }

    private static boolean isConnectsWalkable(Point point1, Point point2) {
        if (!point1.equals(point2)) {
            xd = ((point1.getX() + point2.getX()) / 2d) / Place.tileSize;
            yd = ((point1.getY() + point2.getY()) / 2d) / Place.tileSize;
            return isLineInWalkable(xd, yd);
        }
        return false;
    }

    private static boolean isLineInWalkable(double xd, double yd) {
        int i = getIndex((int) xd, (int) yd);
        x = getXFromIndex(i);
        y = getYFromIndex(i);
        if (!spots.get(i)) {
            return true;
        } else {
            return isWalkableOnDiagonal(i);
        }
    }

    private static boolean isWalkableOnDiagonal(int i) {
        switch (diagonal[i]) {
            case -1:
                return false;
            case LEFT_TOP:
                if ((xd - x) + (yd - y) < Place.tileSize) {
                    return true;
                }
                return false;
            case LEFT_BOTTOM:
                if ((xd - x) < (yd - y)) {
                    return true;
                }
                return false;
            case RIGHT_BOTTOM:
                if ((xd - x) + (yd - y) > Place.tileSize) {
                    return true;
                }
                return false;
            case RIGHT_TOP:
                if ((xd - x) > (yd - y)) {
                    return true;
                }
                return false;
        }
        return false;
    }

    private static void solveLines() {
        sortedLines.clear();
        newLines.stream().forEach((line) -> {
            line.calculateLength();
            sortedLines.add(line);
        }
        );
        newLines.clear();
        newLines.addAll(sureLines);
        while (!sortedLines.isEmpty()) {
            Line line1 = sortedLines.poll();
            start = line1.getStart();
            end = line1.getEnd();
            intersects = false;
            for (Line line2 : newLines) {
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
                newLines.add(line1);
            }
        }
    }

    private static void createTriangles() {
        linesTriangles.clear();
        triangles.clear();
        tempLine1 = new Line();
        newLines.stream().forEach((line1) -> {
            start = line1.getStart();
            end = line1.getEnd();
            newLines.stream().forEach((line2) -> {
                if (line1 != line2) {
                    if (!line1.equals(line2)) {
                        if (start.equals(line2.getStart())) {
                            addTriangleIfPossible(end, line2.getEnd(), line1, line2);
                        } else if (end.equals(line2.getStart())) {
                            addTriangleIfPossible(start, line2.getEnd(), line1, line2);
                        } else if (start.equals(line2.getEnd())) {
                            addTriangleIfPossible(end, line2.getStart(), line1, line2);
                        } else if (end.equals(line2.getEnd())) {
                            addTriangleIfPossible(start, line2.getStart(), line1, line2);
                        }
                    }
                }
            });
        });
    }

    private static void addTriangleIfPossible(Point first, Point second, Line line1, Line line2) {
        tempLine1.setPoints(first, second);
        if (newLines.contains(tempLine1) && isTriangleWalkable(start, end, second)) {
            Triangle triangle = Triangle.create(start, end, second);
            Line line3 = new Line(first, second);
            if (!triangles.contains(triangle)) {
                if (linesTriangles.containsKey(line1)) {
                    ((NeightbourTriangles) linesTriangles.get(line1)).addTriangle(triangle);
                } else {
                    linesTriangles.put(line1, new NeightbourTriangles(triangle));
                }
                if (linesTriangles.containsKey(line2)) {
                    ((NeightbourTriangles) linesTriangles.get(line2)).addTriangle(triangle);
                } else {
                    linesTriangles.put(line2, new NeightbourTriangles(triangle));
                }
                if (linesTriangles.containsKey(line3)) {
                    ((NeightbourTriangles) linesTriangles.get(line3)).addTriangle(triangle);
                } else {
                    linesTriangles.put(line3, new NeightbourTriangles(triangle));
                }
                triangles.add(triangle);
            }
        }
    }

    private static boolean isTriangleWalkable(Point point1, Point point2, Point point3) {
        xd = ((point1.getX() + point2.getX() + point2.getX()) / 3d) / Place.tileSize;
        yd = ((point1.getY() + point2.getY() + point3.getY()) / 3d) / Place.tileSize;
        return isLineInWalkable(xd, yd);
    }

    private static void generateNavigationMesh() {
        linesToCheck.clear();
        navigationMesh = null;
        createNavigationMeshWithFirstTriangle(getFirstTriangle());
        addConnectedTriangles();
    }

    private static Triangle getFirstTriangle() {
        for (Triangle triangle : triangles) {
            return triangle;
        }
        return null;
    }

    private static void createNavigationMeshWithFirstTriangle(Triangle firstTriangle) {
        if (firstTriangle != null) {
            navigationMesh = new NavigationMesh(firstTriangle.getPointFromNode(0), firstTriangle.getPointFromNode(1), firstTriangle.getPointFromNode(2));
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(0), firstTriangle.getPointFromNode(1)));
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(1), firstTriangle.getPointFromNode(2)));
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(2), firstTriangle.getPointFromNode(0)));
        }
    }

    private static void addConnectedTriangles() {
        if (navigationMesh != null) {
            while (!linesToCheck.isEmpty()) {
                currentLine = poll(linesToCheck);
                if (currentLine != null) {
                    neightbours = linesTriangles.get(currentLine);
                    linesTriangles.remove(currentLine);
                    for (int i = 0; i < neightbours.size; i++) {
                        currentTriangle = neightbours.getTriangle(i);
                        if (currentTriangle != null) {
                            navigationMesh.addTriangle(currentTriangle);
                            tempLine1 = new Line(currentTriangle.getPointFromNode(0), currentTriangle.getPointFromNode(1));
                            tempLine2 = new Line(currentTriangle.getPointFromNode(1), currentTriangle.getPointFromNode(2));
                            tempLine3 = new Line(currentTriangle.getPointFromNode(2), currentTriangle.getPointFromNode(0));
                            if (linesTriangles.containsKey(tempLine1)) {
                                linesToCheck.add(tempLine1);
                            }
                            if (linesTriangles.containsKey(tempLine2)) {
                                linesToCheck.add(tempLine2);
                            }
                            if (linesTriangles.containsKey(tempLine3)) {
                                linesToCheck.add(tempLine3);
                            }
                        }
                    }
                }
            }
        }
    }

    private static Line poll(Set<Line> linesToCheck) {
        Line line = null;
        for (Line anyLine : linesToCheck) {
            line = anyLine;
            break;
        }
        linesToCheck.remove(line);
        return line;
    }

    private static Tile getTile(int x, int y, Tile[] tiles) {
        return tiles[x + y * X_IN_TILES];
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
