/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;

/**
 * @author przemek
 */
public class ShadowContainer {

    private static final byte INITIAL_SHADOWS_COUNT = 10;
    private Shadow[] shadows;
    private int shadowsCount;

    public ShadowContainer() {
        shadows = new Shadow[INITIAL_SHADOWS_COUNT];
        for (int i = 0; i < INITIAL_SHADOWS_COUNT; i++) {
            shadows[i] = new Shadow(0);
        }
    }

    public void add(Shadow shadow) {
        ensureCapacity(1);
        shadows[shadowsCount].type = shadow.type;
        shadows[shadowsCount].xS = shadow.xS;
        shadows[shadowsCount].xE = shadow.xE;
        shadowsCount++;
    }

    public void addAll(ShadowContainer newShadows) {
        ensureCapacity(newShadows.shadowsCount);
        for (int i = 0; i < newShadows.shadowsCount; i++) {
            shadows[shadowsCount].type = newShadows.shadows[i].type;
            shadows[shadowsCount].xS = newShadows.shadows[i].xS;
            shadows[shadowsCount].xE = newShadows.shadows[i].xE;
            shadowsCount++;
        }
    }

    public void add(int type, int x, int y) {
        ensureCapacity(1);
        shadows[shadowsCount].type = type;
        shadows[shadowsCount].xS = x;
        shadows[shadowsCount].xE = y;
        shadowsCount++;
    }

    public void addType(int type) {
        ensureCapacity(1);
        shadows[shadowsCount].type = type;
        shadowsCount++;
    }

    public void addWithCaster(int type, int x, int y, Figure caster) {
        ensureCapacity(1);
        shadows[shadowsCount].type = type;
        shadows[shadowsCount].type = type;
        shadows[shadowsCount].xS = x;
        shadows[shadowsCount].xE = y;
        shadows[shadowsCount].xC = caster.getX();
        shadows[shadowsCount].yC = caster.getYEnd();
        shadowsCount++;
    }

    private void ensureCapacity(int capacity) {
        if (shadowsCount + capacity > shadows.length) {
            Shadow[] tempShadows = new Shadow[(int) (1.5 * shadows.length)];
            System.arraycopy(shadows, 0, tempShadows, 0, shadows.length);
            shadows = tempShadows;
            for (int i = shadowsCount; i < shadows.length; i++) {
                shadows[i] = new Shadow(0);
            }
        }
    }

    public Shadow get(int i) {
        return shadows[i];
    }

    public void remove(Shadow shadow) {
        for (int i = 0; i < shadowsCount; i++) {
            if (shadows[i] == shadow) {
                shadowsCount--;
                shadows[i].type = shadows[shadowsCount].type;
                shadows[shadowsCount].xS = shadows[shadowsCount].xS;
                shadows[shadowsCount].xE = shadows[shadowsCount].xE;
                shadows[shadowsCount].xC = shadows[shadowsCount].xC;
                shadows[shadowsCount].yC = shadows[shadowsCount].yC;
                break;
            }
        }
    }

    public boolean isEmpty() {
        return shadowsCount == 0;
    }

    public void clear() {
        shadowsCount = 0;
    }


    public void clearReally() {
        for (int i = 0; i < shadows.length; i++) {
            shadows[i] = null;
        }
    }

    public int size() {
        return shadowsCount;
    }

}
