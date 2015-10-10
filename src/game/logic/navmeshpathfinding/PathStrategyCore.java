/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding;

import collision.Figure;
import collision.Rectangle;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.PointContainer;
import engine.utilities.PointedValue;
import game.gameobject.entities.Entity;
import net.jodk.lang.FastMath;

import java.awt.*;
import java.util.List;

import static collision.RoundRectangle.*;
import static game.logic.navmeshpathfinding.PathData.*;

/**
 * @author przemek
 */
class PathStrategyCore {

    private static final PathWindow pathWindow = new PathWindow();

    private static final boolean DEBUG = false;
    private static boolean WINDOW_SHOWED = false;

    private static void DEBUG(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
        if (!WINDOW_SHOWED && DEBUG) {
            pathWindow.setVisible(true);
            WINDOW_SHOWED = true;
        }
    }

    public static void followPath(Entity requester, PathData data, int xDest, int yDest) {
        updatePath(data);
//        if (WINDOW_SHOWED) {
//            Area area = requester.getMap().getArea(requester.getArea());
//            pathWindow.addVariables(area.getNavigationMesh(), new Point(xDest, yDest),
//                    new Point(requester.getX(), requester.getY()),
//                    data.path, area.getXInPixels(), area.getYInPixels());
//        }
        chooseDestinationPoint(requester, data, xDest, yDest);
        managePassing(requester, data);
        data.calculateSpeed(requester.getMaxSpeed());
        manageBlockedAndAdjustSpeed(data, requester.getMaxSpeed());
        data.rememberPast();
    }

    private static void updatePath(PathData data) {
        if (data.newPath != null) {
            if (data.newPath.size() > 1) {
                DEBUG("NEW PATH");
                copyPath(data.newPath, data);
                data.currentPoint = 1;
                correctDestinationPointIfNeeded(data.path, data);
            }
            data.flags.clear(PATH_REQUESTED);
            data.newPath = null;
        } else if (data.flags.get(STUCK)) {
            data.path.clear();
        }
    }

    private static void copyPath(PointContainer newPath, PathData data) {
        data.path.clear();
        for (int i = 0; i < newPath.size(); i++) {
            data.path.add(newPath.get(i).getX() + data.xRef, newPath.get(i).getY() + data.yRef);
        }
    }

    private static void correctDestinationPointIfNeeded(PointContainer path, PathData data) {
        if (data.destination != null) {
            if (!data.close.isEmpty()) {
                data.desired = path.get(path.size() - 1);
                setTestingPosition(data.testing, data.desired);
                data.collided = whatCollidesWithTesting(data.close, data.testing);
                if (data.collided != null) {
                    correctDestinationPoint(path, data);
                }
            }
        }
    }

    private static void correctDestinationPoint(PointContainer path, PathData data) {
        if (data.collided.getX() >= data.desired.getX()) {
            if (data.collided.getY() >= data.desired.getY()) {
                path.get(path.size() - 1).set(data.collided.getX() - (data.widthHalf), data.collided.getY() - (data.heightHalf));
            } else if (data.collided.getYEnd() <= data.desired.getY()) {
                path.get(path.size() - 1).set(data.collided.getX() - (data.widthHalf), data.collided.getYEnd() + (data.heightHalf));
            } else {
                path.get(path.size() - 1).setX(data.collided.getX() - (data.widthHalf));
            }
        } else if (data.collided.getXEnd() > data.desired.getX()) {
            if (data.collided.getY() >= data.desired.getY()) {
                path.get(path.size() - 1).setY(data.collided.getY() - (data.heightHalf));
            } else if (data.collided.getYEnd() <= data.desired.getY()) {
                path.get(path.size() - 1).setY(data.collided.getYEnd() + (data.heightHalf));
            }
        } else {
            if (data.collided.getY() >= data.desired.getY()) {
                path.get(path.size() - 1).set(data.collided.getXEnd() + (data.widthHalf), data.collided.getY() - (data.heightHalf));
            } else if (data.collided.getYEnd() <= data.desired.getY()) {
                path.get(path.size() - 1).set(data.collided.getXEnd() + (data.widthHalf), data.collided.getYEnd() + (data.heightHalf));
            } else {
                path.get(path.size() - 1).setX(data.collided.getXEnd() + (data.widthHalf));
            }
        }
    }

    private static Figure whatCollidesWithTesting(List<Figure> close, Figure testing) {
        for (Figure figure : close) {
            if (testing.isCollideSingle(0, 0, figure)) {
                return figure;
            }
        }
        return null;
    }

    private static void setTestingPosition(Rectangle testing, Point desired) {
        testing.setXStart(desired.getX() - (testing.getWidth() / 2));
        testing.setYStart(desired.getY() - (testing.getHeight() / 2));
        testing.updateTilePoints();
    }

