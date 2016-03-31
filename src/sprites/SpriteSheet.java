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

/**
 * @author przemek
 */
public class SpriteSheet extends Sprite {

    private static final int NORMAL = 0, MIRRORED = 4;
    private static Point tempPoint = new Point();
    private final boolean isStartMoving, scale;
    private int xTiles;
    private int yTiles;
    private int frame, framesCount;
    private PointedValue[] startingPoints;


    private SpriteSheet(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale) {
        super(path, folder, width, height, xStart, yStart, spriteBase);
        isStartMoving = false;
        this.scale = scale;
        setTilesCount(scale);
        framesCount = xTiles * yTiles;
    }

    private SpriteSheet(String path, String folder, int width, int height, int xStart, int yStart, SpriteBase spriteBase, boolean scale, PointedValue[]
            startingPoints) {
        super(path, folder, width, height, xStart, yStart, spriteBase);
        this.startingPoints = startingPoints;
        isStartMoving = true;
        this.scale = scale;
        setTilesCount(scale);
        framesCount = startingPoints.length;
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
        float[] vertices;
        float[] textureCoordinates;
        vertices = new float[8 + framesCount * (canBeMirrored ? 2 : 1) * 8];
        textureCoordinates = new float[8 + framesCount * (canBeMirrored ? 2 : 1) * 8];
        int[] indices = new int[4 + framesCount * (canBeMirrored ? 2 : 1) * 4];
        vertices[0] = xStart;
        vertices[1] = yStart;
        vertices[2] = xStart;
        vertices[3] = yStart + heightWhole;
        vertices[4] = xStart + widthWhole;
        vertices[5] = yStart;
        vertices[6] = xStart + widthWhole;
        vertices[7] = yStart + heightWhole;
        textureCoordinates[0] = 0;
        textureCoordinates[1] = 0;
        textureCoordinates[2] = 0;
        textureCoordinates[3] = 1f;
        textureCoordinates[4] = 1f;
        textureCoordinates[5] = 0;
        textureCoordinates[6] = 1f;
        textureCoordinates[7] = 1f;
        for (int i = 8; i < vertices.length; i += 8) {
            frame = ((i - 8) % ((vertices.length - 8) / (canBeMirrored ? 2 : 1))) / 8;
            vertices[i] = getXStart();
            vertices[i + 1] = getYStart();
            vertices[i + 2] = getXStart();
            vertices[i + 3] = getYStart() + height;
            vertices[i + 4] = getXStart() + width;
            vertices[i + 5] = getYStart();
            vertices[i + 6] = getXStart() + width;
            vertices[i + 7] = getYStart() + height;
        }
        for (int i = 8; i < textureCoordinates.length / (canBeMirrored ? 2 : 1) + (canBeMirrored ? 4 : 0); i += 8) {
            frame = (i - 8) / 8;
            int piece = getFramesPosition(frame);
            textureCoordinates[i] = (float) (piece % xTiles) / xTiles;
            textureCoordinates[i + 1] = (float) (piece / xTiles) / yTiles;
            textureCoordinates[i + 2] = (float) (piece % xTiles) / xTiles;
            textureCoordinates[i + 3] = (1f + (piece / xTiles)) / yTiles;
            textureCoordinates[i + 4] = (1f + (piece % xTiles)) / xTiles;
            textureCoordinates[i + 5] = (float) (piece / xTiles) / yTiles;
            textureCoordinates[i + 6] = (1f + (piece % xTiles)) / xTiles;
            textureCoordinates[i + 7] = (1f + (piece / xTiles)) / yTiles;
        }
        if (canBeMirrored) {
            for (int i = 4 + textureCoordinates.length / 2; i < textureCoordinates.length; i += 8) {
                frame = ((i - 8) % ((textureCoordinates.length - 8) / 2)) / 8;
                int piece = getFramesPosition(frame);
                textureCoordinates[i] = (1f + (piece % xTiles)) / xTiles;
                textureCoordinates[i + 1] = (float) (piece / xTiles) / yTiles;
                textureCoordinates[i + 2] = (1f + (piece % xTiles)) / xTiles;
                textureCoordinates[i + 3] = (1f + (piece / xTiles)) / yTiles;
                textureCoordinates[i + 4] = (float) (piece % xTiles) / xTiles;
                textureCoordinates[i + 5] = (float) (piece / xTiles) / yTiles;
                textureCoordinates[i + 6] = (float) (piece % xTiles) / xTiles;
                textureCoordinates[i + 7] = (1f + (piece / xTiles)) / yTiles;
            }
        }
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        vbo = VertexBufferObject.create(vertices, textureCoordinates, indices);
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
        if (bindCheck()) {
            MatrixMath.resetMatrix(transformationMatrix);
            Drawer.regularShader.resetUniform();
            vbo.renderTextured(0, 4);
        }
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }

    private int getFramesPosition(int frame) {
        return isStartMoving ? (startingPoints[frame] != null ? startingPoints[frame].getValue() : -1) : frame;
    }


    public void renderPiece(int piece) {
        renderPiece(piece, 1f, 1f, NORMAL);
    }

    public void renderShadowPiece(int x, int y, float color) {
        if (areValidCoordinates(x, y)) {
            renderShadowPiece(x + y * xTiles, color);
        }
    }

    public void renderShadowPiece(int piece, float color) {
        renderShadowPiece(piece, 1f, 1f, NORMAL, color);
    }

