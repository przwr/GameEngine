/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import gamedesigner.designerElements.CentralPoint;
import gamedesigner.designerElements.TemporaryBlock;
import collision.Block;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Area;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import gamedesigner.designerElements.PuzzleLink;
import java.util.ArrayList;
import java.util.Iterator;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectMap extends Map {

    public Tile background;
    private boolean isBackground, areTilesVisible, areBlocksVisible;
    private final CentralPoint centralPoint;
    private final ObjectPlace objPlace;
    private final ArrayList<PuzzleLink> links;

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);
        objPlace = (ObjectPlace) place;
        links = new ArrayList<>();

        centralPoint = new CentralPoint(0, 0, objPlace);
        addObject(centralPoint);

        background = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8);
        background.setDepth(-1);
        isBackground = true;
        areTilesVisible = true;
        areBlocksVisible = true;

        switchTiles(background);
    }

    public void setBackground(int xSheet, int ySheet, SpriteSheet texture) {
        background = new Tile(texture, tileSize, xSheet, ySheet);
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
            lowest.setBlocked(!lowest.isBlocked());
        }
    }

    public void setFGTVisibility(boolean visible) {
        for (Area area : areas) {
            area.getForegroundTiles().stream().forEach((foregroundTile) -> {
                foregroundTile.setVisible(visible);
            });
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

    @Override
    public void clear() {
        Tile background = getBackground();
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                setTile(x, y, background);
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

    public ArrayList<TemporaryBlock> getBlock(int x, int y, int width, int height) {
        ArrayList<TemporaryBlock> tmplist = new ArrayList<>();
        for (Area area : areas) {
            for (GameObject go : area.getFlatObjects()) {
                if (go instanceof TemporaryBlock) {
                    TemporaryBlock tb = (TemporaryBlock) go;
                    if (tb.checkCollision(x, y, width, height)) {
                        tmplist.add(tb);
                    }
                }
            }
        }
        return tmplist;
    }

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex, boolean altmode) {
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
                            if (!tmp.isBlocked() && tmp.checkIfContains(x, y) && tmp.getY() > max) {
                                lowest = tmp;
                                max = tmp.getY();
                            }
                        }
                    }
                }
            }
            if (lowest != null) {
                lowest.addTile(x, y, xSheet, ySheet, tex, altmode).getDepth();
                setTile(x, y, getBackground());
//                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                sortObjectsByDepth(foregroundTiles);
            } else if (areTilesVisible) {
                Tile newtile = new Tile(tex, tileSize, xSheet, ySheet);
                setTile(x, y, newtile);
            }
        }
    }

    public Tile deleteTile(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            Point p = tile.popTileFromStack();
            if (p != null) {
                if (tile.tileStackSize() == 0) {
                    setTile(x, y, getBackground());
                }
                return tile;
            }
        } else {
            ForegroundTile fgt;
            TemporaryBlock tmp = null;
            TemporaryBlock max = null;
            int maxDepth = 0;
            for (Area area : areas) {
                for (GameObject tb : area.getTopObjects()) {
                    if (tb instanceof TemporaryBlock) {
                        tmp = (TemporaryBlock) tb;
                        if (!tmp.isBlocked() && tmp.getDepth() > maxDepth
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
                    return fgt;
                }
            }
        }
        setTile(x, y, getBackground());
        return null;
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
        gameObjects.clear();
        for (Area area : areas) {
            gameObjects.addAll(area.getFlatObjects());
        }
        Object[] tmpTab = gameObjects.toArray();
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
            PuzzleLink tmppl = (PuzzleLink) object;
            for (PuzzleLink pl : links) {
                if (tmppl.getX() == pl.getX() && tmppl.getY() == pl.getY()) {
                    return;
                }
            }
            links.add(tmppl);
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
                    -> (fgt.getX() >= xBegin && fgt.getX() <= xEnd && fgt.getY() >= yBegin && fgt.getY() <= yEnd)).forEach((fgt) -> {
                        tmp.add(fgt);
                    });
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
}
