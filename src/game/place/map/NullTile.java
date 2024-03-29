package game.place.map;

import collision.Figure;
import engine.utilities.ErrorHandler;
import game.gameobject.GameObject;
import sprites.Appearance;

public class NullTile extends Tile implements Appearance {

    public NullTile() {
        super(null, 0, 0);
        name = "NullTile";
        setVisible(false);
    }

    @Override
    public void renderSpecific(int x, int y) {
    }

    @Override
    public void renderShadowLit(Figure figure) {
    }

    @Override
    public void renderShadow(Figure figure) {
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
    }

    @Override
    public void render() {
    }

    @Override
    public boolean bindCheck() {
        return true;
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
    }

    @Override
    public void updateTexture(GameObject owner) {
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
