/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Point;
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

    private final boolean isStartMoving;
    private int frame;
    private Point[] startingPoints;

    public static SpriteSheet create(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(texture, width, height, xStart, yStart, spriteBase, false);
    }

    public static SpriteSheet createWithMovingStart(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase, Point[] stPoints) {
        return new SpriteSheet(texture, width, height, xStart, yStart, spriteBase, false, stPoints);
    }
    
    public static SpriteSheet createSetScale(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(texture, width, height, xStart, yStart, spriteBase, true);
    }

    protected SpriteSheet(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale) {
        super(texture, width, height, xStart, yStart, spriteBase);
        isStartMoving = false;
        if (scale) {
            this.xTiles = (int) (texture.getImageWidth() * Settings.nativeScale) / width;
            this.yTiles = (int) (texture.getImageHeight() * Settings.nativeScale) / height;
        } else {
            this.xTiles = texture.getImageWidth() / width;
            this.yTiles = texture.getImageHeight() / height;
        }
    }

    protected SpriteSheet(Texture texture, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale, Point[] startingPoints) {
        super(texture, width, height, xStart, yStart, spriteBase);
        this.startingPoints = startingPoints;
        isStartMoving = true;
        if (scale) {
            this.xTiles = (int) (texture.getImageWidth() * Settings.nativeScale) / width;
            this.yTiles = (int) (texture.getImageHeight() * Settings.nativeScale) / height;
        } else {
            this.xTiles = texture.getImageWidth() / width;
            this.yTiles = texture.getImageHeight() / height;
        }
    }

    @Override
    protected void moveToStart() {
        if (!isStartMoving) {
            glTranslatef(xStart, yStart, 0);
        } else {
            //System.out.println(startingPoints[frame]);
            frame = Math.min(frame, startingPoints.length - 1);
            glTranslatef(xStart + startingPoints[frame].getX(), yStart + startingPoints[frame].getY(), 0);
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
            frame = piece;
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiece(int x, int y) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceHere(int piece) {
        if (isValidPiece(piece)) {
            int x = (int) (piece % xTiles);
            int y = (int) (piece / xTiles);
            frame = piece;
            renderSpritePieceHere((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceHere(int x, int y) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePieceHere((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiecePart(int id, int xStart, int xEnd) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePiecePart((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePart(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePiecePart((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPieceMirrored(int id) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = id;
            renderSpritePieceMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceMirrored(int x, int y) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePieceMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiecePartMirrored(int id, int xStart, int xEnd) {
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = id;
            renderSpritePiecePartMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePartMirrored(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePiecePartMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    private boolean isValidPiece(int piece) {
        return piece <= xTiles * yTiles;
    }

    private boolean areValidCoordinates(int x, int y) {
        return !(x > xTiles || y > yTiles);
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
