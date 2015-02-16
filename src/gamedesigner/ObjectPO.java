/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Point;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.PuzzleObject;
import java.util.Comparator;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectPO extends PuzzleObject {

    protected final Comparator<AreaContainer> depthComparator = (AreaContainer firstObject, AreaContainer secondObject)
            -> firstObject.getY() - secondObject.getY();

    public ObjectPO(String file, Place place) {
        super(file, place);
    }

    @Override
    public void placePuzzle(int x, int y, Map map) {
        int tileSize = map.getTileSize();
        ObjectMap objMap = (ObjectMap) map;
        objMap.setCentralPoint(xDelta, yDelta);
        bgTiles.stream().forEach((TileContainer tc) -> {
            tc.getPlaces().stream().forEach((p) -> {
                map.setTile(p.getX() + x, p.getY() + y, tc.getTile());
            });
        });
        areas.stream().forEach((area) -> {
            int[] values = area.getValues();
            map.addObject(new TemporaryBlock(values[0] + x * tileSize,
                    values[1] + y * tileSize,
                    (values[4] + values[3]) / tileSize,
                    values[2] / tileSize,
                    values[3] / tileSize,
                    map,
                    map.place));
        });
        objects.stream().forEach((obj) -> {
            map.addObject(obj);
        });
        fgTiles.stream().forEach((FGTileContainer tile) -> {
            ForegroundTile tmp = tile.generateFGT(x * tileSize, y * tileSize);
            SpriteSheet tex = tmp.getSpriteSheet();
            Point p;
            int xStart = tmp.getX() / tileSize;
            int yStart = tmp.getY() / tileSize;
            while ((p = tmp.popTileFromStackBack()) != null) {
                objMap.addTile(xStart, yStart, p.getX(), p.getY(), tex);
            }
        });
    }
}
