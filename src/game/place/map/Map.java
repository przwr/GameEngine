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
import engine.utilities.Drawer;
import engine.utilities.Point;
import engine.utilities.PointContainer;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.logic.betweenareapathfinding.AreaConnection;
import game.logic.betweenareapathfinding.AreaConnector;
import game.logic.betweenareapathfinding.AreaConnectorsGenerator;
import game.logic.betweenareapathfinding.BetweenAreaPathFinder;
import game.place.Place;
import game.place.cameras.Camera;
import org.newdawn.slick.Color;
import sprites.ClothedAppearance;

import java.util.*;

import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;

/**
 * @author Wojtek
 */
public abstract class Map {

    public static final PointContainer NO_SOLUTION = new PointContainer(0);
    protected static final Comparator<GameObject> depthComparator = (GameObject firstObject, GameObject secondObject) ->
            firstObject.getDepth() - secondObject.getDepth();
    private static engine.utilities.Timer timer = new engine.utilities.Timer("Map", 200);
    public final Place place;
    protected final int tileSize;
    protected final ArrayList<GameObject> seeThroughs = new ArrayList<>();
    protected final String name;
    protected final short mapID;
    protected final PointContainer tempTilePositions = new PointContainer();
    protected final Set<Block> tempBlocks = new HashSet<>();
    protected final ArrayList<Mob> tempMobs = new ArrayList<>();
    protected final ArrayList<Entity> tempEntities = new ArrayList<>();
    protected final ArrayList<Interactive> tempInteractiveObjects = new ArrayList<>();
    protected final ArrayList<GameObject> topObjects = new ArrayList<>();
    protected final HashSet<GameObject> tempDepthObjects = new HashSet<>(), tempDepthPbjectsAndForegroundTiles = new HashSet<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();
    protected final ArrayList<Light> lights = new ArrayList<>();
    protected final ArrayList<Block> blocks = new ArrayList<>();
    protected final ArrayList<Light> visibleLights = new ArrayList<>();
    protected final ArrayList<GameObject> staticShadows = new ArrayList<>();
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
    protected int cameraXStart, cameraYStart, cameraXEnd, cameraYEnd;     //Camera's variables for current rendering
    private Color[] colors = {Color.red, Color.magenta};
    private float windStrength = 20, windDirection = 200;

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

    public static void renderBackgroundFromVBO() {
        if (Drawer.streamVertexData.size() > 0) {
            Drawer.regularShader.resetTransformationMatrix();
            Drawer.tileVBO.updateAll(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray(), Drawer.streamIndexData.toArray());
            Drawer.tileVBO.renderTexturedTriangles(0, Drawer.streamIndexData.size());
        }
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        Drawer.streamIndexData.clear();
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
            solution = BetweenAreaPathFinder.findInDifferentAreas(this, area, x, y, xStart, yStart, xDestination, yDestination, collision);
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
        tempDepthObjects.clear();
        tempDepthPbjectsAndForegroundTiles.clear();
        getAreasToUpdate().stream().filter((area) -> (area >= 0 && area < areas.length && areas[area] != null)).forEach((area) -> {
            tempDepthObjects.addAll(areas[area].getDepthObjects());
            tempDepthPbjectsAndForegroundTiles.addAll(areas[area].getDepthObjects());
            tempDepthPbjectsAndForegroundTiles.addAll(areas[area].getForegroundTiles());
        });
        for (GameObject object : tempDepthObjects) {
            if (object.isToUpdate()) {
                object.update();
            }
        }
    }

    public Set<GameObject> getDepthObjectsFromAreasToUpdate() {
        return tempDepthObjects;
    }