    private static void chooseDestinationPoint(Entity requester, PathData data, int xDest, int yDest) {
        if (!data.delay.isOver()) {
            if (!data.path.isEmpty()) {
                data.destination = data.getCurrentPoint();
                if (Methods.pointDistance(data.x, data.y, data.getCurrentPoint().getX(), data.getCurrentPoint().getY()) < requester.getMaxSpeed()) {
                    if (data.currentPoint < data.path.size() - 2) {
                        data.currentPoint++;
                    } else {
                        data.path.clear();
                    }
                }
            } else {
                delayOverChoice(requester, data, xDest, yDest);
            }
        } else {
            data.delay.start();
            delayOverChoice(requester, data, xDest, yDest);
        }
    }

    private static void delayOverChoice(Entity requester, PathData data, int xDest, int yDest) {
        if (data.flags.get(OBSTACLE_BETWEEN) || data.flags.get(STUCK) || Methods.pointDistance(data.x, data.y, xDest, yDest) > data.scope) {
            if (data.path.isEmpty() || data.flags.get(OBSTACLE_BETWEEN) || data.flags.get(STUCK) || (Methods.pointDistance(data.getLastPoint().getX(), data
                    .getLastPoint().getY(), xDest, yDest) > requester.getMaxSpeed())) {
                requestForPath(requester, data, xDest, yDest);
            }
            if (!data.path.isEmpty()) {
                data.destination = data.getCurrentPoint();
            } else {
                data.destination = data.finalDestination;
            }
        } else {
            data.destination = data.finalDestination;
        }
    }

    private static void requestForPath(Entity requester, PathData data, int xDest, int yDest) {
        if (!data.flags.get(PATH_REQUESTED)) {
            PathFindingModule.requestPath(requester, xDest, yDest);
        }
    }

    public synchronized static void findPath(Entity requester, PathData data, int xDest, int yDest) {
        data.newPath = requester.getMap().findPath(data.x, data.y, xDest, yDest, requester.getCollision());
    }

    private static void managePassing(Entity requester, PathData data) {
        if (data.flags.get(PASSING)) {
            passing(data);
            DEBUG("PASSING");
        } else if (data.flags.get(PASSED)) {
            passed(requester, data);
            DEBUG("PASSED");
        } else {
            normal(data);
            DEBUG("NORMAL");
        }
    }

    private static void passing(PathData data) {
        if (data.flags.get(STUCK) || data.x == data.correction.getX() && data.y == data.correction.getY() || (data.inAWay != null && (data.inAWay.getX() !=
                data.xInAWay || data.inAWay.getY() != data.yInAWay))) {
            data.flags.clear(PASSING);
            data.flags.set(PASSED);
            data.destination = data.correction;
        } else {
            if (data.flags.get(AVOID_MOBILE) && data.inAWay != null && data.inAWay.isMobile()) {
                data.lastInAWay = null;
                isSomethingOnTheWay(data);
            } else {
                data.lastInAWay = data.inAWay;
            }
            data.destination = data.correction;
        }
    }

    private static void passed(Entity requester, PathData data) {
        if (Math.signum(data.xSpeed) != data.xPass) {
            data.xSpeed = 0;
        }
        if (Math.signum(data.ySpeed) != data.yPass) {
            data.ySpeed = 0;
        }
        if (data.xSpeed != 0 && data.ySpeed != 0) {
            if (data.flags.get(CHOICE)) {
                data.ySpeed = 0;
            } else {
                data.xSpeed = 0;
            }
        } else if (data.xSpeed == 0 && data.ySpeed == 0) {
            if (data.flags.get(CHOICE)) {
                data.xSpeed = requester.getMaxSpeed();
            } else {
                data.ySpeed = requester.getMaxSpeed();
            }
        }
        if (data.xSpeed != 0) {
            data.xSpeed = requester.getMaxSpeed();
        } else if (data.ySpeed != 0) {
            data.ySpeed = requester.getMaxSpeed();
        }
        countPass(data);
    }

    private static void countPass(PathData data) {
        data.passedCount++;
        if (data.passedCount >= 6) {
            data.passedCount = 0;
            data.flags.clear(PASSED);
            data.flags.set(CHOICE, FastMath.random() > 0.5);
        }
    }

    private static void normal(PathData data) {
        isSomethingOnTheWay(data);
        if (!data.path.isEmpty() || data.destination.equals(data.finalDestination)) {
            data.lastInAWay = null;
            data.last2CorrectionPoint.set(-1, -1);
            data.last1CorrectionPoint.set(-1, -1);
        }
    }

    private static void isSomethingOnTheWay(PathData data) {
        if (!data.close.isEmpty()) {
            setPolygonForTesting(data, data.destination);
            data.inAWay = PathStrategyCore.anyFigureInAWay(data.poly, data.close);
            if (data.inAWay != data.lastInAWay && isNeedToPass(data)) {
                findCorrectionPoint(data);
            }
        }
    }

