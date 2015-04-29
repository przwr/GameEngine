/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import game.Settings;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author przemek
 */
public class SpriteSheet extends Sprite {

    private final float xTiles;
    private final float yTiles;

    public static SpriteSheet create(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(texture, width, height, xStart, yStart, spriteBase, false);
    }

    public static SpriteSheet createSetScale(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(texture, width, height, xStart, yStart, spriteBase, true);
    }

    protected SpriteSheet(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale) {
        super(texture, width, height, xStart, yStart, spriteBase);
        if (scale) {
            this.xTiles = (int) (texture.getImageWidth() * Settings.nativeScale) / width;
            this.yTiles = (int) (texture.getImageHeight() * Settings.nativeScale) / height;
        } else {
            this.xTiles = texture.getImageWidth() / width;
            this.yTiles = texture.getImageHeight() / height;
        }
    }

    @Override
    public void render() {  //Rysuje CAŁY spritesheet
        bindCheckByID();
        float widthWhole = texture.getImageWidth();
        float heightWhole = texture.getImageHeight();
        glTranslatef(xStart, yStart, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, heightWhole);
        glTexCoord2f(1, 1);
        glVertex2f(widthWhole, heightWhole);
        glTexCoord2f(1, 0);
        glVertex2f(widthWhole, 0);
        glEnd();
    }

    public void renderPiece(int piece) {
        if (isValidPiece(piece)) {
            int x = (int) (piece % xTiles);
            int y = (int) (piece / xTiles);
            renderPiece(x, y);
        }
    }

    public void renderPiece(int x, int y) {
        if (areValidCoordinates(x, y)) {
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiecePart(int id, int xStart, int xEnd) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            renderSpritePiecePart((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePart(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            renderSpritePiecePart((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPieceMirrored(int id) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            renderSpritePieceMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceMirrored(int x, int y) {
        if (areValidCoordinates(x, y)) {
            renderSpritePieceMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiecePartMirrored(int id, int xStart, int xEnd) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            renderSpritePiecePartMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePartMirrored(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            renderSpritePiecePartMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
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

    public int getSize() {
        return (int) (xTiles * yTiles);
    }
}
