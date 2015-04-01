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
    
    private Move previous;
    private final ObjectMap map;
    private int tile;
    
    public UndoControl(ObjectMap m) {
        map = m;
        tile = m.getTileSize();
    }
    
    public void tilesWillBeCreated(int x, int y, int xEnd, int yEnd) {
        map.getTile(x, y);
    }
    
    public void tilesWillBeDeleted(int x, int y, int xEnd, int yEnd) {
        
    }
    
    public void undo() {
        previous.undoMove();
        previous = previous.prev;
    }
        
    private void addMove(Move m) {
        m.prev = previous;
        previous = m;
    }
    
    private class TileMove extends Move {
        private ArrayList<Tile> tiles = new ArrayList<>();
        private ArrayList<ForegroundTile> fgtiles = new ArrayList<>();
        
        @Override
        protected void undoMove() {
            if (prev != null) {
                
            }
        }
    }
    
    private abstract class Move {
        Move prev;
        int type;
        
        protected abstract void undoMove();  
    }
}
