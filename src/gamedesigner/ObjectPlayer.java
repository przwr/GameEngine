/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Figure;
import game.gameobject.Player;
import game.place.Place;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Light;
import game.place.Map;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.input.Keyboard.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class ObjectPlayer extends Player {

	private int maxtimer;
	private int ix, iy;
	private int xtimer, ytimer;
	private int tileSize;
	private int xStop, yStop;

	private ObjectMap objMap;
	private ObjectPlace objPlace;
	private ObjectUI ui;

    private int areaHeight;

	public ObjectPlayer(boolean first, String name) {
		super(name);
		this.first = first;
		maxtimer = 7;
		xtimer = 0;
		ytimer = 0;
		initializeController();
	}

	private void initializeController() {
		controler = new ObjectController(this);
		controler.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
		controler.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
		controler.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
		controler.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
		controler.inputs[6] = new InputKeyBoard(Keyboard.KEY_END);
		controler.initialize();
	}

    @Override
    public void addGui(GUIObject gui) {
        super.addGui(gui);
        if (gui instanceof ObjectUI)
            ui = (ObjectUI) gui;
    }

	@Override
	public void initialize(int xStart, int yStart, int width, int height, Place place, int x, int y) {
		initialize(name, x, y);
		initialize(yStart, yStart, width, height, place);
	}

	@Override
	public void initialize(int xStart, int yStart, int width, int height, Place place) {
		this.place = place;
		this.online = place.game.online;
		this.width = width;
		this.height = height;
		this.xStart = xStart;
		this.yStart = yStart;
		this.setResistance(2);
		this.emitter = true;
		this.place = place;
		addLight(Light.create(place.getSpriteInSize("light", Methods.roundHalfUp(Settings.scale * 1024), Methods.roundHalfUp(Settings.scale * 1024)), 0.85f, 0.85f, 0.85f,
				Methods.roundHalfUp(Settings.scale * 1024), Methods.roundHalfUp(Settings.scale * 1024), this));
		emits = false;
		tileSize = place.getTileSize();
		objPlace = (ObjectPlace) place;
		onTop = true;
	}

	@Override
	protected boolean isColided(int xMagnitude, int yMagnitude) {
		if (isInGame()) {
			return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map);
		}
		return false;
	}

	@Override
	protected void move(int xPosition, int yPosition) {
		boolean cltr = objPlace.key(KEY_LCONTROL);

		if (xtimer == 0) {
			ix = Methods.interval(0, ix + xPosition, map.getTileWidth());
			setX(ix * tileSize);
			if (!cltr) {
				xStop = Methods.interval(0, xStop + xPosition, map.getTileWidth());
			}
		}
		if (ytimer == 0) {
			iy = Methods.interval(0, iy + yPosition, map.getTileHeight());
			setY(iy * tileSize);
			if (!cltr) {
				yStop = Methods.interval(0, yStop + yPosition, map.getTileHeight());
			}
		}
		if (camera != null) {
			camera.update();
		}
		xtimer++;
		ytimer++;
		if (xtimer >= maxtimer) {
			xtimer = 0;
		}
		if (ytimer >= maxtimer) {
			ytimer = 0;
		}
	}

	@Override
	protected void setPosition(int xPosition, int yPosition) {
		setX(xPosition);
		setY(yPosition);
		if (camera != null) {
			camera.update();
		}
	}

	@Override
	public void update() {
		int xPos = 0;
		int yPos = 0;
		int mode = objPlace.getMode();

		maxtimer = objPlace.key(KEY_A) ? 2 : 7;

		if (objPlace.key(KEY_LCONTROL) && objPlace.key(KEY_Z)) {
			xStop = ix;
			yStop = iy;
		}

		if (objPlace.key(KEY_UP)) {
			yPos--;
		} else if (objPlace.key(KEY_DOWN)) {
			yPos++;
		} else {
			ytimer = 0;
		}
		if (objPlace.key(KEY_LEFT)) {
			xPos--;
		} else if (objPlace.key(KEY_RIGHT)) {
			xPos++;
		} else {
			xtimer = 0;
		}

		if (mode == 0) {
			ui.setChange(objPlace.key(KEY_LSHIFT));
		}

		if (xPos != 0 || yPos != 0) {
			if (ui.isChanged()) {
				if (xtimer == 0 && ytimer == 0) {
					ui.changeCoordinates(xPos, yPos);
					xtimer = 1;
					ytimer = 1;
				}
			} else if (mode == 1 && objPlace.key(KEY_LSHIFT)) {
				if (xtimer == 0 && ytimer == 0) {
					areaHeight = FastMath.max(0, -yPos + areaHeight);
					xtimer = 1;
					ytimer = 1;
				}
			} else {
				move(xPos, yPos);
			}
		}
		if (objPlace.keyPressed(KEY_SPACE)) {
			int xBegin = Math.min(ix, xStop);
			int yBegin = Math.min(iy, yStop);
			int xEnd = Math.max(ix, xStop);
			int yEnd = Math.max(iy, yStop);
			if (mode == 0) {
				for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
					for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
						Point p = ui.getCoordinates();
						objMap.addTile(xTemp, yTemp, p.getX(), p.getY(), ui.getSpriteSheet());
					}
				}
			} else if (mode == 1) {
				int xd = (Math.abs(ix - xStop) + 1);
				int yd = (Math.abs(iy - yStop) + 1);
				if (!objMap.checkBlockCollision(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize)) {
					objMap.addObject(new TemporaryBlock(xBegin * tileSize, yBegin * tileSize, areaHeight, xd, yd, map, place));
				}
			}
		}

		if (objPlace.keyPressed(KEY_DELETE)) {
			int xBegin = Math.min(ix, xStop);
			int yBegin = Math.min(iy, yStop);
			int xEnd = Math.max(ix, xStop);
			int yEnd = Math.max(iy, yStop);
			if (mode == 0) {
				for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
					for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
						objMap.removeTile(xTemp, yTemp);
					}
				}
			} else if (mode == 1) {
				int xd = (Math.abs(ix - xStop) + 1);
				int yd = (Math.abs(iy - yStop) + 1);
				objMap.deleteBlocks(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize);
			}
		}

		if (objPlace.keyPressed(KEY_HOME)) {
			objPlace.setCentralPoint(ix, iy);
		}

		if (objPlace.keyPressed(KEY_TAB)) {
			objMap.switchBackground();
		}
	}

	@Override
	public void render(int xEffect, int yEffect) {
		glPushMatrix();
		int d = 3;
		int xd = (Math.abs(ix - xStop) + 1) * tileSize;
		int yd = (Math.abs(iy - yStop) + 1) * tileSize;
		glTranslatef(xEffect, yEffect, 0);
		if (Settings.scaled) {
			glScaled(Settings.scale, Settings.scale, 1);
		}
		glTranslatef(Math.min(ix, xStop) * tileSize, Math.min(iy, yStop) * tileSize, 0);

		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
		glColor4f(1f, 1f, 1f, 1f);
		if (objPlace.getMode() == 0) {
			Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
			Drawer.drawRectangle(0, yd + d, xd + 2 * d, d);
			Drawer.drawRectangle(0, -yd, d, yd);
			Drawer.drawRectangle(xd + d, 0, d, yd);
		}
		if (objPlace.getMode() == 1) {
			glColor4f(1f, 0.78f, 0f, 1f);
			//glColor4f(1f, 1f, 1f, 1f);
			int tmpH = areaHeight * tileSize;
			//Drawer.drawRectangle(0, -tmpH, xd, yd);
			if (areaHeight == 0) {
				Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
				Drawer.drawRectangle(0, yd + d, xd + 2 * d, d);
				Drawer.drawRectangle(0, -yd, d, yd);
				Drawer.drawRectangle(xd + d, 0, d, yd);
			} else {
				//glColor4f(0.9f, 0.9f, 0.9f, 1f);
				//Drawer.drawRectangle(0, yd, xd, tmpH);
				//glColor4f(1f, 0.78f, 0f, 1f);
				Drawer.drawRectangle(-d, -d - tmpH, xd + 2 * d, d);
				Drawer.drawRectangle(d, yd + d, xd, d);
				Drawer.drawRectangle(0, tmpH, xd, d);
				Drawer.drawRectangle(-d, d, d, -tmpH - yd - d);
				Drawer.drawRectangle(xd + d, 0, d, -tmpH - yd - d);
			}
		}

		Drawer.refreshForRegularDrawing();

		if (Settings.scaled) {
			glScaled(1 / Settings.scale, 1 / Settings.scale, 1);
		}
		place.renderMessageCentered(0, (int) (-tileSize * Settings.scale) / 2, 0, ((int) x / tileSize) + " " + ((int) y / tileSize), new Color(1f, 1f, 1f));

		glPopMatrix();
	}

	@Override
	public void changeMap(Map newMap) {
		super.changeMap(newMap);
		if (camera != null) {
			camera.setMap(newMap);
		}
		objMap = (ObjectMap) newMap;
	}

	@Override
	public void sendUpdate() {
	}

	@Override
	public void updateOnline() {
	}

	@Override
	public void updateRest(Update update) {
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
