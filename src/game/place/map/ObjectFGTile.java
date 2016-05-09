/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import engine.utilities.Drawer;
import engine.utilities.Point;
import game.place.Place;
import gamedesigner.ObjectPlace;
import gamedesigner.ObjectPlayer;
import sprites.SpriteSheet;

import static collision.OpticProperties.FULL_SHADOW;
import static collision.OpticProperties.TRANSPARENT;

/**
 * @author Wojtek
 */
public class ObjectFGTile extends ForegroundTile {

    ObjectFGTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int type, int yStart, boolean round, boolean solid) {
        super(spriteSheet, size, xSheet, ySheet, type, yStart, round, solid);
    }

    public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, false, false);
    }

    public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, 0, false, false);
    }

    public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, false, true);
    }

    public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, 0, false, true);
    }

    public static ForegroundTile createRoundOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, true, false);
    }

    public static ForegroundTile createRoundOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, 0, true, false);
    }

    public static ForegroundTile createRoundWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, true, true);
    }

    public static ForegroundTile createRoundWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, 0, true, true);
    }

    public static ForegroundTile createShadowTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, Integer.MAX_VALUE, true, false);
    }

    @Override
    public int getYSpriteEnd(boolean... forCover) {
        return super.getYSpriteEnd(forCover) + depth + Place.tileSize / 2;
    }

    @Override
    public void render() {
        if (map != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            if (!isSimpleLighting()) {

            }
            for (Point piece : tileStack) {
                spriteSheet.renderPiece(piece.getX(), piece.getY());
            }
            if (((ObjectPlace) map.place).getMode() == ObjectPlace.MODE_TILE) {
                if (ObjectPlayer.currectDepth == depth) {
                    Drawer.setColorStatic(1f, 0f, 0f, 0.5f);
                } else {
                    Drawer.setColorStatic(1f, 1f, 1f, 0.5f);
                }
                int tile = Place.tileSize;
                Drawer.drawRectangle(tile / 2 - 1, tile, 2, depth - tile / 2);
                Drawer.drawCircle(tile / 2 - 1, depth + tile / 2, (int) (tile * 0.3), 10);
                Drawer.refreshForRegularDrawing();
            }
        }
    }
}
