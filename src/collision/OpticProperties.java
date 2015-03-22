/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.place.Shadow;
import static game.place.Shadow.*;

/**
 *
 * @author przemek
 */
public class OpticProperties {

    public static final int FULL_SHADOW = 0, NO_SHADOW = 1, IN_SHADE_NO_SHADOW = 2, IN_SHADE = 3;
    private static final boolean[] LITABLE = {true, true, false, false};
    private static final boolean[] GIVE_SHADOW = {true, false, false, true};
    private final int type;
    private final int shadowHeight;
    private int lightDistance;
    private float shadowColor;
    private Shadow[] shadowsy = new Shadow[50];
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
    }

    public void addShadow(Shadow shadow) {
        if (shadowsCount == shadowsy.length) {
            resizeShadows();
        }
        if (shadowsy[shadowsCount] == null) {
            shadowsy[shadowsCount] = new Shadow(0);
        }
        switch (shadow.type) {
            case DARK:
                shadowsy[shadowsCount++].setDark();
                break;
            case BRIGHT:
                shadowsy[shadowsCount++].setBright();
                break;
            case BRIGHTEN:
                shadowsy[shadowsCount++].setBrighten(shadow.point.getX(), shadow.point.getY(), shadow.source);
                break;
            case DARKEN:
                shadowsy[shadowsCount++].setDarken(shadow.point.getX(), shadow.point.getY());
                break;
            case BRIGHTEN_OBJECT:
                shadowsy[shadowsCount++].setBrightenObject(shadow.point.getX(), shadow.point.getY());
                break;
            case DARKEN_OBJECT:
                shadowsy[shadowsCount++].setDarkenObject(shadow.point.getX(), shadow.point.getY());
                break;
            default:
                break;
        }
    }

    private void resizeShadows() {
        // TODO
    }

    public void clearShadows() {
        shadowsCount = 0;
    }

    public void removeShadow(Shadow shadow) {
        for (int i = 0; i < shadowsCount; i++) {
            if (shadowsy[i] == shadow) {
                shadowsy[i] = shadowsy[--shadowsCount];
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

    public float getShadowColor() {
        return shadowColor;
    }

    public int getLightDistance() {
        return lightDistance;
    }

    public int getShadowCount() {
        return shadowsCount;
    }

    public Shadow getShadow(int i) {
        return shadowsy[i];
    }

    public void setShadowColor(float shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setLightDistance(int lightDistance) {
        this.lightDistance = lightDistance;
    }
}
