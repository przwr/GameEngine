/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.place.Shadow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
	private float shadowColor;
	private final ArrayList<Shadow> shadows = new ArrayList<>();

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
		shadows.add(shadow);
	}

	public void clearShadows() {
		shadows.clear();
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

	public Collection<Shadow> getShadows() {
		return Collections.unmodifiableList(shadows);
	}

	public void setShadowColor(float shadowColor) {
		this.shadowColor = shadowColor;
	}
}