    public void renderPiece(int piece, float xScale, float yScale, int type) {
        if (bindCheck()) {
            frame = piece;
            piece = getFramesPosition(piece);
            if (isValidPiece(piece)) {
                Drawer.regularShader.resetUniform();
                if (xScale != 1f || yScale != 1f) {
                    translationVector.set(0, 0);
                    MatrixMath.transformMatrix(transformationMatrix, translationVector, xScale, yScale);
                    Drawer.regularShader.loadTransformationMatrix(transformationMatrix);
                }
                vbo.renderTextured(4 + type * framesCount + piece * 4, 4);
            }
        }
    }

    public void renderMultiplePieces(Iterable<Point> pieces) {
        renderMultiplePieces(pieces, 1f, 1f, NORMAL);
    }

    public void renderMultiplePieces(Iterable<Point> pieces, float xScale, float yScale, int type) {
        if (bindCheck()) {
            Drawer.regularShader.resetUniform();
            if (xScale != 1f || yScale != 1f) {
                translationVector.set(0, 0);
                MatrixMath.transformMatrix(transformationMatrix, translationVector, xScale, yScale);
                Drawer.regularShader.loadTransformationMatrix(transformationMatrix);
            }
            for (Point coords : pieces) {
                frame = coords.getX() + coords.getY() * xTiles;
                int piece = getFramesPosition(frame);
                if (isValidPiece(piece)) {
                    vbo.renderTextured(4 + type * framesCount + piece * 4, 4);
                }
            }
        }
    }


//
//    public void renderMultiplePieces(int[] vertices, float xScale, float yScale, int type) {
//        if (bindCheck()) {
//            Drawer.regularShader.resetUniform();
//            if (xScale != 1f || yScale != 1f) {
//                translationVector.set(0, 0);
//                MatrixMath.transformMatrix(transformationMatrix, translationVector, xScale, yScale);
//                Drawer.regularShader.loadTransformationMatrix(transformationMatrix);
//            }
//            vbo.renderTextured(4 + type * framesCount + piece * 4, 4);
//        }
//    }


    public void renderShadowPiece(int piece, float xScale, float yScale, int type, float color) {
        if (bindCheck()) {
            frame = piece;
            piece = getFramesPosition(piece);
            if (isValidPiece(piece)) {
                Drawer.shadowShader.resetUniform();
                vectorModifier.set(color, color, color, 1);
                Drawer.shadowShader.loadColourModifier(vectorModifier);
                if (xScale != 1f || yScale != 1f) {
                    translationVector.set(0, 0);
                    MatrixMath.transformMatrix(transformationMatrix, translationVector, xScale, yScale);
                    Drawer.shadowShader.loadTransformationMatrix(transformationMatrix);
                }
                vbo.renderTextured(4 + type * framesCount + piece * 4, 4);
            }
        }
    }

    public void renderShadowPiecePart(int x, int y, int partXStart, int partXEnd, float color) {
        if (areValidCoordinates(x, y)) {
            renderShadowPiecePart(x + y * xTiles, partXStart, partXEnd, color);
        }
    }

    public void renderShadowPiecePart(int piece, int partXStart, int partXEnd, float color) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = partXStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            frame = piece;
            piece = getFramesPosition(piece);
            if (isValidPiece(piece)) {
                Drawer.shadowShader.resetUniform();
                vectorModifier.set(color, color, color, 1);
                Drawer.shadowShader.loadColourModifier(vectorModifier);
                vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width / xTiles, (partXEnd - width) / (float) width / xTiles);
                Drawer.shadowShader.loadSizeModifier(vectorModifier);
                vbo.renderTextured(4 + piece * 4, 4);
            }
        }
    }

    public void renderPiecePart(int piece, int partXStart, int partXEnd) {
        if (bindCheck()) {
            if (partXStart > partXEnd) {
                int temp = xStart;
                partXStart = partXEnd;
                partXEnd = temp;
            }
            frame = piece;
            piece = getFramesPosition(piece);
            if (isValidPiece(piece)) {
                Drawer.regularShader.resetUniform();
                vectorModifier.set(partXStart, partXEnd - width, partXStart / (float) width / xTiles, (partXEnd - width) / (float) width / xTiles);
                Drawer.regularShader.loadSizeModifier(vectorModifier);
                vbo.renderTextured(4 + piece * 4, 4);
            }
        }
    }

    public int getPieceFromCoordinates(int x, int y) {
        return x + y * xTiles;
    }

    public Point getCoordinatesFromPiece(int piece) {
        tempPoint.set(piece % xTiles, piece / xTiles);
        return tempPoint;
    }

    public void renderPiece(int x, int y) {
        if (areValidCoordinates(x, y)) {
            renderPiece(x + y * xTiles);
        }
    }

    public void renderPieceResized(int x, int y, float width, float height) {
        if (bindCheck()) {
            renderPiece(x + y * xTiles, width / this.width, height / this.height, NORMAL);
        }
    }

    public void renderPiecePart(int x, int y, int partXStart, int partXEnd) {
        if (areValidCoordinates(x, y)) {
            renderPiecePart(x + y * xTiles, partXStart, partXEnd);
        }
    }

    public void renderPieceMirrored(int piece) {
        if (canBeMirrored) {
            renderPiece(piece, 1f, 1f, MIRRORED);
        } else {
            System.out.println(path + " can't be Mirrored!");
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
