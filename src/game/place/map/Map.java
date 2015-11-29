/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Block;
import collision.Figure;
import engine.lights.Light;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.place.Place;
import game.place.cameras.Camera;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static game.place.Place.*;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public abstract class Map {

    protected static final PointContainer NO_SOLUTION = new PointContainer(0);
    protected static Tile tempTile;
    public final Place place;
    protected final int tileSize;
    protected final int widthInTiles, heightInTiles;
    protected final BlueArray<GameObject> pointingArrows = new BlueArray<>();
    protected final String name;
    protected final int width;
    protected final int height;
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
    protected Placement placement;
    protected int xAreas;
    protected int yAreas;
    protected short mobID = 0;
    protected Color lightColor;
    protected List<GameObject> depthObjects, foregroundTiles;
    protected int cameraXStart, cameraYStart, cameraXEnd, cameraYEnd, cameraXOffEffect, cameraYOffEffect;     //Camera's variables for current rendering
    protected Color pointingColor = new Color(0f, 0f, 0f, 0.7f);

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
        for (Area area : areas) {
            tempBlocks.clear();
            for (Block block : getBlocks(areaIndex)) {
                if (isOnArea(block, areaIndex)) {
                    tempBlocks.add(block);
                }
            }
            area.generateNavigationMesh(tempBlocks);
            areaIndex++;
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
        if (area != getAreaIndex(xDestination, yDestination)) {
            return findInDifferentAreas(area, x, y, xStart, yStart, xDestination, yDestination, collision);
        }
        PointContainer solution = areas[area].findPath(xStart - x, yStart - y, xDestination - x, yDestination - y, collision);
        if (solution != null) {
            return solution;
        } else {
            return NO_SOLUTION;
        }
    }

    private PointContainer findInDifferentAreas(int area, int x, int y, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        if (xStart < x + tileSize || xStart > x + xAreaInPixels - tileSize
                || yStart < y + tileSize || yStart > y + yAreaInPixels - tileSize) {
            return NO_SOLUTION;
        } else {
            Point intersection = null;
            if (xDestination > x + xAreaInPixels - tileHalf) {
                if (yDestination > y + yAreaInPixels - tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + yAreaInPixels - tileHalf, x + xAreaInPixels - tileHalf, y + yAreaInPixels - tileHalf);
                    if (intersection == null) {
                        intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                                x + xAreaInPixels - tileHalf, y + yAreaInPixels - tileHalf, x + xAreaInPixels - tileHalf, y + tileHalf);
                    }
                } else if (yDestination < y + tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + xAreaInPixels - tileHalf, y + tileHalf);
                    if (intersection == null) {
                        intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                                x + tileHalf, y + yAreaInPixels - tileHalf, x + xAreaInPixels - tileHalf, y + yAreaInPixels - tileHalf);
                    }
                } else {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + xAreaInPixels - tileHalf, y + tileHalf);
                }
            } else if (xDestination < x + tileHalf) {
                if (yDestination > y + yAreaInPixels - tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + tileHalf, y + yAreaInPixels - tileHalf);
                    if (intersection == null) {
                        intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                                x + tileHalf, y + yAreaInPixels - tileHalf, x + xAreaInPixels - tileHalf, y + yAreaInPixels - tileHalf);
                    }
                } else if (yDestination < y + tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + tileHalf, y + yAreaInPixels - tileHalf);
                    if (intersection == null) {
                        intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                                x + tileHalf, y + tileHalf, x + xAreaInPixels - tileHalf, y + tileHalf);
                    }
                } else {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + tileHalf, y + yAreaInPixels - tileHalf);
                }
            } else {
                if (yDestination > y + yAreaInPixels - tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + yAreaInPixels - tileHalf, x + xAreaInPixels - tileHalf, y + yAreaInPixels - tileHalf);
                } else if (yDestination < y + tileHalf) {
                    intersection = Methods.getTwoLinesIntersection(xStart, yStart, xDestination, yDestination,
                            x + tileHalf, y + tileHalf, x + xAreaInPixels - tileHalf, y + tileHalf);
                }
            }
            if (intersection != null) {
                PointContainer solution = areas[area].findPath(xStart - x, yStart - y, intersection.getX() - x, intersection.getY() - y, collision);
                if (solution != null) {
                    return solution;
                } else {
                    return NO_SOLUTION;
                }
            } else {
                return NO_SOLUTION;
            }
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
                Methods.merge(depthObjects, areas[i].getDepthObjects());
            }
        }
    }

    public void updateNearForegroundTiles(int area, List<GameObject> foregroundTiles) {
        foregroundTiles.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                Methods.merge(foregroundTiles, areas[i].getForegroundTiles());
            }
        }
    }

    private void updateNearTopObjects(int area) {
        topObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length && areas[i] != null) {
                Methods.merge(topObjects, areas[i].getTopObjects());
            }
        }
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
            depthObjects.stream().filter((object) -> object.isToUpdate()).forEach(GameObject::update);
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

    public void addBlock(Block block) {
        areas[getAreaIndex(block.getX(), block.getY())].addBlock(block);
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
            System.out.println("Poza mapą - nie dodaję!");
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
                System.out.println("Nie mogę usunąć - LIPA, chyba, że było przejście między mapami z wczytywaniem. Jak nie dodał, to i nie usunie.");
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
                System.out.println("Nie mogę usunąć - LIPA, chyba, że było przejście między mapami z wczytywaniem. Jak nie dodał, to i nie usunie.");
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
                System.out.println("Nie mogę usunąć - LIPA, chyba, że było przejście między mapami z wczytywaniem. Jak nie dodał, to i nie usunie.");
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
            }
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
        Methods.inSort(depthObjects);
        int y = 0;
        for (GameObject object : areas[camera.getArea()].getNearDepthObjects()) {
            for (; y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < object.getDepth(); y++) {
                if (foregroundTiles.get(y).isVisible() && isObjectInSight(foregroundTiles.get(y))) {
                    foregroundTiles.get(y).render(cameraXOffEffect, cameraYOffEffect);
                }
            }
            if (object.isVisible() && isObjectInSight(object)) {
                if (isBehindForegroundTile(object)) {
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

    private boolean isBehindForegroundTile(GameObject object) {
        for (GameObject tile : foregroundTiles) {
            if (object.getAppearance() != null && tile.getDepth() > object.getDepth()
                    && (tile.getX() / tileSize == object.getX() / tileSize || tile.getX() / tileSize == Methods.roundDouble(object.getX() / tileSize))
                    && (tile.getY() / tileSize == (object.getY() + (object.getCollision().getHeight() - object.getAppearance().getActualHeight()) / 2)
                    / tileSize || tile.getY() / tileSize == Methods.roundDouble((object.getY() + object.getCollision().getHeight() / 2 - object.getAppearance()
                    .getActualHeight() / 2) / tileSize))) {
                return true;
            }
        }
        return false;
    }

    private void renderTop(Camera camera) {
        updateNearTopObjects(camera.getArea());
        topObjects.stream().filter((object) -> (object.isVisible() && isObjectInSight(object))).forEach((object) -> object.render(cameraXOffEffect,
                cameraYOffEffect));
        Methods.inSort(pointingArrows);
        for (GameObject pointer : pointingArrows) {
            renderPointingArrow(pointer);
        }

    }

    private void renderPointingArrow(GameObject object) {
        glPushMatrix();
        glTranslatef(cameraXOffEffect, cameraYOffEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(object.getX(), object.getY() - object.getAppearance().getActualHeight() + object.getCollision().getHeight() / 2, 0);
        Drawer.setColor(pointingColor);
        Drawer.drawEllipseBow(0, 0, 5, 10, 5, -30, 210, 15);
//        Drawer.drawEllipseBow(0, -32, 8, 64, 8, 45, 135, 4);
        Drawer.setColorAlpha(0.7f);
        glScaled(0.5f, 0.5f, 1);
        glTranslatef(0, -object.getAppearance().getActualHeight() / 4 - 12, 0);
        object.getAppearance().render();
        Drawer.refreshColor();
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
            return place.getLightColor();
        }
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
        return ++mobID;
    }
}
