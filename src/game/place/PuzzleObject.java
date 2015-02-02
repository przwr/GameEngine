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
    private ForeGroundTile[] fgTiles;

    public PuzzleObject(String file) {
        try (BufferedReader wczyt = new BufferedReader(new FileReader("obj/" + file + ".puz"))) {
            String line = wczyt.readLine();
            String[] t = line.split(";");
            //if (t[0].equals("b"))
            wczyt.close();
        } catch (IOException e) {
            Methods.error("File " + file + " not found!\n" + e.getMessage());
        }
    }

    public void placePuzzle(int x, int y, Map map) {
        int ix;
        int iy;
        int col = map.getHeight() / map.getTileSize();
        for (int i = 0; i < bgTiles.length; i++) {
            ix = tilePlace[i].getX() + x;
            iy = tilePlace[i].getY() + y;
            map.setTile(ix + iy * col, bgTiles[i]);
        }
        for (GameObject obj : objects) {
            map.addObject(obj);
        }
        for (ForeGroundTile tile : fgTiles) {
            map.addForegroundTile(tile);
        }
    }
}
