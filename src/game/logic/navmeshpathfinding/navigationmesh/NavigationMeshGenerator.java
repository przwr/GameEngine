/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding.navigationmesh;

import collision.Block;
import collision.Figure;
import collision.RoundRectangle;
import engine.utilities.BlueArray;
import engine.utilities.Point;
import game.place.Place;
import game.place.map.Tile;

import java.awt.geom.Line2D;
import java.util.*;

import static collision.RoundRectangle.*;
import static game.logic.navmeshpathfinding.PathFinder.*;
import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;

/**
 * @author przemek
 */
public class NavigationMeshGenerator {

    private static final byte[] diagonals = new byte[X_IN_TILES * Y_IN_TILES];
    private static final Set<Line> newLines = new HashSet<>();
    private static final PriorityQueue<Line> sortedLines = new PriorityQueue<>();
    private static final BlueArray<Line> linesToRemove = new BlueArray<>();
    private static final BlueArray<Line> linesToAdd = new BlueArray<>();
    private static final BlueArray<Line> sureLines = new BlueArray<>();
    private static final Set<Line> linesToCheck = new HashSet<>();
    private static final Set<Point> pointsToConnect = new HashSet<>();
    private static final Set<Triangle> triangles = new HashSet<>();
    private static final Map<Line, NeighbourTriangles> linesTriangles = new HashMap<>();
    private static final Point tempPoint = new Point();
    private final static float EPSILON = 0.001f;
    private static int x, y, xMod, yMod, yStartBound, yEndBound, xStartBound, xEndBound, xStartTemp, yStartTemp, xETemp, yETemp, lineXStart, lineYStart,
            leftTop, rightTop, leftBottom, rightBottom;
    private static double xd, yd;
    private static BitSet spots;
    private static byte[] shiftDirections;
    private static Point start, end, xModded, yModded;
    private static Line tempLine1, tempLine2, tempLine3, currentLine;
    private static Triangle currentTriangle;
    private static NeighbourTriangles neighbours;
    private static Figure figure;
    private static RoundRectangle round;
    private static boolean intersects, inline, changed, previous, next, linePrevious, lineNext;
    private static int sharedPoints;
    private static NavigationMesh navigationMesh;


    public static NavigationMesh generateNavigationMesh(Tile[] tiles, Set<Block> blocks, int xArea, int yArea) {

        if (findBoundsAndSetCollisionSpots(tiles, blocks, xArea, yArea)) {
            createLinesFromSpots();
            createAndAddDiagonalLines(blocks);
            collectPointsFromLines();
            connectPointsAndFindShiftingDirections();
            solveLines();
            createTriangles();
            generateNavigationMesh();
            return navigationMesh;
        }

        return null;
    }

    public static void clear() {
        newLines.clear();
        sortedLines.clear();
        linesToRemove.clearReally();
        linesToAdd.clearReally();
        sureLines.clearReally();
        linesToCheck.clear();
        pointsToConnect.clear();
        triangles.clear();
        linesTriangles.clear();
    }

