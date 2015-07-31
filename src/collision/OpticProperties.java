/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Shadow;
import engine.ShadowContainer;

/**
 * @author przemek
 */
public class OpticProperties {

    public static final byte FULL_SHADOW = 0, NO_SHADOW = 1, IN_SHADE_NO_SHADOW = 2, TRANSPARENT = 3;
    private static final boolean[] LITABLE = {true, true, false, false};
    private static final boolean[] GIVE_SHADOW = {true, false, false, true};
    private int type;
    private int shadowHeight;
    private int lightDistance;
    private ShadowContainer shadows;

    private OpticProperties(int type, int shadowHeight) {
        this.type = type;
        this.shadowHeight = shadowHeight;
        this.shadows = new ShadowContainer();
    }

    public static OpticProperties create(int type, int shadowHeight) {
        return new OpticProperties(type, shadowHeight);
    }

    public static OpticProperties create(int type) {
        return new OpticProperties(type, 0);
    }

    public void addShadow(Shadow shadow) {
        shadows.add(shadow);
    }

    public void addAllShadows(ShadowContainer shadow) {
        shadows.addAll(shadow);
    }

    public void addShadow(int type, int x, int y) {
        shadows.add(type, x, y);
    }

    public void addShadowType(int type) {
        shadows.addType(type);
    }

    public void addShadowWithCaster(int type, int x, int y, Figure caster) {
        shadows.addWithCaster(type, x, y, caster);
    }

    public void clearShadows() {
        shadows.clear();
    }

    public void removeShadow(Shadow shadow) {
        shadows.remove(shadow);
    }

    public boolean isLitable() {
        return LITABLE[type];
    }

    public boolean isGiveShadow() {
        return GIVE_SHADOW[type];
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getShadowHeight() {
        return shadowHeight;
    }

    public void setShadowHeight(int shadowHeight) {
        this.shadowHeight = shadowHeight;
    }

    public int getLightDistance() {
        return lightDistance;
    }

    public void setLightDistance(int lightDistance) {
        this.lightDistance = lightDistance;
    }

    public int getShadowCount() {
        return shadows.size();
    }

    public Shadow getShadow(int i) {
        return shadows.get(i);
    }
}
