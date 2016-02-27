/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Block;
import collision.Figure;
import engine.Main;
import engine.lights.Light;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.logic.betweenareapathfinding.AreaConnection;
import game.logic.betweenareapathfinding.AreaConnector;
import game.logic.betweenareapathfinding.AreaConnectorsGenerator;
import game.logic.betweenareapathfinding.AreaNode;
import game.place.Place;
import game.place.cameras.Camera;
import org.newdawn.slick.Color;

import java.util.*;

import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public abstract class Map {

    protected static final PointContainer NO_SOLUTION = new PointContainer(0);
    protected static final Comparator<GameObject> depthComparator = (GameObject firstObject, GameObject secondObject) ->
            firstObject.getDepth() - secondObject.getDepth();
    protected static Tile tempTile;
    private static int POINTING_ARROW_HEIGHT = 128;
    public final Place place;
    protected final int tileSize;
    protected final BlueArray<GameObject> pointingArrows = new BlueArray<>();
    protected final String name;
    protected final short mapID;
    protected final PointContainer tempTilePositions = new PointContainer();
    protected final Set<Block> tempBlocks = new HashSet<>();
    protected final BlueArray<Mob> tempMobs = new BlueArray<>();
    protected final BlueArray<Entity> tempEntities = new BlueArray<>();
    protected final BlueArray<Interactive> tempInteractiveObjects = new BlueArray<>();
    protected final BlueArray<GameObject> topObjects = new BlueArray<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();
    protected final BlueArray<Light> lights = new BlueArray<>();
    protected final BlueArray<Block> blocks = new BlueArray<>();
    protected final BlueArray<Light> visibleLights = new BlueArray<>();
    protected final Set<Integer> areasToUpdate = new HashSet<>(36);
    public Area[] areas;
    public Area[] areasCopies; //Tylko do testów - powinno być wywalone - a areas wczytywane z pliku
    protected int widthInTiles, heightInTiles;
    protected int width;
    protected int height;
    protected AreaConnector[] areaConnectors;
    protected Placement placement;
    protected int xAreas;
    protected int yAreas;
    protected short mobID = 0;
    protected Color lightColor;
    protected List<GameObject> depthObjects, foregroundTiles;
    protected int cameraXStart, cameraYStart, cameraXEnd, cameraYEnd, cameraXOffEffect, cameraYOffEffect;     //Camera's variables for current rendering
    protected Color pointingColor = new Color(0f, 0f, 0f, 0.7f);
    private Color[] colors = {Color.red, Color.magenta};

    protected Map(short mapID, String name, Place place, int width, int height, int tileSize) {
        this.place = place;
        this.name = name;
        this.mapID = mapID;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        widthInTiles = width / tileSize;
        heightInTiles = height / tileSize;
        createAreas();
    }

    protected Map(short mapID, String name, Place place, int tileSize) {
        this.place = place;
        this.name = name;
        this.mapID = mapID;
        this.tileSize = tileSize;
    }

    protected void initializeAreas(int width, int height) {
        this.width = width;
        this.height = height;
        widthInTiles = width / tileSize;
        heightInTiles = height / tileSize;
        createAreas();
    }

    public void createAreas() {     // must be called after constructor super
        xAreas = (this.widthInTiles / Area.X_IN_TILES) + (widthInTiles % Area.X_IN_TILES != 0 ? 1 : 0);
        yAreas = (heightInTiles / Area.Y_IN_TILES) + (heightInTiles % Area.Y_IN_TILES != 0 ? 1 : 0);
        areas = new Area[xAreas * yAreas];
        areasCopies = new Area[xAreas * yAreas];
        for (int areaIndex = 0; areaIndex < areas.length; areaIndex++) {
            areas[areaIndex] = new Area(place, this, areaIndex % xAreas, areaIndex / xAreas);
            areasCopies[areaIndex] = areas[areaIndex];
        }
        placement = new Placement(this);
    }

    public abstract void populate();

    protected void generateNavigationMeshes() {     // call after adding All blocks and tiles
        int areaIndex = 0;
        Set<Block> allBlocks = new HashSet<>();
        for (Area area : areas) {
            tempBlocks.clear();
            for (Block block : getBlocks(areaIndex)) {
                if (isOnArea(block, areaIndex)) {
                    tempBlocks.add(block);
                }
                allBlocks.add(block);
            }
            area.generateNavigationMesh(tempBlocks);
            areaIndex++;
        }
        this.areaConnectors = AreaConnectorsGenerator.generateAreaConnectors(this);
        for (Block block : allBlocks) {
            if (block.isForNavigationMesh()) {
                deleteBlock(block);
            }
        }
    }

    private List<Block> getBlocks(int area) {
        blocks.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                blocks.addAll(areas[i].getBlocks());
            }
        }
        return blocks;
    }

    public Mob getSolidMobById(int id) {
        for (int area = 0; area < areas.length; area++) {
            for (Mob mob : areas[area].getSolidMobs()) {
                if (mob.mobID == id) {
                    return mob;
                }
            }
        }
        return null;
    }

    private boolean isOnArea(Block block, int area) {
        Figure collision = block.getCollision();
        return getAreaIndex(collision.getX(), collision.getY()) == area || getAreaIndex(collision.getX(), collision.getYEnd() - Place.tileSize) == area
                || getAreaIndex(collision.getXEnd() - Place.tileSize, collision.getYEnd() - Place.tileSize) == area || getAreaIndex(collision.getXEnd() -
                Place.tileSize, collision.getY()) == area;
    }

    public PointContainer findPath(int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        int area = getAreaIndex(xStart, yStart);
        int x = areas[area].getXInPixels();
        int y = areas[area].getYInPixels();
        PointContainer solution;
        if (area != getAreaIndex(xDestination, yDestination)) {
            solution = findInDifferentAreas(area, x, y, xStart, yStart, xDestination, yDestination, collision);
        } else {
            solution = areas[area].findPath(xStart - x, yStart - y, xDestination - x, yDestination - y, collision);
        }
        if (solution != null) {
            if (Main.SHOW_MESH && Main.meshWindow != null && solution.size() > 0) {
                Main.meshWindow.addVariables(areas[area].getNavigationMesh(), solution.get(solution.size() - 1), solution.get(0), solution);
                Main.meshWindow.repaint();
            }
            return solution;
        } else {
            return NO_SOLUTION;
        }
    }

    private PointContainer findInDifferentAreas(int area, int x, int y, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        int endArea = getAreaIndex(xDestination, yDestination);
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
        for (AreaConnection connection : areaConnectors[area].getConnections()) {
            if (beginning.getConnection() != connection && existsConnection(area, beginning, connection, collision)) {
                AreaNode node = new AreaNode(connection, beginning);
                node.setGHCosts(countGCost(node, beginning), countHCost(node, ending));
                openList.add(node);
            }
        }
        int currentArea = area;
        while (!openList.isEmpty()) {
            currentNode = openList.poll();
            closedList.add(currentNode);
            if (currentNode.connectsWithArea(endArea) && existsConnection(endArea, currentNode, end, collision)) {
                break;
            }
            currentArea = currentNode.getConnectedAreaIndex(currentArea);
            for (AreaConnection connection : areaConnectors[currentArea].getConnections()) {
                AreaNode next = closedListContains(closedList, connection);
                if (currentNode.getConnection() != connection) {
                    if (next.getFCost() != Integer.MAX_VALUE) { // już istniał
                        int temp = countGCost(next, currentNode);
                        if (temp + currentNode.getGCost() < next.getGCost() && existsConnection(currentArea, currentNode, next.getConnection(), collision)) {
                            next.setParent(currentNode);
                            next.setGCost(temp);
                        }
                    } else if (existsConnection(currentArea, currentNode, next.getConnection(), collision)) {
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
            PointContainer solution = getBetweenAreaSolution(area, endArea, x, y, xStart, yStart, xDestination, yDestination, prev.getConnection(), collision);
            if (solution != null) {
                return solution;
            }
        }

        return NO_SOLUTION;
    }

    private boolean existsConnection(int area, AreaNode start, AreaConnection end, Figure collision) {
        Area a = areas[area];
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
        return areas[area].pathExists(firstTestX, firstTestY, secondTestX, secondTestY, collision);
    }

    private AreaNode closedListContains(Set<AreaNode> closedList, AreaConnection connection) {
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

    private int countGCost(AreaNode currentNode, AreaNode parentNode) {
        Point current = currentNode.getCentralPoint();
        Point parent = parentNode.getCentralPoint();
        int x = parent.getX() - current.getX();
        int y = parent.getY() - current.getY();
        return 2686976;
    }

    private int countHCost(AreaNode currentNode, AreaNode endNode) {
        Point current = currentNode.getCentralPoint();
        Point parent = endNode.getCentralPoint();
        int x = parent.getX() - current.getX();
        int y = parent.getY() - current.getY();
        return (x * x + y * y);
    }

    private int calculateValueOfPoint(Point point, int xStart, int yStart) {
        return Methods.pointDistanceSimple2(point.getX(), point.getY(), xStart, yStart);
    }

    private int calculateValueOfLastPoint(Point point, int xStart, int yStart) {
        return Methods.pointDistanceSimple2(point.getX(), point.getY(), xStart, yStart);
    }

    private PointContainer getBetweenAreaSolution(int area, int endArea, int x, int y, int xStart, int yStart, int xDestination, int yDestination,
                                                  AreaConnection connection, Figure collision) {
        List<Point> currentAreaPoints = connection.getConnectionPoints();
        List<Point> tempPoints = new ArrayList<>(currentAreaPoints);
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
            solution = areas[area].findPath(xStart - x, yStart - y, testX, testY, collision);
            if (solution != null) {
                int changeX = currentPoint.getX() - x;
                int changeY = currentPoint.getY() - y;
                if (currentPoint.getX() % (X_IN_TILES * 64) == 0) {
                    changeX += currentPoint.getX() > x ? collision.getWidthHalf() : -collision.getWidthHalf();
                }
                if (currentPoint.getY() % (Y_IN_TILES * 64) == 0) {
                    changeY += currentPoint.getY() > y ? collision.getWidthHalf() : -collision.getWidthHalf();
                }
                solution.add(new Point(changeX, changeY));
                return solution;
            }
        }
        return null;
    }


    public void addAreasToUpdate(int[] newAreas) {
        for (int area : newAreas) {
            getAreasToUpdate().add(area);
        }
    }

    public void clearAreasToUpdate() {
        getAreasToUpdate().clear();
    }

    public void updateAreasToUpdate() {
        getAreasToUpdate().stream().filter((area) -> (area >= 0 && area < areas.length && areas[area] != null)).forEach((area) -> areas[area]
                .updateContainers(area));
    }

    public void unloadUnNeededUpdate() {
        for (int area = 0; area < areas.length; area++) {
            if (!getAreasToUpdate().contains(area)) {
                //TODO save to file
                areas[area] = null;
            }
        }
    }

    public void updateNearBlocks(int area, List<Block> blocks) {
        blocks.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                blocks.addAll(areas[i].getBlocks());
            }
        }
    }

    public void updateNearSolidMobs(int area, List<Mob> mobs) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                mobs.addAll(areas[i].getSolidMobs());
            }
        }
    }

    public void updateNearFlatMobs(int area, List<Mob> mobs) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                mobs.addAll(areas[i].getFlatMobs());
            }
        }
    }

    public void updateNearSolidObjects(int area, List<GameObject> objects) {
        objects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                objects.addAll(areas[i].getSolidObjects());
            }
        }
    }

    public void updateNearFlatObjects(int area, List<GameObject> objects) {
        objects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                objects.addAll(areas[i].getFlatObjects());
            }
        }
    }

    public void updateNearWarps(int area, List<WarpPoint> warps) {
        warps.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                warps.addAll(areas[i].getWarps());
            }
        }
    }

    public void updateNearDepthObjects(int area, List<GameObject> depthObjects) {
        depthObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                depthObjects.addAll(areas[i].getDepthObjects());
            }
        }
        depthObjects.sort(depthComparator);
    }

    public void updateNearForegroundTiles(int area, List<GameObject> foregroundTiles) {
        foregroundTiles.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                foregroundTiles.addAll(areas[i].getForegroundTiles());
            }
        }
        foregroundTiles.sort(depthComparator);
    }

    private void updateNearTopObjects(int area) {
        topObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                topObjects.addAll(areas[i].getTopObjects());
            }
        }
        topObjects.sort(depthComparator);
    }

    public PointContainer getNearNullTiles(Figure collision) {
        tempTilePositions.clear();
        int xs = (collision.getX() / Place.tileSize) - 1;
        int ys = (collision.getY() / Place.tileSize) - 1;
        int xe = (collision.getXEnd() / Place.tileSize) + 1;
        int ye = (collision.getYEnd() / Place.tileSize) + 1;
        for (int x = xs; x <= xe; x++) {
            for (int y = ys; y <= ye; y++) {
                if (getTile(x, y) == null) {
                    tempTilePositions.add(x, y);
                }
            }
        }
        return tempTilePositions;
    }

    public void updateEntitesFromAreasToUpdate() {
        tempEntities.clear();
        getAreasToUpdate().stream().filter((area) -> (area >= 0 && area < areas.length && areas[area] != null)).forEach((area) -> {
            tempEntities.addAll(areas[area].getEntities());
        });
        tempEntities.stream().forEach(Entity::update);
    }

    public void updateMobsFromAreasToUpdate() {
        prepareMobsToUpdate();
        tempMobs.stream().forEach(Entity::update);
    }

    public void updateObjectsFromAreasToUpdate() {
        if (depthObjects != null) {
            for (GameObject object : depthObjects) {
                if (object.isToUpdate()) {
                    object.update();
                }
            }
        }
    }

    public void hardUpdateMobsFromAreasToUpdate() {
        prepareMobsToUpdate();
        tempMobs.stream().forEach(Entity::updateHard);
    }

    private void prepareMobsToUpdate() {
        tempMobs.clear();
        getAreasToUpdate().stream().filter((area) -> (area >= 0 && area < areas.length && areas[area] != null)).forEach((area) -> {
            tempMobs.addAll(areas[area].getSolidMobs());
            tempMobs.addAll(areas[area].getFlatMobs());
        });
    }

    public void updateInteractiveObjectsFromAreasToUpdate() {
        prepareInteractive();
        tempInteractiveObjects.stream().forEach((interactive) -> {
            interactive.update();
            if (interactive.isActive()) {
                interactive.actIfActivated(place.players, tempMobs);
            }
        });
    }

    private void prepareInteractive() {
        tempInteractiveObjects.clear();
        getAreasToUpdate().stream().filter((area) -> (area >= 0 && area < areas.length && areas[area] != null)).forEach((area) -> tempInteractiveObjects
                .addAll(areas[area].getInteractiveObjects()));
    }

    public void placePuzzle(int x, int y, PuzzleObject po) {
        po.placePuzzle(x, y, this);
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        areas[getAreaIndex(x, y)].addForegroundTileAndReplace(tile, x, y, depth);
    }

    public void addForegroundTile(GameObject tile) {
        addForegroundTile(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTile(GameObject tile, int x, int y, int depth) {
        areas[getAreaIndex(x, y)].addForegroundTile(tile, x, y, depth);
    }

    public void deleteForegroundTile(GameObject tile) {
        deleteForegroundTile(tile, tile.getX(), tile.getY());
    }

    private void deleteForegroundTile(GameObject tile, int x, int y) {
        areas[getAreaIndex(x, y)].deleteForegroundTile(tile);
    }

    public void addVisibleLight(Light light) {
        visibleLights.add(light);
    }

    public void clearVisibleLights() {
        visibleLights.clear();
    }

    public Block getBlock(int x, int y) {
        return areas[getAreaIndex(x, y)].getBlock(x, y);
    }

    public void addBlock(Block block) {
        areas[getAreaIndex(block.getX(), block.getY())].addBlock(block);
    }

    public void deleteBlock(int x, int y) {
        Block block = getBlock(x, y);
        if (block != null) {
            areas[getAreaIndex(x, y)].deleteBlock(block);
        }
    }

    public void deleteBlock(Block block) {
        areas[getAreaIndex(block.getX(), block.getY())].deleteBlock(block);
    }

    public void addObject(GameObject object) {
        if (object.getX() >= 0 && object.getY() >= 0 && object.getX() < width && object.getY() < height) {
            object.setMapNotChange(this);
            if (object instanceof WarpPoint) {
                warps.add((WarpPoint) object);
            }
            int area = getAreaIndex(object.getX(), object.getY());
            object.setArea(area);
            areas[area].addObject(object);
        } else {
            System.out.println("Poza mapą - nie dodaję! " + object.getName());
        }
    }

    public void deleteObject(GameObject object) {
        if (!areas[object.getArea()].deleteObject(object)) {
            boolean removed = false;
            for (int i : getAreasToUpdate()) {
                if (i > 0 && i < areas.length && areas[i] != null && areas[i].deleteObject(object)) {
//                    System.out.println("Removed on second try");
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                for (int i = 0; i < areas.length; i++) {
                    if (areas[i] != null && areas[i].deleteObject(object)) {
//                        System.out.println("Removed on third try");
                        removed = true;
                        break;
                    }
                }
            }
            if (!removed) {
                for (int i = 0; i < areas.length; i++) {
                    if (areas[i] != null && areas[i].deleteObject(object)) {
//                        System.out.println("Removed on third try");
                        removed = true;
                        break;
                    }
                }
            }
            if (!removed) {
                System.out.println("Nie mogę usunąć - LIPA : " + object.getName());
            }
        }
        object.setMapNotChange(null);
        if (object instanceof WarpPoint) {
            warps.remove(object);
        }
        object.setArea(-1);
    }

    protected void removeForegroundTile(GameObject foregroundTile) {
        areas[getAreaIndex(foregroundTile.getX(), foregroundTile.getY())].deleteForegroundTile(foregroundTile);
    }

    public void changeArea(int area, int prevArea, GameObject object) {
        if (!areas[prevArea].deleteObject(object)) {
            boolean removed = false;
            for (int i : getAreasToUpdate()) {
                if (i >= 0 && i < areas.length && areas[i] != null && areas[i].deleteObject(object)) {
//                    System.out.println("Removed on second try");
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                for (int i = 0; i < areas.length; i++) {
                    if (areas[i] != null && areas[i].deleteObject(object)) {
//                        System.out.println("Removed on third try");
                        removed = true;
                        break;
                    }
                }
            }
            if (!removed) {
                System.out.println("Nie mogę usunąć - LIPA : " + object.getName());
            }
        }
        areas[area].addObject(object);
    }

    public void updateCamerasVariables(Camera camera) {
        cameraXStart = camera.getXStart();
        cameraYStart = camera.getYStart();
        cameraXEnd = camera.getXEnd();
        cameraYEnd = camera.getYEnd();
        cameraXOffEffect = camera.getXOffsetEffect();
        cameraYOffEffect = camera.getYOffsetEffect();
        pointingArrows.clear();
    }

    public void renderBackground(Camera camera) {
        Drawer.clearScreen(0);
        Drawer.refreshForRegularDrawing();
        for (int i : placement.getNearAreas(camera.getArea())) {
            if (i >= 0 && i < areas.length) {
                renderArea(i);
                renderAreaBounds(i);
            }
        }
    }

    private void renderAreaBounds(int i) {
        if (Main.SHOW_AREAS) {
            int yTemp = (i / xAreas) * Y_IN_TILES;
            int xTemp = (i % xAreas) * X_IN_TILES;
            glPushMatrix();
            glTranslatef(cameraXOffEffect, cameraYOffEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(xTemp * Place.tileSize, yTemp * Place.tileSize, 0);
            Drawer.setColorStatic(Color.cyan);
            Drawer.setCentralPoint();
            Drawer.drawRectangleBorder(0, 0, xAreaInPixels, yAreaInPixels);
            Drawer.returnToCentralPoint();
            Drawer.renderStringCentered(String.valueOf(i), Place.tileSize, Place.tileSize,
                    Drawer.getFont("Amble-Regular", (int) (Place.getCurrentScale() * 32)), Color.cyan);
            glPopMatrix();

            glPushMatrix();
            glTranslatef(cameraXOffEffect, cameraYOffEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            Drawer.setCentralPoint();
            if (areaConnectors != null && areaConnectors[i] != null) {
                int c = 0;
                for (AreaConnection connection : areaConnectors[i].getConnections()) {
                    Drawer.setColorStatic(colors[c % colors.length]);
                    for (Point point : connection.getConnectionPoints()) {
                        Drawer.drawRectangle(point.getX() - 5, point.getY() - 5, 10, 10);
                        Drawer.returnToCentralPoint();
                    }
                    c++;
                }
            }
            glPopMatrix();
            Drawer.refreshColor();
        }
    }

    protected void renderArea(int i) {
        int yTemp = (i / xAreas) * Y_IN_TILES;
        int xTemp = (i % xAreas) * X_IN_TILES;
        for (int yTiles = 0; yTiles < Y_IN_TILES; yTiles++) {
            int y = yTemp + yTiles;
            if (cameraYStart < (y + 1) * tileSize && cameraYEnd > y * tileSize) {
                for (int xTiles = 0; xTiles < X_IN_TILES; xTiles++) {
                    int x = xTemp + xTiles;
                    if (cameraXStart < (x + 1) * tileSize && cameraXEnd > x * tileSize) {
                        Area area = areas[i];
                        tempTile = area != null ? area.getTile(xTiles, yTiles) : null;
                        if (tempTile != null && tempTile.isVisible()) {
                            tempTile.renderSpecific(cameraXOffEffect, cameraYOffEffect, x * tileSize, y * tileSize);
                        }
                    }
                }
            }
        }
    }

    public void renderObjects(Camera camera) {
        Drawer.refreshForRegularDrawing();
        renderBottom(camera);
        renderTop(camera);
    }

    private void renderBottom(Camera camera) {
        foregroundTiles = areas[camera.getArea()].getNearForegroundTiles();
        depthObjects = areas[camera.getArea()].getNearDepthObjects();
        int y = 0;
        for (GameObject object : depthObjects) {
            for (; y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < object.getDepth(); y++) {
                if (foregroundTiles.get(y).isVisible() && isObjectInSight(foregroundTiles.get(y))) {
                    foregroundTiles.get(y).render(cameraXOffEffect, cameraYOffEffect);
                }
            }
            if (object.isVisible() && isObjectInSight(object)) {
                if (isBehindSomething(object)) {
                    pointingArrows.add(object);
                }
                object.render(cameraXOffEffect, cameraYOffEffect);
            }
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            if (foregroundTiles.get(i).isVisible() && isObjectInSight(foregroundTiles.get(i))) {
                foregroundTiles.get(i).render(cameraXOffEffect, cameraYOffEffect);
            }
        }
    }

    private boolean isBehindSomething(GameObject object) {
        if (object instanceof Entity && object.getAppearance() != null) {
            for (GameObject tile : foregroundTiles) {
                if (tile.getDepth() > object.getDepth()
                        && (tile.getX() / tileSize == object.getX() / tileSize || tile.getX() / tileSize == Methods.roundDouble(object.getX() / tileSize))
                        && (tile.getY() / tileSize == (object.getY() + (object.getCollision().getHeight() - object.getAppearance().getActualHeight()) / 2)
                        / tileSize || tile.getY() / tileSize == Methods.roundDouble((object.getY() + object.getCollision().getHeightHalf() - object
                        .getAppearance().getActualHeight() / 2) / tileSize))) {
                    return true;
                }
            }
            for (GameObject other : depthObjects) {
                if (other != object) {
                    if (!(other instanceof Entity) && other.canCover() && object.getAppearance() != null && other.getAppearance() != null
                            && object.getCollision() != null && other.getDepth() > object.getDepth()
                            && object.getX() < other.getXSpriteEnd(true) && object.getX() > other.getXSpriteBegin(true)
                            && object.getY() + object.getCollision().getHeightHalf() - object.getAppearance().getActualHeight() / 2 < other.getYSpriteEnd(true)
                            && object.getY() + object.getCollision().getHeightHalf() - object.getAppearance().getActualHeight() / 2 > other.getYSpriteBegin
                            (true)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void renderTop(Camera camera) {
        updateNearTopObjects(camera.getArea());
        topObjects.stream().filter((object) -> (object.isVisible() && isObjectInSight(object))).forEach((object) -> object.render(cameraXOffEffect,
                cameraYOffEffect));
        for (GameObject pointer : pointingArrows) {
            renderPointingArrow(pointer);
        }

    }

    private void renderPointingArrow(GameObject object) {
        glPushMatrix();
        glTranslatef(cameraXOffEffect, cameraYOffEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(object.getX(), object.getY() - POINTING_ARROW_HEIGHT - (int) (object.getFloatHeight()), 0);
        Drawer.setColorStatic(pointingColor);
        Drawer.drawEllipseBow(0, 0, 5, 10, 5, -30, 210, 15);
//        Drawer.drawEllipseBow(0, -32, 8, 64, 8, 45, 135, 4);
        Drawer.refreshColor();
        glScaled(0.5f, 0.5f, 1);
        glTranslatef(0, -POINTING_ARROW_HEIGHT / 4, 0);
        object.getAppearance().render();
        glPopMatrix();
    }

    private boolean isObjectInSight(GameObject object) {
        return cameraYStart <= object.getYSpriteEnd() + object.getFloatHeight()
                && cameraYEnd >= object.getYSpriteBegin() - object.getFloatHeight()
                && cameraXStart <= object.getXSpriteEnd()
                && cameraXEnd >= object.getXSpriteBegin();
    }

    public WarpPoint findWarp(String name) {
        for (WarpPoint warp : warps) {
            if (warp.getName().equals(name)) {
                return warp;
            }
        }
        return null;
    }

    public void clear() {
        for (Area area : areas) {
            area.clear();
        }
        visibleLights.clear();
        topObjects.clear();
        foregroundTiles.clear();
        warps.clear();
        lights.clear();
        blocks.clear();
        tempMobs.clear();
    }

    public int getWidthInTiles() {
        return widthInTiles;
    }

    public int getHeightInTiles() {
        return heightInTiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public String getName() {
        return name;
    }

    public short getID() {
        return mapID;
    }

    public Color getLightColor() {
        if (lightColor != null) {
            return lightColor;
        } else {
            return Place.getLightColor();
        }
    }

    public float getDarkness() {
        Color color = getLightColor();
        return (color.r + color.g + color.b) / 3;
    }


    public int getXAreas() {
        return xAreas;
    }

    public int getYAreas() {
        return yAreas;
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < widthInTiles && y >= 0 && y < heightInTiles) {
            Area area = areas[getAreaIndexCoordinatesInTiles(x, y)];
            if (area != null) {
                return area.getTile(getXInArea(x), getYInArea(y));
            }
        }
        return null;
    }

    public int getAreasSize() {
        return areas.length;
    }

    protected int getAreaIndexCoordinatesInTiles(int x, int y) {
        return x / X_IN_TILES + y / Y_IN_TILES * xAreas;
    }

    public int getAreaIndex(int x, int y) {
        return x / xAreaInPixels + y / yAreaInPixels * xAreas;
    }

    public int[] getNearAreas(int area) {
        return placement.getNearAreas(area);
    }

    public List<Light> getLightsFromAreasToUpdate() {
        lights.clear();
        getAreasToUpdate().stream().filter((i) -> (i >= 0 && i < areas.length && areas[i] != null)).forEach((i) -> lights.addAll(areas[i].getLights()));
        return lights;
    }

    public List<Light> getVisibleLights() {
        return visibleLights;
    }

    public Area getArea(int i) {
        return areas[i];
    }

    public Area getArea(int x, int y) {
        return areas[getAreaIndex(x, y)];
    }

    public void setTile(int x, int y, Tile tile) {
        if (x < widthInTiles && y < heightInTiles) {
            areas[getAreaIndexCoordinatesInTiles(x, y)].setTile(getXInArea(x), getYInArea(y), tile);
        }
    }

    protected int getXInArea(int x) {
        return x % (X_IN_TILES);
    }

    protected int getYInArea(int y) {
        return y % (Y_IN_TILES);
    }

    protected void setColor(Color color) {
        this.lightColor = color;
    }

    public Set<Integer> getAreasToUpdate() {
        return areasToUpdate;
    }

    public short getNextMobID() {
        return mobID++;
    }
}