    private static void findCorrectionPoint(PathData data) {
        data.correctionPoints.clear();
        data.xInAWay = data.inAWay.getX();
        data.yInAWay = data.inAWay.getY();
        data.tempCorrection.set(data.inAWay.getX() - data.xCorrection, data.inAWay.getY() - data.yCorrection);
        addPointIfClearPath(data, data.tempCorrection, LEFT_TOP);
        data.tempCorrection.set(data.inAWay.getX() - data.xCorrection, data.inAWay.getYEnd() + data.yCorrection);
        addPointIfClearPath(data, data.tempCorrection, LEFT_BOTTOM);
        data.tempCorrection.set(data.inAWay.getXEnd() + data.xCorrection, data.inAWay.getYEnd() + data.yCorrection);
        addPointIfClearPath(data, data.tempCorrection, RIGHT_BOTTOM);
        data.tempCorrection.set(data.inAWay.getXEnd() + data.xCorrection, data.inAWay.getY() - data.yCorrection);
        addPointIfClearPath(data, data.tempCorrection, RIGHT_TOP);
        setCorrectionIfFits(data);
    }

    private static void addPointIfClearPath(PathData data, Point correction, int corner) {
        if (!data.last1CorrectionPoint.equals(data.last2CorrectionPoint) || !correction.equals(data.last1CorrectionPoint)) {
            PathStrategyCore.setPolygonForTesting(data, correction);
            if (data.destination.equals(data.finalDestination) || PathStrategyCore.anyFigureInAWay(data.poly, data.close) == null || (corner == data
                    .lastCorner && data.path.isEmpty())) {
                data.correctionPoints.add(correction.getX(), correction.getY(), corner);
            }
        }
    }

    private static void setCorrectionIfFits(PathData data) {
        data.min = Integer.MAX_VALUE;
        data.closest = null;
        for (int i = 0; i < data.correctionPoints.size(); i++) {
            PointedValue point = data.correctionPoints.get(i);
            if (data.correctionPoints.size() == 1 || point.getValue() != data.lastCorner) {
                data.temp = Methods.pointDistance(point.getX(), point.getY(), data.destination.getX(), data.destination.getY());
                if (data.temp < data.min) {
                    data.min = data.temp;
                    data.closest = point;
                }
            }
        }
        if (data.closest != null) {
            if ((data.closest.getY() >= data.y && data.closest.getY() <= data.destination.getY()) || (data.closest.getY() <= data.y && data.closest.getY() >=
                    data.destination.getY()) || data.inAWay.isMobile()) {
                setCorrection(data);
            }
        }
    }

    private static void setCorrection(PathData data) {
        data.flags.set(PASSING);
        data.flags.clear(PASSED);
        data.last2CorrectionPoint.set(data.last1CorrectionPoint.getX(), data.last1CorrectionPoint.getY());
        data.last1CorrectionPoint.set(data.closest.getX(), data.closest.getY());
        data.lastCorner = data.closest.getValue();
        adjustClosest(data);
        data.correction.set(data.closest.getX(), data.closest.getY());
    }

    private static void adjustClosest(PathData data) {
        switch (data.closest.getValue()) {
            case LEFT_TOP:
                data.xPass = 1;
                data.yPass = 1;
                data.closest.set(data.closest.getX() + 1, data.closest.getY() + 1);
                break;
            case LEFT_BOTTOM:
                data.xPass = 1;
                data.yPass = -1;
                data.closest.set(data.closest.getX() + 1, data.closest.getY() - 1);
                break;
            case RIGHT_BOTTOM:
                data.xPass = -1;
                data.yPass = -1;
                data.closest.set(data.closest.getX() - 1, data.closest.getY() - 1);
                break;
            case RIGHT_TOP:
                data.xPass = -1;
                data.yPass = 1;
                data.closest.set(data.closest.getX() - 1, data.closest.getY() + 1);
                break;
        }
    }

    private static boolean isNeedToPass(PathData data) {
        if (data.inAWay != null) {
            if (!data.path.isEmpty()) {
                return data.path.size() - 1 != data.currentPoint || isPassingRequired(data);
            } else {
                return isPassingRequired(data);
            }
        }
        return false;
    }

    private static boolean isPassingRequired(PathData data) {
        if (data.inAWay.getY() == data.yE) {
            return data.yDE > data.inAWay.getY();
        } else if (data.inAWay.getYEnd() == data.yS) {
            return data.yDS < data.yS;
        } else if (data.inAWay.getX() == data.xE) {
            return data.xDE > data.inAWay.getX();
        } else if (data.inAWay.getXEnd() == data.xS) {
            return data.xDS < data.xS;
        }
        return false;
    }

