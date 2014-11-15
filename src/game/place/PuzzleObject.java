/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Wojtek
 */
public class PuzzleObject {

    private Tile[] bgTiles;
    private Point[] tilePlace;
    private GameObject[] objects;
    private FGTile[] fgTiles;

    public PuzzleObject(String file) {
        try (BufferedReader wczyt = new BufferedReader(new FileReader("obj/" + file + ".puz"))) {
            String line = wczyt.readLine();
            String[] t = line.split(";");
            //if (t[0].equals("b"))
        } catch (IOException e) {
            Methods.Error("File " + file + " not found!\n" + e.getMessage());
        }
    }
    
    public void placePuzzle(int x, int y, Place p) {
        int ix;
        int iy;
        int col = p.height / p.sTile;
        for (int i = 0; i < bgTiles.length; i++) {
            ix = tilePlace[i].getX() + x;
            iy = tilePlace[i].getY() + y;
            p.tiles[ix + iy * col] = bgTiles[i];
        }
        for (GameObject object : objects) {
            p.addObj(object);
        }
        for (FGTile tile : fgTiles) {
            p.addFGTile(tile);
        }
    }
}
