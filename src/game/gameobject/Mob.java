/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import net.packets.Update;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

	protected final double range;
	protected GameObject prey;
	public short mobID;

	public abstract void update();

	public Mob(int x, int y, int startX, int startY, int width, int height, int speed, int range, String name, Place place, String spriteName, boolean solid) {
		this.place = place;
		this.width = width;
		this.height = height;
		this.solid = solid;
		this.xStart = startX;
		this.yStart = startY;
		this.range = range;
		this.setMaxSpeed(speed);
		this.sprite = place.getSprite(spriteName);
		initialize(name, x, y);
		setCollision(Rectangle.create(this.width, this.height / 4, OpticProperties.NO_SHADOW, this));
	}

	public synchronized void look(GameObject[] players) {
		GameObject object;
		for (int i = 0; i < getPlace().playersCount; i++) {
			object = players[i];
			if (object.getMap() == map && Methods.pointDistance(object.getX(), object.getY(), getX(), getY()) < range) {
				prey = object;
				break;
			}
		}
	}

	public synchronized void chase(GameObject prey) {
		if (prey != null) {
			double angle = Methods.pointAngle360(getX(), getY(), prey.getX(), prey.getY());
			xSpeed = Methods.xRadius(angle, maxSpeed);
			ySpeed = Methods.yRadius(angle, maxSpeed);
		}
	}

	@Override
	protected boolean isColided(int xMagnitude, int yMagnitude) {
		return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map) || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, getPlace());
	}

	@Override
	public Player getCollided(int xMagnitude, int yMagnitude) {
		return collision.firstPlayerCollide(getX() + xMagnitude, getY() + yMagnitude, getPlace());
	}

	@Override
	protected void move(int xPosition, int yPosition) {
		setX(x + xPosition);
		setY(y + yPosition);
	}

	@Override
	protected void setPosition(int xPosition, int yPosition) {
		setX(xPosition);
		setY(yPosition);
	}

	@Override
	public void renderName(Camera camera) {
		place.renderMessage(0, camera.getXOffset() + (int) (getX() * Settings.scale), camera.getYOffset() + (int) ((getY() + sprite.yStart() + collision.getHeight() / 2 - jumpHeight) * Settings.scale),
				name, new Color(place.red, place.green, place.blue));
	}

	@Override
	public void render(int xEffect, int yEffect) {
		if (sprite != null) {
			glPushMatrix();
			glTranslatef(xEffect, yEffect, 0);
			if (Settings.scaled) {
				glScaled(Settings.scale, Settings.scale, 1);
			}
			glTranslatef(getX(), getY(), 0);
			sprite.render();
			glPopMatrix();
		}
	}

	@Override
	public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
		if (sprite != null) {
			glPushMatrix();
			glTranslatef(getX() + xEffect, getY() + yEffect, 0);
			Drawer.drawShapeInShade(sprite, color);
			glPopMatrix();
		}
	}

	@Override
	public void renderShadow(int xEffect, int yEffect, Figure figure) {
		if (sprite != null) {
			glPushMatrix();

//			glTranslatef(96, 94, 0); //0.75
//			glTranslatef(384 - 384 * Settings.scale, 376 - 376 * Settings.scale, 0);
//			glTranslated(288 * (1 - Settings.scale), 472 * (1 - Settings.scale), 0);
//			glTranslatef(288, 282, 0); //0.25
//			if (Settings.scaled) {
//				glScaled(Settings.scale, Settings.scale, 1);
//			}
			glTranslatef(getX() + xEffect, getY() + yEffect, 0); //1
//			glTranslatef(getX() + 128 + xEffect, getY() + 124 + yEffect, 0); //0.75
//			glTranslatef(getX() + 256 + xEffect, getY() + 252 + yEffect, 0); //0.6
//			glTranslatef(getX() + 384 + xEffect, getY() + 376+ yEffect, 0); //0.5
//			glTranslatef(getX() + 1152 + xEffect, getY() + 1128 + yEffect, 0); //0.25
			Drawer.drawShapeInBlack(sprite);
			glPopMatrix();
		}
	}

	@Override
	public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
		if (sprite != null) {
			glPushMatrix();
			glTranslatef(getX() + xEffect, getY() + yEffect, 0);
			Drawer.drawShapePartInShade(sprite, color, xStart, xEnd);
			glPopMatrix();
		}
	}

	@Override
	public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
		if (sprite != null) {
			glPushMatrix();
			glTranslatef(getX() + xEffect, getY() + yEffect, 0);
			Drawer.drawShapePartInBlack(sprite, xStart, xEnd);
			glPopMatrix();
		}
	}

	@Override
	public void updateRest(Update update) {
	}

	@Override
	public void updateOnline() {
	}
}
