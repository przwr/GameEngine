/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import collision.Figure;

/**
 *
 * @author przemek
 */
public class ShadowContener {

    private static byte INITIAL_SHADOWS_COUNT = 10;
    private Shadow[] shadows;
    private int shadowsCount;

    public ShadowContener() {
        shadows = new Shadow[INITIAL_SHADOWS_COUNT];
        for (int i = 0; i < INITIAL_SHADOWS_COUNT; i++) {
            shadows[i] = new Shadow(0);
        }
    }

    public void add(Shadow shadow) {
        ensureCapacity(1);
        shadows[shadowsCount].type = shadow.type;
        shadows[shadowsCount].point.set(shadow.point.getX(), shadow.point.getY());
        shadowsCount++;
    }

    public void addAll(ShadowContener newshadows) {
        ensureCapacity(newshadows.shadowsCount);
        for (int i = 0; i < newshadows.shadowsCount; i++) {
            shadows[shadowsCount].type = newshadows.shadows[i].type;
            shadows[shadowsCount].point.set(newshadows.shadows[i].point.getX(), newshadows.shadows[i].point.getY());
            shadowsCount++;
        }
    }

    public void add(int type, int x, int y) {
        ensureCapacity(1);
        shadows[shadowsCount].type = type;
        shadows[shadowsCount].point.set(x, y);
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
        shadows[shadowsCount].point.set(x, y);
        shadows[shadowsCount].caster = caster;
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
                shadows[i].point.set(shadows[shadowsCount].point.getX(), shadows[shadowsCount].point.getY());
                shadows[i].caster = shadows[shadowsCount].caster;
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

    public int size() {
        return shadowsCount;
    }

}
