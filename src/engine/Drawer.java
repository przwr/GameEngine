/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.place.Place;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.Color;
import sprites.Appearance;

/**
 *
 * @author Wojtek
 */
public class Drawer {

	private static final int white = glGenTextures();
	private static Place place;
	private static float currentX, currentY;

	public static void setCentralPoint() {
		currentX = 0;
		currentY = 0;
	}

	public static void translateFromCP(float x, float y) {
		currentX += x;
		currentY += y;
		glTranslatef(x, y, 0f);
	}

	public static void returnToCentralPoint() {
		glTranslatef(-currentX, -currentY, 0f);
		setCentralPoint();
	}

	public static void setColor(Color color) {
		glColor4f(color.r, color.g, color.b, color.a);
	}

	public static void refreshForRegularDrawing() {
		refreshColor();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	public static void refreshColor() {
		glColor4f(place.red, place.green, place.blue, 1.0f);
	}

	public static void drawRectangleInShade(int xStart, int yStart, int width, int height, float color) {
		glColor3f(color, color, color);
		drawRectangle(xStart, yStart, width, height);
		glColor3f(1f, 1f, 1f);
	}

	public static void drawRectangleInBlack(int xStart, int yStart, int width, int height) {
		glColor3f(0f, 0f, 0f);
		drawRectangle(xStart, yStart, width, height);
		glColor3f(1f, 1f, 1f);
	}

	public static void drawRectangle(int xStart, int yStart, int width, int height) {
		glTranslatef(xStart, yStart, 0);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(0, height);
		glVertex2f(width, height);
		glVertex2f(width, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawRectangleBorder(int xStart, int yStart, int width, int height) {
		glTranslatef(xStart, yStart, 0);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_LINE_LOOP);
		glVertex2f(0, 0);
		glVertex2f(0, height);
		glVertex2f(width, height);
		glVertex2f(width, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawCircle(int xStart, int yStart, int radius, int precision) {
		drawElipse(xStart, yStart, radius, radius, precision);
	}

	public static void drawElipse(int xStart, int yStart, int xRadius, int yRadius, int precision) {  //Zbyt mała precyzja tworzy figury foremne
		glTranslatef(xStart, yStart, 0);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_TRIANGLE_FAN);
		glVertex2f(0, 0);
		int step = 360 / precision;
		for (int i = 0; i <= 360; i += step) {
			glVertex2f((float) Methods.xRadius(i, xRadius), (float) Methods.yRadius(i, yRadius));
		}
		glVertex2f(xRadius, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawShapeInShade(Appearance appearance, float color) {
		prepareDrawingShape(color);
		appearance.bindCheck();
		changeShapeToColor();
		appearance.render();
		cleanAfterDrawingShape();
	}

	public static void drawShapePartInShade(Appearance appearance, float color, int partXStart, int partXEnd) {
		prepareDrawingShape(color);
		appearance.bindCheck();
		changeShapeToColor();
		appearance.renderPart(partXStart, partXEnd);
		cleanAfterDrawingShape();
	}

	public static void drawShapeInBlack(Appearance appearance) {
		appearance.bindCheck();
		glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
		appearance.render();
		setBlendAttributesForShadows();
	}

	public static void drawShapePartInBlack(Appearance appearance, int partXStart, int partXEnd) {
		appearance.bindCheck();
		glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
		appearance.renderPart(partXStart, partXEnd);
		setBlendAttributesForShadows();
	}

	private static void prepareDrawingShape(float color) {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor3f(color, color, color);
		glActiveTexture(white);
	}

	private static void cleanAfterDrawingShape() {
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		setBlendAttributesForShadows();
		glColor3f(1, 1, 1);
	}

	private static void changeShapeToColor() {
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
		glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
		glTexEnvi(GL_TEXTURE_ENV, GL_SRC0_RGB, GL_PREVIOUS);
		glTexEnvi(GL_TEXTURE_ENV, GL_SRC1_RGB, GL_TEXTURE);
		glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
		glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
	}

	private static void setBlendAttributesForShadows() {
		glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void setPlace(Place place) {
		Drawer.place = place;
	}
}