    private static boolean findBoundsAndSetCollisionSpots(Tile[] tiles, Set<Block> blocks, int xArea, int yArea) {
        yStartBound = yEndBound = xStartBound = xEndBound = -1;
        lineXStart = lineYStart = 0;
        spots = new BitSet();
        boolean noTiles = true;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                noTiles = false;
                break;
            }
        }
        if (noTiles) {
            return false;
        }
        findBoundsFromTiles(tiles);
        setCollisionSpotsFromTiles(tiles);
        findBoundsAndSetCollisionSpotsFromBlocks(blocks, xArea, yArea);
        return true;
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
        for (x = 0; x < X_IN_TILES; x++) {
            for (y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xStartBound = x;
                    return;
                }
            }
        }
    }

    private static void findXEndBound(Tile[] tiles) {
        for (x = X_IN_TILES - 1; x >= 0; x--) {
            for (y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y, tiles) != null) {
                    xEndBound = x;
                    return;
                }
            }
        }
    }

    private static void setCollisionSpotsFromTiles(Tile[] tiles) {
        if (yStartBound != -1) {
            for (x = 0; x < X_IN_TILES; x++) {
                for (y = 0; y < Y_IN_TILES; y++) {
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
        xStartTemp = (figure.getX() / Place.tileSize);
        if (xStartTemp < xA) {
            xStartTemp = 0;
        } else if (xStartTemp >= xA + X_IN_TILES) {
            xStartTemp = X_IN_TILES;
        } else {
            xStartTemp %= X_IN_TILES;
        }
    }

    private static void setYSTemp(int yA) {
        yStartTemp = (figure.getY() / Place.tileSize);
        if (yStartTemp < yA) {
            yStartTemp = 0;
        } else if (yStartTemp >= yA + Y_IN_TILES) {
            yStartTemp = Y_IN_TILES;
        } else {
            yStartTemp %= Y_IN_TILES;
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
        for (x = xStartTemp; x < xETemp; x++) {
            for (y = yStartTemp; y < yETemp; y++) {
                spots.set(getIndex(x, y));
            }
        }
    }

    private static void checkBoundsRange() {
        if (xStartTemp < 0) {
            xStartTemp = 0;
        }
        if (yStartTemp < 0) {
            yStartTemp = 0;
        }
        if (xETemp >= X_IN_TILES) {
            xETemp = X_IN_TILES - 1;
        }
        if (yETemp >= Y_IN_TILES) {
            yETemp = Y_IN_TILES - 1;
        }
    }

    private static void findBoundsFromBlocks() {
        if (xStartTemp < xStartBound) {
            xStartBound = xStartTemp;
        }
        if (xETemp > xEndBound) {
            xEndBound = xETemp;
        }
        if (yStartTemp < yStartBound) {
            yStartBound = yStartTemp;
        }
        if (yETemp > yEndBound) {
            yEndBound = yETemp;
        }
    }

    private static void createLinesFromSpots() {
        newLines.clear();
        createHorizontalLines();
        createVerticalLines();
        sureLines.clear();
        sureLines.addAll(newLines);
    }

    private static void createHorizontalLines() {
        inline = previous = next = linePrevious = lineNext = false;
        for (int y = yStartBound - 1; y <= yEndBound; y++) {
            for (int x = xStartBound; x <= xEndBound + 1; x++) {
                previous = isCollision(x, y);
                next = isCollision(x, y + 1);
                if (inline) {
                    if (next != lineNext || previous != linePrevious) {
                        newLines.add(new Line(new Point(lineXStart, lineYStart), new Point(x * Place.tileSize, (y + 1) * Place.tileSize)));
                        if (next == previous) {
                            inline = false;
                        } else {
                            setLineParameters(x, y + 1);
                        }
                    }
                } else if (next != previous) {
                    setLineParameters(x, y + 1);
                    inline = true;
                }
            }
        }
    }

    private static void createVerticalLines() {
        inline = previous = next = linePrevious = lineNext = false;
        for (int x = xStartBound - 1; x <= xEndBound; x++) {
            for (int y = yStartBound - 1; y <= yEndBound + 1; y++) {
                previous = isCollision(x, y);
                next = isCollision(x + 1, y);
                if (inline) {
                    if (next != lineNext || previous != linePrevious) {
                        newLines.add(new Line(new Point(lineXStart, lineYStart), new Point((x + 1) * Place.tileSize, y * Place.tileSize)));
                        if (next == previous) {
                            inline = false;
                        } else {
                            setLineParameters(x + 1, y);
                        }
                    }
                } else if (next != previous) {
                    setLineParameters(x + 1, y);
                    inline = true;
                }
            }
        }
    }

    private static void setLineParameters(int x, int y) {
        lineXStart = x * Place.tileSize;
        lineYStart = y * Place.tileSize;
        linePrevious = previous;
        lineNext = next;
    }

    private static boolean isCollision(int x, int y) {
        return y > yEndBound || x > xEndBound || y < yStartBound || x < xStartBound || spots.get(getIndex(x, y));
    }

    private static void createAndAddDiagonalLines(Set<Block> blocks) {
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
        for (int i = 0; i < diagonals.length; i++) {
            diagonals[i] = -1;
        }
    }

    private static boolean shouldCorrect(int corner, RoundRectangle figure) {
        xStartTemp = (figure.getX() % xAreaInPixels) / Place.tileSize;
        yStartTemp = ((figure.getY()) % yAreaInPixels) / Place.tileSize;
        xETemp = ((figure.getXEnd()) % xAreaInPixels) / Place.tileSize;
        yETemp = ((figure.getYEnd()) % yAreaInPixels) / Place.tileSize;
        return checkByCorner(corner);
    }

    private static boolean checkByCorner(int corner) {
        switch (corner) {
            case LEFT_TOP:
                if (isCollisionFromCoordinates(xStartTemp, yStartTemp - 1) || isCollisionFromCoordinates(xStartTemp - 1, yStartTemp)) {
                    return false;
                }
                diagonals[getIndex(xStartTemp, yStartTemp)] = LEFT_TOP;
                return true;
            case LEFT_BOTTOM:
                if (isCollisionFromCoordinates(xStartTemp, yETemp) || isCollisionFromCoordinates(xStartTemp - 1, yETemp - 1)) {
                    return false;
                }
                diagonals[getIndex(xStartTemp, yETemp - 1)] = LEFT_BOTTOM;
                return true;
            case RIGHT_BOTTOM:
                if (isCollisionFromCoordinates(xStartTemp, yETemp) || isCollisionFromCoordinates(xETemp, yETemp - 1)) {
                    return false;
                }
                diagonals[getIndex(xStartTemp, yETemp - 1)] = RIGHT_BOTTOM;
                return true;
            case RIGHT_TOP:
                if (isCollisionFromCoordinates(xStartTemp, yStartTemp - 1) || isCollisionFromCoordinates(xETemp, yStartTemp)) {
                    return false;
                }
                diagonals[getIndex(xStartTemp, yStartTemp)] = RIGHT_TOP;
                return true;
        }
        return true;
    }

    private static boolean isCollisionFromCoordinates(int x, int y) {
        return y > yEndBound || x > xEndBound || y < yStartBound || x < xStartBound || spots.get(getIndex(x, y));
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
        newLines.add(new Line(xModded, yModded));
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
        xModded = new Point(xMod, y);
        yModded = new Point(x, yMod);
        tempPoint.set(x, y);
    }

    private static void cornerAtEndOfLine(Line line) {
        if (line.isHorizontal()) {
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
        if (line.isHorizontal()) {
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
        if (line.isHorizontal()) {
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
        if (line.isHorizontal()) {
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
                linesToAdd.add(new Line(new Point(x, y), new Point(x, line.getEnd().getY())));
                if (line.getStart().getY() != yMod) {
                    line.setYEnd(yMod);
                } else {
                    linesToRemove.add(line);
                }
            }
        }
    }

    private static void rightBottomCorner(Line line) {
        if (line.isHorizontal()) {
            if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                linesToAdd.add(new Line(new Point(x, y), new Point(line.getEnd().getX(), y)));
                if (line.getStart().getX() != xMod) {
                    line.setXEnd(xMod);
                } else {
                    linesToRemove.add(line);
                }
            }
        } else if (line.isVertical()) {
            if (line.getEnd().getX() == x && line.getStart().getY() < y && y < line.getEnd().getY()) {
                linesToAdd.add(new Line(new Point(x, y), new Point(x, line.getEnd().getY())));
                if (line.getStart().getY() != yMod) {
                    line.setYEnd(yMod);
                } else {
                    linesToRemove.add(line);
                }
            }
        }
    }

    private static void rightTopCorner(Line line) {
        if (line.isHorizontal()) {
            if (line.getEnd().getY() == y && line.getStart().getX() < x && x < line.getEnd().getX()) {
                linesToAdd.add(new Line(new Point(x, y), new Point(line.getEnd().getX(), y)));
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
            mainLoop:
            for (Line line1 : newLines) {
                for (Line line2 : newLines) {
                    if (!line1.equals(line2)) {
                        double a = line1.getDirectional();
                        if (line1.getStart().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getEnd());
                            removeLine(line1);
                            break mainLoop;
                        } else if (line1.getStart().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getEnd());
                            removeLine(line1);
                            break mainLoop;
                        } else if (line1.getEnd().equals(line2.getStart()) && line2.getDirectional() == a) {
                            line2.setStart(line1.getStart());
                            removeLine(line1);
                            break mainLoop;
                        } else if (line1.getEnd().equals(line2.getEnd()) && line2.getDirectional() == a) {
                            line2.setEnd(line1.getStart());
                            removeLine(line1);
                            break mainLoop;
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
    }

    private static void connectPointsAndFindShiftingDirections() {
        newLines.clear();
        shiftDirections = new byte[(X_IN_TILES + 1) * (Y_IN_TILES + 1)];
        pointsToConnect.stream().forEach((point1) -> {
            pointsToConnect.stream().forEach((point2) -> {
                if (isConnectsWalkable(point1, point2)) {
                    newLines.add(new Line(point1, point2));
                }
            });
            setShiftDirection(point1);
        });
    }

    private static boolean isConnectsWalkable(Point point1, Point point2) {
        if (!point1.equals(point2)) {
            xd = ((point1.getX() + point2.getX()) / 2d) / Place.tileSize;
            yd = ((point1.getY() + point2.getY()) / 2d) / Place.tileSize;
            return isInWalkable(xd, yd);
        }
        return false;
    }

    private static boolean isInWalkable(double xd, double yd) {
        x = (int) xd;
        y = (int) yd;
        int i = getIndex(x, y);
        return !spots.get(i) || isWalkableOnDiagonal(i);
    }

    private static boolean isWalkableOnDiagonal(int i) {
        switch (diagonals[i]) {
            case -1:
                return false;
            case LEFT_TOP:
                return (xd - x) + (yd - y) < Place.tileSize;
            case LEFT_BOTTOM:
                return (xd - x) < (yd - y);
            case RIGHT_BOTTOM:
                return Math.abs(xd - x) + Math.abs(yd - y) > Place.tileSize;
            case RIGHT_TOP:
                return Math.abs(xd - x) > (yd - y);
        }
        return false;
    }

    private static void setShiftDirection(Point point) {
        x = point.getX() / Place.tileSize;
        y = point.getY() / Place.tileSize;
        leftTop = isCollision(x - 1, y - 1) ? 8 : 0;
        rightTop = isCollision(x, y - 1) ? 4 : 0;
        leftBottom = isCollision(x - 1, y) ? 2 : 0;
        rightBottom = isCollision(x, y) ? 1 : 0;
        byte shift = (byte) (leftTop + rightTop + leftBottom + rightBottom);

        switch (shift) {
            case 7:
                shift = TO_LEFT_TOP;
                break;
            case 11:
                shift = TO_RIGHT_TOP;
                break;
            case 13:
                shift = TO_LEFT_BOTTOM;
                break;
            case 14:
                shift = TO_RIGHT_BOTTOM;
                break;
        }
        shiftDirections[getIndexForShifts(x, y)] = shift;
    }

    public static int getIndexForShifts(int x, int y) {
        return x + y * (X_IN_TILES + 1);
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
                    if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), line2.getStart().getX(), line2.getStart().getY(), line2
                            .getEnd().getX(), line2.getEnd().getY())) {
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
                if (line1 != line2 && !line1.equals(line2)) {
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
            });
        });
    }

    private static void addTriangleIfPossible(Point first, Point second, Line line1, Line line2) {
        tempLine1.setPoints(first, second);
        if (newLines.contains(tempLine1) && isTriangleWalkable(start, end, second) && !isTrianglesContainsOtherPoints(start, end, second)) {
            Triangle triangle = Triangle.create(start, end, second);
            Line line3 = new Line(first, second);
            if (!triangles.contains(triangle)) {
                if (linesTriangles.containsKey(line1)) {
                    linesTriangles.get(line1).addTriangle(triangle);
                } else {
                    linesTriangles.put(line1, new NeighbourTriangles(triangle));
                }
                if (linesTriangles.containsKey(line2)) {
                    linesTriangles.get(line2).addTriangle(triangle);
                } else {
                    linesTriangles.put(line2, new NeighbourTriangles(triangle));
                }
                if (linesTriangles.containsKey(line3)) {
                    linesTriangles.get(line3).addTriangle(triangle);
                } else {
                    linesTriangles.put(line3, new NeighbourTriangles(triangle));
                }
                triangles.add(triangle);
            }
        }
    }

    private static boolean isTriangleWalkable(Point point1, Point point2, Point point3) {
        xd = ((point1.getX() + point2.getX() + point2.getX()) / 3d) / Place.tileSize;
        yd = ((point1.getY() + point2.getY() + point3.getY()) / 3d) / Place.tileSize;
        return isInWalkable(xd, yd);
    }

    private static boolean isTrianglesContainsOtherPoints(Point point1, Point point2, Point point3) {
        calculateBounds(point1, point2, point3);
        return pointsToConnect.stream().filter((point) -> (!point.equals(point1) && !point.equals(point2) && !point.equals(point3))).anyMatch((point)
                -> (isPointInTriangle(point, point1, point2, point3)));
    }

    private static boolean isPointInTriangle(Point point, Point point1, Point point2, Point point3) {
        return !isOutOfBoundsToEpsilon(point) && barycentricPointInTriangle(point, point1, point2, point3);
    }

    private static void calculateBounds(Point point1, Point point2, Point point3) {
        xStartTemp = Math.min(Math.min(point1.getX(), point2.getX()), point3.getX());
        xETemp = Math.max(Math.max(point1.getX(), point2.getX()), point3.getX());
        yStartTemp = Math.min(Math.min(point1.getY(), point2.getY()), point3.getY());
        yETemp = Math.max(Math.max(point1.getY(), point2.getY()), point3.getY());
    }

    private static boolean isOutOfBoundsToEpsilon(Point point) {
        return (point.getX() < xStartTemp - EPSILON)
                || (point.getX() - EPSILON > xETemp)
                || (point.getY() < yStartTemp - EPSILON)
                || (point.getY() - EPSILON > yETemp);
    }

    private static boolean barycentricPointInTriangle(Point point, Point point1, Point point2, Point point3) {
        double denominator = getDenominator(point1, point2, point3);
        double a = ((point2.getY() - point3.getY()) * (point.getX() - point3.getX())
                + ((point3.getX() - point2.getX()) * (point.getY() - point3.getY()))) / denominator;
        double b = ((point3.getY() - point1.getY()) * (point.getX() - point3.getX())
                + ((point1.getX() - point3.getX()) * (point.getY() - point3.getY()))) / denominator;
        double c = 1 - a - b;
        return isInZeroOneRangeToEpsilon(a) && isInZeroOneRangeToEpsilon(b) && isInZeroOneRangeToEpsilon(c);
    }

    private static double getDenominator(Point point1, Point point2, Point point3) {
        return ((point2.getY() - point3.getY()) * (point1.getX() - point3.getX()))
                + ((point3.getX() - point2.getX()) * (point1.getY() - point3.getY()));
    }

    private static boolean isInZeroOneRangeToEpsilon(double number) {
        return -EPSILON <= number && number <= 1 + EPSILON;
    }

    private static void generateNavigationMesh() {
        linesToCheck.clear();
        navigationMesh = null;
        createNavigationMeshWithFirstTriangle(pollFirstTriangle());
        addConnectedTriangles();
        while (triangles.size() > 1) {
            addLooseTriangle(pollFirstTriangle());
            addConnectedTriangles();
        }
    }

    private static Triangle pollFirstTriangle() {
        Triangle triangle = null;
        for (Triangle t : triangles) {
            triangle = t;
            break;
        }
        triangles.remove(triangle);
        return triangle;
    }

    private static void createNavigationMeshWithFirstTriangle(Triangle firstTriangle) {
        if (firstTriangle != null) {
            navigationMesh = new NavigationMesh(firstTriangle.getPointFromNode(0), firstTriangle.getPointFromNode(1), firstTriangle.getPointFromNode(2), new
                    ArrayList<>(pointsToConnect), shiftDirections, spots);
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(0), firstTriangle.getPointFromNode(1)));
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(1), firstTriangle.getPointFromNode(2)));
            linesToCheck.add(new Line(firstTriangle.getPointFromNode(2), firstTriangle.getPointFromNode(0)));
        }
    }

    private static void addLooseTriangle(Triangle triangle) {
        navigationMesh.addLooseTriangle(triangle);
        linesToCheck.add(new Line(triangle.getPointFromNode(0), triangle.getPointFromNode(1)));
        linesToCheck.add(new Line(triangle.getPointFromNode(1), triangle.getPointFromNode(2)));
        linesToCheck.add(new Line(triangle.getPointFromNode(2), triangle.getPointFromNode(0)));
    }

    private static void addConnectedTriangles() {
        if (navigationMesh != null) {
            while (!linesToCheck.isEmpty()) {
                currentLine = poll(linesToCheck);
                if (currentLine != null) {
                    neighbours = linesTriangles.get(currentLine);
                    linesTriangles.remove(currentLine);
                    for (int i = 0; i < neighbours.size; i++) {
                        currentTriangle = neighbours.getTriangle(i);
                        if (currentTriangle != null) {
                            navigationMesh.addTriangle(currentTriangle);
                            triangles.remove(currentTriangle);
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

    public static int getIndex(int x, int y) {
        return x + y * X_IN_TILES;
    }

    private static Tile getTile(int x, int y, Tile[] tiles) {
        return tiles[x + y * X_IN_TILES];
    }

    private static int getYFromIndex(int index) {
        return index / X_IN_TILES;
    }
}
