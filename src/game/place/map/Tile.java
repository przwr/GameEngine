package game.place.map;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.place.Place;
import sprites.Appearance;
import sprites.SpriteSheet;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

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

    public void renderSpecific(int xEffect, int yEffect, int x, int y) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(x, y, 0);
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiece(piece.getX(), piece.getY()));
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
        if (isSimpleLighting()) {
            Drawer.drawRectangleInShade(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight(), 1);
        } else {
            Drawer.drawShapeInShade(this, 1);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
        if (isSimpleLighting()) {
            Drawer.drawRectangleInBlack(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
        } else {
            Drawer.drawShapeInBlack(this);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
        if (isSimpleLighting()) {
            Drawer.drawRectangleInShade(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight(), 1);
        } else {
            Drawer.drawShapePartInShade(this, 1, xStart, xEnd);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
        if (isSimpleLighting()) {
            Drawer.drawRectangleInBlack(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
        } else {
            Drawer.drawShapePartInBlack(this, xStart, xEnd);
        }
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);

        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

        glTranslatef(getX(), getY(), 0);
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiece(piece.getX(), piece.getY()));

        glPopMatrix();

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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tile) {
            Tile tile = (Tile) o;
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
    public void bindCheck() {
        spriteSheet.bindCheck();
    }

    @Override
    public void render() {
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiece(piece.getX(), piece.getY()));
    }

    @Override
    public void renderMirrored() {
        tileStack.stream().forEach((piece) -> spriteSheet.renderPieceMirrored(piece.getX(), piece.getY()));
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiecePart(piece.getX(), piece.getY(), partXStart, partXEnd));
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        tileStack.stream().forEach((piece) -> spriteSheet.renderPiecePartMirrored(piece.getX(), piece.getY(), partXStart, partXEnd));
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