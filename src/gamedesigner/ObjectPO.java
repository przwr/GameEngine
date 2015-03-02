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

    protected final Comparator<BlockContainer> depthComparator = (BlockContainer firstObject, BlockContainer secondObject)
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
        blocks.stream().forEach((block) -> {
            int[] values = block.getValues();
            TemporaryBlock tmp;
            if (block instanceof RoundBlockContainer) {
                tmp = new RoundedTMPBlock(values[0] + x * tileSize,
                        values[1] + y * tileSize,
                        (values[4] + values[3]) / tileSize,
                        values[3] / tileSize,
                        map);
            } else {
                tmp = new TemporaryBlock(values[0] + x * tileSize,
                        values[1] + y * tileSize,
                        (values[4] + values[3]) / tileSize,
                        values[2] / tileSize,
                        values[3] / tileSize,
                        map);
            }
            map.addObject(tmp);
            for (FGTileContainer tile : block.getForegroundTiles()) {
                tmp.addTile(tile.generateFGT(x * tileSize, y * tileSize));
//                ForegroundTile fgt = tmp.addTile(tile.getXBegin() / tileSize + x, tile.getYBegin() / tileSize + y, 
//                        tile.getValues()[1], tile.getValues()[2], tile.getTexture(), true);
//                fgt.setSimpleLighting(!tile.getRound());
            }

            if (block instanceof RoundBlockContainer) {
                int[] corners = ((RoundBlockContainer) block).getCorners();
                for (int i = 0; i < 4; i++) {
                    if (corners[2 * i] + corners[2 * i + 1] != 0) {
                        ((RoundedTMPBlock) tmp).pushCorner(i, corners[2 * i], corners[2 * i + 1]);
                    }
                }
            }
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
