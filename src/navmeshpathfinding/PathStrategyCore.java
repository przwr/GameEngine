/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Figure;
import collision.PointContener;
import collision.Rectangle;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.LEFT_TOP;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT_TOP;
import engine.Methods;
import engine.Point;
import engine.PointedValue;
import game.gameobject.Entity;
import java.awt.Polygon;
import java.util.List;
import net.jodk.lang.FastMath;

/**
 *
 * @author przemek
 */
public class PathStrategyCore {

    public static void followPath(Entity requester, PathData data, int xDest, int yDest) {
        chooseDestinationPoint(requester, data, xDest, yDest);
        managePassing(requester, data);
        data.calculateSpeed(requester.getMaxSpeed());
        manageBlockedAndAdjustSpeed(data, requester.getMaxSpeed());
        data.rememberPast();
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
        if (data.obstacleBeetween || data.stuck || Methods.pointDistance(data.x, data.y, xDest, yDest) > data.scope) {
            if (data.path.isEmpty() || data.obstacleBeetween || data.stuck || (Methods.pointDistance(data.getLastPoint().getX(), data.getLastPoint().getY(), xDest, yDest) > requester.getMaxSpeed())) {
                requestForPath(requester.getMap().findPath(data.x, data.y, xDest, yDest, requester.getCollision()), requester, data);
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

    private static void requestForPath(PointContener newPath, Entity requester, PathData data) {
        if (!data.pathRequested) {
//            data.pathRequested = true;
//            data.pathRequested = false;
            if (newPath != null) {
                if (newPath.size() > 1) {
                    copyPath(newPath, data);
                    data.currentPoint = 1;
                    correctDestinationPointIfNeeded(data.path, requester, data);
                } else if (newPath.isEmpty()) {
                    data.diffrentArea = true;
                    return;
                }
            } else if (data.stuck) {
                data.path.clear();
            }
            data.diffrentArea = false;
        }
    }

    private static void copyPath(PointContener newPath, PathData data) {
        data.path.clear();
        for (int i = 0; i < newPath.size(); i++) {
            data.path.add(newPath.get(i).getX() + data.xRef, newPath.get(i).getY() + data.yRef);
        }
    }

    private static void correctDestinationPointIfNeeded(PointContener path, Entity requester, PathData data) {
        if (data.destination != null) {
            data.close = Figure.whatClose(requester, data.x, data.y, (data.width + data.height) * 2, data.destination.getX(), data.destination.getY(), requester.getMap(), data.close);
            if (!data.close.isEmpty()) {
                data.close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());
                data.desired = path.get(path.size() - 1);
                setTestingPosition(data.testing, data.desired);
                data.collided = whatColidesWithTesting(data.close, data.testing);
                if (data.collided != null) {
                    correctDestinationPoint(path, data);
                }
            }
        }
    }

    private static void correctDestinationPoint(PointContener path, PathData data) {
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

    private static Figure whatColidesWithTesting(List<Figure> close, Figure testing) {
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

    private static void managePassing(Entity requester, PathData data) {
        if (data.passing) {
            passing(requester, data);
        } else if (data.passed) {
            passed(requester, data);
        } else {
            normal(requester, data);
        }
    }

    private static void passing(Entity requester, PathData data) {
        if (data.stuck || data.x == data.correction.getX() && data.y == data.correction.getY() || (data.inAWay != null && (data.inAWay.getX() != data.xInAWay || data.inAWay.getY() != data.yInAWay))) {
            data.passing = false;
            data.passed = true;
        } else {
            if (data.inAWay != null && data.inAWay.isMobile()) {
                data.lastInAWay = null;
                isSomethingOnTheWay(requester, data);
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
            if (data.choice) {
                data.ySpeed = 0;
            } else {
                data.xSpeed = 0;
            }
        } else if (data.xSpeed == 0 && data.ySpeed == 0) {
            if (data.choice) {
                data.xSpeed = requester.getMaxSpeed();
            } else {
                data.ySpeed = requester.getMaxSpeed();
            }
        }
        data.passedCount++;
        if (data.passedCount >= 4) {
            data.passedCount = 0;
            data.passed = false;
            data.choice = FastMath.random() > 0.5;
        }
    }

    private static void normal(Entity requester, PathData data) {
        isSomethingOnTheWay(requester, data);
        if (!data.path.isEmpty() || data.destination.equals(data.finalDestination)) {
            data.lastInAWay = null;
            data.last2CorrectionPoint.set(-1, -1);
            data.last1CorrectionPoint.set(-1, -1);
        }
    }

    private static void isSomethingOnTheWay(Entity requester, PathData data) {
        data.close = Figure.whatClose(requester, data.x, data.y, ((int) (requester.getRange()) >> 2), data.x, data.y, requester.getMap(), data.close);
        if (!data.close.isEmpty()) {
            data.close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());
            PathStrategyCore.setPolygonForTesting(data, data.finalDestination);
            data.inAWay = PathStrategyCore.anyFigureInAWay(data.poly, data.close);
            if (data.inAWay != data.lastInAWay && isNeedToPass(data)) {
                data.xCorrection = data.widthHalf + 1;
                data.yCorrection = data.heightHalf + 1;
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
        }
    }

    private static void addPointIfClearPath(PathData data, Point correction, int corner) {
        if (!correction.equals(data.last1CorrectionPoint) || !data.last1CorrectionPoint.equals(data.last2CorrectionPoint)) {
            PathStrategyCore.setPolygonForTesting(data, data.finalDestination);
            if (PathStrategyCore.anyFigureInAWay(data.poly, data.close) == null) {
                data.correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), corner));
            }
        }
    }

    private static void setCorrectionIfFits(PathData data) {
        data.min = Integer.MAX_VALUE;
        data.closest = null;
        data.correctionPoints.stream().forEach((point) -> {
            data.temp = Methods.pointDistance(point.getX(), point.getY(), data.destination.getX(), data.destination.getY());
            if (data.temp < data.min) {
                data.min = data.temp;
                data.closest = point;
            }
        });
        if (data.closest != null) {
            setCorrection(data);
        }
    }

    private static void setCorrection(PathData data) {
        data.passing = true;
        data.passed = false;
        data.last2CorrectionPoint.set(data.last1CorrectionPoint.getX(), data.last1CorrectionPoint.getY());
        data.last1CorrectionPoint.set(data.closest.getX(), data.closest.getY());
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
        data.correction.set(data.closest.getX(), data.closest.getY());
    }

    private static boolean isNeedToPass(PathData data) {
        if (data.inAWay != null) {
            if (!data.path.isEmpty()) {
                if (data.path.size() - 1 != data.currentPoint) {
                    return true;
                } else {
                    return isPassingRequired(data);
                }
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

    public static void manageBlockedAndAdjustSpeed(PathData data, double maxSpeed) {
        if (data.blocked) {
            data.xSpeed = data.pastXSpeed;
            data.ySpeed = data.pastYSpeed;
            data.alternateCount++;
            int value = Methods.roundDouble((data.pastYSpeed > data.pastXSpeed ? data.height * 2 : data.width * 2) / maxSpeed);
            if (data.alternateCount > value) {
                data.alternateCount = 0;
                data.blocked = false;
            }
        } else {
            if (data.xSpeed * data.pastXSpeed > 0 && data.x == data.pastPosition.getX()) {
                data.xSpeed = 0;
                data.ySpeed = (Math.abs(data.ySpeed) > 0) ? Math.signum(data.ySpeed) * maxSpeed : (data.choice ? maxSpeed : -maxSpeed);
                data.blocked = true;
                data.choice = FastMath.random() > 0.5;
            } else if (data.ySpeed * data.pastYSpeed > 0 && data.y == data.pastPosition.getY()) {
                data.ySpeed = 0;
                data.xSpeed = (Math.abs(data.xSpeed) > 0) ? Math.signum(data.xSpeed) * maxSpeed : (data.choice ? maxSpeed : -maxSpeed);
                data.blocked = true;
                data.choice = FastMath.random() > 0.5;
            }
            adjustSpeed(data, maxSpeed);
        }
    }

    private static void adjustSpeed(PathData data, double maxSpeed) {
        if (data.x != data.destination.getX()) {
            if (Math.abs(data.xSpeed) <= 1) {
                data.xSpeed = Math.signum(data.xSpeed);
            } else if (data.xDistance < maxSpeed) {
                data.xSpeed = Math.signum(data.xSpeed) * data.xDistance;
            }
        } else {
            data.xSpeed = 0;
        }
        if (data.y != data.destination.getY()) {
            if (Math.abs(data.ySpeed) <= 1) {
                data.ySpeed = Math.signum(data.ySpeed);
            } else if (data.yDistance < maxSpeed) {
                data.ySpeed = Math.signum(data.ySpeed) * data.yDistance;
            }
        } else {
            data.ySpeed = 0;
        }
    }

    public static void setPolygonForTesting(PathData data, Point destination) {
        Methods.getCastingPoints((2 * data.x - destination.getX()), (2 * data.y - destination.getY()), data.xS, data.xE, data.yS, data.yE, data.castingPoints);
        setDestinationCorners(destination, data);
        Methods.getCastingPoints((2 * destination.getX() - data.x), (2 * destination.getY() - data.y), data.xDS, data.xDE, data.yDS, data.yDE, data.castingDestination);
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
