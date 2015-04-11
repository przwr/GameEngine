/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import org.newdawn.slick.Color;

/**
 *
 * @author Domi
 */
public class DayCycle {

	Color lightColor = new Color(0.15f, 0.15f, 0.1875f);

	short timeInMinutes = 0;
	long midnightTime;
	long currentTime, diffrence, tempMinutes;
	short REAL_MINUTES_IN_HOUR = 4;

	// stop start Time!
	
	public DayCycle() {
		midnightTime = System.currentTimeMillis();
	}

	public void updateTime() {
		updateDiffrence();
		calculateMinutes(diffrence / 1000);
		updateLightColor();
	}

	private void updateDiffrence() {
		currentTime = System.currentTimeMillis();
		diffrence = currentTime - midnightTime;
	}

	private void calculateMinutes(long seconds) {
		tempMinutes = (seconds / REAL_MINUTES_IN_HOUR);
		if (tempMinutes > 1440) {
			tempMinutes -= 1440;
			resetMidnightTime();
		}
		timeInMinutes = (short) tempMinutes;
	}

	private void resetMidnightTime() {
		midnightTime = currentTime;
	}

	short DAWN = 300, DUSK = 1260, NOON = (short) ((DUSK + DAWN) / 2);

	private void updateLightColor() {
		if (timeInMinutes >= DAWN && timeInMinutes < NOON) {
			float temp = timeInMinutes - DAWN;
			float delta = 0.85f / (NOON - DAWN);
			temp *= delta;
			lightColor.r = lightColor.g = 0.15f + temp;
			temp = timeInMinutes - DAWN;
			delta = 0.5625f / (NOON - DAWN);
			temp *= delta;
			lightColor.b = 0.1875f + temp;
		} else if (timeInMinutes == NOON) {
			lightColor.r = lightColor.g = 1f;
			lightColor.b = 0.75f;
		} else if (timeInMinutes >= NOON && timeInMinutes < DUSK) {
			float temp = timeInMinutes - NOON;
			float delta = 0.85f / (NOON - DAWN);
			temp *= delta;
			lightColor.r = lightColor.g = 1f - temp;
			temp = timeInMinutes - NOON;
			delta = 0.5625f / (NOON - DAWN);
			temp *= delta;
			lightColor.b = 0.75f - temp;
		} else if (timeInMinutes >= DUSK) {
			lightColor.r = lightColor.g = 0.15f;
			lightColor.b = 0.1875f;
		}
	}

	public void setToDayLight(Map map) {
		map.setColor(lightColor);
	}

	public short getTime() {
		return timeInMinutes;
	}

	public void setTime(int hour, int minutes) {
		updateDiffrence();
		midnightTime -= (((hour * 60 + minutes) * REAL_MINUTES_IN_HOUR * 1000) - diffrence);
		updateTime();
	}

	public void addOneHour() {
		addHours(1);
	}

	public void addHours(int hours) {
		midnightTime -= hours * REAL_MINUTES_IN_HOUR * 60000;
		updateTime();
	}

	public Color getColor() {
		return lightColor;
	}

	@Override
	public String toString() {
		int hours = this.timeInMinutes / 60;
		int minutes = this.timeInMinutes % 60;
		return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
	}
}
