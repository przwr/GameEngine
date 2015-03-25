/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.place.Shadow;

/**
 *
 * @author przemek
 */
public class OpticProperties {

    public static final int FULL_SHADOW = 0, NO_SHADOW = 1, IN_SHADE_NO_SHADOW = 2, IN_SHADE = 3, INITIAL_SHADOWS_COUNT = 20;
    private static final boolean[] LITABLE = {true, true, false, false};
    private static final boolean[] GIVE_SHADOW = {true, false, false, true};
    private final int type;
    private final int shadowHeight;
    private int lightDistance;
    private Shadow[] shadows = new Shadow[INITIAL_SHADOWS_COUNT];
    private int shadowsCount = 0;

    public static OpticProperties create(int type, int shadowHeight) {
        return new OpticProperties(type, shadowHeight);
    }

    public static OpticProperties create(int type) {
        return new OpticProperties(type, 0);
    }

    private OpticProperties(int type, int shadowHeight) {
        this.type = type;
        this.shadowHeight = shadowHeight;
        for (int i = 0; i < shadows.length; i++) {
            shadows[i] = new Shadow(0);
        }
    }

    public void addShadow(int type, int x, int y, Figure caster) {
        if (shadowsCount == shadows.length) {
            resizeShadows();
        }
        shadows[shadowsCount].type = type;
        shadows[shadowsCount].point.set(x, y);
        shadows[shadowsCount].caster = caster;
        shadowsCount++;
    }

    private void resizeShadows() {
        Shadow[] tempShadows = new Shadow[2 * shadows.length];
        System.arraycopy(shadows, 0, tempShadows, 0, shadows.length);
        shadows = tempShadows;
        for (int i = shadowsCount; i < shadows.length; i++) {
            shadows[i] = new Shadow(0);
        }
    }

    public void clearShadows() {
        shadowsCount = 0;
    }

    public void removeShadow(Shadow shadow) {
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

    public boolean isLitable() {
        return LITABLE[type];
    }

    public boolean isGiveShadow() {
        return GIVE_SHADOW[type];
    }

    public int getShadowHeight() {
        return shadowHeight;
    }

    public int getLightDistance() {
        return lightDistance;
    }

    public int getShadowCount() {
        return shadowsCount;
    }

    public Shadow getShadow(int i) {
        return shadows[i];

    }

    public void setLightDistance(int lightDistance) {
        this.lightDistance = lightDistance;
    }
}
