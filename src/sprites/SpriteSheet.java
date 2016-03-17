/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.matrices.MatrixMath;
import engine.utilities.Drawer;
import engine.utilities.Point;
import engine.utilities.PointedValue;
import game.Settings;
import org.newdawn.slick.opengl.Texture;
import sprites.vbo.VertexBufferObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class SpriteSheet extends Sprite {

    private final boolean isStartMoving, scale;
    private int xTiles;
    private int yTiles;
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

    @Override
    public void initializeBuffers() {
        float[] vertices = {
                0, 0,
                0, height,
                width, height,
                width, 0,
                0, 0,
                0, heightWhole,
                widthWhole, heightWhole,
                widthWhole, 0,
        };
        float[] textureCoordinates = {
                0, 0,
                0, 1f / yTiles,
                1f / xTiles, 1f / yTiles,
                1f / xTiles, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };
        int[] indices = {0, 1, 3, 3, 1, 2, 0, 1, 3, 3, 1, 2};
        vbo = new VertexBufferObject(vertices, textureCoordinates, indices);
    }

    private void setTilesCount(boolean scale) {
        if (scale) {
            this.xTiles = (int) (widthWhole * Settings.nativeScale) / width;
            this.yTiles = (int) (heightWhole * Settings.nativeScale) / height;
        } else {
            this.xTiles = (int) (widthWhole / width);
            this.yTiles = (int) (heightWhole / height);
        }
    }

    @Override
    public void render() {  //Rysuje CAŁY spriteSheet
        bindCheck();
        glBegin(GL_TRIANGLES);
        glTexCoord2f(0, 0);
        glVertex2f(xStart, yStart);
        glTexCoord2f(0, 1);
        glVertex2f(xStart, yStart + heightWhole);
        glTexCoord2f(1, 1);
        glVertex2f(xStart + widthWhole, yStart + heightWhole);

        glTexCoord2f(1, 1);
        glVertex2f(xStart + widthWhole, yStart + heightWhole);
        glTexCoord2f(1, 0);
        glVertex2f(xStart + widthWhole, yStart);
        glTexCoord2f(0, 0);
        glVertex2f(xStart, yStart);
        glEnd();
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }

    private int getFramesPosition(int frame) {
        return isStartMoving ? (startingPoints[frame] != null ? startingPoints[frame].getValue() : -1) : frame;
    }


    public void renderPiece(int piece) {
        if (bindCheck()) {
            frame = piece;
            translationVector.set(getXStart() / 2f, getYStart() / 2f);
            MatrixMath.transformationMatrix(transformationMatrix, translationVector);
            piece = getFramesPosition(piece);
            if (isValidPiece(piece)) {
                Drawer.spriteShader.start();
//                Drawer.spriteShader.loadColourModifier(colorModifier);
                Drawer.spriteShader.loadTextureShift((float) (piece % xTiles) / xTiles, (float) (piece / xTiles) / yTiles);
                Drawer.spriteShader.loadTransformationMatrix(transformationMatrix);
                vbo.renderTextured(0, 6);
                Drawer.spriteShader.stop();
            }
        }

//        frame = piece;
//        piece = getFramesPosition(piece);
//        if (isValidPiece(piece)) {
//            int x = (int) (piece % xTiles);
//            int y = (int) (piece / xTiles);
//            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
//        }
    }

    public void renderPiece(int x, int y) {
        if (areValidCoordinates(x, y)) {
            frame = x + y * xTiles;
            renderSpritePiece((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    public void renderPieceResized(int x, int y, float width, float height) {
        if (areValidCoordinates(x, y)) {
            frame = x + y * xTiles;
            renderSpritePieceResized((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, width, height);
        }
    }

    public void renderPiecePart(int id, int xStart, int xEnd) {
        id = getFramesPosition(id);
        int x = id % xTiles;
        int y = id / xTiles;
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = x + y * xTiles;
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
            frame = x + y * xTiles;
            renderSpritePiecePart((float) x / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles, xStart, xEnd, xTiles);
        }
    }

    public void renderPieceMirrored(int id) {
        id = getFramesPosition(id);
        int x = id % xTiles;
        int y = id / xTiles;
        if (isValidPiece(id) && areValidCoordinates(x, y)) {
            frame = id;
            renderSpritePieceMirrored((float) x / xTiles, (float) (x + 1) / xTiles, (float) y / yTiles, (float) (y + 1) / yTiles);
        }
    }

    private boolean isValidPiece(int piece) {
        return piece >= 0 && piece <= xTiles * yTiles;
    }

    private boolean areValidCoordinates(int x, int y) {
        return !(x > xTiles || y > yTiles);
    }

    public int getXLimit() {
        return xTiles;
    }

    public int getYLimit() {
        return yTiles;
    }

    public int getSize() {
        return xTiles * yTiles;
    }

    @Override
    public int getXStart() {
        if (isStartMoving) {
            frame = Math.min(frame, startingPoints.length - 1);
            if (startingPoints[frame] != null) {
                return xStart + startingPoints[frame].getX();
            }
        }
        return xStart;
    }

    @Override
    public int getYStart() {
        if (isStartMoving) {
            frame = Math.min(frame, startingPoints.length - 1);
            if (startingPoints[frame] != null) {
                return yStart + startingPoints[frame].getY();
            }
        }
        return yStart;
    }

    @Override
    public void setTexture(Texture texture) {
        this.heightWhole = texture.getImageHeight();
        this.widthWhole = texture.getImageWidth();
        setTilesCount(scale);
        setTextureID(texture.getTextureID());
    }
}
