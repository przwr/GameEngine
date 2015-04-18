/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

/**
 *
 * @author przemek
 */
public class Placement {

    public static final short CENTER = 0, TOP_LEFT = 1, TOP_CENTER = 2, TOP_RIGHT = 3, LEFT = 4, RIGHT = 5, LEFT_BOTTOM = 6, BOTTOM_CENTER = 7, BOTTOM_RIGHT = 8;
    private static final int[] emptyAreas = new int[9];

    protected Map map;
    protected int[][] allAreas;

    public Placement(Map map) {
        this.map = map;
        calculateAllAreas();
    }

    private void calculateAllAreas() {
        allAreas = new int[map.getAreasSize()][9];
        int area = 0;
        for (int[] nearAreas : allAreas) {
            calculateNear(nearAreas, area);
            validateNear(nearAreas, area);
            area++;
        }
    }

    private void calculateNear(int[] nearAreas, int area) {
        nearAreas[CENTER] = area;
        nearAreas[TOP_LEFT] = area - map.getXAreas() - 1;
        nearAreas[TOP_CENTER] = area - map.getXAreas();
        nearAreas[TOP_RIGHT] = area - map.getXAreas() + 1;
        nearAreas[LEFT] = area - 1;
        nearAreas[RIGHT] = area + 1;
        nearAreas[LEFT_BOTTOM] = area + map.getXAreas() - 1;
        nearAreas[BOTTOM_CENTER] = area + map.getXAreas();
        nearAreas[BOTTOM_RIGHT] = area + map.getXAreas() + 1;
    }

    private void validateNear(int[] nearAreas, int area) {
        if (area % map.getXAreas() == 0) {
            for (int i = 1; i < nearAreas.length; i++) {
                if (nearAreas[i] % map.getXAreas() == map.getXAreas() - 1) {
                    nearAreas[i] = -1;
                }
            }
        }
        if (area % map.getXAreas() == map.getXAreas() - 1) {
            for (int i = 1; i < nearAreas.length; i++) {
                if (nearAreas[i] % map.getXAreas() == 0) {
                    nearAreas[i] = -1;
                }
            }
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
