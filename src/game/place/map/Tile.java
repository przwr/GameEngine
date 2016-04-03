package game.place.map;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import sprites.Appearance;
import sprites.SpriteSheet;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D;
import static org.lwjgl.opengl.GL11.glGetInteger;

public class Tile extends GameObject implements Appearance {

    final SpriteSheet spriteSheet;
    final ArrayList<Point> tileStack;

    public Tile(SpriteSheet spriteSheet, int xSheet, int ySheet) {
        this.spriteSheet = spriteSheet;
        this.appearance = spriteSheet;
        tileStack = new ArrayList<>(1);
        tileStack.add(new Point(xSheet, ySheet));
        name = "Tile";
        visible = true;
    }

    public void addTileToStack(int xSheet, int ySheet) {
        Point p = new Point(xSheet, ySheet);
        if (!tileStack.contains(p)) {
            tileStack.add(p);
        }
    }

    public int tileStackSize() {
        return tileStack.size();
    }

    public Point popTileFromStack() {
        if (!tileStack.isEmpty()) {
            Point p = tileStack.remove(tileStack.size() - 1);
            tileStack.trimToSize();
            return p;
        }
        return null;
    }

    public Point popTileFromStackBack() {
        if (!tileStack.isEmpty()) {
            Point p = tileStack.remove(0);
            tileStack.trimToSize();
            return p;
        }
        return null;
    }

    public Point getPointFormStack(int i) {
        return i >= tileStack.size() ? null : tileStack.get(i);
    }

    public void renderSpecific(int x, int y) {
        Drawer.regularShader.translate(x, y);
        spriteSheet.renderMultiplePieces(tileStack);
    }

    public void addToTileVBO(int x, int y) {
        if (glGetInteger(GL_TEXTURE_BINDING_2D) != spriteSheet.getTextureID()) {
            Map.renderBackgroundFromVBO();
            bindCheck();
        }
        for (Point tile : tileStack) {
            int size = Drawer.streamVertexData.size() / 2;
            Drawer.streamVertexData.add(
                    spriteSheet.getXStart() + x, spriteSheet.getYStart() + y,
                    spriteSheet.getXStart() + x, spriteSheet.getYStart() + y + spriteSheet.getHeight(),
                    spriteSheet.getXStart() + x + spriteSheet.getWidth(), spriteSheet.getYStart() + y,
                    spriteSheet.getXStart() + x + spriteSheet.getWidth(), spriteSheet.getYStart() + y + spriteSheet.getHeight()
            );
            int piece = spriteSheet.getPieceFromCoordinates(tile.getX(), tile.getY());
            int xTiles = spriteSheet.getXLimit();
            int yTiles = spriteSheet.getYLimit();
            Drawer.streamColorData.add(
                    (float) (piece % xTiles) / xTiles,
                    (float) (piece / xTiles) / yTiles,
                    (float) (piece % xTiles) / xTiles,
                    (1f + (piece / xTiles)) / yTiles,
                    (1f + (piece % xTiles)) / xTiles,
                    (float) (piece / xTiles) / yTiles,
                    (1f + (piece % xTiles)) / xTiles,
                    (1f + (piece / xTiles)) / yTiles
            );
            Drawer.streamIndexData.add(size, size + 1, size + 2, size + 3, size + 2, size + 1);
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleShade(getX(), getY() - collision.getShadowHeight(), collision.getWidth(), collision.getHeight() + collision.getShadowHeight()
                    , 1);
        } else {
            Drawer.drawShapeShade(this, 1, getX(), getY() - collision.getShadowHeight());
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleBlack(getX(), getY() - collision.getShadowHeight(), collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
        } else {
            Drawer.drawShapeBlack(this, getX(), getY() - collision.getShadowHeight());
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleShade(getX(), getY() - collision.getShadowHeight(), collision.getWidth(), collision.getHeight() + collision.getShadowHeight()
                    , 1);
        } else {
            Drawer.drawShapePartShade(this, 1, getX(), getY() - collision.getShadowHeight(), xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (isSimpleLighting()) {
            Drawer.drawRectangleBlack(getX(), getY() - collision.getShadowHeight(), collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
        } else {
            Drawer.drawShapePartBlack(this, getX(), getY() - collision.getShadowHeight(), xStart, xEnd);
        }
    }

    @Override
    public void render() {
        Drawer.regularShader.translate(getX(), getY());
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiece(piece.getX(), piece.getY()));
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public Tile copy() {
        Point first = tileStack.get(0);
        Tile copy = new Tile(spriteSheet, first.getX(), first.getY());
        for (int i = 1; i < tileStack.size(); i++) {
            copy.tileStack.add(tileStack.get(i));
        }
        copy.setDepth(depth);
        return copy;
    }

    public boolean isTheSame(Tile o) {
        Tile tile = o;
        if (tile.spriteSheet.equals(spriteSheet)) {
            if (tileStack.size() == tile.tileStack.size()) {
                for (int i = 0; i < tileStack.size(); i++) {
                    if (!tileStack.get(i).equals(tile.tileStack.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    //t:x:y:texture:TileXSheet:TileYSheet...
    public String saveToString(SpriteSheet s, int x, int y, int xBegin, int yBegin) {
        String txt = "t:" + (x - xBegin) + ":" + (y - yBegin) + ":" + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
        txt = tileStack.stream().map((p) -> ":" + p.getX() + ":" + p.getY()).reduce(txt, String::concat);
        return txt;
    }

    @Override
    public boolean bindCheck() {
        return spriteSheet.bindCheck();
    }

    @Override
    public void renderShadow(float color) {
        tileStack.stream().forEach((piece) -> spriteSheet.renderShadowPiece(piece.getX(), piece.getY(), color));
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiecePart(piece.getX(), piece.getY(), partXStart, partXEnd));
    }

    @Override
    public void renderShadowPart(int partXStart, int partXEnd, float color) {
        tileStack.stream().forEach((piece) -> spriteSheet.renderShadowPiecePart(piece.getX(), piece.getY(), partXStart, partXEnd, color));
    }

    @Override
    public void updateTexture(Player owner) {
        ErrorHandler.warring("Incorrect method use", this);
    }

    @Override
    public void updateFrame() {
        ErrorHandler.warring("Incorrect method use", this);
    }

    @Override
    public int getCurrentFrameIndex() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getXStart() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getYStart() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getActualWidth() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getActualHeight() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getXOffset() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }

    @Override
    public int getYOffset() {
        ErrorHandler.warring("Incorrect method use", this);
        return 0;
    }
}
