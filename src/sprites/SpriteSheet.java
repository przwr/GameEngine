/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.utilities.Drawer;
import engine.utilities.Point;
import engine.utilities.PointedValue;
import game.Settings;
import org.newdawn.slick.opengl.Texture;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class SpriteSheet extends Sprite {

    private final boolean isStartMoving, scale;
    private float xTiles;
    private float yTiles;
    private int frame;
    private PointedValue[] startingPoints;

    private SpriteSheet(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale) {
        super(path, folder, width, height, xStart, yStart, spriteBase);
        isStartMoving = false;
        this.scale = scale;
        setTilesCount(scale);
    }

    private SpriteSheet(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale, PointedValue[]
            startingPoints) {
        super(path, folder, width, height, xStart, yStart, spriteBase);
        this.startingPoints = startingPoints;
        isStartMoving = true;
        this.scale = scale;
        setTilesCount(scale);
    }

    public static SpriteSheet create(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(path, folder, width, height, xStart, yStart, spriteBase, false);
    }

    public static SpriteSheet createWithMovingStart(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase,
                                                    PointedValue[] stPoints) {
        return new SpriteSheet(path, folder, width, height, xStart, yStart, spriteBase, false, stPoints);
    }

    public static SpriteSheet createSetScale(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase) {
        return new SpriteSheet(path, folder, width, height, xStart, yStart, spriteBase, true);
    }


    public static Point[] getMergedDimensions(SpriteSheet... list) {
        int xB = Integer.MAX_VALUE, yB = Integer.MAX_VALUE,
                xE = 0, yE = 0, tmpXS, tmpYS, tmpXE, tmpYE;
        for (SpriteSheet s : list) {
            if (s != null) {
                tmpXS = s.xStart + s.xOffset;
                tmpYS = s.yStart + s.yOffset;
                tmpXE = tmpXS + s.actualWidth;
                tmpYE = tmpYS + s.actualHeight;
                if (tmpXS < xB) {
                    xB = tmpXS;
                }
                if (tmpYS < yB) {
                    yB = tmpYS;
                }
                if (tmpXE > xE) {
                    xE = tmpXE;
                }
                if (tmpYE > yE) {
                    yE = tmpYE;
                }
            }
        }
        return new Point[]{new Point(xE - xB, yE - yB), new Point(xB, yB)};
    }

    private void setTilesCount(boolean scale) {
        if (scale) {
            this.xTiles = (int) (widthWhole * Settings.nativeScale) / width;
            this.yTiles = (int) (heightWhole * Settings.nativeScale) / height;
        } else {
            this.xTiles = widthWhole / width;
            this.yTiles = heightWhole / height;
        }
    }

    @Override
    protected void moveToStart() {
        if (!isStartMoving) {
            if (xStart != 0 && yStart != 0) {
                Drawer.translate(xStart, yStart);
            }
        } else {
            frame = Math.min(frame, startingPoints.length - 1);
            if (startingPoints[frame] != null) {
                Drawer.translate(xStart + startingPoints[frame].getX(), yStart + startingPoints[frame].getY());
            }
        }
    }

    @Override
    public void render() {  //Rysuje CAŁY spriteSheet
        bindCheck();
        glTranslatef(xStart, yStart, 0);
        glBegin(GL_TRIANGLES);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, heightWhole);
        glTexCoord2f(1, 1);
        glVertex2f(widthWhole, heightWhole);

        glTexCoord2f(1, 1);
        glVertex2f(widthWhole, heightWhole);
        glTexCoord2f(1, 0);
        glVertex2f(widthWhole, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glEnd();
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }

    public void returnFromTranslation(int frame) {
        if (!isStartMoving) {
            if (xStart != 0 && yStart != 0) {
                Drawer.translate(-xStart, -yStart);
            }
        } else {
            if (startingPoints[frame] != null) {
                Drawer.translate(-xStart - startingPoints[frame].getX(), -yStart - startingPoints[frame].getY());
            }
        }
    }

    private int getFramesPosition(int frame) {
        return isStartMoving ? (startingPoints[frame] != null ? startingPoints[frame].getValue() : -1) : frame;
    }

    public void renderPiece(int piece) {
        frame = piece;
        piece = getFramesPosition(piece);
        if (isValidPiece(piece)) {
            int x = (int) (piece % xTiles);
            int y = (int) (piece / xTiles);
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPiece(int x, int y) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceResized(int x, int y, float width, float height) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            renderSpritePieceResized((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, width, height);
        }
    }

    public void renderPieceHere(int piece) {
        frame = piece;
        piece = getFramesPosition(piece);
        if (isValidPiece(piece)) {
            int x = (int) (piece % xTiles);
            int y = (int) (piece / xTiles);
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

        id = getFramesPosition(id);
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            renderSpritePiecePart((float) x / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePart(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            frame = (int) (x + y * xTiles);
            renderSpritePiecePart((float) x / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPieceMirrored(int id) {
        id = getFramesPosition(id);
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
        id = getFramesPosition(id);
        int x = (int) (id % xTiles);
        int y = (int) (id / xTiles);

        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = id;
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            renderSpritePiecePartMirrored((float) x / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPiecePartMirrored(int x, int y, int xStart, int xEnd) {
        if (areValidCoordinates(x, y)) {
            frame = (int) (x + y * xTiles);
            if (xStart > xEnd) {
                int temp = xStart;
                xStart = xEnd;
                xEnd = temp;
            }
            renderSpritePiecePartMirrored((float) x / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    private boolean isValidPiece(int piece) {
        return piece >= 0 && piece <= xTiles * yTiles;
    }

    private boolean areValidCoordinates(int x, int y) {
        return !(x > xTiles || y > yTiles);
    }

    public int getXLimit() {
        return (int) xTiles;
    }

    public int getYLimit() {
        return (int) yTiles;
    }

    public int getSize() {
        return (int) (xTiles * yTiles);
    }

    @Override
    public void setTexture(Texture texture) {
        this.heightWhole = texture.getImageHeight();
        this.widthWhole = texture.getImageWidth();
        setTilesCount(scale);
        setTextureID(texture.getTextureID());
    }
}
