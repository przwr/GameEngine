/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import game.place.ForegroundTile;
import game.place.Tile;
import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public class UndoControl {

    private static final int CREATE_TILE = 0;
    private static final int DELETE_TILE = 1;
    private static final int CREATE_BLOCK = 2;
    private static final int DELETE_BLOCK = 3;
    private static final int MOVE_BLOCK = 4;

    private Move previous = null;
    private final ObjectMap map;
    private final int tile, memory;

    public UndoControl(ObjectMap m, int memory) {
        map = m;
        tile = m.getTileSize();
        this.memory = memory;
    }

    public void setUpTilesUndo(int x, int y, int xEnd, int yEnd) {
        TileMove tm = new TileMove(x, y, xEnd, yEnd);
        tm.tiles = map.getTilesCopies(x, y, xEnd, yEnd);
        tm.fgtiles = map.getFGTilesCopies(x, y, xEnd, yEnd);
        addMove(tm);
    }

    public void setUpBlockUndo(int x, int y, int xEnd, int yEnd) {

    }

    public void undo() {
        if (previous != null) {
            previous.undoMove();
            previous = previous.prev;
        } else {
            map.place.printMessage("There is no move to undo!");
        }
    }

    private void addMove(Move m) {
        m.prev = previous;
        previous = m;
        previous.cutMemory(memory);
    }

    private class TileMove extends Move {

        private Tile[][] tiles;
        private ArrayList<ForegroundTile> fgtiles;
        int x, y, xE, yE;

        public TileMove(int xSt, int ySt, int xEn, int yEn) {
            x = xSt;
            y = ySt;
            xE = xEn;
            yE = yEn;
        }

        @Override
        protected void undoMove() {
            for (int ix = 0; ix < tiles.length; ix++) {
                for (int iy = 0; iy < tiles[0].length; iy++) {
                    map.setTile(x + ix, y + iy, tiles[ix][iy]);
                }
            }
            map.removeFGTiles(x * tile, y * tile, xE * tile, yE * tile);
            for (ForegroundTile fgt : fgtiles) {
                
            }
            map.place.printMessage("Tile move was Undone");
        }
    }

    private abstract class Move {

        Move prev;

        protected abstract void undoMove();

        protected void cutMemory(int mem) {
            if (mem > 0) {
                if (prev != null) {
                    prev.cutMemory(mem - 1);
                }
            } else {
                prev = null;
            }
        }
    }
}
