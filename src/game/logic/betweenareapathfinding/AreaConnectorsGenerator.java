package game.logic.betweenareapathfinding;

import engine.utilities.Point;
import game.place.Place;
import game.place.map.Area;
import game.place.map.Map;

import java.util.BitSet;

import static game.logic.navmeshpathfinding.navigationmesh.NavigationMeshGenerator.getIndex;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;
import static game.place.map.Placement.BOTTOM;
import static game.place.map.Placement.RIGHT;

/**
 * Created by przemek on 14.02.16.
 */
public class AreaConnectorsGenerator {

    private static AreaConnector[] connectors;

    public static AreaConnector[] generateAreaConnectors(Map map) {
        connectors = new AreaConnector[map.areas.length];
        for (int areaIndex = 0; areaIndex < map.areas.length; areaIndex++) {
            Area area = map.areas[areaIndex];
            if (area != null) {
                if (area.getNavigationMesh() != null) {
                    if (connectors[areaIndex] == null) {
                        connectors[areaIndex] = new AreaConnector(areaIndex);
                    }
                    BitSet spots = area.getNavigationMesh().getSpots();
                    int[] nearAreas = map.getNearAreas(areaIndex);
                    for (int areaPos = 1; areaPos < 9; areaPos++) {
                        int nearAreaIndex = nearAreas[areaPos];
                        if (nearAreaIndex != -1) {
                            Area nearArea = map.getArea(nearAreaIndex);
                            if (nearArea != null) {
                                if (nearArea.getNavigationMesh() != null) {
                                    if (connectors[nearAreaIndex] == null) {
                                        connectors[nearAreaIndex] = new AreaConnector(nearAreaIndex);
                                    }
                                    BitSet nearAreaSpots = nearArea.getNavigationMesh().getSpots();
                                    resolveCases(areaPos, areaIndex, nearAreaIndex, spots, nearAreaSpots, area, nearArea);
                                }
                            } else {
                                System.out.println("NULL AREA w AreaConnectorsGenerator");
                            }
                        }
                    }
                }
            }
        }
        return connectors;
    }


    private static void resolveCases(int areaPos, int areaIndex, int nearAreaIndex, BitSet spots, BitSet nearAreaSpots, Area area, Area nearArea) {
        boolean inConnection = false;
        AreaConnection connection = null;
        switch (areaPos) {
            case BOTTOM:
                for (int x = 0; x < X_IN_TILES; x++) {
                    if (!spots.get(getIndex(x, Y_IN_TILES - 1)) && !nearAreaSpots.get(getIndex(x, 0))) {
                        if (connection == null) {
                            connection = new AreaConnection(areaIndex, nearAreaIndex);
                        }
                        if (!inConnection) {
                            inConnection = true;
                        }
                        connection.addPoint(new Point(area.getXInPixels() + (int) ((x + 0.5) * Place.tileSize),
                                area.getYInPixels() + Y_IN_TILES * Place.tileSize));
                    } else {
                        if (inConnection) {
                            connectors[areaIndex].addConnection(connection);
                            connectors[nearAreaIndex].addConnection(connection);
                            connection = null;
                        }
                        inConnection = false;
                    }
                }
                break;
            case RIGHT:
                for (int y = 0; y < Y_IN_TILES; y++) {
                    if (!spots.get(getIndex(X_IN_TILES - 1, y)) && !nearAreaSpots.get(getIndex(0, y))) {
                        if (connection == null) {
                            connection = new AreaConnection(areaIndex, nearAreaIndex);
                        }
                        if (!inConnection) {
                            inConnection = true;
                        }
                        connection.addPoint(new Point(area.getXInPixels() + (X_IN_TILES * Place.tileSize),
                                area.getYInPixels() + (int) ((y + 0.5) * Place.tileSize)));
                    } else {
                        if (inConnection) {
                            connectors[areaIndex].addConnection(connection);
                            connectors[nearAreaIndex].addConnection(connection);
                            connection = null;
                        }
                        inConnection = false;
                    }
                }
                break;
            default:
                return;
        }
//        if () {
//            connection = new AreaConnection(areaIndex, nearAreaIndex);
//        }
        if (connection != null && !connectors[areaIndex].getConnections().contains(connection)) {
            connectors[areaIndex].addConnection(connection);
            connectors[nearAreaIndex].addConnection(connection);
        }
    }
}
