/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Point;
import game.place.Place;
import sprites.SpriteSheet;

import static collision.OpticProperties.FULL_SHADOW;
import static collision.OpticProperties.TRANSPARENT;

/**
 * @author Wojtek
 */
public class ForegroundTile extends Tile {

    private final int tmpYStart;
    private boolean blockPart, inCollidingPosition;

    ForegroundTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int type, int yStart, boolean round, boolean solid) {
        super(spriteSheet, xSheet, ySheet);
        this.setSolid(solid);
        name = "FGTile";
        tmpYStart = yStart;
        setCollision(Rectangle.create(0, yStart, size, size, type, this));
        setSimpleLighting(!round);
    }

    public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, false, false);
    }

    public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, 0, false, false);
    }

    public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, false, true);
    }

    public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, OpticProperties.NO_SHADOW, 0, false, true);
    }

    public static ForegroundTile createRoundOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, true, false);
    }

    public static ForegroundTile createRoundOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, 0, true, false);
    }

    public static ForegroundTile createRoundWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, true, true);
    }

    public static ForegroundTile createRoundWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ForegroundTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, 0, true, true);
    }

    @Override
    public ForegroundTile copy() {
        Point first = tileStack.get(0);
        ForegroundTile copy = new ForegroundTile(spriteSheet, Place.tileSize, first.getX(), first.getY(), getCollision().getType(), tmpYStart,
                !isSimpleLighting(), isSolid());
        copy.depth = depth;
        for (int i = 1; i < tileStack.size(); i++) {
            copy.tileStack.add(tileStack.get(i));
        }
        return copy;
    }

    //0  1 2 3     4       5    6      7     8     9          10
    //ft:x:y:depth:texture:type:yStart:round:solid:TileXSheet:TileYSheet...-
    public String saveToString(SpriteSheet s, int xBegin, int yBegin, int tile) {
        String txt = "ft:" + ((getX() - xBegin) / tile) + ":" + ((getY() - yBegin) / tile) + ":" + (depth / (tile / 2)) + ":"
                + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
        txt += ":" + (collision.getType()) + ":" + (collision.getYStart() / tile) + ":" + (isSimpleLighting() ? 0 : 1) + ":" + (isSolid() ? "1" : "0");
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

    public void setInCollidingPosition(boolean inCollidingPosition) {
        this.inCollidingPosition = inCollidingPosition;
    }

    public void setWidth(int width) {
    }

    public void setHeight(int height) {
    }
}
