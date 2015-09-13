/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

/**
 * @author przemek
 */
public class Placement {

    public static final short CENTER = 0;
    private static final short TOP_LEFT = 1;
    private static final short TOP_CENTER = 2;
    private static final short TOP_RIGHT = 3;
    private static final short LEFT = 4;
    private static final short RIGHT = 5;
    private static final short BOTTOM_LEFT = 6;
    private static final short BOTTOM_CENTER = 7;
    private static final short BOTTOM_RIGHT = 8;
    private static final int[] emptyAreas = new int[9];

    static {
        for (int i = 0; i < 9; i++) {
            emptyAreas[i] = -1;
        }
    }

    private final Map map;
    private int[][] allAreas;

    public Placement(Map map) {
        this.map = map;
        calculateAllAreas();
    }

    private void calculateAllAreas() {
        allAreas = new int[map.getAreasSize()][9];
        int area;
        for (int xArea = 0; xArea < map.getXAreas(); xArea++) {
            for (int yArea = 0; yArea < map.getYAreas(); yArea++) {
                area = map.getXAreas() * yArea + xArea;
                calculateNear(allAreas[area], area, xArea, yArea);
            }
        }
    }

    private void calculateNear(int[] nearAreas, int area, int xArea, int yArea) {
        nearAreas[CENTER] = area;
        if (yArea - 1 >= 0) {
            nearAreas[TOP_LEFT] = (xArea - 1 >= 0) ? area - map.getXAreas() - 1 : -1;
            nearAreas[TOP_CENTER] = area - map.getXAreas();
            nearAreas[TOP_RIGHT] = (xArea + 1 < map.getXAreas()) ? area - map.getXAreas() + 1 : -1;
        } else {
            nearAreas[TOP_LEFT] = -1;
            nearAreas[TOP_CENTER] = -1;
            nearAreas[TOP_RIGHT] = -1;
        }
        nearAreas[LEFT] = (xArea - 1 >= 0) ? area - 1 : -1;
        nearAreas[RIGHT] = (xArea + 1 < map.getXAreas()) ? area + 1 : -1;
        if (yArea + 1 < map.getYAreas()) {
            nearAreas[BOTTOM_LEFT] = (xArea - 1 >= 0) ? area + map.getXAreas() - 1 : -1;
            nearAreas[BOTTOM_CENTER] = area + map.getXAreas();
            nearAreas[BOTTOM_RIGHT] = (xArea + 1 < map.getXAreas()) ? area + map.getXAreas() + 1 : -1;
        } else {
            nearAreas[BOTTOM_LEFT] = -1;
            nearAreas[BOTTOM_CENTER] = -1;
            nearAreas[BOTTOM_RIGHT] = -1;
        }
    }

    public int[] getNearAreas(int area) {
        if (area >= 0 && area < map.getAreasSize()) {
            return allAreas[area];
        } else {
            return emptyAreas;
        }
    }
}
