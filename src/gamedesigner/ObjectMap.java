/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Block;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.map.*;
import gamedesigner.designerElements.CentralPoint;
import gamedesigner.designerElements.PuzzleLink;
import gamedesigner.designerElements.TemporaryBlock;
import sprites.SpriteSheet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Wojtek
 */
public class ObjectMap extends Map {

    private final CentralPoint centralPoint;
    private final ObjectPlace objPlace;
    private final ArrayList<PuzzleLink> links;
    private Tile background;
    private boolean isBackground, areTilesVisible, areBlocksVisible;

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);
        objPlace = (ObjectPlace) place;
        links = new ArrayList<>();

        background = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 1, 8);
        background.setDepth(-1);
        isBackground = true;
        areTilesVisible = true;
        areBlocksVisible = true;

        switchTiles(background);

        centralPoint = new CentralPoint(0, 0, objPlace);
        addObject(centralPoint);
    }

    public void setBackground(int xSheet, int ySheet, SpriteSheet texture) {
        background = new Tile(texture, xSheet, ySheet);
        background.setDepth(-1);
        switchTiles(background);
    }

    private Tile getBackground() {
        return isBackground ? background : null;
    }

    public void switchBackground() {
        Tile tmp = null;
        if (!isBackground) {
            tmp = background;
        }
        switchTiles(tmp);
        isBackground = !isBackground;
    }

    public void setBlocksVisibility(boolean visible) {
        areBlocksVisible = visible;
        for (Area area : areas) {
            area.getTopObjects().stream().forEach((b) -> {
                if (b instanceof TemporaryBlock) {
                    ((TemporaryBlock) b).setBlocked(!visible);
                }
            });
        }
    }

    public void changeBlockUsability(int x, int y) {
        TemporaryBlock lowest = null;
        int max = 0;
        if (areBlocksVisible) {
            for (Area area : areas) {
                for (GameObject tb : area.getTopObjects()) {
                    if (tb instanceof TemporaryBlock) {
                        TemporaryBlock tmp = (TemporaryBlock) tb;
                        if (tmp.checkIfBaseContains(x, y) && tmp.getY() > max) {
                            lowest = tmp;
                            max = tmp.getY();
                        }
                    }
                }
            }
        }
        if (lowest != null) {
            lowest.setBlocked(lowest.isNotBlocked());
        }
    }

    public void setFGTVisibility(boolean visible) {
        for (Area area : areas) {
            area.getForegroundTiles().stream().forEach((foregroundTile) -> foregroundTile.setVisible(visible));
        }
    }

    public void setTilesVisibility(boolean visible) {
        areTilesVisible = visible;
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                Tile tempTile = getTile(x, y);
                if (tempTile != null && tempTile.getPureDepth() != -1) {
                    tempTile.setVisible(visible);
                }
            }
        }
    }

    private void switchTiles(Tile background) {
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                Tile tempTile = getTile(x, y);
                if (tempTile == null || tempTile.getPureDepth() == -1) {
                    setTile(x, y, background);
                }
            }
        }
    }

    public ArrayList<TemporaryBlock> getBlock(int x, int y, int width, int height) {
        ArrayList<TemporaryBlock> tmpList = new ArrayList<>();
        for (Area area : areas) {
            for (GameObject go : area.getFlatObjects()) {
                if (go instanceof TemporaryBlock) {
                    TemporaryBlock tb = (TemporaryBlock) go;
                    if (tb.checkCollision(x, y, width, height)) {
                        tmpList.add(tb);
                    }
                }
            }
        }
        return tmpList;
    }

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex, boolean altMode) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            tile = tile.copy();
            setTile(x, y, tile);
            if (areTilesVisible) {
                tile.addTileToStack(xSheet, ySheet);
            }
        } else {
            TemporaryBlock lowest = null;
            int max = 0;
            if (areBlocksVisible) {
                for (Area area : areas) {
                    for (GameObject tb : area.getTopObjects()) {
                        if (tb instanceof TemporaryBlock) {
                            TemporaryBlock tmp = (TemporaryBlock) tb;
                            if (tmp.isNotBlocked() && tmp.checkIfContains(x, y) && tmp.getY() > max) {
                                lowest = tmp;
                                max = tmp.getY();
                            }
                        }
                    }
                }
            }
            if (lowest != null) {
                lowest.addTile(x, y, xSheet, ySheet, tex, altMode).getDepth();
                setTile(x, y, getBackground());
//                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                sortObjectsByDepth(foregroundTiles);
            } else if (areTilesVisible) {
                Tile newTile = new Tile(tex, xSheet, ySheet);
                setTile(x, y, newTile);
            }
        }
    }

    public void addFGTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex, int depth, boolean altMode) {
        ForegroundTile lowest = null;
        depth *= tileSize;
        x *= tileSize;
        y *= tileSize;
        ForegroundTile fg;
        if (areTilesVisible) {
            for (Area area : areas) {
                for (GameObject o : area.getForegroundTiles()) {
                    fg = (ForegroundTile) o;
                    if (fg.getPureDepth() == depth && fg.getX() == x && fg.getY() == y) {
                        lowest = fg;
                        break;
                    }
                }
                if (lowest != null) {
                    break;
                }
            }
        }
        if (lowest != null) {
            lowest.addTileToStack(xSheet, ySheet);
        } else {
            ForegroundTile newTile = ForegroundTile.createOrdinaryShadowHeight(tex, tileSize, xSheet, ySheet, -depth /*TODO SOMETHING!!!*/);
            addForegroundTile(newTile, x, y, depth);
        }
    }

    public void deleteFGTile(int x, int y, int depth) {
        ForegroundTile lowest = null;
        depth *= tileSize;
        x *= tileSize;
        y *= tileSize;
        ForegroundTile fg;
        if (areTilesVisible) {
            for (Area area : areas) {
                for (GameObject o : area.getForegroundTiles()) {
                    fg = (ForegroundTile) o;
                    if (fg.getPureDepth() == depth && fg.getX() == x && fg.getY() == y && fg.getGravity() != 10) {
                        lowest = fg;
                        break;
                    }
                }
                if (lowest != null) {
                    break;
                }
            }
        }
        if (lowest != null) {
            System.out.print(lowest);
            System.out.println("removed in " + x + " " + y + " fg: " + lowest.getX() + " " + lowest.getY());
//            lowest.setGravity(10);
            removeForegroundTile(lowest);
        }
    }

    public void deleteTile(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            Point p = tile.popTileFromStack();
            if (p != null) {
                if (tile.tileStackSize() == 0) {
                    setTile(x, y, getBackground());
                }
                return;
            }
        } else {
            ForegroundTile fgt;
            TemporaryBlock tmp;
            TemporaryBlock max = null;
            int maxDepth = 0;
            for (Area area : areas) {
                for (GameObject tb : area.getTopObjects()) {
                    if (tb instanceof TemporaryBlock) {
                        tmp = (TemporaryBlock) tb;
                        if (tmp.isNotBlocked() && tmp.getDepth() > maxDepth
                                && tmp.checkIfContainsTile(x, y)) {
                            max = tmp;
                            maxDepth = tmp.getDepth();
                        }
                    }
                }
            }
            if (max != null) {
                if ((fgt = max.removeTile(x, y)) != null) {
                    removeForegroundTile(fgt);
                    max.getBlock().removeForegroundTile(fgt);
                    return;
                }
            }
        }
        setTile(x, y, getBackground());
    }

    public void removeFGTiles(int xSt, int ySt, int xEn, int yEn) {
        ForegroundTile fgt;
        for (Area area : areas) {
            for (int i = 0; i < area.getForegroundTiles().size(); i++) {
                fgt = (ForegroundTile) area.getForegroundTile(i);
                if ((fgt.getX() >= xSt && fgt.getX() <= ySt && fgt.getY() >= xEn && fgt.getY() <= yEn)) {
                    area.setForegroundTiles(i, null);
                }
            }
        }
    }

    public boolean checkBlockCollision(int x, int y, int width, int height) {
        for (Area area : areas) {
            for (GameObject go : area.getFlatObjects()) {
                if (go instanceof TemporaryBlock) {
                    TemporaryBlock tb = (TemporaryBlock) go;
                    if (tb.checkCollision(x, y, width, height)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void deleteBlocks(int x, int y, int width, int height) {
        foregroundTiles.clear();
        for (Area area : areas) {
            foregroundTiles.addAll(area.getFlatObjects());
        }
        Object[] tmpTab = foregroundTiles.toArray();
        for (Object go : tmpTab) {
            if (go instanceof TemporaryBlock) {
                TemporaryBlock tb = (TemporaryBlock) go;
                if (tb.checkCollision(x, y, width, height)) {
                    tb.decompose();
                    deleteObject((GameObject) go);
                }
            }
        }
    }

    public void setCentralPoint(int x, int y) {
        centralPoint.setCentralPoint(x, y);
    }

    public void addObject(GameObject object, boolean altMode) {
        if (object instanceof TemporaryBlock) {
            if (!altMode) {
                ((TemporaryBlock) object).changeEnvironment();
            }
        }
        if (object instanceof PuzzleLink) {
            PuzzleLink tmpPl = (PuzzleLink) object;
            for (PuzzleLink pl : links) {
                if (tmpPl.getX() == pl.getX() && tmpPl.getY() == pl.getY()) {
                    return;
                }
            }
            links.add(tmpPl);
        }
        super.addObject(object);
    }

    public void deleteLink(int x, int y) {
        for (PuzzleLink pl : links) {
            if (pl.getX() == x && pl.getY() == y) {
                links.remove(pl);
                deleteObject(pl);
                return;
            }
        }
    }

    public Tile[][] getTilesCopies(int xSt, int ySt, int xEnd, int yEnd) {
        Tile[][] tmp = new Tile[xEnd - xSt + 1][yEnd - ySt + 1];
        for (int x = xSt; x <= xEnd; x++) {
            for (int y = ySt; y <= yEnd; y++) {
                tmp[x - xSt][y - ySt] = getTile(x, y).copy();
            }
        }
        return tmp;
    }

    public ArrayList<ForegroundTile> getFGTilesCopies(int xISt, int yISt, int xIStop, int yIStop) {
        ArrayList<ForegroundTile> tmp = new ArrayList<>();
        int xBegin = xISt * tileSize;
        int yBegin = xISt * tileSize;
        int xEnd = xIStop * tileSize;
        int yEnd = xIStop * tileSize;

        for (Area area : areas) {
            area.getForegroundTiles().stream().map((go) -> (ForegroundTile) go).filter((fgt)
                    -> (fgt.getX() >= xBegin && fgt.getX() <= xEnd && fgt.getY() >= yBegin && fgt.getY() <= yEnd)).forEach(tmp::add);
        }
        return tmp;
    }

    public ArrayList<String> saveMap() {
        ArrayList<String> map = new ArrayList<>();
        SpriteSheet repeated = null;
        Point center = centralPoint.getCentralPoint();
        map.add(center.getX() + ":" + center.getY());
        for (int x = 0; x < widthInTiles; x++) {
            for (int y = 0; y < heightInTiles; y++) {
                Tile t = getTile(x, y);
                if (t != null && t.getPureDepth() != -1) {
                    map.add(t.saveToString(repeated, x, y, center.getX(), center.getY()));
                    repeated = t.getSpriteSheet();
                }
            }
        }
        int xActBegin = center.getX() * tileSize;
        int yActBegin = center.getY() * tileSize;

        for (Area area : areas) {
            for (GameObject foregroundTile : area.getForegroundTiles()) {
                ForegroundTile fgt = (ForegroundTile) foregroundTile;
                if (!fgt.isInBlock()) {
                    map.add(fgt.saveToString(repeated, xActBegin, yActBegin, tileSize));
                    repeated = fgt.getSpriteSheet();
                }
            }

            for (Block a : area.getBlocks()) {
                map.add(a.saveToString(xActBegin, yActBegin, tileSize));
                for (ForegroundTile fgt : a.getTopForegroundTiles()) {
                    map.add(fgt.saveToString(repeated, xActBegin, yActBegin, tileSize));
                    repeated = fgt.getSpriteSheet();
                }
                for (ForegroundTile fgt : a.getWallForegroundTiles()) {
                    map.add(fgt.saveToString(repeated, xActBegin, yActBegin, tileSize));
                    repeated = fgt.getSpriteSheet();
                }
            }
        }

        String linking = "pl";
        for (PuzzleLink pl : links) {
            linking += ":" + pl.saveToString(xActBegin, yActBegin);
        }
        map.add(linking);
        return map;
    }

    @Override
    public void clear() {
        Tile bg = getBackground();
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                setTile(x, y, bg);
            }
        }
        for (Area area : areas) {
            area.clear();
        }
        GameObject object;
        for (Area area : areas) {
            for (Iterator<GameObject> iterator = area.getTopObjects().iterator(); iterator.hasNext();) {
                object = iterator.next();
                if (object instanceof TemporaryBlock) {
                    ((TemporaryBlock) object).clear();
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void createAreas() {
        xAreas = 1;
        yAreas = 1;
        areas = new Area[1];
        areas[0] = new Area(place, this, width, height, widthInTiles * heightInTiles);
        placement = new Placement(this);
    }

//    @Override
//    protected int getAreaIndexCoordinatesInTiles(int x, int y) {
//        return 0;
//    }
//
//    @Override
//    protected int getXInArea(int x) {
//        return x;
//    }
//
//    @Override
//    protected int getYInArea(int y) {
//        return y;
//    }
//
//    @Override
//    public int getAreaIndex(int x, int y) {
//        return 0;
//    }
    @Override
    protected void renderArea(int i) {
        for (int yTiles = 0; yTiles < heightInTiles; yTiles++) {
            if (cameraYStart < (yTiles + 1) * tileSize && cameraYEnd > yTiles * tileSize) {
                for (int xTiles = 0; xTiles < widthInTiles; xTiles++) {
                    if (cameraXStart < (xTiles + 1) * tileSize && cameraXEnd > xTiles * tileSize) {
                        tempTile = areas[i].getTile(xTiles, yTiles);
                        if (tempTile != null && tempTile.isVisible()) {
                            tempTile.renderSpecific(cameraXOffEffect, cameraYOffEffect, xTiles * tileSize, yTiles * tileSize);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void populate() {
    }
}
