/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Point;
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
    private final int memory;

    public UndoControl(ObjectMap m, int memory) {
        map = m;
        this.memory = memory;
    }

    public void setUpUndo() {
        Move tm = new Move(map.saveMap());
        addMove(tm);
    }

    public void undo() {
        if (previous != null) {
            previous.undoMove();
            previous = previous.prev;
            map.place.printMessage("Move was undone");
        } else {
            map.place.printMessage("There is no move to undo!");
        }
    }
    
    public void removeMoves() {
        previous = null;
    }

    private void addMove(Move m) {
        m.prev = previous;
        previous = m;
        previous.cutMemory(memory);
    }

    private class Move {

        Move prev;
        ArrayList<String> mapCopy;

        Move(ArrayList<String> mapCopy) {
            this.mapCopy = mapCopy;
        }
        
        protected void undoMove() {
            ObjectPO po = new ObjectPO(mapCopy, map.place);
            map.clear();
            Point p = po.getStartingPoint();
            po.placePuzzle(p.getX(), p.getY(), map);
            mapCopy = null;
        }

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
