/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import game.place.Lights;
import game.place.Place;

/**
 *
 * @author Domi
 */
public class LightSource extends GameObject {

	private final Lights lights;

	public LightSource(int x, int y, int startX, int startY, int width, int height, String name, Place place, String spriteName, boolean solid) {
		this.width = width;
		this.height = height;
		this.solid = solid;
		this.xStart = startX;
		this.yStart = startY;
		this.sprite = place.getSprite(spriteName);
		emitter = true;
		lights = new Lights(1, place.getSpriteSheet("light"));
		initialize(name, x, y);
		setCollision(Rectangle.create(this.width, this.height, OpticProperties.NO_SHADOW, this));
	}

	@Override
	public void render(int xEffect, int yEffect) {

	}

	@Override
	public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
	}

	@Override
	public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
	}

	@Override
	public void renderShadow(int xEffect, int yEffect, Figure figure) {
	}

	@Override
	public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
	}

}
