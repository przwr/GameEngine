/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Block;
import collision.OpticProperties;
import collision.Rectangle;
import collision.RoundRectangle;
import engine.Point;
import game.gameobject.LightSource;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.PuzzleObject;
import game.place.Tile;
import game.place.WarpPoint;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

/**
 *
 * @author Wojtek
 */
public class GladeMap extends Map {

    public GladeMap(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Polana", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = GRASS;
            }
        }

        PuzzleObject test = new PuzzleObject("test", place);
        test.placePuzzle(3, 14, this);
        test = new PuzzleObject("HighWalls", place);
        test.placePuzzle(17, 5, this);

        ForegroundTile fg;

        {
            Block block1 = Block.createRound(8 * tileSize, 8 * tileSize, 1 * tileSize, 1 * tileSize, 0);
            fg = ForegroundTile.createRoundWall(place.getSpriteSheet("testy"), tileSize, 0, 3);
            block1.addForegroundTile(fg);
            addForegroundTile(fg, 8 * tileSize, 8 * tileSize, 0);
            fg = ForegroundTile.createRoundWall(place.getSpriteSheet("testy"), tileSize, 5, 2);
            block1.addForegroundTile(fg);
            fg.setSolid(false);
            addForegroundTile(fg, 8 * tileSize, 7 * tileSize, tileSize);
            fg = ForegroundTile.createRoundOrdinaryShadowHeight(place.getSpriteSheet("testy"), tileSize, 0, 2, tileSize);
            block1.addForegroundTile(fg);
            addForegroundTile(fg, 8 * tileSize, 7 * tileSize, tileSize);
            block1.pushCorner(RoundRectangle.LEFT_BOTTOM, tileSize, 18, 18);
            areas.add(block1);
        }    
        
        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, OpticProperties.IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 19 * tileSize));
        PuzzleObject portal = new PuzzleObject("portal", place);
        portal.placePuzzle(20, 20, this);
        for (int i = 0; i < 1; i++) {
            addObject(new MyMob(192 + 192 * (i % 50), 2048 + 192 * (i / 50), 0, 8, 128, 112, 4, 512, "rabbit", place, true, mobID++));
        }
        addObject(new LightSource(2048, 1536, 0, 0, 206, 300, "lamp", place, "lamp", true));
    }
}
