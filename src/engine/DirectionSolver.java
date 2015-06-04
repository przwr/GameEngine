/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import collision.Figure;
import game.gameobject.GameObject;
import java.util.List;

/**
 *
 * @author przemek
 */
public class DirectionSolver {

    private static byte LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    private static byte[] directions = new byte[4];

    public static double getXAngle(GameObject chaser, List<Figure> obstacles, Point previousDirections, Point next, GameObject prey, Delay time) {

//        clearDirections();
        time.start();

        Figure colision = chaser.getCollision();
        directions[LEFT] = 0;
        directions[RIGHT] = 0;

        int xS = colision.getX();
        int xE = colision.getXEnd();

        for (Figure figure : obstacles) {
            if (figure.getOwner() != null) {

                if (xE > figure.getX() && xS < figure.getXEnd()) {
                    directions[LEFT]++;
                    directions[RIGHT]++;
                    if (figure.getX() - xS > Math.abs(xE - figure.getXEnd())) {
                        directions[LEFT]++;
                    } else {
                        directions[RIGHT]++;
                    }
                }
            }
        }

        System.out.println("Kierunki: ");
        for (int i = 0; i < 4; i++) {
            System.out.println(directions[i]);
        }

        int xD;

        if (prey != null) {
            if (next != null) {
                xD = next.getX();

            } else {
                xD = prey.getX();
            }
            if (directions[RIGHT] == 0) {
                if (xD > chaser.getX()) {
                    directions[RIGHT]++;
                    directions[RIGHT]++;
                    //previousDirections.setX(RIGHT);
                } else {
                    directions[LEFT]++;
                    directions[LEFT]++;
                    //previousDirections.setX(LEFT);
                }
            }
        }

        int x = chaser.getX();

        if (previousDirections.getX() == -1) {
            if (directions[LEFT] > directions[RIGHT]) {
                x -= (colision.getWidth() + colision.getHeight());
                // previousDirections.setX(LEFT);
            } else if (directions[RIGHT] != 0) {
                x += (colision.getWidth() + colision.getHeight());
                // previousDirections.setX(RIGHT);
            } else {
                // previousDirections.setX(-1);
            }
        } else {
            x += ((previousDirections.getX() == LEFT) ? -1 : 1) * (colision.getWidth() + colision.getHeight());
        }

        System.out.println("Kierunki: ");
        for (int i = 0; i < 4; i++) {
            System.out.println(directions[i]);
        }
//        System.out.println("Kolizje: " + obstacles);
//        for (Figure figure : obstacles) {
//            System.out.println(figure.getX() / Place.tileSize + " " + figure.getY() / Place.tileSize);
//        }

        return x;
    }

    public static double getYAngle(GameObject chaser, List<Figure> obstacles, Point previousDirections, Point next, GameObject prey, Delay time) {

//        clearDirections();
        directions[UP] = 0;
        directions[DOWN] = 0;
        time.start();

        Figure colision = chaser.getCollision();

        int yS = colision.getY();
        int yE = colision.getYEnd();

        for (Figure figure : obstacles) {
            if (figure.getOwner() != null) {
                if (yE > figure.getY() && yS < figure.getYEnd()) {
                    directions[UP]++;
                    directions[DOWN]++;
                    if (figure.getY() - yS > Math.abs(yE - figure.getYEnd())) {
                        directions[UP]++;
                    } else {
                        directions[DOWN]++;
                    }
                }

            }
        }

        System.out.println("Kierunki: ");
        for (int i = 0; i < 4; i++) {
            System.out.println(directions[i]);
        }

        int yD;
        if (prey != null) {
            if (next != null) {
                yD = next.getY();
            } else {
                yD = prey.getY();
            }
            if (directions[UP] == 0) {
                if (yD > chaser.getY()) {
                    directions[DOWN]++;
                    directions[DOWN]++;
                    // previousDirections.setY(DOWN);
                } else {
                    directions[UP]++;
                    directions[UP]++;
                    // previousDirections.setY(UP);
                }
            }
        }
        int y = chaser.getY();

        if (previousDirections.getY() == -1) {
            if (directions[UP] > directions[DOWN]) {
                y -= (colision.getWidth() + colision.getHeight());
                // previousDirections.setY(UP);
            } else if (directions[DOWN] != 0) {
                y += (colision.getWidth() + colision.getHeight());
                // previousDirections.setY(DOWN);
            } else {
                //  previousDirections.setY(-1);
            }
        } else {
            y += ((previousDirections.getY() == UP) ? -1 : 1) * (colision.getWidth() + colision.getHeight());
        }

        System.out.println("Kierunki: ");
        for (int i = 0; i < 4; i++) {
            System.out.println(directions[i]);
        }
//        System.out.println("Kolizje: " + obstacles);
//        for (Figure figure : obstacles) {
//            System.out.println(figure.getX() / Place.tileSize + " " + figure.getY() / Place.tileSize);
//        }

        return y;
    }

    private static void clearDirections() {
        directions[LEFT] = 0;
        directions[RIGHT] = 0;
        directions[UP] = 0;
        directions[DOWN] = 0;
    }

}
