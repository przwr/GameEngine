package game.place.map;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.place.Place;
import game.place.fbo.FrameBufferObject;
import game.place.fbo.RegularFrameBufferObject;
import org.lwjgl.opengl.Display;
import sprites.Appearance;
import sprites.SpriteSheet;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class NullTile extends Tile implements Appearance {

    public NullTile() {
        super(null, 0, 0);
        name = "NullTile";
        visible = false;
    }

    @Override
    public void renderSpecific(int xEffect, int yEffect, int x, int y) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }

    @Override
    public void bindCheck() {
    }

    @Override
    public void render() {
    }

    @Override
    public void renderMirrored() {
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
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