    private static void manageBlockedAndAdjustSpeed(PathData data, double maxSpeed) {
        if (data.flags.get(BLOCKED)) {
            DEBUG("BLOCKED");
            data.xSpeed = data.pastXSpeed;
            data.ySpeed = data.pastYSpeed;
            data.alternateCount++;
            if (data.alternateCount > Methods.roundDouble((data.pastYSpeed > data.pastXSpeed ? data.height * 2 : data.width * 2) / maxSpeed)) {
                data.alternateCount = 0;
                data.flags.clear(BLOCKED);
            }
        } else {
            if (data.inAWay != null && data.inAWay.isMobile()) {
                if (data.xSpeed * data.pastXSpeed > 0 && data.x == data.pastPosition.getX()) {
                    data.xSpeed = 0;
                    data.ySpeed = (Math.abs(data.ySpeed) > 0) ? Math.signum(data.ySpeed) * maxSpeed : (data.flags.get(CHOICE) ? maxSpeed : -maxSpeed);
                    data.flags.set(BLOCKED);
                    data.flags.set(CHOICE, FastMath.random() > 0.5);
                } else if (data.ySpeed * data.pastYSpeed > 0 && data.y == data.pastPosition.getY()) {
                    data.ySpeed = 0;
                    data.xSpeed = (Math.abs(data.xSpeed) > 0) ? Math.signum(data.xSpeed) * maxSpeed : (data.flags.get(CHOICE) ? maxSpeed : -maxSpeed);
                    data.flags.set(BLOCKED);
                    data.flags.set(CHOICE, FastMath.random() > 0.5);
                }
            }
            if (!data.flags.get(PASSED)) {
                adjustSpeed(data, maxSpeed);
            }
        }
    }

    private static void adjustSpeed(PathData data, double maxSpeed) {
        if (data.x != data.destination.getX()) {
//            if (Math.abs(data.xSpeed) <= 1) {
//                data.xSpeed = Math.signum(data.xSpeed);
//            } else
            if (data.xDistance < maxSpeed) {
                data.xSpeed = Math.signum(data.xSpeed) * data.xDistance;
            }
        } else {
            data.xSpeed = 0;
        }
        if (data.y != data.destination.getY()) {
//            if (Math.abs(data.ySpeed) <= 1) {
//                data.ySpeed = Math.signum(data.ySpeed);
//            } else
            if (data.yDistance < maxSpeed) {
                data.ySpeed = Math.signum(data.ySpeed) * data.yDistance;
            }
        } else {
            data.ySpeed = 0;
        }
    }

    public static void setPolygonForTesting(PathData data, Point correction) {
        Methods.getCastingPoints((2 * data.x - correction.getX()), (2 * data.y - correction.getY()), data.xS, data.xE, data.yS, data.yE, data.castingPoints);
        setDestinationCorners(correction, data);
        Methods.getCastingPoints((2 * correction.getX() - data.x), (2 * correction.getY() - data.y), data.xDS, data.xDE, data.yDS, data.yDE, data
                .castingDestination);
        data.poly.reset();
        data.poly.addPoint(data.castingPoints[0].getX(), data.castingPoints[0].getY());
        data.poly.addPoint(data.castingPoints[1].getX(), data.castingPoints[1].getY());
        if (data.castingDestination[0].getY() != data.castingDestination[1].getY()) {
            data.poly.addPoint(data.castingDestination[1].getX(), data.castingDestination[1].getY());
            data.poly.addPoint(data.castingDestination[0].getX(), data.castingDestination[0].getY());
        } else if (data.castingDestination[0].getX() > data.castingDestination[1].getX()) {
            data.poly.addPoint(data.castingDestination[0].getX(), data.castingDestination[0].getY());
            data.poly.addPoint(data.castingDestination[1].getX(), data.castingDestination[1].getY());
        } else {
            data.poly.addPoint(data.castingDestination[1].getX(), data.castingDestination[1].getY());
            data.poly.addPoint(data.castingDestination[0].getX(), data.castingDestination[0].getY());
        }
    }

    private static void setDestinationCorners(Point destination, PathData data) {
        data.xDS = destination.getX() - data.widthHalf;
        data.xDE = destination.getX() + data.widthHalf;
        data.yDS = destination.getY() - data.heightHalf;
        data.yDE = destination.getY() + data.heightHalf;
    }

    public static Figure anyFigureInAWay(Polygon poly, List<Figure> close) {
        for (Figure figure : close) {
            if (poly.contains(figure.getXCentral(), figure.getYCentral())) {
                return figure;
            }
            if (poly.intersects(figure.getX(), figure.getY(), figure.getWidth(), figure.getHeight())) {
                return figure;
            }
        }
        return null;
    }
}
