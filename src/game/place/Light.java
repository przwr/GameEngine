/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.place.fbo.RegularFrameBufferObject;
import game.place.fbo.MultisampleFrameBufferObject;
import game.place.fbo.FrameBufferObject;
import game.Settings;
import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Light {

	protected Lights lights;
	protected Sprite texture;
	protected int piece;
	protected float red, green, blue;

	protected FrameBufferObject frameBufferObject;

	public Light(String name, float red, float green, float blue, int width, int height, Place place) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		if (!Settings.shadowOff) {
			frameBufferObject = (Settings.samplesCount > 0) ? new MultisampleFrameBufferObject(width, height)
					: new RegularFrameBufferObject(width, height);
		}
		this.texture = Sprite.create(name, width, height, null);
		this.lights = new Lights(1, place.getSpriteSheet("light"));
	}

	public void setSize(int width, int height) {
		texture.setWidth(width);
		texture.setHeight(height);
	}

	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void render(GameObject emitter, Place place, int x, int y) {
		if (texture != null) {
			glColor3f(red, green, blue);
			glPushMatrix();
			glTranslatef(emitter.getX() - texture.getWidth() / 2 + x, emitter.getY() - texture.getHeight() / 2 + y, 0);
			texture.render();
			glPopMatrix();
		}
//		lights.renderPiece(piece);		
	}

	public void render(int height) {
		if (texture != null) {
			glColor3f(red, green, blue);
			glPushMatrix();
			glTranslatef(0, height, 0);
			texture.render();
			glPopMatrix();
		}
	}

	public void render() {
		if (texture != null) {
			glColor3f(red, green, blue);
			glPushMatrix();
			texture.render();
			glPopMatrix();
		}
	}

	public int getWidth() {
		return texture.getWidth();
	}

	public int getHeight() {
		return texture.getHeight();
	}

	public FrameBufferObject getFBO() {
		return frameBufferObject;
	}
}
