/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Rectangle;
import engine.utilities.Point;
import game.place.Place;
import sprites.SpriteSheet;

import static collision.OpticProperties.FULL_SHADOW;
import static collision.OpticProperties.IN_SHADE_NO_SHADOW;

/**
 * @author Wojtek
 */
public class ForegroundTile extends Tile {

    private final int tmpYStart;
    private boolean blockPart, inCollidingPosition;

    ForegroundTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart, boolean round) {
        super(spriteSheet, xSheet, ySheet);
        solid = wall;
        name = "FGTile";
        tmpYStart = yStart;
        int type = wall ? FULL_SHADOW : IN_SHADE_NO_SHADOW;
        setCollision(Rectangle.create(0, yStart, size, size, type, this));
        simpleLighting = !round;
    }

    public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, yStart, false);
    }

    public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, 0, false);
    }

    public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, yStart, false);
    }

    public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, 0, false);
    }

    public static ForegroundTile createRoundOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, yStart, true);
    }

    public static ForegroundTile createRoundOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, 0, true);
    }

    public static ForegroundTile createRoundWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, yStart, true);
    }

    public static ForegroundTile createRoundWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, 0, true);
    }

    @Override
    public ForegroundTile copy() {
        Point first = tileStack.get(0);
        ForegroundTile copy = new ForegroundTile(spriteSheet, Place.tileSize, first.getX(), first.getY(), solid, tmpYStart, !simpleLighting);
        copy.depth = depth;
        for (int i = 1; i < tileStack.size(); i++) {
            copy.tileStack.add(tileStack.get(i));
        }
        return copy;
    }

    //0  1 2 3     4       5    6      7     8          9
    //ft:x:y:depth:texture:wall:yStart:round:TileXSheet:TileYSheet...
    public String saveToString(SpriteSheet s, int xBegin, int yBegin, int tile) {
        String txt = "ft:" + ((getX() - xBegin) / tile) + ":" + ((getY() - yBegin) / tile) + ":" + (depth / (tile / 2)) + ":"
                + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
        txt += ":" + (solid ? "1" : "0") + ":" + (collision.getYStart() / tile) + ":" + (simpleLighting ? 0 : 1);
        txt = tileStack.stream().map((p) -> ":" + p.getX() + ":" + p.getY()).reduce(txt, String::concat);
        return txt;
    }

    //Potrzebne?
    public String saveToStringAsTile(SpriteSheet s, int xBegin, int yBegin, int tile) {
        String txt = "t:" + ((getX() - xBegin) / tile) + ":" + ((getY() - yBegin) / tile) + ":" + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
        txt = tileStack.stream().map((p) -> ":" + p.getX() + ":" + p.getY()).reduce(txt, String::concat);
        return txt;
    }

    public void setBlockPart(boolean blockPart) {
        this.blockPart = blockPart;
    }

    @Override
    public boolean isInBlock() {
        return blockPart;
    }

    public boolean isWall() {
        return collision.isGiveShadow();
    }

    @Override
    public boolean isInCollidingPosition() {
        return inCollidingPosition;
    }

    public void setInCollidingPosition(boolean inCollidingPosition) {
        this.inCollidingPosition = inCollidingPosition;
    }
}
