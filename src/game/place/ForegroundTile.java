/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ForegroundTile extends Tile {

	public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
		return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, yStart);
	}

	public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
		return new ForegroundTile(spriteSheet, size, xSheet, ySheet, false, 0);
	}

	public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart) {
		return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, yStart);
	}

	public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
		return new ForegroundTile(spriteSheet, size, xSheet, ySheet, true, 0);
	}

	ForegroundTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean wall, int yStart) {
		super(spriteSheet, size, xSheet, ySheet);
		simpleLighting = true;
		solid = wall;
		int type = wall ? OpticProperties.FULL_SHADOW : OpticProperties.IN_SHADE_NO_SHADOW;
		setCollision(Rectangle.create(0, yStart, size, size, type, this));
	}

	@Override
	public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
		glPushMatrix();
		glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
		if (isSimpleLighting()) {
			Drawer.drawRectangleInShade(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight(), color);
		} else if (sprite != null) {
			Drawer.drawShapeInShade(sprite, color);
			glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
		}
		glPopMatrix();
	}

	@Override
	public void renderShadow(int xEffect, int yEffect, Figure figure) {
		glPushMatrix();
		glTranslatef(getX() + xEffect, getY() + yEffect - collision.getShadowHeight(), 0);
		if (isSimpleLighting()) {
			Drawer.drawRectangleInBlack(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
		} else if (sprite != null) {
			Drawer.drawShapeInBlack(sprite);
			glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
		}
		glPopMatrix();
	}

	//0  1 2 3       4    5      6          7
	//ft:x:y:texture:wall:yStart:TileXSheet:TileYSheet...
	public String saveToString(SpriteSheet s, int xBegin, int yBegin, int tile) {
		String txt = "ft:" + ((getX() - xBegin) / tile) + ":" + ((getY() - yBegin) / tile) + ":" + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
		txt += ":" + (solid ? "1" : "0") + ":" + (collision.getYStart() / tile);
		txt = tileStack.stream().map((p) -> ":" + p.getX() + ":" + p.getY()).reduce(txt, String::concat);
		return txt;
	}
}
