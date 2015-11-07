package game.gameobject.interactive.collision;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;

import static game.gameobject.GameObject.*;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 29.08.15.
 */
public class LineInteractiveCollision extends InteractiveCollision {

	private int length, width;
	private Point end = new Point();

	public LineInteractiveCollision(int fromBottom, int height, int shift, int length, int width) {
		super(fromBottom, height, shift);
		this.length = length;
		this.width = width;
	}

	@Override
	public void updatePosition(GameObject owner) {
		int x = owner.getX();
		int y = owner.getY();
		switch (owner.getDirection8Way()) {
			case RIGHT:
				x += (owner.getCollision().getWidth() / 2 + shift);
				position.set(x, y);
				x += length;
				break;
			case UP:
				y -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				position.set(x, y);
				y -= length;
				break;
			case LEFT:
				x -= (owner.getCollision().getWidth() / 2 + shift);
				position.set(x, y);
				x -= length;
				break;
			case DOWN:
				y += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				position.set(x, y);
				y += length;
				break;
			case UP_RIGHT:
				x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				y -= (owner.getCollision().getWidth() / 4 + shift / 2);
				position.set(x, y);
				x += length;
				y -= length / 2;
				break;
			case UP_LEFT:
				x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				y -= (owner.getCollision().getWidth() / 4 + shift / 2);
				position.set(x, y);
				x -= length;
				y -= length / 2;
				break;
			case DOWN_LEFT:
				x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				y += (owner.getCollision().getWidth() / 4 + shift / 2);
				position.set(x, y);
				x -= length;
				y += length / 2;
				break;
			case DOWN_RIGHT:
				x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
				y += (owner.getCollision().getWidth() / 4 + shift / 2);
				position.set(x, y);
				x += length;
				y += length / 2;
				break;
		}
		end.set(x, y);
	}

	@Override
	protected InteractiveResponse collideImplementation(GameObject owner, GameObject object, byte attackType) {
		if (object != null && object.getCollision() != null) {
			int objectBottom = (int) object.getFloatHeight();
			int objectTop = objectBottom + object.getActualHeight();
			int bottom = (int) owner.getFloatHeight() + fromBottom;
			int top = bottom + height;
			if (objectTop > bottom && objectBottom < top) {
				int pixelsIn = lineToCircleDistance(object.getX(), object.getY(), (object.getCollisionWidth() + width) / 2, position, end, length);
				if (pixelsIn > 0) {
					response.setResponse(pixelsIn, length, (byte) (calculateInteractionDirection(object.getDirection8Way(), object.getCollision(), owner.getX(), owner.getY())), attackType, owner);
					return response;
				}
			}
		}
		return InteractiveResponse.NO_RESPONSE;
	}

	@Override
	protected InteractiveResponse collideImplementation(GameObject owner, Player player, byte attackType) {
		if (player != null && player.isInGame()) {
			int playerBottom = (int) player.getFloatHeight();
			int playerTop = playerBottom + player.getActualHeight();
			int bottom = (int) owner.getFloatHeight() + fromBottom;
			int top = bottom + height;
			if (playerTop > bottom && playerBottom < top) {
				int pixelsIn = lineToCircleDistance(player.getX(), player.getY(), (player.getCollisionWidth() + width) / 2, position, end, length);
				if (pixelsIn > 0) {
					response.setResponse(pixelsIn, length, (byte) (calculateInteractionDirection(player
							.getDirection8Way(),
							player.getCollision(), owner.getX(), owner.getY())), attackType, owner);
					return response;
				}
			}
		}
		return InteractiveResponse.NO_RESPONSE;
	}

	@Override
	public void render(GameObject owner, int xEffect, int yEffect) {
		Drawer.setColor(new Color(0.9f, 0.1f, 0.1f));
		int tempWidth = (int) (Methods.ONE_BY_SQRT_ROOT_OF_2 * width);
		boolean ellipse = true;
		switch (owner.getDirection8Way()) {
			case RIGHT:
			case LEFT:
				break;
			case UP:
			case DOWN:
				tempWidth = width;
				break;
			case UP_RIGHT:
			case UP_LEFT:
			case DOWN_LEFT:
			case DOWN_RIGHT:
				ellipse = false;
				break;
		}
		glPushMatrix();
		glTranslatef(xEffect, yEffect, 0);
		glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
		Drawer.setCentralPoint();
		Drawer.drawLineWidth(position.getX(), position.getY(), end.getX() - position.getX(), end.getY() - position.getY(), tempWidth);
		Drawer.returnToCentralPoint();
		Drawer.setCentralPoint();
		if (ellipse) {
			Drawer.drawEllipse(position.getX(), position.getY(), width / 2, (int) (Methods.ONE_BY_SQRT_ROOT_OF_2 * (width / 2)), 32);
		} else {
			Drawer.drawCircle(position.getX(), position.getY(), width / 4, 16);
		}
		Drawer.returnToCentralPoint();
		if (ellipse) {
			Drawer.drawEllipse(end.getX(), end.getY(), width / 2, (int) (Methods.ONE_BY_SQRT_ROOT_OF_2 * (width / 2)), 32);
		} else {
			Drawer.drawCircle(end.getX(), end.getY(), width / 4, 16);
		}
		Drawer.refreshColor();
		glPopMatrix();
	}
}
