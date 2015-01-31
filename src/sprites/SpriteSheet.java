/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author przemek
 */
public class SpriteSheet extends Sprite {

    private final float xTiles;
    private final float yTiles;

    public SpriteSheet(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        super(texture, (int) (width * spriteBase.getScale()), (int) (height * spriteBase.getScale()), xStart, yStart, spriteBase);
        this.xTiles = texture.getImageWidth() / width;
        this.yTiles = texture.getImageHeight() / height;
    }

    public void render(int piece) {
        if (isValidPiece(piece)) {
            int x = (int) (piece % xTiles);
            int y = (int) (piece / xTiles);
            render(x, y);
        }
    }

    public void render(int x, int y) {
        if (areValidCoordinates(x, y)) {
            renderPieceNotBind((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderSpriteSheetPart(int id, int xStart, int xEnd) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            renderPiecePartNotBind((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderSpriteSheetMirrored(int id) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            renderPieceMirroredNotBind((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    private boolean isValidPiece(int piece) {
        if (piece > xTiles * yTiles) {
            return false;
        }
        return true;
    }

    private boolean areValidCoordinates(int x, int y) {
        if (x > xTiles || y > yTiles) {
            return false;
        }
        return true;
    }

    public int getXlimit() {
        return (int) xTiles;
    }

    public int getYlimit() {
        return (int) yTiles;
    }

    public int getLenght() {
        return (int) (xTiles * yTiles);
    }
}
