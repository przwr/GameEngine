/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Point;

/**
 *
 * @author przemek
 */
public class Shadow {

	public int type;
	public Point[] points;
	public static final int DARK = 0, BRIGHT = 1, DARKEN = 2, BRIGHTEN = 3,
			DARKEN_OBJECT = 4, BRIGHTEN_OBJECT = 5;

	public Shadow(int type) {
		this.type = type;
	}

	public void addPoints(Point point1, Point point2, Point point3, Point point4) {
		points = new Point[4];
		points[0] = point1;
		points[1] = point2;
		points[2] = point3;
		points[3] = point4;
	}
}