    public Set<GameObject> getDepthObjectsAndForegroundTilesFromAreasToUpdate() {
        return tempDepthPbjectsAndForegroundTiles;
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

    public void addStaticShadows(GameObject staticShadow) {
        staticShadows.add(staticShadow);
    }

    public void clearStaticShadows() {
        staticShadows.clear();
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
        seeThroughs.clear();
    }

    public void renderBackground(Camera camera) {
        Drawer.clearScreen(0);
        Drawer.refreshForRegularDrawing();
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        Drawer.streamIndexData.clear();
        for (int i : placement.getNearAreas(camera.getArea())) {
            if (i >= 0 && i < areas.length) {
                renderArea(i);
            }
        }
        renderBackgroundFromVBO();
        if (Main.SHOW_AREAS) {
            for (int i : placement.getNearAreas(camera.getArea())) {
                if (i >= 0 && i < areas.length) {
                    renderAreaBounds(i);
                }
            }
        }
    }

    private void renderAreaBounds(int i) {
        int yTemp = (i / xAreas) * Y_IN_TILES;
        int xTemp = (i % xAreas) * X_IN_TILES;
        Drawer.regularShader.translate(xTemp * Place.tileSize, yTemp * Place.tileSize);
        Drawer.setColorStatic(Color.cyan);
        Drawer.drawRectangleBorder(0, 0, xAreaInPixels, yAreaInPixels);
        if (areaConnectors != null && areaConnectors[i] != null) {
            int c = 0;
            for (AreaConnection connection : areaConnectors[i].getConnections()) {
                Drawer.setColorStatic(colors[c % colors.length]);
                for (Point point : connection.getConnectionPoints()) {
                    Drawer.drawRectangle(point.getX() - 5, point.getY() - 5, 10, 10);
                }
                c++;
            }
        }
        Drawer.refreshColor();
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
                        Tile tempTile = area != null ? area.getTile(xTiles, yTiles) : null;
                        if (tempTile != null && tempTile.isVisible()) {
                            tempTile.addToTileVBO(x * tileSize, y * tileSize);
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
                    foregroundTiles.get(y).render();
                }
            }
            if (object.isVisible() && isObjectInSight(object)) {
                if (isBehindSomething(object)) {
                    seeThroughs.add(object);
                }
                object.render();
            }
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            if (foregroundTiles.get(i).isVisible() && isObjectInSight(foregroundTiles.get(i))) {
                foregroundTiles.get(i).render();
            }
        }
    }

    private boolean isBehindSomething(GameObject object) {
        if (object instanceof Entity && object.getAppearance() != null && object.canBeCovered()) {
            for (GameObject tile : foregroundTiles) {
                if (tile.getDepth() > object.getDepth()
                        && (tile.getX() + tileSize > object.getX() - object.getAppearance().getActualWidth()
                        && tile.getX() < object.getX() + object.getAppearance().getActualWidth())
                        && (tile.getY() + tileSize > object.getY() + object.getCollision().getHeightHalf() - object.getAppearance().getActualHeight()
                        && tile.getY() < object.getY() + object.getCollision().getHeightHalf())) {
                    return true;
                }
            }
            for (GameObject other : depthObjects) {
                if (other != object) {
                    if (!(other instanceof Entity) && other.canCover() && object.getAppearance() != null && other.getAppearance() != null
                            && object.getCollision() != null && other.getDepth() > object.getDepth()
                            && object.getX() - object.getAppearance().getActualWidth() / 2 < other.getXSpriteEnd(true)
                            && object.getX() + object.getAppearance().getActualWidth() / 2 > other.getXSpriteBegin(true)
                            && object.getY() + object.getCollision().getHeightHalf() - object.getAppearance().getActualHeight() < other.getYSpriteEnd(true)
                            && object.getY() + object.getCollision().getHeightHalf() > other.getYSpriteBegin(true)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void renderTop(Camera camera) {
        updateNearTopObjects(camera.getArea());
        topObjects.stream().filter((object) -> (object.isVisible() && isObjectInSight(object))).forEach((object) -> object.render());
        for (GameObject see : seeThroughs) {
            renderSeeThrough(see);
        }
    }

    private void renderSeeThrough(GameObject object) {
        Color c = Drawer.getCurrentColor();
        float val = Math.min(Math.min(c.r, c.g), c.b);
        if (object instanceof Entity) {
            Drawer.setColorAlpha(((Entity) object).getColorAlpha() * 0.3f * val);
        } else {
            Drawer.setColorAlpha(0.5f);
        }
        Drawer.regularShader.translate(object.getX(), object.getY() - (int) (object.getFloatHeight()));
        if (object.getAppearance() instanceof ClothedAppearance) {
            object.getAppearance().renderPart(0, object.getAppearance().getWidth());
        } else {
            object.getAppearance().render();
        }
        Drawer.refreshColor();
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
        warps.clear();
        lights.clear();
        blocks.clear();
        tempMobs.clear();
        seeThroughs.clear();
        tempBlocks.clear();
        tempTilePositions.clear();
        tempDepthObjects.clear();
        tempEntities.clear();
        tempInteractiveObjects.clear();
        areasToUpdate.clear();
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

    public AreaConnector[] getAreaConnectors() {
        return areaConnectors;
    }

    public ArrayList<GameObject> getStaticShadows() {
        return staticShadows;
    }

    public double getWindStrength() {
        return windStrength;
    }

    public float getWindDirection() {
        return windDirection;
    }
}

