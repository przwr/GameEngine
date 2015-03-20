/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.generators;

import engine.Methods;
import engine.Point;
import game.place.Map;
import game.place.PuzzleObject;
import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public class DungeonGenerator {

    private final Map map;
    private final ArrayList<Exit> exits;
    private final ArrayList<Puzzle> puzzles;
    private final int tile, width, height, border;

    public DungeonGenerator(Map map, int border) {
        this.map = map;
        exits = new ArrayList<>();
        puzzles = new ArrayList<>();
        tile = map.getTileSize();
        width = map.getWidth();
        height = map.getHeight();
        this.border = border;
    }

    public void addPuzzleRandomly(PuzzleObject puz, int chance) {
        Puzzle p = new Puzzle(puz);
        p.chance = chance;
        puzzles.add(p);
    }
    
    public void addPuzzleAmount(PuzzleObject puz, int amount) {
        Puzzle p = new Puzzle(puz);
        p.amount = amount;
        puzzles.add(p);
    }
    
    public void addPuzzleSpecific(PuzzleObject puz, int angle, int percentRadius) {
        Puzzle p = new Puzzle(puz);
        Point point = getSquareRadius(angle, percentRadius);
        p.x = point.getX();
        p.y = point.getY();
        puzzles.add(p);
    }

    public void addExit(int angle, int percentRadius, String link) { //angle - ktora strona planszy, rad - jak daleko od srodka (100 - na brzegu, 0 - w centrum)
        Point p = getSquareRadius(angle, percentRadius);
        exits.add(new Exit(p.getX(), p.getY(), link));
    }

    private Point getSquareRadius(int angle, int percentRadius) {
        double percent = (double) Methods.interval(0, percentRadius, 100) / 100;
        int x, y;
        if ((angle > 45 && angle <= 135) || (angle > 225 && angle <= 315)) {
            x = (width / 2 + (int) (Methods.xRadius(angle, width / 2 - border * tile) * percent)) / tile;
            y = (height / 2 - (int) Math.signum(Methods.xRadius(angle, 1)) * (height / 2 - border * tile)) / tile;
        } else {
            y = (height / 2 + (int) (Methods.xRadius(angle, height / 2 - border * tile) * percent)) / tile;
            x = (width / 2 - (int) Math.signum(Methods.xRadius(angle, 1)) * (width / 2 - border * tile)) / tile;
        }
        return new Point(x, y);
    }
    
    private class Puzzle {

        PuzzleObject puzzle;
        Point[] links;
        int chance = 0;
        int amount = 0;
        int x = -1;
        int y = -1;

        Puzzle(PuzzleObject puzzle) {
            this.puzzle = puzzle;
        }
    }

    private class Exit {

        int x;
        int y;
        String link;

        Exit(int x, int y, String link) {
            this.x = x;
            this.y = y;
            this.link = link;
        }
    }
}
