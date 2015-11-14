/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import engine.utilities.Drawer;
import game.place.Place;
import gamedesigner.ObjectPlace;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectFGTile extends ForegroundTile {


    ObjectFGTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart, boolean round) {
        super(spriteSheet, size, xSheet, ySheet, wall, yStart, round);
    }

    public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, false, yStart, false);
    }

    public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, false, 0, false);
    }

    public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, true, yStart, false);
    }

    public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, true, 0, false);
    }

    public static ForegroundTile createRoundOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, false, yStart, true);
    }

    public static ForegroundTile createRoundOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, false, 0, true);
    }

    public static ForegroundTile createRoundWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, true, yStart, true);
    }

    public static ForegroundTile createRoundWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        return new ObjectFGTile(spriteSheet, size, xSheet, ySheet, true, 0, true);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);

        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

        glTranslatef(getX(), getY(), 0);

        if (tileStack.size() <= 1 || fbo == null) {
            tileStack.stream().forEach((piece) -> spriteSheet.renderPiece(piece.getX(), piece.getY()));
        } else {
            fbo.render();
        }
        if (((ObjectPlace) map.place).getMode() == ObjectPlace.MODE_TILE) {
            glColor4f(1f, 1f, 1f, 0.5f);
            int tile = Place.tileSize;
            Drawer.drawRectangle(tile / 2 - 1, tile, 2, depth - tile / 2);
            Drawer.drawCircle(0, depth - tile / 2, (int) (tile * 0.3), 10);
            Drawer.refreshForRegularDrawing();
        }
        glPopMatrix();

    }
}
